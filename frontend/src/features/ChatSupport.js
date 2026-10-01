import React, { useState, useEffect, useRef } from 'react';
import { 
    Container, Grid, Paper, Typography, List, ListItem, 
    ListItemText, TextField, Button, Box, Divider 
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import axiosInstance from '../helper/axios';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const ChatSupport = () => {
    const [clients, setClients] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null); // Cu cine vorbesc acum
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [stompClient, setStompClient] = useState(null);

    const currentUser = JSON.parse(localStorage.getItem('user'));
    const isAdmin = currentUser && currentUser.role === 'ROLE_ADMIN';
    const messagesEndRef = useRef(null);

    useEffect(() => {
        // Scroll la ultimul mesaj mereu
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    useEffect(() => {
        if (!currentUser) return;

        // Daca e admin, luam lista de clienti
        if (isAdmin) {
            axiosInstance.get('/api/chat/clients')
                .then(res => setClients(res.data))
                .catch(err => console.error("Eroare preluare clienti", err));
        } else {
            // Daca e client, ii gasim adminul cu care sa vorbeasca automat
            axiosInstance.get('/api/chat/admin')
                .then(res => {
                    setSelectedUser(res.data);
                    loadHistory(currentUser.id, res.data.id);
                })
                .catch(err => console.error("Eroare gasire admin", err));
        }

        // Configurare WebSocket pentru CHAT
        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('Conectat la STOMP pentru Chat');
                // Ma abonez la propriile mesaje care vin spre mine
                client.subscribe(`/topic/chat/${currentUser.id}`, (msg) => {
                    if (msg.body) {
                        const newMsg = JSON.parse(msg.body);
                        // Daca primesc un mesaj, il adaug in lista DACA este de la cel cu care vorbesc
                        setMessages(prev => {
                            // Daca nu am pe nimeni selectat inca (sunt admin si primesc mesaj random)
                            return [...prev, newMsg];
                        });
                    }
                });
            }
        });

        client.activate();
        setStompClient(client);

        return () => client.deactivate();
    }, []);

    const loadHistory = (myId, otherId) => {
        axiosInstance.get(`/api/chat/history/${myId}/${otherId}`)
            .then(res => setMessages(res.data))
            .catch(err => console.error("Eroare istoric", err));
    };

    const handleSelectClient = (clientUser) => {
        setSelectedUser(clientUser);
        loadHistory(currentUser.id, clientUser.id);
    };

    const handleSendMessage = () => {
        if (!newMessage.trim() || !selectedUser) return;

        const msgObj = {
            senderId: currentUser.id,
            receiverId: selectedUser.id,
            content: newMessage
        };

        axiosInstance.post('/api/chat/send', msgObj)
            .then(res => {
                setMessages(prev => [...prev, res.data]); // Il adaug pe ecran si pe al meu
                setNewMessage('');
            })
            .catch(err => console.error("Eroare trimitere mesaj", err));
    };

    if (!currentUser) {
        return <Typography variant="h6" align="center">Trebuie sa fii logat!</Typography>;
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4, height: '70vh' }}>
            <Paper elevation={3} sx={{ height: '100%', display: 'flex' }}>
                
                {/* Partea Stanga (Lista Clienti - doar pentru Admin) */}
                {isAdmin && (
                    <Box sx={{ width: '30%', borderRight: '1px solid #ccc', overflowY: 'auto' }}>
                        <Typography variant="h6" align="center" sx={{ p: 2, bgcolor: '#f5f5f5' }}>
                            Clienti
                        </Typography>
                        <Divider />
                        <List>
                            {clients.map(c => (
                                <ListItem 
                                    button 
                                    key={c.id} 
                                    onClick={() => handleSelectClient(c)}
                                    selected={selectedUser?.id === c.id}
                                >
                                    <ListItemText primary={c.username} secondary={c.email} />
                                </ListItem>
                            ))}
                        </List>
                    </Box>
                )}

                {/* Partea Dreapta (Fereastra de Chat) */}
                <Box sx={{ width: isAdmin ? '70%' : '100%', display: 'flex', flexDirection: 'column' }}>
                    
                    {/* Header Chat */}
                    <Box sx={{ p: 2, bgcolor: '#1976d2', color: 'white' }}>
                        <Typography variant="h6">
                            {selectedUser ? `Chat cu ${selectedUser.username}` : (isAdmin ? 'Selecteaza un client pentru a discuta' : 'Suport Administrator')}
                        </Typography>
                    </Box>

                    {/* Istoric Mesaje */}
                    <Box sx={{ flexGrow: 1, p: 2, overflowY: 'auto', bgcolor: '#fafafa' }}>
                        {selectedUser && messages
                            // Afisam doar mesajele care ma implica pe mine si pe selectedUser
                            .filter(m => (m.senderId === selectedUser.id || m.receiverId === selectedUser.id))
                            .map((msg, idx) => {
                                const isMe = msg.senderId === currentUser.id;
                                return (
                                    <Box key={idx} sx={{ 
                                        display: 'flex', 
                                        justifyContent: isMe ? 'flex-end' : 'flex-start',
                                        mb: 2 
                                    }}>
                                        <Paper elevation={1} sx={{ 
                                            p: 1.5, 
                                            maxWidth: '70%', 
                                            bgcolor: isMe ? '#dcf8c6' : '#fff',
                                            borderRadius: isMe ? '15px 15px 0 15px' : '15px 15px 15px 0'
                                        }}>
                                            <Typography variant="body1">{msg.content}</Typography>
                                            <Typography variant="caption" color="textSecondary" sx={{ display: 'block', textAlign: 'right', mt: 0.5 }}>
                                                {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                            </Typography>
                                        </Paper>
                                    </Box>
                                );
                            })}
                        <div ref={messagesEndRef} />
                    </Box>

                    {/* Input Trimite */}
                    <Box sx={{ p: 2, bgcolor: '#fff', borderTop: '1px solid #ccc', display: 'flex', alignItems: 'center' }}>
                        <TextField 
                            fullWidth 
                            variant="outlined" 
                            placeholder={selectedUser ? "Scrie un mesaj..." : "Asteapta conectarea..."}
                            value={newMessage}
                            onChange={(e) => setNewMessage(e.target.value)}
                            disabled={!selectedUser}
                            onKeyPress={(e) => { if (e.key === 'Enter') handleSendMessage(); }}
                        />
                        <Button 
                            variant="contained" 
                            color="primary" 
                            sx={{ ml: 2, height: '56px' }} 
                            onClick={handleSendMessage}
                            disabled={!selectedUser || !newMessage.trim()}
                        >
                            <SendIcon />
                        </Button>
                    </Box>
                </Box>
            </Paper>
        </Container>
    );
};

export default ChatSupport;
