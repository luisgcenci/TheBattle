package com.mygdx.thebattle;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 *
 * @author lgcencir
 */
import java.net.*;
import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private BufferedReader input = null;
    String username;
    private final LinkedBlockingQueue<String> messages;
    String address;
    int port;


    public Client(String address, int port) {

        this.address = address;
        this.port = port;
        messages = new LinkedBlockingQueue<>();

        try {
            socket = new Socket(address, port);

            in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream())); 
            out = new DataOutputStream(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(System.in));
        
            //get username from user
            System.out.println(in.readUTF());
            username = input.readLine();
            out.writeUTF(username);            

            //connection accepted and welcome
            System.out.println(in.readUTF());

        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
        }

        ReadMessagesFromServer server = new ReadMessagesFromServer(socket);
        new Thread(server).start();
    }

    private class ReadMessagesFromServer implements Runnable{
        DataInputStream in = null;
        DataOutputStream out = null;
        Socket socket;

        ReadMessagesFromServer(Socket socket){
            this.socket = socket;
        }

        public void run(){
            try{
                in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());

                while(true){
                    try{
                        String line = in.readUTF();
                        messages.put(line);
                    }catch (IOException | InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }catch(IOException e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args)
	{   
        int port;
        String address;

        if (args.length > 2 || args.length <= 0){
            System.err.println("Arguments don't match the arguments length requirement" + 
            "(arguments length requirement : 2 Arguments)");
        }

        else {
            try {
                //socket info
                address =  args[0];
                port = Integer.parseInt(args[1]);
                new Client(address, port);
            }
            catch (NumberFormatException e){
                System.err.println("Argument : \"" + args[0] + "\" Needs to be a String" +
                "and Argument : \"" + args[1] + "\" Needs to be an integer");
            }
        }
		
	}
    
    
}
