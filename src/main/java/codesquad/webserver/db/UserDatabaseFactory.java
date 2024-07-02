package codesquad.webserver.db;

public class UserDatabaseFactory {
    private static UserDatabase instance;

    private UserDatabaseFactory(){}

    public static synchronized UserDatabase getInstance() {
        if (instance == null) {
            instance = new InMemoryUserDatabase();
        }
        return instance;
    }
}
