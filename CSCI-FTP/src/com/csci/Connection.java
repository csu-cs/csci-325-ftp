package com.csci;
import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.net.*;

/**
 * Created by shea on 11/5/2015.
 * Use this class to send and receive files. Do not encode/decode them here, do that in the Upload/Download classes.
 * Send: The encoded file from Upload to the file server.
 * Send: The encoded file from the file server to Download.
 * Receive: The encoded file from the file server.
 * Receive: The encoded file from Upload.
 */
class Connection {

    // The actual connection
    private URLConnection urlConnection;

    private String hostURL;
    private String userLogin;
    private String userPassword;

    public boolean uploadConnection(InputStream inputStream) throws IOException {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            OutputStream outputStream = urlConnection.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            byte[] byteBuffer = new byte[4096];
            int counter;

            while ((counter = bufferedInputStream.read(byteBuffer)) > 0) {

                bufferedOutputStream.write(byteBuffer, 0, counter);

            }

            bufferedOutputStream.close();

            return true;

        } catch (Exception ex){

            ex.printStackTrace();
            return false;

        }
    }

    public void setHostURL(String URL){
        this.hostURL = URL;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public boolean connectToServer() {

        try {
            URL host = new URL("ftp://" + userLogin + ":" + userPassword + "@" + hostURL);
            urlConnection = host.openConnection();
            return true;
        } catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}

