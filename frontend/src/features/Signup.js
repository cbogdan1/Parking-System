// src/features/Signup.js
import React, { useState } from 'react';
import { Container, Paper, TextField, Button, Typography, Box, Alert } from '@mui/material';
import axiosInstance from '../helper/axios';

const Signup = ({ onGoToLogin }) => {
    const [formData, setFormData] = useState({ username: '', email: '', password: '' });
    const [loading, setLoading]   = useState(false);
    const [error, setError]       = useState('');
    const [success, setSuccess]   = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSignup = () => {
        setError('');
        setSuccess('');

        if (!formData.username || !formData.email || !formData.password) {
            setError('Toate campurile sunt obligatorii!');
            return;
        }

        setLoading(true);
        axiosInstance.post('/api/auth/signup', formData)
            .then(() => {
                setSuccess('Cont creat cu succes! Te poti autentifica acum.');
                setTimeout(() => onGoToLogin(), 1500);
            })
            .catch(err => {
                const msg = err.response?.data || 'Eroare la inregistrare. Incearca din nou.';
                setError(msg);
            })
            .finally(() => setLoading(false));
    };

    return (
        <Container maxWidth="xs" sx={{ mt: 10 }}>
            <Paper elevation={3} sx={{ p: 4, borderRadius: '15px', textAlign: 'center' }}>
                <Typography variant="h5" gutterBottom sx={{ fontWeight: 'bold' }}>
                    Inregistrare Client
                </Typography>

                {error   && <Alert severity="error"   sx={{ mb: 2, textAlign: 'left' }}>{error}</Alert>}
                {success && <Alert severity="success" sx={{ mb: 2, textAlign: 'left' }}>{success}</Alert>}

                <Box sx={{ mt: 2 }}>
                    <TextField
                        fullWidth label="Username" name="username"
                        value={formData.username} onChange={handleChange}
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        fullWidth label="Email" name="email" type="email"
                        value={formData.email} onChange={handleChange}
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        fullWidth label="Parola" name="password" type="password"
                        value={formData.password} onChange={handleChange}
                        sx={{ mb: 3 }}
                    />

                    <Button
                        variant="contained"
                        fullWidth
                        size="large"
                        onClick={handleSignup}
                        disabled={loading}
                    >
                        {loading ? 'Se inregistreaza...' : 'Creeaza Cont'}
                    </Button>

                    <Button
                        fullWidth
                        sx={{ mt: 2, textTransform: 'none', color: 'text.secondary' }}
                        onClick={onGoToLogin}
                    >
                        Ai deja cont? Autentifica-te
                    </Button>
                </Box>
            </Paper>
        </Container>
    );
};

export default Signup;
