package com.wordslearning.wl.exercises;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.ui.desktop.WLExercise;


public abstract class SelectExercise extends WLExercise {

	private enum State {
		ANSWERING, ANSWERED
	}

	private State currentState;
	private JLabel keyLabel;
	private JList valuesList;
	private boolean correctAnswer;

	public SelectExercise() {
		keyLabel = new JLabel("", JLabel.CENTER);
		keyLabel.setFont(keyLabel.getFont().deriveFont(25.0F));
		DefaultListModel model = new DefaultListModel();
		valuesList = new JList(model);
		valuesList.setCellRenderer(new ValueCellRenderer());
		valuesList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!isMultipleSelectionPossible()) {
					if (currentState == State.ANSWERING) {
						processAnswer();
					} else
						finishAnswer(correctAnswer);
				} else if (currentState == State.ANSWERED)
					finishAnswer(correctAnswer);
			}

		});
		valuesList
				.setSelectionMode(isMultipleSelectionPossible() ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
						: ListSelectionModel.SINGLE_SELECTION);
		setLayout(new BorderLayout());
		add(keyLabel, BorderLayout.PAGE_START);
		add(valuesList);
		if (isMultipleSelectionPossible()) {
			JButton confirmButton = new JButton("Answer");
			confirmButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					processAnswer();
				}
			});
			JPanel panel = new JPanel();
			panel.add(confirmButton);
			add(panel, BorderLayout.PAGE_END);
		}
	}

	private void processAnswer() {
		setState(State.ANSWERED);

		Object[] selectedValues = valuesList.getSelectedValues();
		correctAnswer = true;
		for (Object answer : selectedValues) {
			if (!isCorrectVariant((String) answer)) {
				correctAnswer = false;
				break;
			}
		}
		repaint();
	}

	protected abstract boolean isMultipleSelectionPossible();

	protected abstract Set<String> getAnswerVariants(WLWord wlWord);

	protected abstract boolean isCorrectVariant(String value);

	@Override
	public void showArticle(WLWord word) {
		setWord(word);
		setState(State.ANSWERING);
		keyLabel.setText(word.getWLArticle().getKey());

		Set<String> possibleVariantsSet = new HashSet<String>();
		Map<Integer, WLWord> words = getAllWords();
		for (Integer key : words.keySet()) {
			possibleVariantsSet.addAll(getAnswerVariants(words.get(key)));
		}
		String[] possibleVariants = possibleVariantsSet
				.toArray(new String[possibleVariantsSet.size()]);
		Set<String> variants = new HashSet<String>();
		variants.addAll(getAnswerVariants(word));

		do {
			variants.add(possibleVariants[(int) (possibleVariants.length * Math
					.random())]);
		} while (variants.size() < Math.min(possibleVariantsSet.size(), 10));

		((DefaultListModel) valuesList.getModel()).clear();

		for (String variant : variants) {
			((DefaultListModel) valuesList.getModel()).addElement(variant);
		}
	}

	private void setState(State state) {
		this.currentState = state;
		if (currentState == State.ANSWERING) {
			valuesList.setCursor(new Cursor(Cursor.HAND_CURSOR));
			valuesList.setEnabled(true);
		} else {
			valuesList.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			valuesList.setEnabled(false);
		}
	}

	private class ValueCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String content = "<html><body><p align=\"center\">" + value
					+ "</p></body></html>";
			Component comp = super.getListCellRendererComponent(list, content,
					index, isSelected, cellHasFocus);

			((JLabel) comp).setVerticalAlignment(JLabel.TOP);
			((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
			((JLabel) comp)
					.setFont(((JLabel) comp).getFont().deriveFont(15.0F));
			FontMetrics metrics = comp.getFontMetrics(getFont());
			int linesNumber = metrics.stringWidth((String) value)
					/ (list.getSize().width - 20) + 1;
			setPreferredSize(new Dimension(getPreferredSize().width, Math.max(
					(metrics.getHeight() + metrics.getLeading()) * linesNumber,
					38)));
			comp.setBackground(index % 2 == 1 ? Color.WHITE : new Color(
					0xeeeeee));

			if (currentState == State.ANSWERED) {
				if (isCorrectVariant((String) value)) {
					if (isSelected)
						comp.setBackground(new Color(155, 233, 104));
					else
						comp.setBackground(new Color(0xccff55));
				} else if (isSelected)
					comp.setBackground(new Color(255, 125, 93));
			} else {
				if (isSelected)
					comp.setBackground(new Color(0xffffaa));
			}

			return comp;
		}

	}

}
