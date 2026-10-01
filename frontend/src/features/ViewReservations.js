// src/features/ViewReservations.js
import React from 'react';
import axiosInstance from '../helper/axios';
import ReservationItem from '../components/ReservationItem';
import ExtendReservation from './ExtendReservation';
import {
    Container, Typography, List, CircularProgress, Button,
    Dialog, DialogTitle, DialogContent, DialogActions,
    TextField, MenuItem, Box, Alert
} from '@mui/material';

class ViewReservations extends React.Component {
    state = {
        reservations: [],
        vehicles: [],
        spots: [],
        loading: true,
        isModalOpen: false,
        saveErrors: [],
        formData: { id: null, vehicle: { id: '' }, parkingSpot: { id: '' }, startTime: '', endTime: '' },
        extendTarget: null
    };

    componentDidMount() {
        this.loadAllData();
    }

    loadAllData = async () => {
        try {
            const [resReservations, resVehicles, resSpots] = await Promise.all([
                axiosInstance.get('/api/reservations'),
                axiosInstance.get('/api/vehicles'),
                axiosInstance.get('/api/spots')
            ]);
            this.setState({
                reservations: resReservations.data,
                vehicles: resVehicles.data,
                spots: resSpots.data,
                loading: false
            });
        } catch (err) {
            console.error('Eroare la incarcarea datelor:', err);
            this.setState({ loading: false });
        }
    }

    handleCheckout = (id) => {
        if (window.confirm('Confirmati plecarea? Locul va fi eliberat si pretul recalculat conform timpului parcurs.')) {
            axiosInstance.post(`/api/reservations/${id}/checkout`)
                .then(() => {
                    alert('Check-out realizat cu succes!');
                    this.loadAllData();
                })
                .catch(() => alert('Eroare la procesarea check-out-ului.'));
        }
    }

    handleExtend = (id) => {
        const reservation = this.state.reservations.find(r => r.id === id);
        if (reservation) {
            this.setState({ extendTarget: reservation });
        }
    }

    handleExtendSuccess = () => {
        this.setState({ extendTarget: null });
        this.loadAllData();
    }

    handleOpenModal = (reservation = null) => {
        if (reservation) {
            this.setState({ isModalOpen: true, saveErrors: [], formData: { ...reservation } });
        } else {
            this.setState({
                isModalOpen: true,
                saveErrors: [],
                formData: { id: null, vehicle: { id: '' }, parkingSpot: { id: '' }, startTime: '', endTime: '' }
            });
        }
    }

    handleCloseModal = () => this.setState({ isModalOpen: false, saveErrors: [] });

    handleChange = (e) => {
        const { name, value } = e.target;
        if (name.includes('.')) {
            const [obj, field] = name.split('.');
            this.setState(prev => ({
                formData: { ...prev.formData, [obj]: { ...prev.formData[obj], [field]: value } }
            }));
        } else {
            this.setState(prev => ({ formData: { ...prev.formData, [name]: value } }));
        }
    }

    handleSave = () => {
        const { formData } = this.state;
        const errors = [];

        // Validare locala inainte de submit
        if (!formData.vehicle.id) {
            errors.push('Trebuie sa selectezi un vehicul!');
        }
        if (!formData.parkingSpot.id) {
            errors.push('Trebuie sa selectezi un loc de parcare!');
        }
        if (!formData.startTime) {
            errors.push('Data de inceput este obligatorie!');
        }
        if (!formData.endTime) {
            errors.push('Data de sfarsit este obligatorie!');
        }
        if (formData.startTime && formData.endTime && formData.endTime <= formData.startTime) {
            errors.push('Data de sfarsit trebuie sa fie dupa data de inceput!');
        }

        if (errors.length > 0) {
            this.setState({ saveErrors: errors });
            return;
        }

        const payload = {
            id: formData.id,
            vehicleId: parseInt(formData.vehicle.id),
            parkingSpotId: parseInt(formData.parkingSpot.id),
            startTime: formData.startTime,
            endTime: formData.endTime,
            totalCost: 0
        };

        const endpoint = formData.id ? `/api/reservations/${formData.id}` : '/api/reservations';
        const method   = formData.id ? 'put' : 'post';

        axiosInstance[method](endpoint, payload)
            .then(() => {
                this.handleCloseModal();
                this.loadAllData();
            })
            .catch(err => {
                if (err.response && err.response.data && err.response.data.errors) {
                    this.setState({ saveErrors: err.response.data.errors });
                } else if (err.response && err.response.data && err.response.data.message) {
                    this.setState({ saveErrors: [err.response.data.message] });
                } else {
                    this.setState({ saveErrors: ['Eroare la salvarea rezervarii!'] });
                }
            });
    }

    handleDelete = (id) => {
        if (window.confirm('Sigur vrei sa anulezi rezervarea?')) {
            axiosInstance.delete(`/api/reservations/${id}`).then(() => this.loadAllData());
        }
    }

    render() {
        const { reservations, vehicles, spots, loading, isModalOpen, formData, extendTarget, saveErrors } = this.state;
        const user    = JSON.parse(localStorage.getItem('user'));
        const isAdmin = user && user.role === 'ROLE_ADMIN';

        const visibleReservations = isAdmin
            ? reservations
            : reservations.filter(r => r.vehicleOwnerId === user?.id);

        const vehicleOptions = isAdmin
            ? vehicles
            : vehicles.filter(v => v.ownerId === user?.id);

        if (loading) return <CircularProgress sx={{ display: 'block', m: '50px auto' }} />;

        return (
            <Container sx={{ mt: 4, pb: 6 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                    <Typography variant="h5">Rezervari Active</Typography>
                    <Button variant="contained" onClick={() => this.handleOpenModal()}>
                        Rezerva Acum
                    </Button>
                </Box>

                <List disablePadding>
                    {visibleReservations.map(res => (
                        <ReservationItem
                            key={res.id}
                            reservation={res}
                            isAdmin={isAdmin}
                            onEdit={this.handleOpenModal}
                            onDelete={this.handleDelete}
                            onCheckout={this.handleCheckout}
                            onExtend={this.handleExtend}
                        />
                    ))}
                </List>

                {/* Modal Adaugare / Editare */}
                <Dialog open={isModalOpen} onClose={this.handleCloseModal} fullWidth maxWidth="xs">
                    <DialogTitle>
                        {formData.id ? 'Editeaza Rezervarea' : 'Rezerva un Loc'}
                    </DialogTitle>
                    <DialogContent>
                        {saveErrors.length > 0 && (
                            <Alert severity="error" sx={{ mb: 2 }}>
                                {saveErrors.map((e, i) => <div key={i}>{e}</div>)}
                            </Alert>
                        )}
                        <TextField
                            select fullWidth label="Alege Masina" name="vehicle.id"
                            value={formData.vehicle?.id || ''} onChange={this.handleChange} sx={{ mb: 2, mt: 1 }}
                        >
                            {vehicleOptions.map(v => (
                                <MenuItem key={v.id} value={v.id}>{v.licensePlate} ({v.model})</MenuItem>
                            ))}
                        </TextField>

                        <TextField
                            select fullWidth label="Alege Locul" name="parkingSpot.id"
                            value={formData.parkingSpot?.id || ''} onChange={this.handleChange} sx={{ mb: 2 }}
                        >
                            {spots.map(s => (
                                <MenuItem key={s.id} value={s.id} disabled={s.occupied && s.id !== formData.parkingSpot?.id}>
                                    {s.spotNumber} - {s.pricePerHour} RON/h {s.occupied ? '(Ocupat)' : ''}
                                </MenuItem>
                            ))}
                        </TextField>

                        <TextField
                            fullWidth label="Data Inceput" name="startTime" type="datetime-local"
                            value={formData.startTime || ''} onChange={this.handleChange}
                            InputLabelProps={{ shrink: true }} sx={{ mb: 2 }}
                        />

                        <TextField
                            fullWidth label="Data Sfarsit" name="endTime" type="datetime-local"
                            value={formData.endTime || ''} onChange={this.handleChange}
                            InputLabelProps={{ shrink: true }}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleCloseModal}>Anuleaza</Button>
                        <Button onClick={this.handleSave} variant="contained">Confirma</Button>
                    </DialogActions>
                </Dialog>

                {/* Dialog Extindere Rezervare */}
                <ExtendReservation
                    open={!!extendTarget}
                    reservation={extendTarget}
                    onClose={() => this.setState({ extendTarget: null })}
                    onSuccess={this.handleExtendSuccess}
                />
            </Container>
        );
    }
}

export default ViewReservations;