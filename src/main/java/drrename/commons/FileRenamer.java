package drrename.commons;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
public class FileRenamer {

    private final static Pattern pattern = Pattern.compile(".*_copy(\\d*)$");

    private int fileNameCounter = 0;

    enum STRATEGY {
        REPLACE, RENAME
    }

    public Path rename(final Path file, String newFileName) throws IOException {
        return rename(file, newFileName, null);
    }

    public Path rename(final Path file, String newFileName, STRATEGY strategy) throws IOException {
        if(!Files.isRegularFile(file)){
            throw new IllegalArgumentException("Not a file");
        }
        if(strategy == null){
            return Files.move(file, file.resolveSibling(newFileName));
        }
        switch (strategy){
            case REPLACE -> {
                return Files.move(file, file.resolveSibling(newFileName), REPLACE_EXISTING);
            }
            case RENAME -> {
                try {
                    return Files.move(file, file.resolveSibling(newFileName));
                } catch (FileAlreadyExistsException e){
                    log.debug(e.getLocalizedMessage());
                    return rename(file, getFileAlreadyExistsFileName(newFileName), STRATEGY.RENAME);
                }
            }
            default -> throw new RuntimeException();
        }
    }

    String getFileAlreadyExistsFileName(String nameNew) {
        final String extension = FilenameUtils.getExtension(nameNew);
        String baseName = FilenameUtils.getBaseName(nameNew);
        final Matcher matcher = pattern.matcher(baseName);
        if (matcher.matches()) {
            final String group = matcher.group(1);
            final int index = matcher.start(0);
            baseName = baseName.substring(index, baseName.length() - "copy".length() - group.length() - 1); // also omit '_'
            // string
            fileNameCounter = Integer.parseInt(group);
        }
        fileNameCounter++;
        nameNew = baseName + "_copy" + fileNameCounter + "." + extension;
        return nameNew;
    }
}
