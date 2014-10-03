/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packageClient;

import packageDivers.Message;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Benoit
 */
public class FenetreClient extends JFrame {

    //Main Window 
    private JMenuBar mb;
    private JMenu mPartie;
    protected JMenuItem mQuiEstLa;
    private JMenuItem mSauver;
    protected JMenuItem mQuitter;
    protected JTextPane tpAff;
    private JScrollPane spAff;
    private JButton btCol;
    protected JTextField tf;
    protected String pseudo;
    protected String host;
    protected String currentDir = System.getProperty("user.dir");
    protected ImageIcon imgColor = new ImageIcon(currentDir + "\\img\\colors.png");
    protected Color couleur=Color.BLACK;

    protected JTextArea taNoms;
    String str;
    protected String names;
    protected StyledDocument document = new DefaultStyledDocument();
    protected SimpleAttributeSet attributes = new SimpleAttributeSet();

    private PrintWriter log;

    FenetreClient() {
        dialogStart();
        this.setMinimumSize(new Dimension(350, 300));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        installMainWindow();
        installEvent();
        this.setVisible(true);
        this.pack();
    }
    public void installMainWindow(){
        mb = new JMenuBar();
        mPartie = new JMenu("Game");
        mQuiEstLa = new JMenuItem("Who is there ?");
        mSauver = new JMenuItem("Save");
        mQuitter = new JMenuItem("Quit");
        this.setJMenuBar(mb);
        mb.add(mPartie);
        mPartie.add(mQuiEstLa);
        mPartie.add(mSauver);
        mPartie.add(mQuitter);
        
        JPanel panUp = new JPanel(new FlowLayout());
        tpAff = new JTextPane(document);
        tpAff.setEditable(false);
        tpAff.setPreferredSize(new Dimension(350, 200));
        spAff = new JScrollPane(tpAff);
        panUp.add(spAff);
        
        JPanel panDown = new JPanel(new FlowLayout());
        btCol = new JButton(imgColor);
        btCol.setPreferredSize(new Dimension(40, 40));
        tf = new JTextField();
        tf.setPreferredSize(new Dimension(300, 30));
        panDown.add(btCol);
        panDown.add(tf);
        
        this.add(panUp, BorderLayout.NORTH);
        this.add(panDown, BorderLayout.SOUTH);
    }
    public void installEvent(){
        mSauver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                dialogSauver();
            }
        });
        btCol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                couleur = JColorChooser.showDialog(null, "Text color", Color.BLACK);
                tf.setForeground(couleur);
            }
        });
    }
    public void dialogStart() {
        JPanel pan = new JPanel(new GridLayout(0, 2));
        JTextField tfHost = new JTextField("localhost");
        JTextField tfPseudo = new JTextField("User");
        pan.add(new JLabel("Ip address"));
        pan.add(tfHost);
        pan.add(new JLabel("Pseudo"));
        pan.add(tfPseudo);
        int result = JOptionPane.showConfirmDialog(null, pan, "Connexion :", JOptionPane.OK_CANCEL_OPTION, -1);
        switch (result) {
            case JOptionPane.OK_OPTION:
                pseudo = tfPseudo.getText();
                host = tfHost.getText();
                ;
                break;
            case JOptionPane.CANCEL_OPTION:
            case JOptionPane.CLOSED_OPTION:
                System.exit(0);
                ;
                break;
        }
    }
    public void dialogQuiEstLà() {
        JPanel pan = new JPanel(new FlowLayout());
        taNoms = new JTextArea();
        taNoms.setEditable(false);
        taNoms.setPreferredSize(new Dimension(300, 200));
        JScrollPane spNoms = new JScrollPane(taNoms);
        pan.add(spNoms);
        taNoms.setText(names);
        JOptionPane.showConfirmDialog(null, pan, "Connected guest  :", JOptionPane.CANCEL_OPTION, -1);        
    }

    public void dialogSauver() {
        JPanel pan = new JPanel(new GridLayout(0, 2));
        JTextField tfNomLog = new JTextField("log_" + pseudo);
        tfNomLog.setPreferredSize(new Dimension(75, 15));
        pan.add(new JLabel("Enter the file name : "));
        pan.add(tfNomLog);
        int result = JOptionPane.showConfirmDialog(null, pan, "Save history :", JOptionPane.OK_CANCEL_OPTION, -1);
        switch (result) {
            case JOptionPane.OK_OPTION:
                try {
                    if (!tfNomLog.getText().equals("")) {
                        log = new PrintWriter(new FileOutputStream(tfNomLog.getText() + ".txt", true));
                        log.write(tpAff.getText());
                        log.write("\r\n");
                        log.close();
                    }
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "File doesn't exist");
                }
                ;
                break;
        }
    }
    public void dialogQuitter() {
        if (JOptionPane.showConfirmDialog(this, "Want to leave ? ", "Quit ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

}
