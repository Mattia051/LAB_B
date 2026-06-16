-- Script SQL per la creazione del Database TheKnife (PostgreSQL)

CREATE TABLE IF NOT EXISTS utenti (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    nome VARCHAR(100),
    cognome VARCHAR(100),
    data_nascita VARCHAR(20),
    domicilio VARCHAR(255),
    ruolo VARCHAR(20) CHECK (ruolo IN ('cliente', 'ristoratore'))
);

CREATE TABLE IF NOT EXISTS ristoranti (
    nome VARCHAR(100) PRIMARY KEY,
    indirizzo VARCHAR(255),
    location VARCHAR(100) NOT NULL,
    fascia_prezzo VARCHAR(10),
    cucina VARCHAR(100),
    longitude DOUBLE PRECISION,
    latitude DOUBLE PRECISION,
    telefono VARCHAR(20),
    url TEXT,
    website_url TEXT,
    award TEXT,
    green_star VARCHAR(10),
    servizi TEXT,
    descrizione TEXT,
    proprietario VARCHAR(50) REFERENCES utenti(username)
);

CREATE TABLE IF NOT EXISTS recensioni (
    id SERIAL PRIMARY KEY,
    username_utente VARCHAR(50) REFERENCES utenti(username),
    nome_ristorante VARCHAR(100) REFERENCES ristoranti(nome),
    stelle INTEGER CHECK (stelle >= 1 AND stelle <= 5),
    testo TEXT,
    risposta TEXT
);

CREATE TABLE IF NOT EXISTS preferiti (
    username_utente VARCHAR(50) REFERENCES utenti(username),
    nome_ristorante VARCHAR(100) REFERENCES ristoranti(nome),
    PRIMARY KEY (username_utente, nome_ristorante)
);
