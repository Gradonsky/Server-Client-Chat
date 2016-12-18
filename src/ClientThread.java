import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientThread extends Thread {

    private BufferedReader in = null;
    private PrintWriter out = null;
    private Socket clientSocket = null;
    private CopyOnWriteArrayList<ClientThread> threads;
    private Server frame;
    private String name;
    /**
     * Konstruktor der Klasse ClientThread , initialisiert alles.
     * @param clientSocket
     * @param threads
     * @param frame
     */
    public ClientThread(Socket clientSocket, CopyOnWriteArrayList<ClientThread> threads, Server frame) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        this.frame = frame;
    }
    /**
     * Thread-Run
     */
    public void run() {
        try {
        	// Der Input und Output werden hier initialisiert.
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.write("<<< Enter your name >>>\r");
            out.flush();
            name = in.readLine().trim();
            out.write("<<< Hi " + name + ". Welcome to the chat room >>>\n<<< Enter /quit in a new line to exit >>>\r");
            out.flush();
            frame.changeTitle();
            synchronized (this) {
                for (ClientThread c : threads) {
                    if (c != this) {
                        c.out.write("<<< User " + name + " has connected >>>\r");
                        c.out.flush();
                    }
                }
                frame.writeInTextArea("<<< User " + name + " has connected >>>\n");
            }
            while (true) {
                String line = in.readLine();
                if (line.startsWith("/quit")) {
                    // Wenn der user /Quit in seinem Fesnter eingibt wird das ganze Prozess abgebrochen
                    break;
                }
                for (ClientThread c : threads) {
                    c.out.write("<" + name + "> " + line + "\r");
                    c.out.flush();
                }
                frame.writeInTextArea("<" + name + "> " + line + "\n");
            }

            synchronized (this) {
                for (ClientThread c : threads) {
                    if (c != null && c != this) {
                        c.out.write("<<< User " + name + " has disconnected >>>\r");
                        c.out.flush();
                    }
                }
                frame.writeInTextArea("<<< User " + name + " has disconnected >>>\n");
                out.write("<<< Goodbye " + name + " >>>");
                out.flush();
          
                Iterator i = threads.iterator();
                while (i.hasNext()) {
                    ClientThread c = (ClientThread) i.next();
                    if (c == this) {
                        c.interrupt();
                        threads.remove(c);
                        frame.changeTitle();
                    }
                }

                // Schliessen von den Verbindungen
                out.close();
                in.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            synchronized (this) {
                // Alle Clients werden Informiert, wenn der Juser ausgeloggt wird.
                for (ClientThread c : threads) {
                    if (c != null) {
                        c.out.write("<<< User " + name + " has disconnected >>>\r");
                        c.out.flush();
                    }
                }
                frame.writeInTextArea("<<< User " + name + " has disconnected >>>\n");

                Iterator i = threads.listIterator();
                while (i.hasNext()) {
                    ClientThread c = (ClientThread) i.next();
                    if (c == this) {
                        c.interrupt();
                        frame.changeTitle();
                    }
                }
                try {
                    out.close();
                    in.close();
                    clientSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}