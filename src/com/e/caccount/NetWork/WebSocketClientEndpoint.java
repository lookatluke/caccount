package com.e.caccount.NetWork;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

import com.e.caccount.Message.Message;

public class WebSocketClientEndpoint extends Thread {

    private Socket socket;
    private TcpToWebSocketSendThread threadSend;
    private TcpToWebSocketReceiveThread threadReceive;

    private int hostPort = 3030;
    private String hostIp = "coucou.myasustor.com";

    @Override
    public void run() {
        connect();
    }

    public void connect() {
        try {
            // Client Socket
            socket = new Socket();
            socket.connect(new InetSocketAddress(hostIp, hostPort), 3000);
            System.out.println("Client Connected");

            // Output Thread
            threadSend = new TcpToWebSocketSendThread(socket);
            threadSend.start();

            // Input Thread
            threadReceive = new TcpToWebSocketReceiveThread(socket, threadSend);
            threadReceive.start();

            // IllegalArgumentException was added in case InetsocketAddress' ipaddress null
            // exception
        } catch (IOException | IllegalArgumentException e) {
            try {
                System.out.println("IOException Try Connection Again! " + hostIp);
                Thread.sleep(1000);
                connect();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void close() {
        if (socket != null) {
            threadSend.close();
            threadReceive.close();
            interrupt();
        }
    }

    public void snedMessage(Message msg) {
        if (threadSend != null) {
            threadSend.sendMessage(checkMessage(msg));
        }
    }

    private Message checkMessage(Message msg) {
        if (msg.getName().equals("") || msg.getName() == null) {
            msg.setName("NoName");
        }

        if (msg.getType().equals("") || msg.getType() == null) {
            msg.setType("NoType");
        }

        if (msg.getAmount() == null) {
            msg.setAmount(0);
        }

        return msg;
    }
}