package com.geekbrains.cloud;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CloudServer {

    public static void main(String[] args) {
        try(ServerSocket server = new ServerSocket(8189)){
            System.out.println("Сервер создан");
            while (true){
                Socket socket = server.accept();
                CloudHandler cloudHandler = new CloudHandler(socket);
                new Thread(cloudHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
