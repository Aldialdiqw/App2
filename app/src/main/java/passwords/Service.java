package passwords;

import java.io.Serializable;

public class Service implements Serializable {

    private int p_id;
    private String service_name;
    private String username;
    private String password;


    public Service(int p_id, String service_name, String username, String password) {
        this.p_id = p_id;
        this.service_name = service_name;
        this.username = username;
        this.password = password;
    }

    // Getter and Setter for p_id
    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    // Getter and Setter for servicename
    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
