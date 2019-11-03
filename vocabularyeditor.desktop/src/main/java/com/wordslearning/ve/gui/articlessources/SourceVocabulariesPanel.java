package com.wordslearning.ve.gui.articlessources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import com.wordslearning.ve.ArticlesListListener;
import com.wordslearning.ve.gui.widgets.FilterableArticlesList;
import com.wordslearning.ve.model.VEModel;
import com.wordslearning.ve.model.VEModelListener;
import com.wordslearning.ve.model.article.AbstractLocalWordsStorage;
import com.wordslearning.ve.model.article.WLArticle;
import com.wordslearning.ve.model.article.WordsStorage;


public class SourceVocabulariesPanel extends JPanel implements VEModelListener {

	private VEModel model;
	private JTextField wordSearchTF;
	private JList prevList;

	public SourceVocabulariesPanel(VEModel model) {
		this.model = model;
		setLayout(new BorderLayout(5, 5));
		model.addListener(this);
		wordSearchTF = createWordSearchTF();
		// modelChanged();
		add(wordSearchTF, BorderLayout.NORTH);
		prevList = new JList(new PreviewListModel());
		prevList.setVisibleRowCount(18);
		prevList.setCellRenderer(new ArticlePreviewListCellRenderer());
		JScrollPane prevListSP = new JScrollPane(prevList);
		add(prevListSP);
		// statusLabel.setBorder(BorderFactory
		// .createBevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(300, getPreferredSize().height));
	}

	private JTextField createWordSearchTF() {
		JTextField textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			private Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			private Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					setCursor(hourglassCursor);
					showInPreview(((JTextField) e.getComponent()).getText());
					setCursor(normalCursor);
				}
			}
		});
		return textField;
	}

	@Override
	public void modelChanged() {
		showRelevantSourceArticles();
	}

	private void showRelevantSourceArticles() {
		List<WLArticle> articles = new ArrayList<WLArticle>();
		List<WordsStorage> sources = model.getSourceVocabularies();
		for (WordsStorage sourceVoc : sources) {
			if (sourceVoc instanceof AbstractLocalWordsStorage
					&& sourceVoc.getLangFrom() == model.getEditedVocabulary()
							.getLangFrom())
				articles.addAll(((AbstractLocalWordsStorage) sourceVoc)
						.getArticles());
		}
		// sourceDictionariesPanel.setArticles(articles);
	}

	public JFrame getParentFrame() {
		Component parent = this;
		while (!(parent instanceof JFrame)) {
			parent = parent.getParent();
		}
		return (JFrame) parent;
	}

	public WLArticle getSelectedWord() {
		if (prevList.getSelectedValue() instanceof WLArticle) {
			return (WLArticle) prevList.getSelectedValue();
		} else {
			return null;
		}
	}

	@Override
	public void homonymsRevealed(WLArticle wlArticle, List<WLArticle> homonyms) {
		// TODO Auto-generated method stub

	}

	@Override
	public void synonymsRevealed(WLArticle wlArticle, List<WLArticle> synonyms) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void articleSelected(WLArticle article) {
	// showInPreview(article.getKey());
	// }

	private void showInPreview(String key) {
		((PreviewListModel) prevList.getModel())
				.setArticles(extractWordsFromDictionaries(key));
	}

	private Map<WordsStorage, List<WLArticle>> extractWordsFromDictionaries(
			String key) {
		boolean wasNotifiedAboutProblem = false;
		Map<WordsStorage, List<WLArticle>> wordsToBeShown = new HashMap<WordsStorage, List<WLArticle>>();
		List<WordsStorage> vocs = model.getSourceVocabularies();
		for (WordsStorage wordsStorage : vocs) {
			if (wordsStorage.getLangFrom() == model.getCurrentLanguage()) {
				try {
					wordsToBeShown
							.put(wordsStorage, new ArrayList<WLArticle>());
					List<WLArticle> words = wordsStorage.getWords(key);
					wordsToBeShown.get(wordsStorage).addAll(words);
				} catch (IOException e) {
					if (!wasNotifiedAboutProblem) {
						JOptionPane.showMessageDialog(this,
								"A problem occurred during words extraction",
								"I/O problem", JOptionPane.WARNING_MESSAGE);
						wasNotifiedAboutProblem = true;
					}
				}
			}
		}
		return wordsToBeShown;
	}

	// @Override
	// public void articleDoubleClicked(WLArticle article) {
	//
	// }

	private class ArticlePreviewListCellRenderer extends
			DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel jLabel = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			if (value instanceof WLArticle) {
				jLabel.setText("<html><body><p>"
						+ ((WLArticle) value).getValue() + "</p></body></html>");
				jLabel.setVerticalAlignment(JLabel.TOP);
				jLabel.setHorizontalAlignment(JLabel.LEFT);

				FontMetrics metrics = jLabel.getFontMetrics(getFont());
				int linesNumber = metrics.stringWidth(((WLArticle) value)
						.getValue()) / (list.getSize().width - 20) + 1;
				jLabel.setPreferredSize(new Dimension(
						list.getSize().width - 20, Math.max(
								(metrics.getHeight() + metrics.getLeading())
										* linesNumber, 38)));
				if (!isSelected) {
					jLabel.setBackground(index % 2 == 1 ? Color.WHITE
							: new Color(0xeeeeee));
				}

			} else {
				jLabel.setPreferredSize(new Dimension(
						list.getSize().width - 20, 30));
				jLabel.setBackground(Color.GRAY);
				jLabel.setVerticalAlignment(JLabel.CENTER);
				jLabel.setForeground(Color.WHITE);
				jLabel.setFont(jLabel.getFont().deriveFont(Font.BOLD));
				jLabel.setText(((WordsStorage) value).getName());
			}
			return jLabel;
		}
	}

	private class PreviewListModel extends DefaultListModel {
		private List<Object> items = new ArrayList<Object>();

		@Override
		public Object getElementAt(int index) {
			return items.get(index);
		}

		@Override
		public int getSize() {
			return items != null ? items.size() : 0;
		}

		public void setArticles(Map<WordsStorage, List<WLArticle>> articles) {
			items.clear();
			for (WordsStorage storage : articles.keySet()) {
				items.add(storage);
				items.addAll(articles.get(storage));
			}
			fireContentsChanged(this, 0, getSize());
		}
	}

	// @Override
	// public void customArticleSelected(String key) {
	// }

	@Override
	public void editedVocabularyChanged() {
		showRelevantSourceArticles();
	}

	@Override
	public void vocabulariesInitialized() {
		// TODO Auto-generated method stub
		
	}
}
