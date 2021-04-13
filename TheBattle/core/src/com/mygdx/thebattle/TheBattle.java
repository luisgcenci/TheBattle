package com.mygdx.thebattle;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.thebattle.Screens.GameScreen;
import java.net.*;
import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;


public class TheBattle extends Game {
	SpriteBatch batch;

	String address;
	int port;
	Socket socket;

	DataInputStream in;
	DataOutputStream out;
	BufferedReader input;
	
	@Override
	public void create () {

		batch = new SpriteBatch();
		address = "localhost";
		port = 3000;
		connectSocket(address, port);

		//set screen
		this.setScreen(new GameScreen(this));
	}

	public SpriteBatch getSpriteBatch(){
		return this.batch;
	}
	public DataInputStream getInputStream() { return this.in; }
	public DataOutputStream getOutputStream() { return this.out; }
	public BufferedReader getInputReader() { return this.input; }

	public void connectSocket(String address, int port){
		try {
			socket = new Socket(address, port);
			in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			out = new DataOutputStream(socket.getOutputStream());
			input = new BufferedReader(new InputStreamReader(System.in));

		} catch (Exception e) {
			System.out.println("error " + e.getMessage());
		}
	}

	public void disconnectGame(){
		try {

			this.out.writeUTF("gameover");
			String closeConnection = this.in.readUTF();

			this.in.close();
			this.out.close();
			this.socket.close();
			System.exit(0);

		} catch (Exception e) {
			System.out.println("error " + e.getMessage());
		}
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		this.batch.dispose();
	}

}
