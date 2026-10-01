// src/features/ExtendReservation.js
import React, { useState } from 'react';
import {
    Dialog, DialogTitle, DialogContent, DialogActions,
    Button, TextField, Typography, Divider, Alert
} from '@mui/material';
import axiosInstance from '../helper/axios';

const ExtendReservation = ({ open, reservation, onClose, onSuccess }) => {
    const [extraHours, setExtraHours] = useState(1);
    const [loading, setLoading]       = useState(false);
    const [error, setError]           = useState('');

    if (!reservation) return null;

    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        return new Date(dateString).toLocaleString('ro-RO', {
            day: '2-digit', month: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
        });
    };

    const estimatedEndTime = () => {
        if (!reservation.endTime) return 'N/A';
        const d = new Date(reservation.endTime);
        d.setHours(d.getHours() + (parseInt(extraHours) || 0));
        return d.toLocaleString('ro-RO', {
            day: '2-digit', month: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
        });
    };

    const handleConfirm = () => {
        const hours = parseInt(extraHours, 10);
        if (!hours || hours <= 0) {
            setError('Introduceti un numar valid de ore (minim 1)!');
            return;
        }

        setError('');
        setLoading(true);

        axiosInstance.post(`/api/reservations/${reservation.id}/extend?extraHours=${hours}`)
            .then(res => {
                setLoading(false);
                setExtraHours(1);
                onSuccess(res.data);
                onClose();
            })
            .catch(err => {
                setLoading(false);
                setError(err.response?.data || 'Eroare la extinderea rezervarii. Incearca din nou.');
            });
    };

    const handleClose = () => {
        setError('');
        setExtraHours(1);
        onClose();
    };

    return (
        <Dialog open={open} onClose={handleClose} fullWidth maxWidth="xs">
            <DialogTitle>Extinde Rezervarea</DialogTitle>

            <DialogContent>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                    Detalii rezervare curenta
                </Typography>
                <Typography variant="body1">
                    <strong>{reservation.vehicleLicensePlate || 'N/A'}</strong>
                    {' '}({reservation.vehicleModel || 'N/A'})
                </Typography>
                <Typography variant="body2">
                    Loc: {reservation.spotNumber || 'N/A'} ({reservation.spotSection || 'N/A'})
                </Typography>

                <Divider sx={{ my: 1.5 }} />

                <Typography variant="body2">Start: {formatDate(reservation.startTime)}</Typography>
                <Typography variant="body2">Sfarsit actual: {formatDate(reservation.endTime)}</Typography>
                <Typography variant="body2">Sfarsit nou (estimat): {estimatedEndTime()}</Typography>

                <Divider sx={{ my: 1.5 }} />

                <Typography variant="body2">
                    Cost curent: <strong>{reservation.totalCost} RON</strong>
                    &nbsp;·&nbsp; {reservation.parkingSpot?.pricePerHour || 0} RON/h
                </Typography>

                <TextField
                    fullWidth
                    label="Numar de ore suplimentare"
                    type="number"
                    value={extraHours}
                    onChange={(e) => { setExtraHours(e.target.value); setError(''); }}
                    inputProps={{ min: 1, max: 72 }}
                    sx={{ mt: 2 }}
                    autoFocus
                    helperText="Introduceti cate ore doriti sa adaugati la rezervare"
                />

                {error && <Alert severity="error" sx={{ mt: 1 }}>{error}</Alert>}
            </DialogContent>

            <DialogActions>
                <Button onClick={handleClose} disabled={loading}>Anuleaza</Button>
                <Button onClick={handleConfirm} variant="contained" disabled={loading}>
                    {loading ? 'Se proceseaza...' : `Extinde cu ${extraHours || 0}h`}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default ExtendReservation;
