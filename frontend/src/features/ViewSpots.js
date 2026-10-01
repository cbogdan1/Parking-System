// src/features/ViewSpots.js
import React from 'react';
import axiosInstance from '../helper/axios';
import SpotItem from '../components/SpotItem';
import {
    Container, Typography, List, CircularProgress, Alert,
    Dialog, DialogTitle, DialogContent, DialogActions,
    Button, TextField, FormControlLabel, Checkbox, Box
} from '@mui/material';

class ViewSpots extends React.Component {
    state = {
        spots: [],
        loading: true,
        error: null,
        saveErrors: [],
        isModalOpen: false,
        selectedSpot: { id: null, spotNumber: '', section: '', pricePerHour: '', occupied: false }
    };

    componentDidMount() {
        this.fetchSpots();
    }

    fetchSpots = () => {
        axiosInstance.get('/api/spots')
            .then(res => this.setState({ spots: res.data, loading: false }))
            .catch(() => this.setState({ error: 'Nu am putut incarca datele', loading: false }));
    }

    handleOpenAddModal = () => {
        this.setState({
            isModalOpen: true,
            saveErrors: [],
            selectedSpot: { id: null, spotNumber: '', section: '', pricePerHour: '', occupied: false }
        });
    }

    handleOpenEditModal = (spot) => {
        this.setState({ isModalOpen: true, saveErrors: [], selectedSpot: { ...spot } });
    }

    handleClose = () => this.setState({ isModalOpen: false, saveErrors: [] });

    handleChange = (e) => {
        const { name, value, checked, type } = e.target;
        this.setState(prevState => ({
            selectedSpot: {
                ...prevState.selectedSpot,
                [name]: type === 'checkbox' ? checked : value
            }
        }));
    }

    handleSave = () => {
        const { selectedSpot } = this.state;
        const errors = [];

        // Validare locala inainte de submit
        if (!selectedSpot.spotNumber || selectedSpot.spotNumber.trim() === '') {
            errors.push('Numarul locului este obligatoriu!');
        }
        if (!selectedSpot.section || selectedSpot.section.trim() === '') {
            errors.push('Sectiunea este obligatorie!');
        }
        if (selectedSpot.pricePerHour === '' || selectedSpot.pricePerHour === null || selectedSpot.pricePerHour === undefined) {
            errors.push('Pretul este obligatoriu!');
        } else if (parseFloat(selectedSpot.pricePerHour) < 0) {
            errors.push('Pretul nu poate fi negativ!');
        }

        if (errors.length > 0) {
            this.setState({ saveErrors: errors });
            return;
        }

        const endpoint = selectedSpot.id ? `/api/spots/${selectedSpot.id}` : '/api/spots';
        const method   = selectedSpot.id ? 'put' : 'post';

        axiosInstance[method](endpoint, selectedSpot)
            .then(() => {
                this.setState({ isModalOpen: false, saveErrors: [] });
                this.fetchSpots();
            })
            .catch((err) => {
                if (err.response && err.response.data && err.response.data.errors) {
                    this.setState({ saveErrors: err.response.data.errors });
                } else if (err.response && err.response.data && err.response.data.message) {
                    this.setState({ saveErrors: [err.response.data.message] });
                } else {
                    this.setState({ saveErrors: ['Eroare la salvarea locului de parcare!'] });
                }
            });
    }

    handleDelete = (id) => {
        if (window.confirm('Sigur vrei sa stergi locul?')) {
            axiosInstance.delete(`/api/spots/${id}`)
                .then(() => this.fetchSpots())
                .catch((err) => {
                    const message = err.response?.data?.message || 'Eroare la stergerea locului de parcare!';
                    this.setState({ error: message });
                });
        }
    }

    render() {
        const { spots, loading, error, isModalOpen, selectedSpot, saveErrors } = this.state;
        if (loading) return <CircularProgress sx={{ display: 'block', m: '50px auto' }} />;

        return (
            <Container sx={{ mt: 4 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h5">Locuri Parcare</Typography>
                    <Button variant="contained" onClick={this.handleOpenAddModal}>
                        Adauga Loc Nou
                    </Button>
                </Box>

                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

                <List disablePadding>
                    {spots.map(spot => (
                        <SpotItem
                            key={spot.id}
                            spot={spot}
                            onEdit={this.handleOpenEditModal}
                            onDelete={this.handleDelete}
                        />
                    ))}
                </List>

                <Dialog open={isModalOpen} onClose={this.handleClose} fullWidth maxWidth="sm">
                    <DialogTitle>
                        {selectedSpot.id ? `Editeaza Locul ${selectedSpot.spotNumber}` : 'Inregistrare Loc Nou'}
                    </DialogTitle>
                    <DialogContent>
                        {saveErrors.length > 0 && (
                            <Alert severity="error" sx={{ mb: 2 }}>
                                {saveErrors.map((e, i) => <div key={i}>{e}</div>)}
                            </Alert>
                        )}
                        <TextField
                            margin="dense" name="spotNumber" label="Numar Loc" type="text" fullWidth
                            value={selectedSpot.spotNumber} onChange={this.handleChange} sx={{ mb: 1 }}
                        />
                        <TextField
                            margin="dense" name="section" label="Sectiune (ex: A, B, C)" type="text" fullWidth
                            value={selectedSpot.section} onChange={this.handleChange} sx={{ mb: 1 }}
                        />
                        <TextField
                            margin="dense" name="pricePerHour" label="Pret pe Ora (RON)" type="number" fullWidth
                            value={selectedSpot.pricePerHour} onChange={this.handleChange} sx={{ mb: 1 }}
                        />
                        <FormControlLabel
                            control={
                                <Checkbox
                                    name="occupied" checked={selectedSpot.occupied || false}
                                    onChange={this.handleChange}
                                />
                            }
                            label="Este Ocupat?"
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleClose}>Anuleaza</Button>
                        <Button onClick={this.handleSave} variant="contained">
                            {selectedSpot.id ? 'Salveaza Modificarile' : 'Salveaza Locul'}
                        </Button>
                    </DialogActions>
                </Dialog>
            </Container>
        );
    }
}

export default ViewSpots;