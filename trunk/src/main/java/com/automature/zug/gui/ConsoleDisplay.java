package com.automature.zug.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class ConsoleDisplay {
	
	private  static  JPanel outputPanel;
	private  static JScrollPane scrollPane;
	private  static JTextArea textArea= new JTextArea();
//	private CmdGUI cmd;
	private  static DefaultCaret caret;
	
	ConsoleDisplay(){
	
		outputPanel = new JPanel();
		outputPanel.setBackground(Color.WHITE);
		outputPanel.setLocation(0, 300);
		outputPanel.setPreferredSize(new Dimension(767,278));
		outputPanel.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		outputPanel.add(scrollPane);
		scrollPane.setAutoscrolls(true);
		scrollPane.setViewportView(textArea);
		textArea.setForeground(Color.black);
		textArea.setBackground(Color.lightGray);
        textArea.setFont(Font.getFont("Arial"));
		textArea.setEditable(false);
		textArea.setAutoscrolls(true);

		caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);


	}



	public void clearDisplay(){
		textArea.setText("");
	}
	
	public JPanel getConsole(){
		return outputPanel;
	}
	public JTextArea getConsoleDisplay(){
		return textArea;
	}




    public void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textArea.append(text);
            }
        });
    }

    public void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }














}
