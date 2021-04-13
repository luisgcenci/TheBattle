
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/

/*
 *
 * @author yasin
*/
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    private Socket socket = null;
    private ServerSocket server = null;
    private final ArrayList<HandleClient> CLIENTS = new ArrayList<>();
    LinkedBlockingQueue<String> inputs;
    LinkedBlockingQueue<String> clientEvents;
    int id = 0;

    public Server(final int port) {

        inputs = new LinkedBlockingQueue<>();
        clientEvents = new LinkedBlockingQueue<>();

        Thread acceptClients = new Thread(){
            public void run(){
                try {
                    server = new ServerSocket(port);
                    System.out.println("Server started");
                    System.out.println("Waiting client");

                    while(true){

                        socket = server.accept();

                        //handle multithreading for clients
                        HandleClient client = new HandleClient(id += 1, socket.getRemoteSocketAddress().toString(), socket.getPort(), socket);
                        CLIENTS.add(client);
                        new Thread(client).start();
                    }

                } catch (Exception e) {
                    System.out.println("Error here " + e.getMessage());
                }
            }
        };

        acceptClients.start();

        Thread writeInputEvents = new Thread(){
            public void run(){
                while(true){
                    try{
                        String input = inputs.take();
                        for (HandleClient client : CLIENTS){
                            client.write(input);
                        }

                    }catch (Exception e) {
                        System.out.println("Error is it here " + e.getMessage());
                    }
                }
            }
        };

        writeInputEvents.start();

        Thread writeClientEvents = new Thread(){
            public void run(){
                while(true){
                    try{
                        String event = clientEvents.take();
                        for (HandleClient client : CLIENTS){
                            client.write("event:" + event);
                        }

                    }catch (Exception e) {
                        System.out.println("Error is it here " + e.getMessage());
                    }
                }
            }
        };

        writeClientEvents.start();
    }

    public class HandleClient implements Runnable{
        
        private DataInputStream in = null;
        private DataOutputStream out = null;
        public String address;
        public int port;
        public Socket socket;
        private Hero hero;
    
        public HandleClient(int id, String address, int port, Socket socket){
    
            this.address = address;
            this.port = port;
            this.socket = socket;
            hero = new Hero();

            try{
                this.in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));
                this.out = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                System.out.println("Error here " + e.getMessage());
            }
        }
    
        public void run(){
            try {

                //get username from client
                out.writeUTF("Enter username: ");

                while(this.hero.getName() == null){
                    String name = in.readUTF();
                    this.hero.setName(name);
                }

                //pass id to client
                out.writeUTF(String.valueOf(this.hero.getId()));
                
                //welcome client
                out.writeUTF("Connection Accepted" + "\nWelcome " + this.hero.getName());

                //client connected, get client's hero information
                String heroInfo = in.readUTF();
                String [] info = heroInfo.split(",");
                this.hero.setX(Float.parseFloat(info[1]));
                this.hero.setY(Float.parseFloat(info[2]));
                this.hero.setCharacterId(Integer.parseInt(info[3]));
                this.hero.setHealth(Integer.parseInt(info[4]));
                
                //send info of players that are already in the game
                for (HandleClient hc : CLIENTS){
                    
                    Hero h = hc.getHero();
                    if (h != this.hero){
                        out.writeUTF(h.getId() + "," + h.getName() + "," + h.getX() + "," + h.getY() + "," + h.getCharacterId() + "," + h.getHealth());
                    }
                }

                //no more players in the game
                out.writeUTF("end");
                
                //send client event
                clientEvents.put(this.hero.getId() + "," + this.hero.getName() + "," + this.hero.getX() + "," + this.hero.getY() + "," + this.hero.getCharacterId() + "," + this.hero.getHealth());
                
                //welcome message to server
                System.out.println("Welcome " + this.hero.getName());

                String keyInput = "";
                while (!keyInput.equals("gameover")){

                    try{
                        keyInput = in.readUTF();
                        if (keyInput.equals("gameover")){
                            inputs.put("gameover:" + this.hero.getId());
                            break; 
                        }
                        else if(keyInput.contains("event:")){
                        
                            keyInput = keyInput.replace("event:", "");
                            if (keyInput.contains("new_health")){
                                keyInput = keyInput.replace("new_health", "");
                                String[] keyInputInfo = keyInput.split(",");
                                int heroId = Integer.parseInt(keyInputInfo[0]);
                                int newHealth = Integer.parseInt(keyInputInfo[1]);
                                for (HandleClient c : CLIENTS){
                                    if (c.getHero().getId() == heroId){
                                        c.getHero().setHealth(newHealth);
                                    }
                                }
                            }
                        }
                        String[] keyInputInfo = keyInput.split(",");
                        String input = keyInputInfo[0];
                        String dt = keyInputInfo[1];
                        if (input.equals("W") | input.equals("S") | input.equals("A") | input.equals("D")){
                            this.hero.setPosition(input, Float.parseFloat(dt));
                        }
                        inputs.put("KeyInput:" + this.hero.getId() + "," + dt + "," + input);
                    }catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }

                //client closed confirmation
                String closeConnection = in.readUTF();
                this.out.writeUTF(closeConnection);

                CLIENTS.remove(this);
                this.in.close();
                this.out.close();
                socket.close();
            }
    
            catch (IOException | InterruptedException e){
                e.printStackTrace();
            }
        }

        public void write(String message){
            try{
                this.out.writeUTF(message);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public Hero getHero(){
            return this.hero;
        }

    }

    public static void main(String[] args)
    {
        int port;

//        if (args.length != 1){
//            System.err.println("Arguments don't match the arguments length requirement" +
//            "(arguments length requirement : 1 Argument)");
//        }
//        else {
        try {
//            port = Integer.parseInt(args[0]);
            new Server(3000);
        }
        catch (NumberFormatException e){

            System.err.println("Argument : \"" + args[0] + "\" Needs to be an integer");
        }
//        }
    }
}


