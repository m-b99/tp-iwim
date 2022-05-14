package com.example.tp_iwim.Modal;

public class User {

    private String uid;
    private String nom;
    private String prenom;
    private String cine;
    private String email;
    private String password;
    private String statut;
    private String telephone;
    private String annee;

    public User() {}

    public String getUid(){
        return uid;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getCine() {
        return cine;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getStatut() {
        return statut;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAnnee() {
        return annee;
    }

    public void setUid(String uid) { this.uid = uid; }

    public void setNom(String nom) { this.nom = nom; }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setCine(String cine) {
        this.cine = cine;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

}
