package com.wordslearning.ve.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import com.wordslearning.ve.model.WLArticleFormatter;
import com.wordslearning.ve.model.article.WLArticle;

public class EditWordDialog extends JDialog {
	private WLArticle article;
	private JTextArea editorTextArea = new JTextArea(9, 9);
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	public final static int APPROVE_OPTION = 1;
	public final static int CANCEL_OPTION = 0;
	private WLArticleFormatter articleFormatter = new WLArticleFormatter();

	private int res = CANCEL_OPTION;

	public EditWordDialog(JFrame parent) {

		super(parent);

		setLocationByPlatform(true);

		okButton.addActionListener(new OkayButtonListener());
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				res = CANCEL_OPTION;
				dispose();
			}
		});

		InputMap keyStrokesMap = editorTextArea
				.getInputMap(JComponent.WHEN_FOCUSED);
		keyStrokesMap.put(KeyStroke.getKeyStroke("ctrl E"),
				"example.learntword.marker");
		Action markLearntWordAction = new MarkLearntWordAction();
		editorTextArea.getActionMap().put("example.learntword.marker",
				markLearntWordAction);

		editorTextArea.setLineWrap(true);

		setSize(400, 300);

		JScrollPane taScrollPane = new JScrollPane(editorTextArea);

		add(taScrollPane);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		add(buttonsPanel, BorderLayout.SOUTH);
		JToolBar toolBar = new JToolBar();
		toolBar.add(markLearntWordAction);
		add(toolBar, BorderLayout.NORTH);
		setModal(true);
	}

	public void setEditedArticle(WLArticle article) {
		this.article = article;
	}

	public int showDialog() {
		if (article == null) {
			article = new WLArticle();
		}
		showWordInEditor();
		setVisible(true);
		return res;
	}

	private void showWordInEditor() {
		String contentToShow = articleFormatter.format(article);
		editorTextArea.setText(contentToShow);
		editorTextArea.setCaretPosition(0);
	}

	public WLArticle getEditedWord() {
		return article;
	}

	private class MarkLearntWordAction extends AbstractAction {

		public MarkLearntWordAction() {
			putValue(Action.LARGE_ICON_KEY, new ImageIcon(getClass()
					.getResource("markExample.png")));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String selectedText = editorTextArea.getSelectedText();
			if (selectedText != null) {
				int startSelection = editorTextArea.getSelectionStart();
				StringBuilder articleText = new StringBuilder(
						editorTextArea.getText());
				articleText.replace(startSelection,
						editorTextArea.getSelectionEnd(), "$" + selectedText
								+ "$");
				editorTextArea.setText(articleText.toString());

				editorTextArea.setCaretPosition(startSelection);
			}
		}

	}

	private class OkayButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			WLArticle article = articleFormatter.parseArticle(editorTextArea
					.getText());
			if (article.getKey() != null && article.getValue() != null) {
				setEditedArticle(article);
				res = APPROVE_OPTION;
				dispose();
			} else
				JOptionPane.showMessageDialog(EditWordDialog.this,
						"Word and its translation were not specified",
						"Word Addition Error", JOptionPane.WARNING_MESSAGE);
		}
	}

}
