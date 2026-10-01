import React, { useEffect, useState } from 'react';
import { Snackbar, Alert } from '@mui/material';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const NotificationListener = () => {
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState('');

    useEffect(() => {
        const userStr = localStorage.getItem('user');
        if (!userStr) return; // Daca nu este logat, nu ne conectam

        const user = JSON.parse(userStr);
        const userId = user.id;

        // Configurarea clientului STOMP peste SockJS
        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('Conectat la WebSocket pentru Notificari!');

                // Ne abonam la topicul specific utilizatorului
                client.subscribe(`/topic/user/${userId}`, (msg) => {
                    if (msg.body) {
                        setMessage(msg.body);
                        setOpen(true); // Afiseaza alerta
                    }
                });
            },
            onStompError: (frame) => {
                console.error('Broker STOMP a raportat o eroare: ' + frame.headers['message']);
                console.error('Detalii: ' + frame.body);
            }
        });

        client.activate();

        // Cleanup la demontarea componentei
        return () => {
            client.deactivate();
        };
    }, []);

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') return;
        setOpen(false);
    };

    return (
        <Snackbar 
            open={open} 
            autoHideDuration={10000} // Dispare dupa 10 secunde
            onClose={handleClose} 
            anchorOrigin={{ vertical: 'top', horizontal: 'right' }} // Apare in coltul dreapta sus
        >
            <Alert onClose={handleClose} severity="warning" sx={{ width: '100%', fontSize: '1.1rem', boxShadow: 3 }}>
                <strong>Atentie:</strong> {message}
            </Alert>
        </Snackbar>
    );
};

export default NotificationListener;
