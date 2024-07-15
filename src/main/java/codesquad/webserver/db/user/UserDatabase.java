package codesquad.webserver.db.user;

import java.util.List;
import java.util.Optional;

public interface UserDatabase {
    void save(User user);

    Optional<User> findByUserId(String userId);

    List<User> findAllUsers();

    boolean existsByUserId(String userId);

    void clear();
}
