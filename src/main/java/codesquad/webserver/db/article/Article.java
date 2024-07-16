package codesquad.webserver.db.article;

import java.util.ArrayList;
import java.util.List;

public class Article {
    private Long id;
    private String title;
    private String content;
    private List<Image> images = new ArrayList<>();

    public Article(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Article(String title, String content) {
        this.title = title;
        this.content = content;
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