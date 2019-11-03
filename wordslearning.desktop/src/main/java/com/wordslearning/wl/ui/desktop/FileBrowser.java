package com.wordslearning.wl.ui.desktop;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;

public class FileBrowser extends JPanel {
	private JTextField pathTF = new JTextField();
	private JButton browseButton;
	private List<FileBrowserListener> listeners = new ArrayList<FileBrowserListener>();

	public FileBrowser() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		pathTF.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				notifyListeners(((JTextField) e.getSource()).getText());
			}
		});
		add(pathTF);
		browseButton = new JButton("Browse...");
		browseButton.addActionListener(new ActionListener() {
			private int fileSelectionMode = JFileChooser.DIRECTORIES_ONLY;

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(fileSelectionMode);
				int res = fileChooser.showSaveDialog(FileBrowser.this);
				if (res == JFileChooser.APPROVE_OPTION) {
					pathTF.setText(fileChooser.getSelectedFile()
							.getAbsolutePath());
					notifyListeners(fileChooser.getSelectedFile()
							.getAbsolutePath());
				}
			}
		});
		add(browseButton);
		layout.putConstraint(SpringLayout.WEST, pathTF, 0, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, pathTF, 0, SpringLayout.NORTH,
				browseButton);
		layout.putConstraint(SpringLayout.SOUTH, pathTF, 0, SpringLayout.SOUTH,
				browseButton);
		layout.putConstraint(SpringLayout.EAST, pathTF,
				Spring.minus(Spring.constant(5)), SpringLayout.WEST,
				browseButton);
		layout.putConstraint(SpringLayout.EAST, browseButton, 0,
				SpringLayout.EAST, this);

	}

	public String getSelectedFilePath() {
		return pathTF.getText();
	}

	private void notifyListeners(String filePath) {
		for (FileBrowserListener listener : listeners) {
			listener.fileSelected(filePath);
		}
	}

	public void addListener(FileBrowserListener listener) {
		listeners.add(listener);
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension prefSize = super.getPreferredSize();
		prefSize.height = browseButton.getPreferredSize().height;
		return prefSize;
	}

	public interface FileBrowserListener {
		public void fileSelected(String selectedFilePath);
	}

	public void setSelectedFilePath(String filePath) {
		pathTF.setText(filePath);
	}
}
