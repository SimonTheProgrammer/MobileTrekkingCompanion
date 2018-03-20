package com.example.maurer.sensorstream.Frontend;

/**
 * Created by hbayer on 05.01.2018.
 */
public class NotfallKontaktDaten {

    public NotfallKontaktDaten(){}

    String vorname;
    String nachname;
    String telefonnummer;
    String emailadresse;
    boolean smsTrue=true;
    boolean emailTrue=true;

    public String getEmailadresse() {
        return emailadresse;
    }

    public void setEmailadresse(String emailadresse) {
        this.emailadresse = emailadresse;
    }

    public boolean isEmailTrue() {
        return emailTrue;
    }

    public void setEmailTrue(boolean emailTrue) {
        this.emailTrue = emailTrue;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public boolean isSmsTrue() {
        return smsTrue;
    }

    public void setSmsTrue(boolean smsTrue) {
        this.smsTrue = smsTrue;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }
}
