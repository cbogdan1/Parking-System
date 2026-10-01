// src/components/ReservationItem.js
import React from 'react';
import { ListItem, Paper, Typography, Box, Button } from '@mui/material';
import ReceiptIcon from '@mui/icons-material/Receipt';
import axiosInstance from '../helper/axios';

const ReservationItem = ({ reservation, onEdit, onDelete, isAdmin, onCheckout, onExtend }) => {
    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleString('ro-RO', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' });
    };

    // Descarca factura XML pentru aceasta rezervare
    const handleDownloadInvoice = () => {
        axiosInstance.get(`/api/reservations/${reservation.id}/invoice`, {
            responseType: 'blob',  // Primim raspunsul ca fisier binar
            headers: { 'Accept': 'application/xml' }
        })
        .then(res => {
            // Cream un link temporar pentru descarcare
            const blob = new Blob([res.data], { type: 'application/xml' });
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `factura_rezervare_${reservation.id}.xml`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        })
        .catch(err => {
            console.error('Eroare la descarcarea facturii:', err);
            alert('Nu s-a putut descarca factura!');
        });
    };

    return (
        <Paper variant="outlined" sx={{ mb: 1 }}>
            <ListItem sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                    <Typography variant="subtitle1">
                        <strong>{reservation.vehicleLicensePlate || 'Fara Masina'}</strong>
                        {' '}({reservation.vehicleModel || 'N/A'})
                    </Typography>
                    <Typography variant="body2">
                        Loc: {reservation.spotNumber || 'Loc Sters'} ({reservation.spotSection})
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                        {formatDate(reservation.startTime)} - {formatDate(reservation.endTime)}
                    </Typography>
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Typography variant="subtitle1">
                        <strong>{reservation.totalCost} RON</strong>
                    </Typography>

                    <Button
                        size="small"
                        variant="outlined"
                        color="success"
                        startIcon={<ReceiptIcon />}
                        onClick={handleDownloadInvoice}
                    >
                        Factura XML
                    </Button>
                    <Button size="small" variant="outlined" onClick={() => onCheckout(reservation.id)}>
                        Check-out
                    </Button>
                    <Button size="small" variant="outlined" onClick={() => onExtend(reservation.id)}>
                        Extinde
                    </Button>
                    {isAdmin && (
                        <>
                            <Button size="small" variant="outlined" onClick={() => onEdit(reservation)}>
                                Editeaza
                            </Button>
                            <Button size="small" variant="outlined" color="error" onClick={() => onDelete(reservation.id)}>
                                Sterge
                            </Button>
                        </>
                    )}
                </Box>
            </ListItem>
        </Paper>
    );
};

export default ReservationItem;