package com.example.tacademy.miniproject.data;

public class ContentData implements java.io.Serializable {
    private static final long serialVersionUID = -4108179806751969785L;
    private String imageUrl;
    private long id;
    private String content;
    private long writerid;

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getWriterid() {
        return this.writerid;
    }

    public void setWriterid(long writerid) {
        this.writerid = writerid;
    }
}
