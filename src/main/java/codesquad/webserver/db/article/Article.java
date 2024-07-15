package codesquad.webserver.db.article;

import java.util.ArrayList;
import java.util.List;

public class Article {
    private Long id;
    private String content;
    private List<Image> images = new ArrayList<>();

    public Article(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Article(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void addImage(Image image) {
        images.add(image);
    }
}