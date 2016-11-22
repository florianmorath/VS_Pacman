package com.mygdx.game.communication;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;

/**
 * Created by johannes on 22.11.16.
 */

public class Server implements CommunicationConstants{
    private final List<Socket> clients;
    private boolean gameStarted;

    public Server(){
        gameStarted = false;
        clients = new ArrayList<Socket>();
    }

    public void start(){
        Gdx.app.log(LOGGING_TAG,"Start server.");
        new Thread(new Runnable(){

            @Override
            public void run() {
                ServerSocketHints serverSocketHint = new ServerSocketHints();

                serverSocketHint.acceptTimeout = 3000;
                ServerSocket serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, SERVER_PORT, serverSocketHint);


                // Loop until game starts.
                while(!gameStarted){
                    // Create a socket

                    Gdx.app.log(LOGGING_TAG, "Waiting for connections.");
                    Socket socket = serverSocket.accept(null);
                    clients.add(socket);
                    Gdx.app.log(LOGGING_TAG, "Got connection from " + socket.getRemoteAddress());

                }
            }
        }).start();
    }
    public void stop(){
    }
    public void startGame(){
        gameStarted = true;
    }
}
