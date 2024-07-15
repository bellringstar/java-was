package codesquad.webserver.db;

import codesquad.webserver.annotation.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class H2DataSource implements DataSource {
    private static final String JDBC_URL = "jdbc:h2:~/test";
    private static final String JDBC_USERNAME = "sa";
    private static final String JDBC_PASSWORD = "";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
    }
}
