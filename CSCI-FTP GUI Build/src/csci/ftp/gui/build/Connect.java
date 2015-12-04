package csci.ftp.gui.build;


import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

public class Connect {

    public Socket socket = null;
    public BufferedReader bufferedReader = null;
    public BufferedWriter bufferedWriter = null;
    public static boolean DEBUG = false;
    public PrintWriter printWriter;

    // Pushes forward to next connect with user and password of "anonymous".
    public synchronized void connect(String host, int port) throws IOException {
        connect(host, port, "anonymous", "anonymous");
    }

    // Connects to the server.
    public synchronized String connect(String host, int port, String userName, String password) throws IOException {
        if (socket != null) {
            return "There is already a connection established.";
        }
        try {
            socket = new Socket(host, port);
        } catch (ConnectException e) {
            return "Connection refused.";
        }


        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (ConnectException e) {
            return "Connection refused.";
        }

        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        String response = readResponse();
        if (!response.startsWith("220 ")) {
            return "Could not connect to the server: " + response;
        }

        sendCommand("USER " + userName);

        response = readResponse();
        if (!response.startsWith("331 ")) {
            return "Received an unknown response: " + response;
        }

        sendCommand("PASS " + password);

        response = readResponse();
        if (!response.startsWith("230 ")) {
            return "Unable to login.";
        }
        printWriter = new PrintWriter(this.socket.getOutputStream(), true);
        return "Connected to server.";
    }

    public void sendCommand(String line) throws IOException {
        Socket socket = this.socket;
        BufferedWriter bufferedWriter = this.bufferedWriter;
        Boolean DEBUG = this.DEBUG;

        if (socket == null) {
            MainGUI.jConsole.append("No established connection.\n");
        }
        try {
            bufferedWriter.write(line + "\r\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            socket = null;
            throw e;
        }
    }
    public String readResponse() throws IOException {
        BufferedReader bufferedReader = this.bufferedReader;
        String line = bufferedReader.readLine();
        return line;
    }

}

