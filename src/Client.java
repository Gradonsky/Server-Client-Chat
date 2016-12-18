import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.awt.event.ActionListener;

public class Client extends JFrame {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private JTextField inputTextField = new JTextField(48);
    private JTextArea textArea = new JTextArea(15, 60);
    private Object serializable;

    /*
     *Der BackgroundThread dient dazu um ganze Zeit auf ein Input von Bufferedreader abzufragen.
     * Wenn kein input da ist dann wurde die Verbindung unterbrochen. 
     */
    private Thread backgroundThread = new Thread(new Runnable() {
        @Override
        public void run() {
            String line;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (true) {
                    line = in.readLine();
                    textArea.append(line + "\n");
                }
            } catch (IOException e) {
            	JOptionPane.showMessageDialog(null, "Verbindung wurde unterbrochen \n Kein Input.",null, JOptionPane.ERROR_MESSAGE);
            	System.exit(1);
            }
        }

    });

    /**
     * Methode um nach dem drucken von Enter die Nachricht geschickt wird
     * 
     */
    private AbstractAction onEnterPressAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String textToSend = inputTextField.getText();
            inputTextField.setText("");
            try {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"), true);
                out.write(textToSend + "\n");
                out.flush();
                if (textToSend.startsWith("/quit")) {
                    if (out != null) out.close();
                    if (in != null) in.close();
                    if (socket != null) socket.close();
                    System.exit(0);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };
    /**
     * Konstruktor der Klasse Client hier wird  die GUI initialisiert und auch die Verbindung zu dem Server
     * @param host
     * @param port
     */
    public Client(String host, int port) {
    	// Socket wird initialisiert
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            //Wenn keine Verbindung 
            JOptionPane.showMessageDialog(null, "Could not connect to server", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // Wenn verbunden dann wird der Host und die Port in der TitelZeile angezeigt
        setTitle("CONNECTED TO SERVER: " + host + " IN PORT: " + port);

        // ein Thread wird im hintergrund gestartet um die Nachrichten vom Server zu empfangen
        backgroundThread.start();

        // Die GUI wird initialisiert.
        getContentPane().setLayout(new BorderLayout());
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        textArea.setEditable(false);

        JPanel southPanel = new JPanel();
        southPanel.add(inputTextField);
        inputTextField.addActionListener(onEnterPressAction);

        JPanel centerPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(textArea);
        centerPanel.add(scrollPane);

        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        
        /* Button Senden.. Die Nachricht wird aus 
         * dem InputTextField rausgelesen und dann uber den PrintWriter an den Socket weitergeleitet.
         */
        JButton btnNewButton = new JButton("Senden");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
                String textToSend = inputTextField.getText();
                inputTextField.setText("");
                try {
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"), true);
                    out.write(textToSend + "\n");
                    // Die Nachricht wird mittels flush() weitergeleitet
                    out.flush();
                    if (textToSend.startsWith("/quit")) {
                        if (out != null) out.close();
                        if (in != null) in.close();
                        if (socket != null) socket.close();
                        System.exit(0);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        		
        	}
        });
        southPanel.add(btnNewButton);
        pack();
        setLocationRelativeTo(null);

    }
/*
 * Die Main-methode , wenn keine
 * angaben zum Server uber Kommandozeile angegeben werden, dann werden die Daten uber das JOptionPane abgefragt.
 */
    public static void main(String[] args) {
    	try{
    		// Wenn keine Serverangaben bei args da sind wird dann JOptionPane aufgerufen.
        if (args.length == 0) {
            new Client("localhost", 7171);
        } else if (args.length == 1) {
            new Client(JOptionPane.showInputDialog("Server"), 7171);
        } else if (args.length == 2) {
            new Client(args[0], Integer.parseInt(args[1]));
        }
    	}catch(Exception e){
    		JOptionPane.showMessageDialog(null, "Falsche eingaben",null,JOptionPane.ERROR_MESSAGE);
    	}
    }
    	
}