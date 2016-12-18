import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
/**
 * Gradonski Janusz Aufgabe Server-Client Chat
 * @author Nervous
 *
 */
public class Server extends JFrame implements java.io.Serializable {

    JTextArea textArea;
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private CopyOnWriteArrayList<ClientThread> threads = new CopyOnWriteArrayList<>();
    private JList list;

    /**
     * Die Main-methode.. wenn ein Port angegeben wird , so wird dieser auch ubernommen , wenn nicht dann defaultport 7171
     * @param args
     */
    public static void main(String args[]) {
        int portNumber = 7171;
        if (args.length > 1) {
            portNumber = Integer.valueOf(args[0]);
        }

        new Server(portNumber);
    }

    /**
     * Server-Konstruktor hier wird die Verbindung aufgebaut und auch die GUI fur den Server initialisiert
     * Wenn eine Verbindung aufgebaut wird so wird ein ClientThread gestartet und das Socket dann weitergegeben.
     * 
     * @param portNumber
     */
    public Server(int portNumber) {
    	//GUI
        getContentPane().setLayout(new BorderLayout());
        setVisible(true);
        setLocation(750, 0);
        JPanel centerPanel = new JPanel();
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Das Fenster wird initialisiert 
        centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        centerPanel.add(new JScrollPane());
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        Button button_1 = new Button("Clear");
        button_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		textArea.setText("");
        	}
        });
        
        list = new JList();
        centerPanel.add(list);
        textArea = new JTextArea(15, 40);
        centerPanel.add(textArea);
        textArea.setEditable(false);
        centerPanel.add(button_1);
        // Button Stop server
        Button button = new Button("Stop Server");
        button.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		 try {
                     serverSocket.close();
                 } catch (Exception e1) {
                     System.out.println(e);
                 }
        		System.exit(1);
        	}
        });
        centerPanel.add(button);
        pack();

        // Es wird ein neuer Server fur die Verbindungen geoffnet
        try {
            serverSocket = new ServerSocket(portNumber);
            changeTitle();
            textArea.append(
            		"Server Started... \nWaiting for Connections...\n");
        } catch (IOException e) {
            System.out.println(e);
        }

        // Wenn ein Client sich verbindet wird er somit in ein threads Array hinzugeugt
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                ClientThread temp = new ClientThread(clientSocket, threads, this);
                temp.start();
                threads.add(temp);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ein Server ist schon online.");
            }
            
        }
    }
    /**
     *Methode um in die TextArea was 
     * @param message
     */
    public void writeInTextArea(String message) {
        textArea.append(message);
    }

    /**
     * Der Titel von dem Fenster wird da geandert wenn eine Verbindung hinzugefugt wird usw.
     */
    public void changeTitle() {
        String host = "";
        int portNumber = 0;
        try {
            host = serverSocket.getInetAddress().getLocalHost().getHostAddress();
            portNumber = serverSocket.getLocalPort();
        } catch (UnknownHostException e) {
            System.out.println("Falsch Host");
        }
        setTitle("5AHITM Gradonski Chat | HOST: " + host + " | PORT: " + portNumber + " | NUMBER OF CLIENTS: " + threads.size());
    }
}


