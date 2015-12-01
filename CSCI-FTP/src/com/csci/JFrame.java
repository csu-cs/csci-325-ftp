package com.csci;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Created by shea on 11/5/2015.
 * This is the GUI, it sends info to Upload and Connection, and receives info from Download.
 */
public class JFrame extends JPanel{
    public static int BUFFER_SIZE = 10240;



    protected JTextField userNameTextField = new JTextField("Username?");
    protected JPasswordField passwordTextField = new JPasswordField(20);
    protected JTextField urlTextField = new JTextField(20);
    protected JTextField fileTextField = new JTextField(50);
    protected JTextArea monitorTextArea = new JTextArea(5, 20);
    protected JProgressBar m_progress = new JProgressBar();
    protected JButton putButton = new JButton("Put");
    protected JButton getButton;
    protected JButton fileButton = new JButton("File");
    protected JButton closeButton = new JButton("Close");
    protected JFileChooser fileChooser = new JFileChooser();

    protected String localFileName;

    protected String remoteFileName;

    public JFrame(){
    }
}
