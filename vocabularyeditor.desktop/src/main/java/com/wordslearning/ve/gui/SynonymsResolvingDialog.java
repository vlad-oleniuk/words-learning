package com.wordslearning.ve.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.wordslearning.ve.model.article.WLArticle;


public class SynonymsResolvingDialog extends WordsConflictsResolvingDialog {

	private Set<WLArticle> approvedSynonyms = new HashSet<WLArticle>();

	@Override
	protected String getConflictingWordContent(WLArticle selectedValue) {
		return selectedValue.getValue();
	}

	@Override
	protected ListCellRenderer getConflictingWordsCellRenderer() {

		return new SynonymListCellRenderer();
	}

	@Override
	protected JPanel getButtonsPanel() {
		JPanel panel = new JPanel();

		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (WLArticle article : approvedSynonyms) {
					addedWord.getSynonyms().add(article.getKey());
					addedWord.getSynonyms().addAll(article.getSynonyms());
				}
				addedWord.getSynonyms().remove(addedWord.getKey());
				for (WLArticle article : approvedSynonyms) {
					article.getSynonyms().add(addedWord.getKey());
					article.getSynonyms().addAll(addedWord.getSynonyms());
					article.getSynonyms().remove(article.getKey());
				}
				status = APPROVE_OPTION;
				dispose();
			}
		});
		panel.add(okButton);

		JButton synonymButton = new JButton("Resolve");
		synonymButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (conflictingWordsList.getSelectedIndex() != -1) {
					WLArticle selectedWord = (WLArticle) conflictingWordsList
							.getSelectedValue();
					approvedSynonyms.add(selectedWord);
				}
				conflictingWordsList.repaint();
			}
		});
		panel.add(synonymButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				status = CANCEL_OPTION;
				dispose();
			}
		});

		panel.add(cancelButton);

		return panel;
	}

	@Override
	protected void reset() {

	}

	private class SynonymListCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel comp = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			WLArticle word = (WLArticle) value;
			comp.setText(word.getKey());

			if (addedWord.getSynonyms().contains(word.getKey())
					&& word.getSynonyms().contains(addedWord.getKey()))
				comp.setForeground(Color.GREEN);

			return comp;
		}
	}

	public WLArticle getEditedWord() {
		return addedWord;
	}

	public Set<WLArticle> getApprovedSynonyms() {
		// Set<WLArticle> res = new HashSet<WLArticle>();
		// for (WLArticle conflictArticle : conflictingWords) {
		// if (addedWord.getSynonyms().contains(conflictArticle.getKey())
		// && conflictArticle.getSynonyms().contains(
		// addedWord.getKey()))
		// res.add(conflictArticle);
		// }
		// return res;
		return null;
	}

}
