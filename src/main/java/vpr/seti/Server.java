package vpr.seti;

import java.net.*;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class Server {
    private static final int PORT = 8080;
    public static boolean isBusy = false;
    public static Queue<Socket> queue = new LinkedList<Socket>();
    public static PrintWriter out = null;
    public static BufferedReader in = null;


    public static void main(String[] args) throws IOException {
        System.out.println("Это сервер\n");

        ServerSocket server = null;
        boolean listen = true;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен под портом " + PORT + "\n");
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера: \n\n" + e + "\n\nЗавершение работы");
            System.exit(-1);
        }

        while (listen) {

            System.out.println("Ожидание клиента...\n\n");
            Socket socket = server.accept();

            try{
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            synchronized(queue) {
                if (isBusy) {
                    System.out.println("Сервер занят, оправляю в очередь...");
                    queue.add(socket);
                    out.println(409);
                } else {
                    isBusy = true;
                    new Thread(new ServerThread(socket)).start();
                    out.println(100);
                }
            }
        }
        server.close();
    }
}