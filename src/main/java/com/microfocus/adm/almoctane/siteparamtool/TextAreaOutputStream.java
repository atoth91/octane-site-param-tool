package com.microfocus.adm.almoctane.siteparamtool;

import javax.swing.*;
import java.io.OutputStream;

class TextAreaOutputStream extends OutputStream {

    private JTextPane textArea;
    private String buffer = "";

    public TextAreaOutputStream(JTextPane textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
    	buffer += (char) b;
    	
    	if(buffer.length() > 100) {
            textArea.setText(textArea.getText() + buffer);
            buffer = "";
    	} 	
    }

}