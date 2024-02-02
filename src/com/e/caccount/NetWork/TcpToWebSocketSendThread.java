/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.e.caccount.NetWork;

import com.e.caccount.Message.Message;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author tritonle
 */
public class TcpToWebSocketSendThread extends Thread {

    private Socket socket;
    private OutputStream  outSt;

    public TcpToWebSocketSendThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            outSt =socket.getOutputStream();
        } catch (IOException ioe) {
            close();
        }
    }

    public void sendMessage(Message msg) {
        try {
            String jsonMessage = convertToJson(msg);
            outSt.write(jsonMessage.getBytes("UTF-8"));
            outSt.flush();
        } catch (IOException ioe) {
            System.out.println("sendThread writeObject Exception Occurs");
            // Close Socket
            close();
        }
    }

    private static String convertToJson(Message message) {
        // Message 객체를 JSON 형태의 문자열로 변환
        return String.format("{\"name\":\"%s\",\"type\":\"%s\",\"amount\":%d}",
                message.getName(), message.getType(), message.getAmount());
    }

    public void close() {
        try {
            if (outSt != null) {
                outSt.close();
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
