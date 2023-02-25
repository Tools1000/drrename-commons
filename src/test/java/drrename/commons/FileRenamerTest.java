package drrename.commons;

import com.github.tools1000.io.BackupCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileRenamerTest {

    Path file = Paths.get("src/test/resources/file-renamer/simple-file.txt");

    Path file02 = Paths.get("src/test/resources/file-renamer/simple-file02.txt");

    Path conflictFile = Paths.get("src/test/resources/file-renamer/simple-file-conflict.txt");

    Path conflictCopyFile01 = Paths.get("src/test/resources/file-renamer/simple-file-conflict_copy1.txt");

    Path conflictCopyFile02 = Paths.get("src/test/resources/file-renamer/simple-file-conflict_copy2.txt");

    Path backupFile = Paths.get("src/test/resources/file-renamer/simple-file.txt.bak");

    FileRenamer fileRenamer;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        fileRenamer = new FileRenamer();
        new BackupCreator().makeBackup(file.toFile());
    }

    @AfterEach
    void tearDown() throws IOException {
        new BackupCreator().restoreBackup(backupFile.toFile());
        Files.deleteIfExists(file02);
        Files.deleteIfExists(backupFile);
        Files.deleteIfExists(conflictCopyFile01);
        Files.deleteIfExists(conflictCopyFile02);
    }

    @Test
    void testRenameSimple() throws IOException {
        fileRenamer.rename(file, file02.getFileName().toString());
        assertTrue(Files.exists(file02));
    }

    @Test
    void testRenameConflict01() throws IOException {
        FileAlreadyExistsException thrown = assertThrows(
                FileAlreadyExistsException.class,
                () -> fileRenamer.rename(file, conflictFile.getFileName().toString()),
                "Expected FileAlreadyExistsException"
        );
        assertNotNull(thrown);
    }

    @Test
    void testRenameConflictOverride() throws IOException {
        fileRenamer.rename(file, conflictFile.getFileName().toString(), FileRenamer.STRATEGY.REPLACE);
    }

    @Test
    void testRenameConflictRename() throws IOException {
        fileRenamer.rename(file, conflictFile.getFileName().toString(), FileRenamer.STRATEGY.RENAME);
    }

    @Test
    void testRenameConflictRename01() throws IOException {
        // conflict copy already there, expect counter to increment
        Files.copy(conflictFile, conflictCopyFile01);
        fileRenamer.rename(file, conflictFile.getFileName().toString(), FileRenamer.STRATEGY.RENAME);
    }

    @Test
    void testRenameDir() throws IOException {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () ->  fileRenamer.rename(file.getParent(), "bla"),
                "Expected IllegalArgumentException"
        );
        assertNotNull(thrown);
    }
}