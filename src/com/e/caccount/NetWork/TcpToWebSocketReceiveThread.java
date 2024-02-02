/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e.caccount.NetWork;

import com.e.caccount.Message.Message;
import com.e.caccount.Model.Person;
import com.e.caccount.Utils.Parsing;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;

/* @author tritonle */
public class TcpToWebSocketReceiveThread extends Thread {

    private final Socket socket;
    private InputStream inputSt;
    private final TcpToWebSocketSendThread SnedThread;
    private final Person person = Person.getInstance();

    public TcpToWebSocketReceiveThread(Socket socket, TcpToWebSocketSendThread SnedThread) {
        this.socket = socket;
        this.SnedThread = SnedThread;
    }

    @Override
    public void run() {
        try {
            inputSt = socket.getInputStream();
        } catch (IOException e) {
            close();
        }

        while (true) {
            try {
                // 버퍼 크기에 따라 적절히 조절
                byte[] buffer = new byte[1024];
                int bytesRead = inputSt.read(buffer);
                if (bytesRead != -1) {
                    String receivedMessage = new String(buffer, 0, bytesRead, "UTF-8");

                    Message message = parseJson(Parsing.extractContent(receivedMessage));
                    System.out.println("msg receiver : " + message.getName() + " " + message.getType() + " "
                            + message.getAmount());
                    WhatIsMessageAbout(message);
                } else {
                    System.out.println("No data received.");
                }
            } catch (Exception ex) {
                System.out.println("[Client Out - " + socket.getInetAddress() + "] " + ex);
                // Close Socket
                close();
                // break out of loop
                break;
            }

        }
    }

    private static Message parseJson(String jsonString) {
        // 간단한 JSON 파싱 코드 작성
        Message message = new Message();
        String[] parts = jsonString.split("\"");

        message.setName(parts[3]);
        message.setType(parts[7]);
        message.setAmount(Integer.valueOf(parts[10].replaceAll("[^0-9]", "")));

        // for (int i = 0; i < parts.length; i++) {
        //     System.out.println(i + " : " + parts[i]);
        // }
        return message;
    }

    // Pattern is like this ("헌금종류,NEW")
    public void WhatIsMessageAbout(Message msg) {
        Platform.runLater(() -> {
            person.setUserData(msg.getName(), msg.getType(), msg.getAmount());
        });

        SnedThread.sendMessage(msg);
    }

    public void close() {
        try {
            if (inputSt != null) {
                inputSt.close();
            }
        } catch (IOException ioe) {

        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {

        }

        // Close Thread
        interrupt();
    }

}
