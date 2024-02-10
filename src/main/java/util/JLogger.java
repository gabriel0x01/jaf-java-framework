package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JLogger {
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String WHITE  = "\u001B[37m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";
    public static final String RESET  = "\u001B[0m";

    public static DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void showBanner() {
        System.out.println(RED);
        System.out.println("  ______          _____    ______   ______  ");
        System.out.println(" / / / /         / /   |  / ____/   \\ \\ \\ \\ ");
        System.out.println("/ / / /     __  / / /| | / /_        \\ \\ \\ \\  Jaf's a framework (Jaf)");
        System.out.println("\\ \\ \\ \\    / /_/ / ___ |/ __/        / / / /  Java Framework");
        System.out.println(" \\_\\_\\_\\   \\____/_/  |_/_/          /_/_/_/   Minimalist Web Framework");
        System.out.println(RESET);
    }
    public static void log(String modulo, String mensagem) {
        String date = LocalDateTime.now().format(DATE);
        System.out.printf(YELLOW+"%15s "+WHITE+"%-30s:"+GREEN+"%s\n"+RESET, date, modulo, mensagem);
    }

}
