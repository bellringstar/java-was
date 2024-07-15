package codesquad.webserver.db.article;

public class Image {
    private Long id;
    private String path;
    private Long articleId;

    public Image(Long id, String path, Long articleId) {
        this.id = id;
        this.path = path;
        this.articleId = articleId;
    }

    public Long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public Long getArticleId() {
        return articleId;
    }
}