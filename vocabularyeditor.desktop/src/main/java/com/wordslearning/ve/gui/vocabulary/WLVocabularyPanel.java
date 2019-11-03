package com.wordslearning.ve.gui.vocabulary;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.wordslearning.ve.ArticlesListListener;
import com.wordslearning.ve.gui.EditWordDialog;
import com.wordslearning.ve.gui.HomonymsResolverDialog;
import com.wordslearning.ve.gui.SynonymsResolvingDialog;
import com.wordslearning.ve.gui.VocabularyEditor;
import com.wordslearning.ve.gui.widgets.FilterableArticlesList;
import com.wordslearning.ve.model.VEModel;
import com.wordslearning.ve.model.VEModelListener;
import com.wordslearning.ve.model.article.WLArticle;
import com.wordslearning.ve.model.article.WLVocabulary;
//import com.wordslearning.ve.words_extractor.LingvoTutorExtractor;


public class WLVocabularyPanel extends JPanel implements VEModelListener,
		ArticlesListListener {
	private FilterableArticlesList vocList = new FilterableArticlesList();
	private VEModel model;
	private VocabularyEditor parent;
	private JComboBox currentVocabularyChooser;
	private JButton removeWordButton;
	private JButton addNewWordButton;
	private JButton importWordsButton;

	public WLVocabularyPanel(VocabularyEditor vocabularyEditor, VEModel model) {
		this.parent = vocabularyEditor;
		this.model = model;
		model.addListener(this);
		vocList.addArticlesListListener(this);
		setLayout(new BorderLayout(5, 5));
		add(createPanelToolBar(), BorderLayout.NORTH);
		add(vocList);
		setPreferredSize(new Dimension(300, getPreferredSize().height));
	}

	private JToolBar createPanelToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		currentVocabularyChooser = new JComboBox(new WLVocabulariesModel());
		toolBar.add(currentVocabularyChooser);
		currentVocabularyChooser
				.addActionListener(new OpenVocabularyActionListener());
		// saveVocabularyButton = new JButton(new ImageIcon(
		// WLVocabularyPanel.class.getResource("saveWLVoc.png")));
		// saveVocabularyButton.setEnabled(false);
		// saveVocabularyButton
		// .addActionListener(new SaveVocabularyActionListener());
		toolBar.addSeparator();
		// toolBar.add(saveVocabularyButton);

		importWordsButton = new JButton(new ImageIcon(
				WLVocabularyPanel.class.getResource("importWords.png")));
		importWordsButton.addActionListener(new ImportActionListener());
		importWordsButton.setEnabled(false);
		toolBar.add(importWordsButton);
		addNewWordButton = new JButton(new ImageIcon(
				WLVocabularyPanel.class.getResource("addWLArticle.png")));
		addNewWordButton.addActionListener(new AddWordActionListener());
		addNewWordButton.setEnabled(false);
		toolBar.add(addNewWordButton);
		removeWordButton = new JButton(new ImageIcon(
				WLVocabularyPanel.class.getResource("delWLArticle.png")));
		removeWordButton.addActionListener(new RemoveWordActionListener());
		removeWordButton.setEnabled(false);
		toolBar.add(removeWordButton);

		return toolBar;
	}

	@Override
	public void modelChanged() {
		if (model.getEditedVocabulary() != null) {
			vocList.setArticles(model.getEditedVocabulary().getArticles());
		}
	}

	@Override
	public void homonymsRevealed(WLArticle wlArticle, List<WLArticle> homonyms) {
		HomonymsResolverDialog dialog = new HomonymsResolverDialog();
		int res = dialog.showDialog(wlArticle, homonyms);
		if (res == HomonymsResolverDialog.APPROVE_OPTION) {
			Set<WLArticle> wordsToRemove = dialog.getWordsToRemove();
			for (WLArticle wordToRemove : wordsToRemove) {
				model.removeArticle(wordToRemove);
			}
			model.addWLArticle(wlArticle, true, false);
		}

	}

	@Override
	public void synonymsRevealed(WLArticle wlArticle, List<WLArticle> synonyms) {
		SynonymsResolvingDialog dialog = new SynonymsResolvingDialog();
		int res = dialog.showDialog(wlArticle, synonyms);
		if (res == HomonymsResolverDialog.APPROVE_OPTION) {
			model.addWLArticle(dialog.getEditedWord(), true, true);
		}
	}

	// private class SaveVocabularyActionListener implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// parent.saveVocabularies();
	//
	// }
	// }

	private class OpenVocabularyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.setEditedVocabulary((WLVocabulary) ((JComboBox) e.getSource())
					.getSelectedItem());
		}
	}

	private class RemoveWordActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.removeArticle((WLArticle) vocList.getSelectedValue());
		}
	}

	@Override
	public void articleSelected(WLArticle article) {
		// TODO Auto-generated method stub

	}

	@Override
	public void articleDoubleClicked(WLArticle article) {
		EditWordDialog ewd = new EditWordDialog(parent);
		ewd.setEditedArticle(article);
		int res = ewd.showDialog();
		if (res == EditWordDialog.APPROVE_OPTION) {
			WLArticle word = ewd.getEditedWord();
			model.updateWord(article, word);
		}

	}

	@Override
	public void customArticleSelected(String key) {
		// TODO Auto-generated method stub
	}

	@Override
	public void editedVocabularyChanged() {
		if (currentVocabularyChooser.getSelectedItem() != model
				.getEditedVocabulary())
			currentVocabularyChooser.setSelectedItem(model
					.getEditedVocabulary());
		vocList.setArticles(model.getEditedVocabulary().getArticles());
		addNewWordButton.setEnabled(true);
		removeWordButton.setEnabled(true);
		importWordsButton.setEnabled(true);
	}

	private class WLVocabulariesModel extends DefaultComboBoxModel {
		@Override
		public int getSize() {
			List<WLVocabulary> vocs = model.getWlVocabularies();
			if (vocs == null)
				return 0;
			else
				return vocs.size();
		}

		@Override
		public Object getElementAt(int index) {
			return model.getWlVocabularies().get(index);
		}
	}

	private class AddWordActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			EditWordDialog ewd = new EditWordDialog(parent);
			int res = ewd.showDialog();
			if (res == EditWordDialog.APPROVE_OPTION) {
				model.addWLArticle(ewd.getEditedWord());
			}
		}
	}

	private class ImportActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
//			JFileChooser fileChooser = new JFileChooser();
//
//			int res = fileChooser.showOpenDialog(parent);
//
//			if (res == JFileChooser.APPROVE_OPTION) {
//				File sourceFile = fileChooser.getSelectedFile();
//				List<WLArticle> words = LingvoTutorExtractor
//						.getAllWords(sourceFile.getAbsolutePath());
//				for (int i = 0; i < words.size(); i++) {
//					model.addWLArticle(words.get(i), false, false,
//							i == words.size() - 1);
//				}
//			}

		}
	}

	@Override
	public void vocabulariesInitialized() {
		// TODO Auto-generated method stub
		
	}
}
