package codesquad.webserver.db.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CsvUserRepositoryTest {

    private UserDatabase userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new CsvUserRepository();
    }

    @AfterEach
    void tearDown() {
        userRepository.clear();
    }

    @Test
    void saveAndFindByUserId() {
        User user = new User("testUser", "password", "Test User");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUserId("testUser");
        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUserId());
        assertEquals("password", foundUser.get().getPassword());
        assertEquals("Test User", foundUser.get().getName());
    }

    @Test
    void findByUserIdNotFound() {
        Optional<User> notFoundUser = userRepository.findByUserId("nonexistentUser");
        assertFalse(notFoundUser.isPresent());
    }

    @Test
    void findAllUsers() {
        User user1 = new User("user1", "password1", "User One");
        User user2 = new User("user2", "password2", "User Two");
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAllUsers();
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUserId().equals("user1")));
        assertTrue(users.stream().anyMatch(u -> u.getUserId().equals("user2")));
    }

    @Test
    void existsByUserId() {
        User user = new User("existingUser", "password", "Existing User");
        userRepository.save(user);

        assertTrue(userRepository.existsByUserId("existingUser"));
        assertFalse(userRepository.existsByUserId("nonexistentUser"));
    }

    @Test
    void clear() {
        User user = new User("userToClear", "password", "User To Clear");
        userRepository.save(user);
        assertFalse(userRepository.findAllUsers().isEmpty());

        userRepository.clear();
        assertTrue(userRepository.findAllUsers().isEmpty());
    }
}