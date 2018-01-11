package chatV2.common.data;

import java.io.Serializable;

public class UserInfo implements Serializable {
    public static final int STATE_ONLINE = 0;
    public static final int STATE_OFFLINE = 1;
    private int accountId;
    private String userName;
    private int state;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj != null && obj instanceof UserInfo) && ((UserInfo) obj).getAccountId() == accountId;
    }
}
