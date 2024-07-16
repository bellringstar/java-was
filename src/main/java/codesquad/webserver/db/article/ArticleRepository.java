package codesquad.webserver.db.article;

import codesquad.webserver.annotation.Autowired;
import codesquad.webserver.annotation.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ArticleRepository implements ArticleDatabase {

    private static final Logger logger = LoggerFactory.getLogger(ArticleRepository.class);
    private final DataSource dataSource;

    @Autowired
    public ArticleRepository(DataSource dataSource) throws SQLException{
        this.dataSource = dataSource;
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException{
        String createArticlesTable = "CREATE TABLE IF NOT EXISTS `articles` (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "title VARCHAR(255) NOT NULL," +
                "content TEXT NOT NULL" +
                ")";
        String createImagesTable = "CREATE TABLE IF NOT EXISTS `images` (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "path VARCHAR(255) NOT NULL," +
                "filename VARCHAR(255) NOT NULL," +
                "article_id BIGINT NOT NULL" +
                ")";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createArticlesTable);
            logger.debug("Articles table created");

            stmt.execute(createImagesTable);
            logger.debug("Images table created");
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public Article save(Article article) throws SQLException{
        String insertArticleSql = "INSERT INTO `articles` (title, content) VALUES (?, ?)";
        String insertImagesSql = "INSERT INTO `images` (path, filename, article_id) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            Long articleId;
            try (PreparedStatement pstmt = conn.prepareStatement(insertArticleSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, article.getTitle());
                pstmt.setString(2, article.getContent());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        articleId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Failed to create article");
                    }
                }
            }

            List<Image> savedImages = new ArrayList<>();
            if (!article.getImages().isEmpty()) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertImagesSql, Statement.RETURN_GENERATED_KEYS)) {
                    for (Image image : article.getImages()) {
                        pstmt.setString(1, image.getPath());
                        pstmt.setString(2, image.getFilename());
                        pstmt.setLong(3, articleId);
                        pstmt.executeUpdate();

                        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                savedImages.add(new Image(generatedKeys.getLong(1), image.getPath(), image.getFilename(), articleId));
                            }
                        }
                    }
                }
            }
            conn.commit();
            return new Article(articleId, article.getTitle(), article.getContent(), savedImages);
        } catch (SQLException e) {
            logger.error("Error saving article: ", e);
            throw e;
        }
    }

    @Override
    public Optional<Article> findByArticleId(long id) throws SQLException{
        String articleSql = "SELECT * FROM `articles` WHERE `article_id` = ?";
        String imageSql = "SELECT * FROM `images` WHERE `article_id` = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement articleStmt = conn.prepareStatement(articleSql);
             PreparedStatement imageStmt = conn.prepareStatement(imageSql)) {

            articleStmt.setLong(1, id);
            try (ResultSet articleRs = articleStmt.executeQuery()) {
                if (articleRs.next()) {
                    Long articleId = articleRs.getLong(1);
                    String title = articleRs.getString(2);
                    String content = articleRs.getString(3);

                    List<Image> images = new ArrayList<>();
                    imageStmt.setLong(1, articleId);
                    try (ResultSet imageRs = imageStmt.executeQuery()) {
                        while (imageRs.next()) {
                            images.add(new Image(
                                    imageRs.getLong("id"),
                                    imageRs.getString("path"),
                                    imageRs.getString("filename"),
                                    articleId
                            ));
                        }
                    }
                    return Optional.of(new Article(articleId, title, content, images));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to find article with id {}", id, e);
            throw e;
        }
        return Optional.empty();
    }

    @Override
    public List<Article> findAllArticle() throws SQLException{
        //TODO: 페이지네이션 필요
        String sql = "SELECT a.id as article_id,a.title, a.content, i.id as image_id, i.path, i.filename " +
                "FROM articles a LEFT JOIN images i ON a.id = i.article_id " +
                "ORDER BY a.id, i.id";

        Map<Long, Article> articleMap = new LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Long articleId = rs.getLong("article_id");
                Article article = articleMap.get(articleId);

                if (article == null) {
                    article = new Article(articleId, rs.getString("title"), rs.getString("content"), new ArrayList<>());
                    articleMap.put(articleId, article);
                }

                Long imageId = rs.getLong("image_id");
                if (!rs.wasNull()) {
                    Image image = new Image(imageId, rs.getString("path"), rs.getString("filename"), articleId);
                    article.getImages().add(image);
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding all articles", e);
            throw e;
        }

        return new ArrayList<>(articleMap.values());
    }

    @Override
    public void clear() throws SQLException{
        String clearImagesSql = "DELETE FROM images";
        String clearArticlesSql = "DELETE FROM articles";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                // TODO: 로컬파일 삭제 필요
                int imagesDeleted = stmt.executeUpdate(clearImagesSql);
                logger.info("Cleared {} rows from images table", imagesDeleted);

                int articlesDeleted = stmt.executeUpdate(clearArticlesSql);
                logger.info("Cleared {} rows from articles table", articlesDeleted);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Error clearing database", e);
                throw e;
            }
        } catch (SQLException e) {
            logger.error("Error getting database connection", e);
            throw e;
        }
    }
}