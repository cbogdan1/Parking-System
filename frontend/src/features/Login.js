// src/features/Login.js
import React, { useState } from 'react';
import { Container, Paper, TextField, Button, Typography, Box, Alert } from '@mui/material';
import axiosInstance from '../helper/axios';

const Login = ({ onLoginSuccess, onGoToSignup }) => {
    const [credentials, setCredentials] = useState({ username: '', password: '' });
    const [error, setError] = useState(null);

    const handleLogin = () => {
        setError(null);

        // Validare locala inainte de submit
        if (!credentials.username || credentials.username.trim() === '') {
            setError('Utilizatorul este obligatoriu!');
            return;
        }
        if (!credentials.password || credentials.password.trim() === '') {
            setError('Parola este obligatorie!');
            return;
        }

        axiosInstance.post('/api/auth/login', credentials)
            .then(res => {
                // Generam token-ul Basic Auth si il salvam in localStorage
                // Interceptorul din axios.js il va atasa automat la toate request-urile
                const basicToken = btoa(credentials.username + ':' + credentials.password);
                localStorage.setItem('authToken', basicToken);
                localStorage.setItem('isAuthenticated', 'true');
                localStorage.setItem('user', JSON.stringify(res.data));
                onLoginSuccess();
            })
            .catch(err => {
                if (err.response && err.response.data && err.response.data.message) {
                    setError(err.response.data.message);
                } else if (err.response && err.response.data && typeof err.response.data === 'string') {
                    setError(err.response.data);
                } else {
                    setError('Login esuat! Verificati datele introduse.');
                }
            });
    };

    return (
        <Container maxWidth="xs" sx={{ mt: 10 }}>
            <Paper elevation={3} sx={{ p: 4, borderRadius: '15px', textAlign: 'center' }}>
                <Typography variant="h5" gutterBottom sx={{ fontWeight: 'bold' }}>
                    Parking Login
                </Typography>
                <Box sx={{ mt: 2 }}>
                    {error && <Alert severity="error" sx={{ mb: 2, textAlign: 'left' }}>{error}</Alert>}
                    <TextField
                        fullWidth label="Utilizator" name="username" sx={{ mb: 2 }}
                        onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
                    />
                    <TextField
                        fullWidth label="Parola" type="password" name="password" sx={{ mb: 3 }}
                        onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
                    />
                    <Button variant="contained" fullWidth size="large" onClick={handleLogin}>
                        Intra in Sistem
                    </Button>

                    {/* Buton catre pagina de Signup */}
                    <Button
                        fullWidth
                        sx={{ mt: 2, textTransform: 'none', color: 'text.secondary' }}
                        onClick={onGoToSignup}
                    >
                        Nu ai cont? Inregistreaza-te
                    </Button>
                </Box>
            </Paper>
        </Container>
    );
};

export default Login;