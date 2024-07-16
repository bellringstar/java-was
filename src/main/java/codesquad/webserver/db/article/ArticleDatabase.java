package codesquad.webserver.db.article;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ArticleDatabase {

    Article save(Article article) throws SQLException;

    Optional<Article> findByArticleId(long id) throws SQLException;

    List<Article> findAllArticle() throws SQLException;

    void clear() throws SQLException;
}
