package codesquad.webserver.db.article;

import codesquad.webserver.annotation.Autowired;
import codesquad.webserver.annotation.Component;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ArticleRepository implements ArticleDatabase {

    private static final Logger logger = LoggerFactory.getLogger(ArticleRepository.class);
    private final DataSource dataSource;

    @Autowired
    public ArticleRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        initializeDatabase();
    }

    private void initializeDatabase() {
        String createArticlesTable = "CREATE TABLE IF NOT EXISTS `articles` (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "title VARCHAR(255) NOT NULL," +
                "content TEXT NOT NULL"+
                ")";
        String createImagesTable = "CREATE TABLE IF NOT EXISTS `images` (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "path VARCHAR(255) NOT NULL," +
                "filename VARCHAR(255) NOT NULL,"+
                "article_id BIGINT NOT NULL,"+
                ")";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createArticlesTable);
            logger.debug("Articles table created");

            stmt.execute(createImagesTable);
            logger.debug("Images table created");
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Failed to create table", e);
        }
    }

    @Override
    public void save(Article article) {

    }

    @Override
    public Optional<Article> findByArticleId(long id) {
        return Optional.empty();
    }

    @Override
    public List<Article> findAllArticle() {
        return List.of();
    }

    @Override
    public void clear() {

    }
}