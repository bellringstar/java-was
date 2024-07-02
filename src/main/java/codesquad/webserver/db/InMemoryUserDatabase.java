package codesquad.webserver.db;

import codesquad.webserver.model.User;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserDatabase implements UserDatabase{

    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        User existingUser = users.putIfAbsent(user.getUserId(), user);
        if (existingUser != null) {
            throw new IllegalArgumentException("User with id " + user.getUserId() + " already exists");
        }
    }

    @Override
    public User findByUserId(String userId) {
        return Optional.ofNullable(users.get(userId)).orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
    }

    @Override
    public boolean existsByUserId(String userId) {
        return users.containsKey(userId);
    }
}
