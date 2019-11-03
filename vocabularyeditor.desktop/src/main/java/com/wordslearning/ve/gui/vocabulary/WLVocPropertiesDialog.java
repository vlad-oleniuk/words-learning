package com.wordslearning.ve.gui.vocabulary;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.ve.model.article.WLVocabulary;


public class WLVocPropertiesDialog extends JDialog {
	public static final int APPROVE_OPTION = 0;
	public static final int CANCEL_OPTION = 1;
	private int retCode = 0;

	private JTextField nameTextField = new JTextField(30);
	private JComboBox langFromComboBox = new JComboBox(Language.values());

	// private JTextField pathTextField = new JTextField();
	private WLVocabulary wlVoc;

	public WLVocPropertiesDialog(Window parent) {
		super(parent, "Vocabulary Properties");
		setLocationByPlatform(true);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new OkActionListener());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelActionListener());
		JLabel nameLabel = new JLabel("Name");
		JLabel lFromLabel = new JLabel("From");

		// JLabel pathLabel = new JLabel("Path");
		// JComponent pathPanel = createPathPanel();

		GroupLayout layout = new GroupLayout(this.getContentPane());
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
								.addComponent(nameLabel)
								.addComponent(lFromLabel)
								/* .addComponent(pathLabel) */.addComponent(
										okButton))
				.addGroup(
						layout.createParallelGroup()
								.addComponent(nameTextField)
								.addComponent(langFromComboBox)
								/* .addComponent(pathPanel) */
								.addComponent(cancelButton)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup().addComponent(nameLabel)
								.addComponent(nameTextField))
				.addGroup(
						layout.createParallelGroup().addComponent(lFromLabel)
								.addComponent(langFromComboBox))
				// .addGroup(
				// layout.createParallelGroup()
				// .addComponent(pathLabel,
				// GroupLayout.Alignment.CENTER)
				// .addComponent(pathPanel))
				.addGroup(
						layout.createParallelGroup().addComponent(okButton)
								.addComponent(cancelButton)));
		pack();
	}

	private JComponent createPathPanel() {
		JPanel pathPanel = new JPanel();
		pathPanel.setLayout(new BorderLayout(5, 5));
		// pathPanel.add(pathTextField);
		JButton browseButton = new JButton("Browse...");
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int res = fileChooser
						.showSaveDialog(WLVocPropertiesDialog.this);
				if (res == JFileChooser.APPROVE_OPTION) {
					// pathTextField.setText(fileChooser.getSelectedFile()
					// .getAbsolutePath());
				}
			}
		});
		pathPanel.add(browseButton, BorderLayout.EAST);
		return pathPanel;
	}

	private class OkActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			wlVoc = new WLVocabulary();
			wlVoc.setName(nameTextField.getText());
			wlVoc.setLangFrom((Language) langFromComboBox.getSelectedItem());
			retCode = APPROVE_OPTION;
			dispose();
		}
	}

	private class CancelActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			retCode = CANCEL_OPTION;
			dispose();
		}
	}

	public int showDialog() {
		setModal(true);
		setVisible(true);
		return retCode;
	}

	public WLVocabulary getWLVocabulary() {
		return wlVoc;
	}
}
