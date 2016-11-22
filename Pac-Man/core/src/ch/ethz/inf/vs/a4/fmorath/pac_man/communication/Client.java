package com.mygdx.game.communication;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.net.Socket;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by johannes on 22.11.16.
 */

public class Client implements CommunicationConstants{
    private Socket socket;

    public Client(){
        socket = null;
    }
    public void connectTo(String serverAddress) {
        SocketHints hints = new SocketHints();
        socket = Gdx.net.newClientSocket(Net.Protocol.TCP, serverAddress,SERVER_PORT, hints);
    }

}
