package chatV2.common.messages;

import java.io.Serializable;

public class Message implements Serializable {
    private int whoId;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWhoId() {
        return whoId;
    }

    public void setWhoId(int whoId) {
        this.whoId = whoId;
    }
}
