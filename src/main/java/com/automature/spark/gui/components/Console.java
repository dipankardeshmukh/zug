/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.automature.spark.gui.components;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javafx.scene.control.TextArea;

/**
 *
 * @author skhan
 */
public class Console {
    
    private TextArea textArea;
    
    public Console(){
        textArea=new TextArea();
       // textArea.autosize();
       // textArea.
      //  redirectSystemStreams();
    }
    
    private void updateTextAreaWithOutput(String value){
        textArea.appendText(value);
    }
    
     private void updateTextAreaWithError(String value){
        textArea.appendText(value);
    }
    
    public void redirectSystemStreams() {

		OutputStream out = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				updateTextAreaWithOutput(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextAreaWithOutput(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		OutputStream err = new OutputStream() {

			@Override
			public void write(int b) throws IOException {

                            updateTextAreaWithError(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
						updateTextAreaWithError(	new String(b, off, len));
				
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};

		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(err, true));
	}
    
        public void clear(){
            textArea.clear();
        }
        
        public TextArea getTextArea(){
            return textArea;
        }
}
