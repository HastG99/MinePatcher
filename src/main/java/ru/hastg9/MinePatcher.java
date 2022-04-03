package ru.hastg9;

public class MinePatcher {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF8");
        System.setProperty("defaultCharset", "UTF8");

        printLogo();

        Settings.loadSettings();

        Injector injector = new Injector();

        System.out.println();

        injector.inject();
    }

    public static void printLogo() {
        System.out.println("\n░█▀▄▀█ ─▀─ █▀▀▄ █▀▀ ░█▀▀█ █▀▀█ ▀▀█▀▀ █▀▀ █──█ █▀▀ █▀▀█ \n" +
                "░█░█░█ ▀█▀ █──█ █▀▀ ░█▄▄█ █▄▄█ ──█── █── █▀▀█ █▀▀ █▄▄▀ \n" +
                "░█──░█ ▀▀▀ ▀──▀ ▀▀▀ ░█─── ▀──▀ ──▀── ▀▀▀ ▀──▀ ▀▀▀ ▀─▀▀");
        System.out.println("by HastG9\n");
        System.out.println("Youtube: https://www.youtube.com/c/HastG9\n");
    }

}
