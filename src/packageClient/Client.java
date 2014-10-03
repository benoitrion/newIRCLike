/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packageClient;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import packageDivers.Message;

/**
 *
 * @author Benoit
 */
public class Client extends FenetreClient {

    private Socket s;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean connected;
    Client() {
        super();
        try {
            s = new Socket(host, Message.PORT_NUM);
            ois = new ObjectInputStream(s.getInputStream());
            oos = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "\nFatal error : \n"
                    + "Please check server is running and "
                    + "check the port you want to listen.\n"
                    + "And check your IP address.");
            System.exit(0);
        }
    }
    public void quitter() {
        try {
            oos.writeObject((Message) new Message(Message.Type.CLT_LOGOUT, null, Color.BLACK));
            oos.flush();
            connected = false;
            System.exit(0);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Erreur IOException :" + ioe.getMessage());
        }
    }
    public void tourner() throws IOException {
        Message msg;
        installEvents();
        connected = true;
        oos.writeObject((Message) new Message(Message.Type.CLT_LOGIN, pseudo, null));
        oos.flush();
        try {
            while (connected && ((msg = (Message) ois.readObject()) != null)) {
                switch (msg.getType()) {
                    case SRV_RESPONSE:
                        pseudo = msg.getMessage();
                        setTitle("IRCClient: [" + pseudo + "]");
                        ;
                        break;
                    case SRV_BROADCAST:
                        try {
                            if(msg.getColor()!=null){
                                attributes.addAttribute(StyleConstants.CharacterConstants.Foreground, msg.getColor());
                            }
                            document.insertString(document.getLength(), msg.getMessage(), attributes);
                        } catch (BadLocationException ex) {
                            JOptionPane.showMessageDialog(null, ex.getMessage());
                        }
                        tf.setText("");
                        ;
                        break;

                    case SRV_NAMES:
                        names=msg.getMessage();
                        dialogQuiEstLà();
                        ;
                        break;
                    default:
                        System.err.println("Unknown command server : ");
                }
            }
            //quitter();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        s.close();
    }

    public void installEvents() {
        mQuiEstLa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    oos.writeObject((Message) new Message(Message.Type.CLT_NAMES, null, Color.BLACK));
                    oos.flush();
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, "IOException error:" + ioe.getMessage());
                }
            }
        });
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent we) {
            }
            @Override
            public void windowClosing(WindowEvent we) {
                quitter();
                System.exit(0);
            }
            @Override
            public void windowClosed(WindowEvent we) {
            }
            @Override
            public void windowIconified(WindowEvent we) {
            }
            @Override
            public void windowDeiconified(WindowEvent we) {
            }
            @Override
            public void windowActivated(WindowEvent we) {
            }
            @Override
            public void windowDeactivated(WindowEvent we) {
            }
        });
        mQuitter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quitter();
            }
        });
        tf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER && tf.getText().compareTo("") != 0) {
                    try {
                        oos.writeObject(new Message(Message.Type.CLT_MESSAGE, tf.getText() + "\n", couleur));
                        oos.flush();
                        tf.setText("");
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "IOException error:" + ioe.getMessage());
                    }
                }

            }
        });
    }

    public static void main(String[] args) throws IOException {
        Client cl = new Client();
        cl.tourner();
    }
}
