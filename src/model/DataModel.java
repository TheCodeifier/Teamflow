package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

class Gebruiker {
    private String gebruikersnaam;
    private String weergavenaam;

    public Gebruiker(String gebruikersnaam, String weergavenaam) {
        this.gebruikersnaam = gebruikersnaam;
        this.weergavenaam = weergavenaam;
    }

    public String getGebruikersnaam() {
        return gebruikersnaam;
    }

    public void setGebruikersnaam(String gebruikersnaam) {
        this.gebruikersnaam = gebruikersnaam;
    }

    public String getWeergavenaam() {
        return weergavenaam;
    }

    public void setWeergavenaam(String weergavenaam) {
        this.weergavenaam = weergavenaam;
    }
}

class Sprint {
    private int sprintNummer;
    private LocalDate beginDatum;
    private LocalDate eindDatum;

    public Sprint(int sprintNummer, LocalDate beginDatum, LocalDate eindDatum) {
        this.sprintNummer = sprintNummer;
        this.beginDatum = beginDatum;
        this.eindDatum = eindDatum;
    }

    public int getSprintNummer() {
        return sprintNummer;
    }

    public void setSprintNummer(int sprintNummer) {
        this.sprintNummer = sprintNummer;
    }

    public LocalDate getBeginDatum() {
        return beginDatum;
    }

    public void setBeginDatum(LocalDate beginDatum) {
        this.beginDatum = beginDatum;
    }

    public LocalDate getEindDatum() {
        return eindDatum;
    }

    public void setEindDatum(LocalDate eindDatum) {
        this.eindDatum = eindDatum;
    }
}

class Bericht {
    private int berichtID;
    private String inhoud;
    private LocalDateTime tijdstip;
    private String afzender;
    private int sprintNummer;

    public Bericht(int berichtID, String inhoud, LocalDateTime tijdstip, String afzender, int sprintNummer) {
        this.berichtID = berichtID;
        this.inhoud = inhoud;
        this.tijdstip = tijdstip;
        this.afzender = afzender;
        this.sprintNummer = sprintNummer;
    }

    public int getBerichtID() {
        return berichtID;
    }

    public void setBerichtID(int berichtID) {
        this.berichtID = berichtID;
    }

    public String getInhoud() {
        return inhoud;
    }

    public void setInhoud(String inhoud) {
        this.inhoud = inhoud;
    }

    public LocalDateTime getTijdstip() {
        return tijdstip;
    }

    public void setTijdstip(LocalDateTime tijdstip) {
        this.tijdstip = tijdstip;
    }

    public String getAfzender() {
        return afzender;
    }

    public void setAfzender(String afzender) {
        this.afzender = afzender;
    }

    public int getSprintNummer() {
        return sprintNummer;
    }

    public void setSprintNummer(int sprintNummer) {
        this.sprintNummer = sprintNummer;
    }
}

class Trello {
    private int trelloID;
    private String trelloURL;

    public Trello(int trelloID, String trelloURL) {
        this.trelloID = trelloID;
        this.trelloURL = trelloURL;
    }

    public int getTrelloID() {
        return trelloID;
    }

    public void setTrelloID(int trelloID) {
        this.trelloID = trelloID;
    }

    public String getTrelloURL() {
        return trelloURL;
    }

    public void setTrelloURL(String trelloURL) {
        this.trelloURL = trelloURL;
    }
}

class Taak {
    private int berichtID;
    private int trelloID;
    private String beschrijving;

    public Taak(int berichtID, int trelloID, String beschrijving) {
        this.berichtID = berichtID;
        this.trelloID = trelloID;
        this.beschrijving = beschrijving;
    }

    public int getBerichtID() {
        return berichtID;
    }

    public void setBerichtID(int berichtID) {
        this.berichtID = berichtID;
    }

    public int getTrelloID() {
        return trelloID;
    }

    public void setTrelloID(int trelloID) {
        this.trelloID = trelloID;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }
}