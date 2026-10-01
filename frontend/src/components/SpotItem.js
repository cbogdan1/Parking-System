// src/components/SpotItem.js
import React from 'react';
import { ListItem, ListItemText, Paper, Typography, Box, Button } from '@mui/material';

const SpotItem = ({ spot, onEdit, onDelete }) => {
    return (
        <Paper variant="outlined" sx={{ mb: 1 }}>
            <ListItem
                secondaryAction={
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Typography variant="body2">
                            {spot.occupied ? 'OCUPAT' : 'LIBER'}
                        </Typography>
                        <Button size="small" variant="outlined" onClick={() => onEdit(spot)}>
                            Editeaza
                        </Button>
                        <Button size="small" variant="outlined" color="error" onClick={() => onDelete(spot.id)}>
                            Sterge
                        </Button>
                    </Box>
                }
            >
                <ListItemText
                    primary={`Loc: ${spot.spotNumber} (Sectiunea ${spot.section})`}
                    secondary={`Tarif: ${spot.pricePerHour} RON / ora`}
                />
            </ListItem>
        </Paper>
    );
};

export default SpotItem;