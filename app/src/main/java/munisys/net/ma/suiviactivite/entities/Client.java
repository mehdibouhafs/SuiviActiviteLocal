package munisys.net.ma.suiviactivite.entities;

import java.io.Serializable;

/**
 * Created by mehdibouhafs on 28/07/2017.
 */

public class Client implements Serializable{

    private int id;
    private String client;

    public Client() {
    }

    public Client(int id, String client) {
        this.id = id;
        this.client = client;
    }

    public Client(String client) {
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", client='" + client + '\'' +
                '}';
    }
}
