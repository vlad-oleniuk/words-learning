package com.wordslearning.ve.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.wordslearning.ve.model.article.WLArticle;


public class HomonymsResolverDialog extends WordsConflictsResolvingDialog {

	
	
	private JPanel buttonsPanel;
	protected Set<WLArticle> wordsToRemove=new HashSet<WLArticle>();

	@Override
	protected JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			JButton addButton = new JButton("Add");
			addButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					status = APPROVE_OPTION;
					dispose();
				}
			});
			buttonsPanel.add(addButton);
			JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (conflictingWordsList.getSelectedIndex() != -1) {
						wordsToRemove.add((WLArticle) conflictingWordsList
								.getSelectedValue());
					}
					repaint();
				}
			});
			buttonsPanel.add(removeButton);
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					status = CANCEL_OPTION;
					dispose();
				}
			});
			buttonsPanel.add(cancelButton);
		}
		return buttonsPanel;
	}

	@Override
	protected ListCellRenderer getConflictingWordsCellRenderer() {
		return new HomonymsConflictingWordsListCellRenderer();
	}

	private class HomonymsConflictingWordsListCellRenderer extends
			DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			JLabel panel = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			panel.setText(((WLArticle) value).getValue());
			if (wordsToRemove.contains(value))
				panel.setForeground(Color.LIGHT_GRAY);

			return panel;
		}

	}

	@Override
	protected String getConflictingWordContent(WLArticle selectedValue) {
		return selectedValue.getValue();
	}

	public Set<WLArticle> getWordsToRemove() {
		return wordsToRemove;
	}

	@Override
	protected void reset() {
		wordsToRemove.clear();
	}
	
	@Override
	public String getTitle() {
		return "Homonyms";
	}

}
