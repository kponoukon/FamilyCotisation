package com.famille.cotisation;

public class Cotisation {
    public int id;
    public int membreId;
    public double montant;
    public String date;
    public int mois;
    public int annee;
    public String note;
    public String membreNom; // joined from membres table

    public Cotisation() {}
}
