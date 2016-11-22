package ch.ethz.inf.vs.a4.fmorath.pac_man;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.IOException;

import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Client;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.ExampleHandler;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.PlayerAction;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Server;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		try {
			testCommunication();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	/**
	 * TODO: Remove this method. Only for Demonstration purposes how to use the communication protocol.
	 * @throws IOException
	 * @throws InterruptedException
     */
	public void testCommunication() throws IOException, InterruptedException {
		ExampleHandler serverHandler = new ExampleHandler("Server");
		ExampleHandler client1Handler = new ExampleHandler("Client1");
		ExampleHandler client2Handler = new ExampleHandler("Client2");

		Server server = new Server(serverHandler);
		final Client client1 = new Client(client1Handler);
		final Client client2 = new Client(client2Handler);

		server.start();
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					client1.connectAndStartGame("localhost");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable(){

			@Override
			public void run() {
				try {
					client2.connectAndStartGame("localhost");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		Thread.sleep(5000);
		server.startGame();
		Thread.sleep(3000);
		server.send(new PlayerAction(0, 0, 0, MovementDirection.UP));
		client1.send(new PlayerAction(2, 20, 0, MovementDirection.UP));
		client1.send(new PlayerAction(2, 21, 0, MovementDirection.UP));
		Thread.sleep(100);
		server.send(new PlayerAction(0, 1, 0, MovementDirection.UP));
		client1.send(new PlayerAction(1, 10, 0, MovementDirection.UP));
		server.send(new PlayerAction(0, 2, 0, MovementDirection.UP));
		Thread.sleep(20);
		client1.send(new PlayerAction(1, 11, 0, MovementDirection.UP));
		client1.send(new PlayerAction(1, 12, 0, MovementDirection.UP));
		client1.send(new PlayerAction(2, 22, 0, MovementDirection.UP));
		server.send(new PlayerAction(0, 3, 0, MovementDirection.UP));
		Thread.sleep(500);
		server.stop();
	}
}
