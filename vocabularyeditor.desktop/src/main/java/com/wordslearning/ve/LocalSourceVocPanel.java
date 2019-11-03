package com.wordslearning.ve;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.wordslearning.ve.model.article.AbstractLocalWordsStorage;
import com.wordslearning.ve.model.article.WLArticle;


public class LocalSourceVocPanel extends JPanel {

	private JList list;

	private JTextArea previewTA = new JTextArea();

	// private DictionaryListModel dictListModel;

	public LocalSourceVocPanel() {
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateGaps(true);
		// layout.setAutoCreateContainerGaps(true);
		setLayout(layout);
		list = createDictionaryList();
		JScrollPane scrollPane = new JScrollPane(list);
		JComponent prevArea = createPreviewArea();
		JComponent searchF = createSearchField();
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(scrollPane).addComponent(prevArea)
				.addComponent(searchF));
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(scrollPane)
				.addComponent(prevArea)
				.addComponent(searchF, GroupLayout.PREFERRED_SIZE,
						GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));
	}

	private JComponent createSearchField() {
		JTextField textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			private String tfText = "";

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (!tfText.equals(((JTextField) e.getComponent()).getText())) {
					tfText = ((JTextField) e.getComponent()).getText();
					selectWord(tfText);
				}
			}
		});
		return textField;
	}

	private JComponent createPreviewArea() {
		previewTA.setEditable(false);
		previewTA.setLineWrap(true);
		return new JScrollPane(previewTA);
	}

	private void selectWord(String prefix) {
		for (int i = 0; i < list.getModel().getSize(); i++) {
			if (((WLArticle) list.getModel().getElementAt(i)).getKey()
					.startsWith(prefix)) {
				list.setSelectedIndex(i);
				list.ensureIndexIsVisible(i);
				break;
			}
		}

	}

	private JList createDictionaryList() {
		DictionaryListModel dictListModel = new DictionaryListModel();
		JList dictList = new JList(dictListModel);
		dictList.addListSelectionListener(new ListSelectionListener() {
			// when a new word in the source vocabulary is selected it should be
			// shown in the preview area
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					WLArticle selectedArticle = (WLArticle) ((JList) e
							.getSource()).getSelectedValue();
					if (selectedArticle != null) {
						// TODO scroll the textarea to the begin
						previewTA.setText(selectedArticle.getValue());
					}
				}
			}
		});
		return dictList;
	}

	public void setVocabularySource(AbstractLocalWordsStorage storage) {
		((DictionaryListModel) list.getModel()).setWordsStorage(storage);
	}

	public AbstractLocalWordsStorage getVocabularySource() {
		return ((DictionaryListModel) list.getModel()).getWordsStorage();
	}

	private class DictionaryListModel extends AbstractListModel {
		private int size = 0;
		private AbstractLocalWordsStorage wordsStorage;

		public DictionaryListModel() {

		}

		public AbstractLocalWordsStorage getWordsStorage() {
			return this.wordsStorage;

		}

		public void setWordsStorage(AbstractLocalWordsStorage storage) {
			this.wordsStorage = storage;
		}

		@Override
		public Object getElementAt(int index) {
			return wordsStorage.getArticles().get(index);
		}

		@Override
		public int getSize() {
			return size;
		}

		public void fireModelChanged() {
			int oldSize = size;
			size = wordsStorage != null ? wordsStorage.getDictionarySize() : 0;
			if (size > oldSize)
				fireIntervalAdded(this, oldSize, size - 1);
			else if (size < oldSize)
				fireIntervalRemoved(this, oldSize, size - 1);
		}
	}

	public void fireModelChanged() {
		((DictionaryListModel) list.getModel()).fireModelChanged();
	}

	public WLArticle getSelectedWord() {
		return (WLArticle) list.getSelectedValue();
	}
}
