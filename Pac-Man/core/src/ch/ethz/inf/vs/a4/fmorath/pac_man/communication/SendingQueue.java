package ch.ethz.inf.vs.a4.fmorath.pac_man.communication;


import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import ch.ethz.inf.vs.a4.fmorath.pac_man.actions.Action;

/**
 * Created by johannes on 22.11.16.
 */

/**
 * Thread safe queue that allows for asynchronous sending of actions to another client.
 * This is achieved by starting a sending thread. This threads waits for the queue to be filled (concurrently) by other threads.
 * As soon as there are elements in the queue, the sending thread starts sending them over the network.
 */
class SendingQueue{
    private final DataOutputStream stream;
    private final Queue<Action> outQueue;
    private Thread thread;
    private boolean stopped;

    /**
     * Constructor.
     * @param stream Actions are written to this stream.
     */
    public SendingQueue(DataOutputStream stream){
        this.stopped = false;
        this.stream = stream;
        this.outQueue = new ArrayDeque<Action>();
    }

    /**
     * Add a new action to the queue.
     * @param action The action to be added to the queue (and eventually be sent over the network).
     */
    public void send(Action action){
        synchronized (this) {
            outQueue.add(action);
            if(thread != null)
                notifyAll();
        }
    }

    /**
     * Start a thread that sends all available actions to the receiver and then waits for new actions to be sent.
     */
    public void startSendingLoop(){
        synchronized (this) {
            if (thread != null) {
                return; //thread already running.
            }
            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!stopped) {
                        Action action = null;
                        synchronized (SendingQueue.this) {
                            while (!stopped && outQueue.isEmpty()) {
                                try {
                                    SendingQueue.this.wait();
                                } catch (InterruptedException e) {
                                    //thread was interrupted.
                                    e.printStackTrace();
                                }
                            }
                            if (!stopped) {
                                action = outQueue.remove();
                            }
                        }
                        if (action != null) {
                            try {
                                GameCommunicator.sendAction(stream, action);
                            } catch (IOException e) {
                                //Network error. stop the communication.
                                stopped = true;
                            }
                        }
                    }
                }
            });
        }
        thread.start();
    }


    /**
     * Set the "stopped" flag, that will initiate the shutdown procedure of this queue.
     */
    public void stop(){
        synchronized (this){
            this.stopped = true;
            notifyAll();
        }
    }


}
