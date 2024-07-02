package codesquad.webserver;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FileReaderTest {

    private FileReader fileReader;

    @BeforeEach
    public void setUp() {
        fileReader = new FileReader();
    }

    @Test
    @DisplayName("루트 경로 요청 시 index.html 파일을 반환해야 한다")
    public void testReadRootPath() throws FileNotFoundException {
        // Given
        String requestPath = "/";

        // When
        File file = fileReader.read(requestPath);

        // Then
        assertTrue(file.exists(), "index.html 파일이 존재해야 합니다.");
        assertTrue(file.getPath().endsWith("index.html"), "index.html 파일이어야 합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 파일 요청 시 FileNotFoundException을 발생시켜야 한다")
    public void testReadNonExistentFile() {
        // Given
        String requestPath = "/nonexistentfile.html";

        // When & Then
        assertThrows(FileNotFoundException.class, () -> {
            fileReader.read(requestPath);
        }, "존재하지 않는 파일 요청 시 FileNotFoundException이 발생해야 합니다.");
    }

    @Test
    @DisplayName("디렉토리 요청 시 index.html 파일을 반환해야 한다")
    public void testReadDirectoryPath() throws FileNotFoundException {
        // Given
        String requestPath = "/registration";

        // When
        File file = fileReader.read(requestPath);

        // Then
        assertTrue(file.exists(), "index.html 파일이 존재해야 합니다.");
        assertTrue(file.getPath().endsWith("index.html"), "디렉토리 경로 요청 시 index.html 파일이어야 합니다.");
    }
}
