package codesquad.webserver.db.article;

import java.util.ArrayList;
import java.util.List;

public class Article {
    private Long id;
    private String title;
    private String content;
    private List<Image> images = new ArrayList<>();

    public Article(String title, String content) {
        this(null, title, content);
    }

    public Article(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Article(Long id, String title, String content, List<Image> images) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.images = images;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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