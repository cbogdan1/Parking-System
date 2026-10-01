# Parking System

**Autor:** Bogdan Campean | **Data:** Mai 2026
**Tehnologii:** Spring Boot 4.0.3, React (Material UI), MySQL

## Despre Proiect

Sistemul Parking System este o aplicatie web full-stack creata pentru gestionarea vehiculelor, a locurilor de parcare si a rezervarilor. Aplicatia ofera o interfata responsiva construita in React si un backend robust in Spring Boot, integrand functionalitati avansate precum comunicarea in timp real, calculul automat al costurilor si generarea documentelor structurate.

## Functionalitati Principale

* **Sistem Complet de Rezervari:** Utilizatorii pot crea rezervari alegand vehiculul, locul de parcare si intervalul orar. Sistemul valideaza disponibilitatea locului, calculeaza costul total si marcheaza locul ca ocupat.


* **Notificari Automate:** La crearea unei rezervari, sistemul trimite un email de confirmare proprietarului.


* **Facturare:** Utilizatorii autentificati pot descarca direct din aplicatie facturile aferente rezervarilor in format XML.


* **Securitate si Roluri:** Sistemul foloseste Spring Security cu autentificare Basic Auth si criptare BCrypt. Accesul este restrictionat in functie de rol (ex: sectiunile de administrare si statistici sunt vizibile doar pentru rolul ADMIN).


* **Validari Customizate:** Interfata si serverul valideaza datele introduse, inclusiv printr-un validator special pentru formatul numerelor de inmatriculare romanesti.



## Arhitectura si Design Patterns

Proiectul respecta principiile SOLID si integreaza mai multe design pattern-uri pentru un cod curat si mentenabil:

* **Strategy Pattern:** Implementat pentru exportul datelor (TXT si XML), permitand adaugarea usoara de noi formate fara a modifica logica existenta.


* **Builder Pattern:** Folosit prin Lombok pentru a construi obiecte complexe (DTO-uri si entitati) intr-un mod lizibil.


* **Repository Pattern:** Abstractizeaza accesul la baza de date prin Spring Data JPA.



## Structura Bazei de Date

Aplicatia utilizeaza o baza de date relationala MySQL cu cinci entitati principale:

* **users:** Stocheaza datele de autentificare si rolurile.


* **vehicles:** Contine vehiculele inregistrate si apartenenta lor.


* **parking_spots:** Mentine informatii despre sectiune, numar, disponibilitate si pret pe ora.


* **reservations:** Conecteaza vehiculele de locurile de parcare pe o perioada specificata, inregistrand costul total.


* **chat_messages:** Salveaza istoricul conversatiilor live dintre utilizatori.



## Imbunatatiri Viitoare

* Integrarea unui procesator de plati online (ex: Stripe, PayPal) pentru achitarea directa din interfata.


* Adaugarea unui sistem de alerte prin SMS (ex: Twilio) pentru expirarea rezervarilor.


* Implementarea recunoasterii automate a numerelor de inmatriculare (ANPR) folosind camere video pentru ridicarea automata a barierei.


* Dezvoltarea unor rapoarte vizuale si grafice interactive pentru a analiza tendintele de ocupare si profitabilitatea.