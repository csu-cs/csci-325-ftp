package com.csci;

import java.io.*;
import java.awt.*;
import javax.swing.JFrame;

public class Main {
    // On return from the JFrame, call the other functions.
    // May need connection security info.
    public String ipAddress;
    public int Port;
    public String UserName;
    public String Password;

    public static void main(String[] args) {

        JFrame frame = new JFrame("FTP Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(new JFrame());

        frame.pack();
        frame.setVisible(true);



    }
}
