// src/components/VehicleItem.js
import React from 'react';
import { ListItem, ListItemText, Paper, Box, Button } from '@mui/material';

const VehicleItem = ({ vehicle, onEdit, onDelete }) => {
    return (
        <Paper variant="outlined" sx={{ mb: 1 }}>
            <ListItem
                secondaryAction={
                    <Box sx={{ display: 'flex', gap: 1 }}>
                        <Button size="small" variant="outlined" onClick={() => onEdit(vehicle)}>
                            Editeaza
                        </Button>
                        <Button size="small" variant="outlined" color="error" onClick={() => onDelete(vehicle.id)}>
                            Sterge
                        </Button>
                    </Box>
                }
            >
                <ListItemText
                    primary={vehicle.licensePlate}
                    secondary={`Model: ${vehicle.model} | Culoare: ${vehicle.color}`}
                />
            </ListItem>
        </Paper>
    );
};

export default VehicleItem;