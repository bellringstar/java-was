package codesquad.webserver.db.article;

import java.util.List;
import java.util.Optional;

public interface ArticleDatabase {

    void save(Article article);

    Optional<Article> findByArticleId(long id);

    List<Article> findAllArticle();

    void clear();
}
