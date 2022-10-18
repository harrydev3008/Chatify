package com.hisu.zola.entity;

public class Message {
    //  Todo: Message object structure will change later, this is just for quick test
    private String from;
    private String content;
    private String type;
//    private LocalDateTime sentDate;

    public Message() {
    }

    public Message(String from, String content, String type) {
        this.from = from;
        this.content = content;
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}