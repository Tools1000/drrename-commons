package drrename.commons;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
@Slf4j
public class DirectoryRenamer {

    public static interface MoveExistingFileStrategy {
        void moveExistingFile(Path oldPath, Path newPath) throws IOException;
    }

    public static class OverrideExistingFileStrategy implements MoveExistingFileStrategy {
        @Override
        public void moveExistingFile(Path oldPath, Path newPath) throws IOException {
            log.debug("Moving file {}", oldPath);
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static class KeepLargerFileStrategy implements MoveExistingFileStrategy {

        @Override
        public void moveExistingFile(Path oldPath, Path newPath) throws IOException {
            if (Files.size(oldPath) > Files.size(newPath)) {
                log.debug("Moving file {}", oldPath);
                Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                log.debug("Deleting file {}", oldPath);
                Files.deleteIfExists(oldPath);
            }
        }
    }

    public static class KeepNewerFileStrategy implements MoveExistingFileStrategy {

        @Override
        public void moveExistingFile(Path oldPath, Path newPath) throws IOException {
            if (Files.getLastModifiedTime(oldPath).compareTo(Files.getLastModifiedTime(newPath)) > 0) {
                log.debug("Moving file {}", oldPath);
                Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                log.debug("Deleting file {}", oldPath);
                Files.deleteIfExists(oldPath);
            }
        }
    }

    private final MoveExistingFileStrategy moveExistingFileStrategy;

    public Path rename(final Path directory, String newDirectoryName) throws IOException {
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Not a directory");
        }
        Path newPath = directory.resolveSibling(newDirectoryName);
        if (moveExistingFileStrategy == null || !Files.exists(newPath)) {
            return Files.move(directory, newPath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.debug("{} exists", newPath);
        try (Stream<Path> walk = Files.walk(directory)) {
            walk.forEach(p -> {
                if (Files.isRegularFile(p)) {
                    var targetFile = Paths.get(newPath.toString(), p.getFileName().toString());
                    log.debug("Moving {} to\n{}", p, targetFile);
                    if (Files.exists(targetFile)) {
                        log.debug("merging existing file {}", targetFile);
                        try {
                            moveExistingFileStrategy.moveExistingFile(p, targetFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
        log.debug("Deleting {}", directory);
        Files.deleteIfExists(directory);
        return directory.resolveSibling(newDirectoryName);
    }
}
