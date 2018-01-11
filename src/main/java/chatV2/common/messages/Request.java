package chatV2.common.messages;

import java.io.Serializable;

public class Request implements Serializable {
    public static final int CODE_MY_ACCOUNT_INFO = 0;
    public static final int CODE_FRIENDS_LIST = 1;
    public static final int CODE_CHAT_MESSAGE = 2;
    public static final int CODE_LOGIN = 3;
    public static final int CODE_REGISTER = 4;
    public static final int CODE_FRIEND_STATE = 5;

    private int code;
    private Object extra;

    public Request(int code) {
        this.code = code;
    }

    public Request(int code, Object extra) {
        this.code = code;
        this.extra = extra;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
