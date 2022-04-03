package ru.hastg9;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Scanner;

final public class Settings {

    private static final Logger LOGGER = LogManager.getLogger(Settings.class.getSimpleName());

    public static int THREADS = 10;
    public static String
            HACK_API,
            SERVER_API_PATH,
            INPUT_FILE_PATH,
            OUTPUT_FILE_PATH;


    public static void loadSettings() {
        LOGGER.info("Loading properties ...");

        File config = new File("config.yml");

        if(!config.exists()) {
            InputStream inputStream = MinePatcher.class
                    .getClassLoader()
                    .getResourceAsStream("config.yml");

            try {
                Files.copy(inputStream, config.toPath());
            } catch (IOException ex) {
                LOGGER.error("An error occurred while extracting config!");
                LOGGER.trace(ex.getMessage(), ex);

                return;
            }
        }

        Yaml yaml = new Yaml();
        try {
            Map<String, Object> data = yaml.load(new FileInputStream(config));
            INPUT_FILE_PATH = (String) data.get("input");
            OUTPUT_FILE_PATH = (String) data.get("output");
            THREADS = (int) data.get("threads");
            HACK_API = (String) data.get("hack-api");
            SERVER_API_PATH = (String) data.get("server-api");

            Scanner scanner = new Scanner(System.in);

            if(INPUT_FILE_PATH.trim().length() == 0) {
                System.out.print("Input file/dir: ");
                INPUT_FILE_PATH = scanner.next();
            }

            if(OUTPUT_FILE_PATH.trim().length() == 0) {
                System.out.print("Output file/dir: ");
                OUTPUT_FILE_PATH = scanner.next();
            }


        } catch (FileNotFoundException ex) {
            LOGGER.error("An error occurred while loading config!");
            LOGGER.trace(ex.getMessage(), ex);
        }

    }

}
