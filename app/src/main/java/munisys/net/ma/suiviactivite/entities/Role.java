package munisys.net.ma.suiviactivite.entities;

import java.io.Serializable;

/**
 * Created by mehdibouhafs on 27/07/2017.
 */

public class Role implements Serializable {

    private String role;
    private String description;


    public Role() {

    }
    public Role(String role, String description) {
        super();
        this.role = role;
        this.description = description;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Role{" +
                "role='" + role + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
