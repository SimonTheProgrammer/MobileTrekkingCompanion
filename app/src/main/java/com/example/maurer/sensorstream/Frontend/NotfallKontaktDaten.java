package com.example.maurer.sensorstream.Frontend;

/**
 * Created by hbayer on 05.01.2018.
 */
public class NotfallKontaktDaten {

    public NotfallKontaktDaten(String vorname, String nachname, String telefonnummer, String emailadresse, boolean smsTrue, boolean emailTrue)
    {
        this.vorname = vorname;
        this.nachname = nachname;
        this.telefonnummer = telefonnummer;
        this.emailadresse = emailadresse;
        this.smsTrue = smsTrue;
        this.emailTrue = emailTrue;
    }

    String vorname;
    String nachname;
    String telefonnummer;
    String emailadresse;
    boolean smsTrue=true;
    boolean emailTrue=true;

}
