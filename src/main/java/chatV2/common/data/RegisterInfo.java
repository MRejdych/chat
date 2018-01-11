package chatV2.common.data;

import java.io.Serializable;

public class RegisterInfo implements Serializable {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
