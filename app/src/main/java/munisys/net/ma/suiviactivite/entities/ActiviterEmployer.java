package munisys.net.ma.suiviactivite.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mehdibouhafs on 24/07/2017.
 */

public class ActiviterEmployer implements Serializable {

    private int id;
    private String date;
    private String dateSortie;
    private String heureDebut;
    private String heureFin;
    private String client;
    private String nature;
    private String descProjet;
    private String lieu;
    private String ville;
    private String duree;
    private int tag;
    private String employer;

    public ActiviterEmployer() {
    }

    public ActiviterEmployer(int id,String employer,String date, String dateSortie, String heureDebut, String heureFin,String duree, String client,
                             String nature, String descProjet, String lieu, String ville,int tag) {
        this.id = id;
        this.employer = employer;
        this.date = date;
        this.dateSortie = dateSortie;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.client = client;
        this.nature = nature;
        this.descProjet = descProjet;
        this.lieu = lieu;
        this.ville = ville;
        this.duree = duree;
        this.tag = tag;
    }

    public ActiviterEmployer(String employer,String date, String dateSortie, String heureDebut, String duree, String heureFin, String client,
                             String nature, String descProjet, String lieu, String ville) {
        this.employer = employer;
        this.date = date;
        this.dateSortie = dateSortie;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.client = client;
        this.nature = nature;
        this.descProjet = descProjet;
        this.lieu = lieu;
        this.ville = ville;
        this.duree = duree;
        this.tag = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(String dateSortie) {
        this.dateSortie = dateSortie;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getDescProjet() {
        return descProjet;
    }

    public void setDescProjet(String descProjet) {
        this.descProjet = descProjet;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    @Override
    public String toString() {
        return "ActiviterEmployer{" +
                "id=" + id +
                "employer=" + employer +
                ", date='" + date + '\'' +
                ", dateSortie='" + dateSortie + '\'' +
                ", heureDebut='" + heureDebut + '\'' +
                ", heureFin='" + heureFin + '\'' +
                ", client='" + client + '\'' +
                ", nature='" + nature + '\'' +
                ", descProjet='" + descProjet + '\'' +
                ", lieu='" + lieu + '\'' +
                ", ville='" + ville + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
