package com.wordslearning.wl.exercises;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.ui.ExerciseCommons;
import com.wordslearning.wl.ui.KeyParts;
import com.wordslearning.wl.ui.WordsLearningUI;
import com.wordslearning.wl.ui.desktop.WLExercise;

public class WordConstructor extends WLExercise {

	// TODO put letter to the current cursor position
	// TODO check invalid input - no dialogs, just warning somewhere

	private enum State {
		ANSWERED_CORRECT, ANSWERED_NOT_CORRECT, NOT_ANSWERED
	};

	private JLabel transLabel = new JLabel("", JLabel.CENTER);
	private JLabel correctAnswerLabel = new JLabel();
	private JPanel resPanel = new JPanel(new BorderLayout());
	private JTextField answerTF = new JTextField();
	private JPanel lettersPanel = new JPanel();
	private JButton bottomButton = new JButton();
	private State state;
	private boolean phrase;
	
	private ExerciseCommons exerciseComons=new ExerciseCommons();

	public WordConstructor() {
		transLabel.setFont(correctAnswerLabel.getFont().deriveFont(16.0F));
		answerTF.setFont(answerTF.getFont().deriveFont(16.0F));
		transLabel.setOpaque(true);
		transLabel.setBackground(Color.WHITE);
		resPanel.setBorder(BorderFactory.createTitledBorder("Answer"));
		resPanel.add(answerTF);
		correctAnswerLabel.setFont(correctAnswerLabel.getFont().deriveFont(
				18.0F));
		answerTF.setHorizontalAlignment(JTextField.CENTER);
		setState(State.NOT_ANSWERED);
		correctAnswerLabel.setForeground(Color.RED);
		answerTF.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				String currentText = ((JTextField) e.getSource()).getText();
				synchronizeLetterButtons(currentText.toCharArray());
				finishAnswerIfReady();
			}
		});

		GroupLayout gl = new GroupLayout(this);
		setLayout(gl);

		gl.setAutoCreateContainerGaps(true);
		gl.setAutoCreateGaps(true);

		gl.setHorizontalGroup(gl
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(transLabel).addComponent(resPanel)
				.addComponent(correctAnswerLabel).addComponent(lettersPanel)
				.addComponent(bottomButton));
		gl.setVerticalGroup(gl.createSequentialGroup()
				.addComponent(transLabel, 20, 40, Short.MAX_VALUE)
				.addComponent(resPanel, 20, 20, Short.MAX_VALUE)
				.addComponent(correctAnswerLabel, 20, 20, Short.MAX_VALUE)
				.addComponent(lettersPanel).addComponent(bottomButton));
	}

	protected void finishAnswerIfReady() {
		if (answerFinished()) {
			answerTF.setEditable(false);
			checkAnswer();
		}
	}

	protected void synchronizeLetterButtons(char[] charArray) {
		activateAllLetterButtons();
		for (char c : charArray) {
			changeLetterButtonState(new String(new char[] { c }), false);
		}
	}

	private void setState(State state) {
		this.state = state;
		if (bottomButton.getActionListeners().length > 0)
			bottomButton
					.removeActionListener(bottomButton.getActionListeners()[0]);

		if (state == State.NOT_ANSWERED) {
			resetPanel();
			bottomButton.setText("Show Answer");

			bottomButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showAnswer();
					setState(State.ANSWERED_NOT_CORRECT);
				}
			});
		} else if (state == State.ANSWERED_NOT_CORRECT) {
			bottomButton.setText("Next");
			showAnswer();
			bottomButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					finishAnswer(false);
				}
			});
		} else if (state == State.ANSWERED_CORRECT) {
			finishAnswer(true);
		}
	}

	void showAnswer() {
		correctAnswerLabel.setText(getWord().getWLArticle().getKey());
	}

	@Override
	public String getExerciseName() {
		return "Word Constructor";
	}

	@Override
	public void showArticle(WLWord word) {
		setState(State.NOT_ANSWERED);

		setWord(word);
		transLabel.setText("<html><body><p>"
				+ getWord().getWLArticle().getValue() + "</p></body></html>");

		KeyParts keyParts = exerciseComons.getShuffledKeyParts(getWord()
				.getWLArticle().getKey());
		this.phrase=keyParts.isPhrase();

		for (String keyButton : keyParts.getParts()) {
			LetterButton letterButton = new LetterButton(keyButton);
			lettersPanel.add(letterButton);
		}

		revalidate();
		repaint();

	}

	@Override
	public boolean isApplicable(WLWord word) {
		return true;
	}

	private void resetPanel() {
		transLabel.setText("");
		correctAnswerLabel.setText("");
		lettersPanel.removeAll();
		answerTF.setEditable(true);
		answerTF.setText("");
	}

	private class LetterButton extends JButton {
		private String letter;

		public LetterButton(String label) {
			super(label);
			letter = label;

			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addLetterToAnswer(letter);
					LetterButton.this.setEnabled(false);
					finishAnswerIfReady();
				}
			});
		}
	}

	void changeLetterButtonState(String string, boolean enabled) {
		for (int i = 0; i < lettersPanel.getComponentCount(); i++) {
			if (((JButton) lettersPanel.getComponent(i)).getText().equals(
					string)
					&& ((JButton) lettersPanel.getComponent(i)).isEnabled() != enabled) {
				((JButton) lettersPanel.getComponent(i)).setEnabled(enabled);
				break;
			}
		}

	}

	void activateAllLetterButtons() {
		for (int i = 0; i < lettersPanel.getComponentCount(); i++) {
			((JButton) lettersPanel.getComponent(i)).setEnabled(true);
		}
	}

	void addLetterToAnswer(String letter) {
		String divider = phrase ? " " : "";
		answerTF.setText(answerTF.getText() + divider + letter);
	}

	private boolean answerFinished() {
		boolean finished = true;
		for (int i = 0; i < lettersPanel.getComponentCount(); i++) {
			if (((JButton) lettersPanel.getComponent(i)).isEnabled()) {
				finished = false;
				break;
			}
		}
		return finished;
	}

	private void checkAnswer() {
		if (state == State.NOT_ANSWERED) {
			String sb = answerTF.getText();
			if (sb.toString().trim().equals(getWord().getWLArticle().getKey())) {
				setState(State.ANSWERED_CORRECT);
			} else {
				setState(State.ANSWERED_NOT_CORRECT);
			}
		}
	}

	// for testing purposes

	JLabel getTransLabel() {
		return transLabel;
	}

	JPanel getResPanel() {
		return resPanel;
	}

	JPanel getLettersPanel() {
		return lettersPanel;
	}

	JButton getShowAnswerButton() {
		return bottomButton;
	}

}
