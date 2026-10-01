// src/App.js
import React, { useState } from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import ViewSpots from './features/ViewSpots';
import ViewReservations from './features/ViewReservations';
import ViewVehicles from './features/ViewVehicles';
import ViewStatistics from './features/ViewStatistics';
import Login from './features/Login';
import Signup from './features/Signup';
import ChatSupport from './features/ChatSupport';
import { AppBar, Toolbar, Typography, Button, Container, Box } from '@mui/material';
import NotificationListener from './components/NotificationListener';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(
        localStorage.getItem('isAuthenticated') === 'true'
    );
    const [authPage, setAuthPage] = useState('login');

    const user = JSON.parse(localStorage.getItem('user'));
    const isAdmin = user && user.role === 'ROLE_ADMIN';

    const handleLogout = () => {
        localStorage.removeItem('authToken');        // Stergem token-ul Basic Auth
        localStorage.removeItem('isAuthenticated');
        localStorage.removeItem('user');
        setIsLoggedIn(false);
        setAuthPage('login');
    };

    if (!isLoggedIn) {
        if (authPage === 'signup') {
            return <Signup onGoToLogin={() => setAuthPage('login')} />;
        }
        return (
            <Login
                onLoginSuccess={() => setIsLoggedIn(true)}
                onGoToSignup={() => setAuthPage('signup')}
            />
        );
    }

    return (
        <div>
            <NotificationListener />
            <AppBar position="static" color="primary">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        Parking System
                    </Typography>
                    <Box>
                        {isAdmin && (
                            <>
                                <Button color="inherit" component={Link} to="/vehicles">Masini</Button>
                                <Button color="inherit" component={Link} to="/spots">Locuri Parcare</Button>
                                <Button color="inherit" component={Link} to="/stats">Statistici</Button>
                            </>
                        )}
                        <Button color="inherit" component={Link} to="/reservations">Rezervari</Button>
                        <Button color="inherit" component={Link} to="/chat">Suport Chat</Button>
                        <Button color="inherit" onClick={handleLogout} sx={{ ml: 2 }}>
                            Logout
                        </Button>
                    </Box>
                </Toolbar>
            </AppBar>

            <Container sx={{ mt: 4 }}>
                <Routes>
                    {isAdmin && (
                        <>
                            <Route path="/vehicles" element={<ViewVehicles />} />
                            <Route path="/spots" element={<ViewSpots />} />
                            <Route path="/stats" element={<ViewStatistics />} />
                        </>
                    )}
                    <Route path="/reservations" element={<ViewReservations />} />
                    <Route path="/chat" element={<ChatSupport />} />
                    <Route path="/" element={
                        <Typography variant="h5" align="center" sx={{ mt: 10 }}>
                            Bine ai venit la Panoul de Administrare!
                        </Typography>
                    } />
                </Routes>
            </Container>
        </div>
    );
}

export default App;