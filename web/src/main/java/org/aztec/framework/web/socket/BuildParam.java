package org.aztec.framework.web.socket;

public class BuildParam {
    private DestinationType type;
    private String source;
    private String[] consumers;
    private String topics;
    private String title;
    private String contents;
    public DestinationType getType() {
        return type;
    }
    public void setType(DestinationType type) {
        this.type = type;
    }
    public String[] getConsumers() {
        return consumers;
    }
    public void setConsumers(String[] consumers) {
        this.consumers = consumers;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getTopics() {
        return topics;
    }
    public void setTopics(String topics) {
        this.topics = topics;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }
}
