package com.microfocus.adm.almoctane.siteparamtool;

import javax.swing.*;
import java.io.OutputStream;

class TextAreaOutputStream extends OutputStream {

    private JTextPane textArea;

    public TextAreaOutputStream(JTextPane textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
        textArea.setText(textArea.getText() + (char) b);
    }

}