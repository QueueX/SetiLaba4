package vpr.seti;

import java.net.*;
import java.io.*;
import java.util.Queue;

public class ServerThread implements Runnable {
    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            String inputLine;

            while ((inputLine = Server.in.readLine()) != null) {
                String lower = inputLine.toLowerCase();
                if (lower.equals("отключиться")) {
                    break;
                }

                String response = "Сообщение от сервера: " + inputLine;
                Server.out.println(response);
                System.out.println("Сервер отправил ответ: \n" + response + "\n");
            }

            socket.close();

            synchronized(Server.class) {
                Server.isBusy = false;
                Queue<Socket> queue = Server.queue;
                if (!queue.isEmpty()) {
                    Socket nextSocket = queue.poll();
                    Server.out.println(100);
                    new Thread(new ServerThread(nextSocket)).start();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}