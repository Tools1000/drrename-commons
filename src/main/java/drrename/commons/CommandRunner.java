package drrename.commons;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@Slf4j
public class CommandRunner {

    public void runCommand(String[] command) {
        log.debug("Running {}", Arrays.toString(command));
        try {
            Process process = Runtime.getRuntime().exec(command);
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.debug("Process out: {}", line);
            }
            final BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line2;
            while ((line2 = bufferedReader2.readLine()) != null) {
                log.debug("Process err: {}", line2);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
