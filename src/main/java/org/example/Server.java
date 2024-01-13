package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server

{

    public static void main(String[] args) throws IOException {

        System.out.println("Старт сервера");
        BufferedReader in = null;// поток для чтения данных
        PrintWriter out= null;// поток для отправки данных
        ServerSocket server = null;// серверный сокет
        Socket client = null;// сокет для обслуживания клиента

// создаем серверный сокет
        try {
            server = new ServerSocket(1234);
        } catch (IOException e) {
            System.out.println("Ошибка связывания с портом 1234");
            System.exit(-1);
            System.out.print("Ждем соединения");
            client= server.accept();
            System.out.println("Клиент подключился");
        }

// создаем потоки для связи с клиентом
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(),true);
        String input,output;

// цикл ожидания сообщений от клиента
        System.out.println("Ожидаем сообщений");
        while ((input = in.readLine()) != null) {
            if (input.equalsIgnoreCase("exit"))
                break;
            out.println("Сервер: "+input);
            System.out.println(input);
        }

        out.close();
        in.close();
        client.close();
        server.close();

    }
}