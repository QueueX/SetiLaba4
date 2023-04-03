package vpr.seti;

import java.net.*;
import java.io.*;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;
    private static int serverConnectionStatus = 0;

    public static void main(String[] args) {
        System.out.println("Это клиент\n");

        boolean session = true;
        boolean connectionStatusKnown = false;

        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        System.out.println("Подключение к серверу...\n");
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                try {
                    serverConnectionStatus = Integer.parseInt(in.readLine());
                } catch (IOException e) {
                    System.out.println("В качестве статуса подключения передано не число!\n\n");
                    throw new RuntimeException(e);
                }

                if (serverConnectionStatus == 409) {
                    if (!connectionStatusKnown) {
                        System.out.println("Сервер занят. Ожидайте...\nМы сообщим вам когда сервер освободится");
                        connectionStatusKnown = true;
                    }
                } else if (serverConnectionStatus == 100) {
                    if (!connectionStatusKnown) {
                        System.out.println("Установлено соединение с сервером\n");
                        connectionStatusKnown = true;
                    }
                    break;
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("Не получилось подключиться к localhost");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Ошибка подключения ввода-вывода к localhost");
            System.exit(1);
        }

        System.out.println("Подключение прошло успешно!\n");

        BufferedReader stdIn = null;
        try {
            stdIn = new BufferedReader(new InputStreamReader(System.in, "cp866"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String userInput;
        String lower;

        while (session) {
            try {
                System.out.print("Введите запрос серверу: ");
                userInput = stdIn.readLine();
                out.println(userInput);
                System.out.println("Запрос отправлен! \n");
                lower = userInput.toLowerCase();

                if (lower.equals("отключиться")) {
                    session = false;
                } else {
                    String serverResponse = in.readLine();
                    System.out.println("Ответ сервера: \n" + serverResponse + "\n\n");
                }
            } catch (IOException e) {
                System.out.println("Скорее всего сервер отключен. Отключаю вас...");
                session = false;
            }
        }
        try {
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Произошла неведомая ошибка при закрытии соккета\n\n");
            throw new RuntimeException(e);
        }
    }
}
