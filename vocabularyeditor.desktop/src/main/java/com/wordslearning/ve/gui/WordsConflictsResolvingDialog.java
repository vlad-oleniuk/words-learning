package com.wordslearning.ve.gui;

import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.wordslearning.ve.model.article.WLArticle;


public abstract class WordsConflictsResolvingDialog extends JDialog {

	public static final int UNDEFINED_OPTION = 1;
	public static final int APPROVE_OPTION = 2;
	public static final int CANCEL_OPTION = 3;

	private JLabel addedWordKeyTF;
	private JLabel addedWordValueTF;
	protected JList conflictingWordsList;
	private JTextArea conflictingWordTA;
	protected WLArticle addedWord;
	protected List<WLArticle> conflictingWords;
	protected int status;

	public WordsConflictsResolvingDialog() {
		setLocationByPlatform(true);
	}

	protected void initUI() {
		setResizable(false);
		addedWordKeyTF = new JLabel("<html><body>" + addedWord.getKey()
				+ "</body></html>");
		// addedWordKeyTF.setVerticalTextPosition(JLabel.TOP);
		addedWordValueTF = new JLabel("<html><body>" + addedWord.getValue()
				+ "</body></html>");
		// addedWordValueTF.setVerticalTextPosition(JLabel.TOP);
		conflictingWordsList = new JList(conflictingWords.toArray());
		conflictingWordTA = new JTextArea();
		conflictingWordTA.setLineWrap(true);
		conflictingWordsList.setCellRenderer(getConflictingWordsCellRenderer());
		conflictingWordsList
				.addListSelectionListener(new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							conflictingWordTA
									.setText(getConflictingWordContent((WLArticle) ((JList) e
											.getSource()).getSelectedValue()));
						}

					}
				});

		GroupLayout gl = new GroupLayout(this.getContentPane());
		setLayout(gl);
		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);
		setModal(true);
		JScrollPane taPane = new JScrollPane(conflictingWordTA);
		JPanel buttonsPanel = getButtonsPanel();
		gl.setHorizontalGroup(gl
				.createParallelGroup()
				.addGroup(
						gl.createSequentialGroup()
								.addGroup(
										gl.createParallelGroup()
												.addComponent(addedWordKeyTF,
														5, 180, 220)
												.addComponent(
														conflictingWordsList,
														60, 180,
														Short.MAX_VALUE))
								.addGroup(
										gl.createParallelGroup()
												.addComponent(addedWordValueTF,
														5, 300, 300)
												.addComponent(taPane, 300, 300,
														Short.MAX_VALUE)))
				.addComponent(buttonsPanel));

		gl.setVerticalGroup(gl
				.createSequentialGroup()
				.addGroup(
						gl.createParallelGroup()
								.addComponent(
										addedWordKeyTF,
										10,
										(int) (addedWordValueTF
												.getPreferredSize().height * 3),
										Short.MAX_VALUE)
								.addComponent(
										addedWordValueTF,
										10,
										(int) (addedWordValueTF
												.getPreferredSize().height * 3),
										Short.MAX_VALUE))
				.addGroup(
						gl.createParallelGroup()
								.addComponent(conflictingWordsList)
								.addComponent(taPane, 100, 200, Short.MAX_VALUE))
				.addComponent(buttonsPanel));

	}

	public int showDialog(WLArticle addedWord, List<WLArticle> conflictingWords) {
		reset();
		status = UNDEFINED_OPTION;
		this.addedWord = addedWord;
		this.conflictingWords = conflictingWords;
		initUI();
		pack();
		setVisible(true);
		return status;
	}

	protected abstract String getConflictingWordContent(WLArticle selectedValue);

	protected abstract ListCellRenderer getConflictingWordsCellRenderer();

	protected abstract JPanel getButtonsPanel();

	protected abstract void reset();

}
