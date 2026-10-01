// src/features/ViewVehicles.js
import React from 'react';
import axiosInstance from '../helper/axios';
import VehicleItem from '../components/VehicleItem';
import {
    Container, Typography, List, CircularProgress, Button,
    Dialog, DialogTitle, DialogContent, DialogActions,
    TextField, Box, Alert
} from '@mui/material';

class ViewVehicles extends React.Component {
    state = {
        vehicles: [],
        loading: true,
        isModalOpen: false,
        error: null,
        saveErrors: [],
        formData: { id: null, licensePlate: '', model: '', color: '' }
    };

    componentDidMount() {
        this.fetchVehicles();
    }

    fetchVehicles = () => {
        axiosInstance.get('/api/vehicles')
            .then(res => {
                const user = JSON.parse(localStorage.getItem('user'));
                const allVehicles = res.data;
                const vehicles = (user && user.role === 'ROLE_CLIENT')
                    ? allVehicles.filter(v => v.ownerId === user.id)
                    : allVehicles;
                this.setState({ vehicles, loading: false });
            })
            .catch(() => this.setState({ error: 'Nu am putut incarca masinile', loading: false }));
    }

    handleOpenModal = (vehicle = null) => {
        if (vehicle) {
            this.setState({ isModalOpen: true, saveErrors: [], formData: { ...vehicle } });
        } else {
            this.setState({
                isModalOpen: true,
                saveErrors: [],
                formData: { id: null, licensePlate: '', model: '', color: '' }
            });
        }
    }

    handleCloseModal = () => this.setState({ isModalOpen: false, saveErrors: [] });

    handleChange = (e) => {
        const { name, value } = e.target;
        const { formData} =this.state;
        const errors =[];
        this.setState(prev => ({
            formData: { ...prev.formData, [name]: value }
        }));

        console.log(this.formData);
        if (!formData.licensePlate || formData.licensePlate.trim() === '') {
            errors.push('Numarul de inmatriculare este obligatoriu!');
        }

        if (errors.length > 0) {
            this.setState({ saveErrors: errors });
        }
    }

    handleSave = () => {
        const { formData } = this.state;
        const errors = [];

        // Validare locala inainte de submit
        if (!formData.licensePlate || formData.licensePlate.trim() === '') {
            errors.push('Numarul de inmatriculare este obligatoriu!');
        } else {
            const plateRegex = /^(B|AB|AG|AR|BC|BH|BN|BR|BT|BV|BZ|CJ|CL|CS|CT|CV|DB|DJ|GJ|GL|GR|HD|HR|IF|IL|IS|MH|MM|MS|NT|OT|PH|SB|SJ|SM|SV|TL|TM|TR|VL|VN|VS)-(\d{2,3})-([A-Z]{3})$/;
            if (!plateRegex.test(formData.licensePlate.trim())) {
                errors.push('Numarul de inmatriculare este invalid! Format: B-123-ABC');
            }
        }
        if (!formData.model || formData.model.trim() === '') {
            errors.push('Modelul / Marca este obligatoriu!');
        }
        if (!formData.color || formData.color.trim() === '') {
            errors.push('Culoarea este obligatorie!');
        }

        if (errors.length > 0) {
            this.setState({ saveErrors: errors });
            return;
        }

        const endpoint = formData.id ? `/api/vehicles/${formData.id}` : '/api/vehicles';
        const method   = formData.id ? 'put' : 'post';

        axiosInstance[method](endpoint, formData)
            .then(() => {
                this.handleCloseModal();
                this.fetchVehicles();
            })
            .catch((err) => {
                if (err.response && err.response.data && err.response.data.errors) {
                    this.setState({ saveErrors: err.response.data.errors });
                } else if (err.response && err.response.data && err.response.data.message) {
                    this.setState({ saveErrors: [err.response.data.message] });
                } else {
                    this.setState({ saveErrors: ['Eroare la salvarea vehiculului!'] });
                }
            });
    }

    handleDelete = (id) => {
        if (window.confirm('Sigur vrei sa stergi masina din sistem?')) {
            axiosInstance.delete(`/api/vehicles/${id}`)
                .then(() => this.fetchVehicles())
                .catch(() => alert('Nu se poate sterge masina (probabil are o rezervare activa)!'));
        }
    }

    render() {
        const { vehicles, loading, isModalOpen, formData, error, saveErrors } = this.state;
        if (loading) return <CircularProgress sx={{ display: 'block', m: '50px auto' }} />;

        return (
            <Container sx={{ mt: 4 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                    <Typography variant="h5">Gestiune Masini</Typography>
                    <Button variant="contained" onClick={() => this.handleOpenModal()}>
                        Adauga Masina
                    </Button>
                </Box>

                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

                <List disablePadding>
                    {vehicles.map(v => (
                        <VehicleItem key={v.id} vehicle={v} onEdit={this.handleOpenModal} onDelete={this.handleDelete} />
                    ))}
                </List>

                <Dialog open={isModalOpen} onClose={this.handleCloseModal} fullWidth maxWidth="xs">
                    <DialogTitle>
                        {formData.id ? 'Editeaza Masina' : 'Inregistreaza Masina Noua'}
                    </DialogTitle>
                    <DialogContent>
                        {saveErrors.length > 0 && (
                            <Alert severity="error" sx={{ mb: 2 }}>
                                {saveErrors.map((e, i) => <div key={i}>{e}</div>)}
                            </Alert>
                        )}
                        <TextField
                            fullWidth label="Numar Inmatriculare" name="licensePlate"
                            value={formData.licensePlate} onChange={this.handleChange} sx={{ mb: 2, mt: 1 }}
                        />
                        <TextField
                            fullWidth label="Model / Marca" name="model"
                            value={formData.model} onChange={this.handleChange} sx={{ mb: 2 }}
                        />
                        <TextField
                            fullWidth label="Culoare" name="color"
                            value={formData.color} onChange={this.handleChange}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleCloseModal}>Anuleaza</Button>
                        <Button onClick={this.handleSave} variant="contained">
                            {formData.id ? 'Salveaza Modificarile' : 'Salveaza Masina'}
                        </Button>
                    </DialogActions>
                </Dialog>
            </Container>
        );
    }
}

export default ViewVehicles;

