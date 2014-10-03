/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packageServeur;

import packageDivers.Message;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benoit
 */
public class Serveur extends Thread {

    PrintWriter log;
    private final List<ThreadClient> clientConnecte;
    private ServerSocket serverSocket;
    Scanner sc = new Scanner(System.in);

    public Serveur() throws IOException {
        serverSocket = new ServerSocket(Message.PORT_NUM);
        clientConnecte = new ArrayList<>();
        log = new PrintWriter(new FileOutputStream("log.txt", true));
        System.out.println("A log file has been created");
    }

    @Override
    public void run() {
        try {
            System.out.println("Server running on port : " + serverSocket.getLocalPort());
            System.out.println("---------------------------------");
            System.out.println();
            log.write(Calendar.getInstance().getTime().toString()
                    + " : IRCServer Started\r\n");
            while (true) {
                Socket client = serverSocket.accept();
                try {

                    ThreadClient tc = new ThreadClient(client);

                    synchronized (clientConnecte) { // synchronisation...
                        clientConnecte.add(tc);
                        tc.start();
                    } // fin synchronized

                } catch (IOException ioe) {
                    System.err.println("Accepting client problem.");
                }
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    private class ThreadClient extends Thread {

        private Socket socketClient;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private String nickname;
        private int nbMessage=0;
        private int nbIt=1;
        private boolean trouvé;
        int i;
        public ThreadClient(Socket s) throws IOException {
            socketClient = s;
            oos = new ObjectOutputStream(socketClient.getOutputStream());
            ois = new ObjectInputStream(socketClient.getInputStream());
        }

        @Override
        public void run() {

            Message msg;

            try {
                while ((msg = (Message) ois.readObject()) != null) { // arrêt : voir CMD_QUIT
                    
                    switch (msg.getType()) {

                        case CLT_LOGIN:
                            trouvé = false;
                            i =0;
                            while (i < clientConnecte.size() && !trouvé) {
                                if (msg.getMessage().equals(clientConnecte.get(i).getNickname())) {
                                    trouvé = true;
                                    clientConnecte.get(i).incNbIt();
                                    nickname = msg.getMessage() + "_" + (clientConnecte.get(i).getNbIt());
                                }
                                i++;
                            }
                            if (!trouvé) {
                                nickname = msg.getMessage();
                            }
                            envoi(new Message (Message.Type.SRV_RESPONSE, nickname, msg.getColor()));
                            broadcast(new Message(Message.Type.SRV_BROADCAST, nickname + " : Log in \n", msg.getColor()));
                            System.out.println(nickname + " is loging in ("
                                    + socketClient.getInetAddress().getCanonicalHostName()
                                    + ")");
                            log.write(Calendar.getInstance().getTime().toString()
                                    + " : Created socket with client : " + socketClient.getInetAddress().toString());
                            log.write("\r\n");
                            break;

                        case CLT_LOGOUT:
                            broadcast(new Message(Message.Type.SRV_BROADCAST, nickname + " : Log out\n", msg.getColor()));
                            System.out.println(nickname + " is leaving ("
                                    + socketClient.getInetAddress().getCanonicalHostName()
                                    + ")");
                            return;  // kills client thread

                        case CLT_MESSAGE:
                            nbMessage++;
                            broadcast(new Message(Message.Type.SRV_BROADCAST, nickname + " : " + msg.getMessage(), msg.getColor()));
                            break;
                            
                        case CLT_NAMES:
                            envoi(new Message(Message.Type.SRV_NAMES, getNames(), msg.getColor()));
                            break;

                        default:
                            System.err.println("Unknown client command : "
                                    + socketClient.getInetAddress().getCanonicalHostName());
                    } // fin switch

                } // fin while
            } catch (IOException ioe) {
                System.out.println("IOException error: "+ioe.getMessage());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (oos != null) {
                        oos.flush();
                        oos.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                    if (socketClient != null) {
                        socketClient.close();
                        log.write("----------------------------------------------------------------------\r\n\r\n");
                        log.close();
                    }
                } catch (IOException ioe) {
                    System.err.println("Exiting client problem "
                            + nickname);
                }
                // retrait de la thread client de la liste des threads !
                synchronized (clientConnecte) {
                    clientConnecte.remove(this);
                    if (clientConnecte.isEmpty()) {
                        System.out.println("No more client...");
                    } 
                }
            }
        } 

        public void envoi(Message msg) {
            try {
                oos.writeObject(msg);
                oos.flush();
            } catch (IOException ex) {
                System.out.println("Error :" + ex.getMessage());
            }
        }

        public void broadcast(Message msg) {
            synchronized (clientConnecte) {
                for (ThreadClient tcl : clientConnecte) {
                    tcl.envoi(msg);
                }
            }
        }
        public String getNames(){
            String str = "";
            for (int i = 0; i < clientConnecte.size(); i++) {
                str = str.concat(clientConnecte.get(i).getNickname() + " (" + clientConnecte.get(i).getNbMessage() + ")\n");
            }
            return str;
        }
        public String getNickname(){
            return nickname;
        }
        public int getNbMessage(){
            return nbMessage;
        }
        public int getNbIt(){
            return nbIt;
        }
        public void incNbIt(){
            this.nbIt++;
        }
    }

    public static void main(String[] args) {
        try {
            Serveur serveur = new Serveur();
            serveur.start();
        } catch (IOException ioe) {
            System.err.println("Create server problem");

        }
    }
}
