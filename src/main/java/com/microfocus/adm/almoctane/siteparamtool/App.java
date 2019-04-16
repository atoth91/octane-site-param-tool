package com.microfocus.adm.almoctane.siteparamtool;

import javax.swing.*;
import java.awt.EventQueue;

public class App {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {

				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception ignored){}

				new Frame();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}