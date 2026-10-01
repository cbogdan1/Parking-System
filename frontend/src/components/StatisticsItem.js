// src/components/StatisticsItem.js
import React from 'react';
import { Grid, Paper, Typography } from '@mui/material';

const StatisticsItem = ({ label, value, color }) => {
    return (
        <Grid item xs={12} sm={4}>
            <Paper
                elevation={3}
                sx={{
                    p: 3,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    borderRadius: '12px',
                    borderLeft: `6px solid ${color}`
                }}
            >
                <Typography variant="subtitle1" color="text.secondary" gutterBottom>
                    {label}
                </Typography>
                <Typography variant="h3" component="div" sx={{ fontWeight: 'bold', color: color }}>
                    {value}
                </Typography>
            </Paper>
        </Grid>
    );
};

export default StatisticsItem;