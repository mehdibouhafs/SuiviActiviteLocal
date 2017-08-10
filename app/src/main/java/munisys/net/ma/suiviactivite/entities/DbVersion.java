package munisys.net.ma.suiviactivite.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mehdibouhafs on 29/07/2017.
 */

public class DbVersion implements Serializable {

    private int id;
    private String dateUpdateUser;
    private String dateUpdateNature;
    private String dateUpdateClient;


    public DbVersion() {

    }

    public DbVersion(int id, String dateUpdateUser, String dateUpdateNature, String dateUpdateClient) {
        this.id = id;
        this.dateUpdateUser = dateUpdateUser;
        this.dateUpdateNature = dateUpdateNature;
        this.dateUpdateClient = dateUpdateClient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateUpdateUser() {
        return dateUpdateUser;
    }

    public void setDateUpdateUser(String dateUpdateUser) {
        this.dateUpdateUser = dateUpdateUser;
    }

    public String getDateUpdateNature() {
        return dateUpdateNature;
    }

    public void setDateUpdateNature(String dateUpdateNature) {
        this.dateUpdateNature = dateUpdateNature;
    }

    public String getDateUpdateClient() {
        return dateUpdateClient;
    }

    public void setDateUpdateClient(String dateUpdateClient) {
        this.dateUpdateClient = dateUpdateClient;
    }

    @Override
    public String toString() {
        return "DbVersion{" +
                "id=" + id +
                ", dateUpdateUser='" + dateUpdateUser + '\'' +
                ", dateUpdateNature='" + dateUpdateNature + '\'' +
                ", dateUpdateClient='" + dateUpdateClient + '\'' +
                '}';
    }
}
