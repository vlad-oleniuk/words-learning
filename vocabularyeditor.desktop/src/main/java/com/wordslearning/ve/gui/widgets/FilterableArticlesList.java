package com.wordslearning.ve.gui.widgets;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.wordslearning.ve.ArticlesListListener;
import com.wordslearning.ve.model.article.WLArticle;

public class FilterableArticlesList extends JPanel {
	private JList vocList;
	private WLVocabularyListModel vocListModel;
	private List<ArticlesListListener> listeners = new ArrayList<ArticlesListListener>();

	public FilterableArticlesList() {
		setLayout(new BorderLayout(5, 5));
		add(createSearchField(), BorderLayout.SOUTH);
		add(createVocabularyList());
	}

	public void addArticlesListListener(ArticlesListListener listener) {
		listeners.add(listener);
	}

	private JComponent createVocabularyList() {
		vocListModel = new WLVocabularyListModel();
		vocList = new JList(vocListModel);
		vocList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() == 2) {
					int index = ((JList) e.getComponent()).locationToIndex(e
							.getPoint());
					if (index != -1) {
						for (ArticlesListListener listener : listeners) {
							listener.articleDoubleClicked((WLArticle) ((JList) e
									.getComponent()).getModel().getElementAt(
									index));
						}
					}
				}
			}
		});

		vocList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
					for (ArticlesListListener listener : listeners) {
						listener.articleSelected((WLArticle) ((JList) e
								.getSource()).getSelectedValue());
					}

			}
		});

		JScrollPane scrollPane = new JScrollPane(vocList);
		// vocListModel.fireModelChanged();
		return scrollPane;
	}

	private JTextField createSearchField() {
		JTextField textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			private String tfText = "";

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (!tfText.equals(((JTextField) e.getComponent()).getText())) {
					tfText = ((JTextField) e.getComponent()).getText();
					vocListModel.fireModelChanged(tfText);
					if (tfText.equals("")) {
						selectAndShowLastItem();
					}
				}

				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					for (ArticlesListListener listener : listeners) {
						listener.customArticleSelected(((JTextField) e
								.getComponent()).getText());
					}
			}
		});
		return textField;
	}

	private void selectAndShowLastItem() {
		vocList.setSelectedIndex(vocList.getModel().getSize() - 1);
		vocList.ensureIndexIsVisible(vocList.getModel().getSize() - 1);
		repaint();
	}

	public void setArticles(List<WLArticle> words) {
		vocListModel.setWords(words);
		selectAndShowLastItem();
	}

	private class WLVocabularyListModel extends AbstractListModel {
		private List<WLArticle> wordsToShow;
		private List<WLArticle> allWords;
		private String filter = "";

		@Override
		public Object getElementAt(int index) {
			return wordsToShow.get(index);
		}

		public void setWords(List<WLArticle> words) {
			allWords = words;
			fireModelChanged(null);
		}

		@Override
		public int getSize() {
			return wordsToShow != null ? wordsToShow.size() : 0;
		}

		public void fireModelChanged(String tfText) {
			if (tfText != null)
				this.filter = tfText;
			wordsToShow = filterWords(allWords, this.filter);
			fireContentsChanged(this, 0, wordsToShow.size() - 1);

		}

		private List<WLArticle> filterWords(List<WLArticle> allWords,
				String text) {
			List<WLArticle> res = new ArrayList<WLArticle>();
			for (WLArticle wlArticle : allWords) {
				if (wlArticle.getKey().contains(text))
					res.add(wlArticle);
			}
			return res;
		}

	}

	public WLArticle getSelectedValue() {
		return (WLArticle) vocList.getSelectedValue();
	}
}
