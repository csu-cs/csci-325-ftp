package com.csci;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Created by Shea on 11/27/2015.
 */
public class Commands {

    public Socket dataSocket;
    public boolean isPause = false;

    // Gets the current dir from the server


    public String getList(String add) throws IOException {

        ftpSetTransferType(true);
        dataSocket = ftpGetDataSock();
        InputStream is = dataSocket.getInputStream();
        ftpSendCmd("LIST " + add);
        String contents = getAsString(is);
        return contents;
    }

    public void ftpSetTransferType(boolean isAscii) throws IOException {
        String ftpType = (isAscii? "A" : "I");
        ftpSendCmd("TYPE " + ftpType);
    }

    private String getAsString(InputStream inputStream) {
        int c = 0;
        char lineBuffer[]=new char[128], buf[]=lineBuffer;
        int room= buf.length, offset=0;
        try {
            loop:
            while (true) {
                switch (c = inputStream.read() ) {
                    case -1:
                        break loop;
                    default:
                        if (--room < 0) {
                            buf = new char[offset + 128];
                            room = buf.length - offset - 1;
                            System.arraycopy(lineBuffer, 0,buf, 0, offset);
                            lineBuffer = buf;
                        }
                        buf[offset++] = (char) c;
                        break;
                }
            }
        } catch(IOException ioe) {
            System.err.println("Error in getAsString." + ioe.getMessage());
//ioe.printStackTrace();
        }
        if ((c == -1) && (offset == 0)) {
            return null;
        }
        return String.copyValueOf(buf, 0, offset);
    }

    private Socket ftpGetDataSock() throws IOException {
        String reply = ftpSendCmd("PASV");
        StringTokenizer st = new StringTokenizer(reply, ",");
        String[] parts = new String[6];
        int i = 0;
        while(st.hasMoreElements()) {
            try {
                parts[ i ] = st.nextToken();
                i++;
            } catch(NoSuchElementException nope){nope.printStackTrace();}
        }
        String[] possNum = new String[3];
        for(int j = 0; j < 3; j++) {
            possNum[j] = parts[0].substring(parts[0].length() - (j + 1),
                    parts[0].length() - j);
            if(!Character.isDigit(possNum[j].charAt(0)))
                possNum[j] = "";
        }
        parts[0] = possNum[2] + possNum[1] + possNum[0];
        String[] porties = new String[3];
        for(int k = 0; k < 3; k++) {
            if((k + 1) <= parts[5].length())
                porties[k] = parts[5].substring(k, k + 1);
            else porties[k] = "FOOBAR";
            if(!Character.isDigit(porties[k].charAt(0)))
                porties[k] = "";
        }
        parts[5] = porties[0] + porties[1] + porties[2];
        String ip = parts[0]+"."+parts[1]+"."+parts[2]+"."+parts[3];
        int port = -1;
        try {
            int big = Integer.parseInt(parts[4]) << 8;
            int small = Integer.parseInt(parts[5]);
            port = big + small;
        } catch(NumberFormatException nfe) {nfe.printStackTrace();}
            dataSocket = new Socket(ip, port);
        return dataSocket;
    }

    private String ftpSendCmd(String cmd) throws IOException {
        if (isPause) {
            if (Main.connection.bufferedReader != null) {
                String discard = Main.connection.bufferedReader.readLine();
                isPause = false;
            }
        }
        Main.connection.printWriter.print(cmd + "\r\n");
        Main.connection.printWriter.flush();

        return(responseHandler());
    }

    private String responseHandler() throws IOException {
        String reply = this.responseParser(Main.connection.bufferedReader.readLine());
        String numerals = reply.substring(0,3);
        String hyph_test = reply.substring(3,4);
        String next = null;
        if(hyph_test.equals("-")) {
            String tester = numerals + " ";
            boolean done = false;
            while(!done) {
                next = Main.connection.bufferedReader.readLine();
                while (next.equals("") || next.equals("  ")) {
                    next = Main.connection.bufferedReader.readLine();
                }
                if(next.substring(0,4).equals(tester))
                    done = true;
            }
            return next;
        } else
            return reply;
    }

    private String responseParser(String resp) throws IOException {
        if (resp.substring(0,1).equals("1")) {
            isPause = true;
            return(resp);
        } else if (resp.substring(0,1).equals("2")) {
            isPause = false;
            return(resp);
        } else if ((resp.substring(0,1).equals("3"))
                || (resp.substring(0,1).equals("4"))
                || (resp.substring(0,1).equals("5"))) {
            return(resp);
        } else {
            return(null);
        }
    }

    // Changes working dir on server.
    public synchronized boolean setDir(String dir) throws IOException {
        String response = ftpSendCmd("CWD " + dir);
        return (response.startsWith("250 "));
    }

    // Delete file on server
    public synchronized boolean remDel(String dir) throws IOException {
        String response = ftpSendCmd("DELE " + dir);
        MainGUI.jConsole.append(response + "\n");
        return true;
    }


    // Disconnect from the server.
    public synchronized void disconnect() throws IOException {
        try {
            Main.connection.sendCommand("QUIT");
        } finally {
            Main.connection.socket = null;
            Main.isConnected = false;
        }
    }
    public synchronized boolean uploadFile(File fileToUpload) throws IOException {
        if (fileToUpload.isDirectory()) {
            throw new IOException("The client cannot upload a directory.");
        }

        String fileToUpload_Name = fileToUpload.getAbsolutePath();

        return uploadFile(new FileInputStream(fileToUpload), fileToUpload_Name);
    }

    public synchronized boolean uploadFile(InputStream inputStream, String filename) throws IOException {

        BufferedInputStream input = new BufferedInputStream(inputStream);
        Main.connection.sendCommand("TYPE I");
        String response = Main.connection.readResponse();
        Main.connection.sendCommand("PASV");
        response = Main.connection.readResponse();
        if (!response.startsWith("227 ")) {
            throw new IOException("The client could not use passive mode: "
                    + response);
        }

        String ip;
        int port;
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "." + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256 + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("The client received bad data link information: "
                        + response);
            }
        }

        response = ("STOR " + filename);

        Socket dataSocket = Main.connection.socket;

        if (!response.startsWith ("125 ")) {
            Main.mainGUI.jConsole.append("The client was not allowed to send the file: "
                    + response);
        }

        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        output.close();
        input.close();

        response = Main.connection.readResponse();
        return response.startsWith("226 ");
    }

    public void receiveFile(InputStream is) throws Exception {
        int filesize = 6022386;
        int bytesRead;
        int current = 0;
        byte[] mybytearray = new byte[filesize];

        FileOutputStream fos = new FileOutputStream("def");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bytesRead = is.read(mybytearray, 0, mybytearray.length);
        current = bytesRead;

        do {
            bytesRead = is.read(mybytearray, current,
                    (mybytearray.length - current));
            if (bytesRead >= 0)
                current += bytesRead;
        } while (bytesRead > -1);

        bos.write(mybytearray, 0, current);
        bos.flush();
        bos.close();
    }

    public synchronized boolean bin() throws IOException {
        Main.connection.sendCommand("TYPE I");
        String response = Main.connection.readResponse();
        return (response.startsWith("200 "));
    }

    public synchronized boolean ascii() throws IOException {
        Main.connection.sendCommand("TYPE A");
        String response = Main.connection.readResponse();
        return (response.startsWith("200 "));
    }
}
