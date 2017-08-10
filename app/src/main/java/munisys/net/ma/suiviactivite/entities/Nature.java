package munisys.net.ma.suiviactivite.entities;

import java.io.Serializable;

/**
 * Created by mehdibouhafs on 28/07/2017.
 */

public class Nature implements Serializable {

    private int id;
    private String nature;

    public Nature() {
    }

    public Nature(int id, String nature) {
        this.id = id;
        this.nature = nature;
    }

    public Nature(String nature) {
        this.nature = nature;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }
}
