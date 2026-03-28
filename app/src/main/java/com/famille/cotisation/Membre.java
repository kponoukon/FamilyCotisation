package com.famille.cotisation;

public class Membre {
    public int id;
    public String nom;
    public String prenom;
    public String telephone;

    public Membre() {}
    public Membre(String nom, String prenom, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }

    public String getNomComplet() {
        return nom + (prenom != null && !prenom.isEmpty() ? " " + prenom : "");
    }
}
