package drrename.commons;

import org.junit.jupiter.api.Assertions;
import tools1000.io.BackupCreator;
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

class DirectoryRenamerTest {

    Path directory = Paths.get("src/test/resources/directory-renamer/folderA");

    String newName = "folderB";

    Path directoryBackup = Paths.get(directory + ".bak");

    DirectoryRenamer directoryRenamer;

    @BeforeEach
    void setUp() throws IOException {
        directoryRenamer  = new DirectoryRenamer(null);
        new BackupCreator().makeBackup(directory.toFile());
    }

    @AfterEach
    void tearDown() throws IOException {
        new BackupCreator().restoreBackup(directoryBackup.toFile());
//        Files.deleteIfExists(directoryBackup);
    }

    @Test
    void testRename() throws IOException {
        new DirectoryRenamer(new DirectoryRenamer.KeepLargerFileStrategy()).rename(directory, newName);
    }


}