package codesquad.webserver.db.user;

import codesquad.webserver.annotation.Component;
import codesquad.webserver.model.User;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class InMemoryUserDatabase implements UserDatabase {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryUserDatabase.class);
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public InMemoryUserDatabase() {
        users.put("1", new User("1", "1", "1"));
    }

    @Override
    public void save(User user) {
        User existingUser = users.putIfAbsent(user.getUserId(), user);
        if (existingUser != null) {
            throw new IllegalArgumentException("User with id " + user.getUserId() + " already exists");
        }
    }

    @Override
    public User findByUserId(String userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> findAllUsers() {
        return users.values().stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean existsByUserId(String userId) {
        return users.containsKey(userId);
    }

    @Override
    public void print() {
        for (String s : users.keySet()) {
            logger.debug("가입한 사용자 : {}", s);
        }
    }

    @Override
    public void clear() {
        users.clear();
    }
}
