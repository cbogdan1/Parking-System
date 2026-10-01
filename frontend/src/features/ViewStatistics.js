// src/features/ViewStatistics.js
import React, { useState, useEffect } from 'react';
import { Container, Grid, Typography, CircularProgress, Box } from '@mui/material';
import axiosInstance from '../helper/axios';
import StatisticsItem from '../components/StatisticsItem';

const ViewStatistics = () => {
    const [stats, setStats]     = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        axiosInstance.get('/api/stats')
            .then(response => {
                setStats(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Eroare la incarcarea statisticilor:', error);
                setLoading(false);
            });
    }, []);

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 5 }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            <Typography variant="h4" gutterBottom sx={{ mb: 4, fontWeight: 'bold' }}>
                Panou Statistici
            </Typography>

            <Grid container spacing={3}>
                <StatisticsItem
                    label="Profit Total"
                    value={`${stats?.totalProfit || 0} RON`}
                    color="#2e7d32"
                />
                <StatisticsItem
                    label="Rezervari Totale"
                    value={stats?.totalReservations || 0}
                    color="#1976d2"
                />
                <StatisticsItem
                    label="Locuri Ocupate"
                    value={stats?.occupiedSpotsCount || 0}
                    color="#ed6c02"
                />
            </Grid>
        </Container>
    );
};

export default ViewStatistics;