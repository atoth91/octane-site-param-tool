package com.microfocus.adm.almoctane.siteparamtool;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.swingx.JXTextField;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String frameTitle = "Site Param Tool";

	private JTextField textFieldUrl;
	private JTextField textFieldUsername;
	private JPasswordField passwordField;
	private JTable tableParams;
	private JTextField textFieldFilter;
	private SiteParamTableModel siteParamTableModel = new SiteParamTableModel();
	private JTextPane textPaneLogs;
	private JTextField textFieldProxyHost;
	private JTextField textFieldProxyPort;
	private JButton btnSave;
	private JButton btnLoad;

	@SuppressWarnings("serial")
	public Frame() {
		setTitle(frameTitle);
		setSize(1200, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel rootPanel = new JPanel();
		rootPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(rootPanel);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 500, 14, 130};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		JLabel lblHttpProxy = new JLabel("Proxy host:");
		GridBagConstraints gbc_lblHttpProxy = new GridBagConstraints();
		gbc_lblHttpProxy.anchor = GridBagConstraints.EAST;
		gbc_lblHttpProxy.insets = new Insets(0, 0, 5, 5);
		gbc_lblHttpProxy.gridx = 0;
		gbc_lblHttpProxy.gridy = 0;
		rootPanel.add(lblHttpProxy, gbc_lblHttpProxy);

		textFieldProxyHost = new JXTextField("ex. web-proxy.il.hpecorp.net, leave empty for direct");
		GridBagConstraints gbc_textFieldProxyHost = new GridBagConstraints();
		gbc_textFieldProxyHost.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldProxyHost.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldProxyHost.gridx = 1;
		gbc_textFieldProxyHost.gridy = 0;
		rootPanel.add(textFieldProxyHost, gbc_textFieldProxyHost);
		textFieldProxyHost.setColumns(10);

		JScrollPane scrollPaneLogs = new JScrollPane();
		GridBagConstraints gbc_scrollPaneLogs = new GridBagConstraints();
		gbc_scrollPaneLogs.gridheight = 9;
		gbc_scrollPaneLogs.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPaneLogs.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneLogs.gridx = 3;
		gbc_scrollPaneLogs.gridy = 0;
		gbc_scrollPaneLogs.weightx = 0.7;
		getContentPane().add(scrollPaneLogs, gbc_scrollPaneLogs);

		textPaneLogs = new JTextPane();
		scrollPaneLogs.setViewportView(textPaneLogs);
		textPaneLogs.setEditable(false);
		textPaneLogs.setEnabled(true);

		OutputStream os = new TextAreaOutputStream(textPaneLogs);

		JLabel lblProxyPort = new JLabel("Proxy port:");
		GridBagConstraints gbc_lblProxyPort = new GridBagConstraints();
		gbc_lblProxyPort.anchor = GridBagConstraints.EAST;
		gbc_lblProxyPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblProxyPort.gridx = 0;
		gbc_lblProxyPort.gridy = 1;
		rootPanel.add(lblProxyPort, gbc_lblProxyPort);

		textFieldProxyPort = new JXTextField("ex. 8080, leave empty for direct");
		GridBagConstraints gbc_textFieldProxyPort = new GridBagConstraints();
		gbc_textFieldProxyPort.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldProxyPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldProxyPort.gridx = 1;
		gbc_textFieldProxyPort.gridy = 1;
		rootPanel.add(textFieldProxyPort, gbc_textFieldProxyPort);
		textFieldProxyPort.setColumns(10);

		JLabel lblUrl = new JLabel("Url:");
		GridBagConstraints gbc_lblUrl = new GridBagConstraints();
		gbc_lblUrl.anchor = GridBagConstraints.EAST;
		gbc_lblUrl.insets = new Insets(0, 0, 5, 5);
		gbc_lblUrl.gridx = 0;
		gbc_lblUrl.gridy = 2;
		getContentPane().add(lblUrl, gbc_lblUrl);


		textFieldUrl = new JTextField();
		GridBagConstraints gbc_textFieldUrl = new GridBagConstraints();
		gbc_textFieldUrl.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUrl.gridx = 1;
		gbc_textFieldUrl.gridy = 2;
		getContentPane().add(textFieldUrl, gbc_textFieldUrl);
		textFieldUrl.setColumns(10);

		JLabel lblUsername = new JLabel("Username:");
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.anchor = GridBagConstraints.EAST;
		gbc_lblUsername.insets = new Insets(0, 0, 5, 5);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 3;
		getContentPane().add(lblUsername, gbc_lblUsername);

		textFieldUsername = new JTextField();
		GridBagConstraints gbc_textFieldUsername = new GridBagConstraints();
		gbc_textFieldUsername.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldUsername.gridx = 1;
		gbc_textFieldUsername.gridy = 3;
		getContentPane().add(textFieldUsername, gbc_textFieldUsername);
		textFieldUsername.setColumns(10);

		JLabel lblPassword = new JLabel("Password:");
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 4;
		getContentPane().add(lblPassword, gbc_lblPassword);

		passwordField = new JPasswordField();
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 5);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 1;
		gbc_passwordField.gridy = 4;
		getContentPane().add(passwordField, gbc_passwordField);

		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.fill = GridBagConstraints.BOTH;
		gbc_separator.anchor = GridBagConstraints.WEST;
		gbc_separator.insets = new Insets(0, 0, 5, 5);
		gbc_separator.gridwidth = 2;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 5;
		getContentPane().add(separator, gbc_separator);

		textFieldFilter = new JXTextField("Filter");
		GridBagConstraints gbc_textFieldFilter = new GridBagConstraints();
		gbc_textFieldFilter.gridwidth = 2;
		gbc_textFieldFilter.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldFilter.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldFilter.gridx = 0;
		gbc_textFieldFilter.gridy = 6;
		rootPanel.add(textFieldFilter, gbc_textFieldFilter);
		textFieldFilter.setColumns(10);
		textFieldFilter.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				siteParamTableModel.setFilter(textFieldFilter.getText());
				tableParams.setModel(tableParams.getModel());
			}

			public void removeUpdate(DocumentEvent e) {
				siteParamTableModel.setFilter(textFieldFilter.getText());
				tableParams.setModel(tableParams.getModel());
			}

			public void insertUpdate(DocumentEvent e) {
				siteParamTableModel.setFilter(textFieldFilter.getText());
				tableParams.setModel(tableParams.getModel());
			}
		});

		JScrollPane scrollPaneParams = new JScrollPane();
		GridBagConstraints gbc_scrollPaneParams = new GridBagConstraints();
		gbc_scrollPaneParams.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPaneParams.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneParams.gridwidth = 2;
		gbc_scrollPaneParams.gridx = 0;
		gbc_scrollPaneParams.gridy = 7;
		getContentPane().add(scrollPaneParams, gbc_scrollPaneParams);

		tableParams = new JTable();
		tableParams.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		tableParams.setModel(siteParamTableModel);
		tableParams.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable tab, Object val, boolean isSelected, boolean hasFocus, int row, int col){
				setEnabled(tab.isEnabled());
				return super.getTableCellRendererComponent(tab, val, isSelected, hasFocus, row, col);
			}
		});
		scrollPaneParams.setViewportView(tableParams);

		JSeparator separator_1 = new JSeparator(SwingConstants.VERTICAL);
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.VERTICAL;
		gbc_separator_1.gridheight = 10;
		gbc_separator_1.insets = new Insets(0, 0, 5, 5);
		gbc_separator_1.gridx = 2;
		gbc_separator_1.gridy = 0;
		rootPanel.add(separator_1, gbc_separator_1);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 8;
		rootPanel.add(panel, gbc_panel);

		btnLoad = new JButton("Load");
		btnLoad.addActionListener(e -> {
			load();
		});
		panel.add(btnLoad);

		btnSave = new JButton("Save");
		btnSave.addActionListener(e -> {
			btnSave.requestFocus();
			save();
		});
		panel.add(btnSave);
		MyStaticOutputStreamAppender.setStaticOutputStream(os);

		setVisible(true);
		textFieldUrl.requestFocus();
	}

	private void initProxy() {
		if(!textFieldProxyHost.getText().trim().isEmpty()) {
			System.setProperty("http.proxyHost", textFieldProxyHost.getText().trim());
			System.setProperty("https.proxyHost", textFieldProxyHost.getText().trim());
		}
		if(!textFieldProxyPort.getText().trim().isEmpty()) {
			System.setProperty("http.proxyPort", textFieldProxyPort.getText().trim());
			System.setProperty("https.proxyPort", textFieldProxyPort.getText().trim());
		}
	}

	private void load() {
		initProxy();

		String url = textFieldUrl.getText().trim();
		String username = textFieldUsername.getText().trim();
		String password = new String(passwordField.getPassword());

		shouldEnableUi(false);

		new Thread(() -> {
			try {
				List<SiteParam> params = ParamService.getParams(url, username, password);

				SwingUtilities.invokeLater(() -> {
					siteParamTableModel.setSiteParams(params);
				});
			} catch (Exception ex) {
				SwingUtilities.invokeLater(() -> {
					textPaneLogs.setText(textPaneLogs.getText() + ex.toString() + "\n");
				});
			}
			shouldEnableUi(true);
		}).start();


	}

	private void shouldEnableUi(boolean shouldDisableUi) {
		SwingUtilities.invokeLater(() -> {
			btnLoad.setEnabled(shouldDisableUi);
			btnSave.setEnabled(shouldDisableUi);

			textFieldUrl.setEnabled(shouldDisableUi);
			textFieldUsername.setEnabled(shouldDisableUi);
			passwordField.setEnabled(shouldDisableUi);
			tableParams.setEnabled(shouldDisableUi);
			textFieldFilter.setEnabled(shouldDisableUi);
			textFieldProxyHost.setEnabled(shouldDisableUi);
			textFieldProxyPort.setEnabled(shouldDisableUi);

			if(!shouldDisableUi) {
				Frame.this.setTitle(frameTitle + " (Loading...)");
			} else {
				Frame.this.setTitle(frameTitle);
			}
		});
	}

	private void save() {
		initProxy();
		if(siteParamTableModel.getDirtySiteParams().size() == 0) {
			JOptionPane.showMessageDialog(this, "No changes detected, nothing to save.");
			return;
		}

		String url = textFieldUrl.getText().trim();
		String username = textFieldUsername.getText().trim();
		String password = new String(passwordField.getPassword());

		String paramString = siteParamTableModel
				.getDirtySiteParams()
				.stream()
				.map(param -> param.getParamName() + ": \"" + param.getParamValue() + "\"")
				.collect(Collectors.joining("\n"));

		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(
				this,
				paramString,
				"Would you like to save the following changes?", dialogButton);

		if (dialogResult == 0) {
			shouldEnableUi(false);
			new Thread(() -> {
				try {
					ParamService.saveParams(url, username, password, siteParamTableModel.getDirtySiteParams());
				} catch (Exception ex) {
					SwingUtilities.invokeLater(() -> {
						textPaneLogs.setText(textPaneLogs.getText() + ex.toString() + "\n");
					});
				}
				shouldEnableUi(true);
			}).start();
			
			load();
		}
	}

}
