-- Clean up existing tables if they exist
DROP TABLE IF EXISTS TAAK;
DROP TABLE IF EXISTS TRELLO;
DROP TABLE IF EXISTS BERICHT;
DROP TABLE IF EXISTS SPRINT;
DROP TABLE IF EXISTS GEBRUIKER;

CREATE TABLE GEBRUIKER (
    gebruikersnaam TEXT PRIMARY KEY,
    weergavenaam TEXT UNIQUE NOT NULL
);

CREATE TABLE SPRINT (
    sprintNummer INTEGER PRIMARY KEY,
    beginDatum DATE NOT NULL,
    eindDatum DATE NOT NULL,
    CHECK (eindDatum >= beginDatum)
);

CREATE TABLE BERICHT (
    berichtID INTEGER PRIMARY KEY,
    inhoud TEXT NOT NULL,
    tijdstip DATETIME NOT NULL,
    afzender TEXT NOT NULL,
    sprintNummer INTEGER NOT NULL,
    FOREIGN KEY (afzender) REFERENCES GEBRUIKER(gebruikersnaam),
    FOREIGN KEY (sprintNummer) REFERENCES SPRINT(sprintNummer)
);

CREATE TABLE TRELLO (
    trelloID INTEGER PRIMARY KEY,
    berichtID INTEGER,
    trelloURL TEXT UNIQUE NOT NULL,
    FOREIGN KEY (berichtID) REFERENCES BERICHT(berichtID)
);

CREATE TABLE TAAK (
    berichtID INTEGER NOT NULL,
    trelloID INTEGER NOT NULL,
    beschrijving TEXT NOT NULL,
    PRIMARY KEY (berichtID, trelloID),
    FOREIGN KEY (berichtID) REFERENCES BERICHT(berichtID),
    FOREIGN KEY (trelloID) REFERENCES TRELLO(trelloID)
);

CREATE INDEX idx_bericht_afzender ON BERICHT(afzender);
CREATE INDEX idx_bericht_sprint ON BERICHT(sprintNummer);
CREATE INDEX idx_taak_bericht ON TAAK(berichtID);
CREATE INDEX idx_taak_trello ON TAAK(trelloID);
CREATE INDEX idx_trello_bericht ON TRELLO(berichtID);