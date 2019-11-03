package com.wordslearning.wl.exercises;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.model.learnprocess.LearnProcessEngine.AnswerGrade;
import com.wordslearning.wl.ui.ExampleWord;
import com.wordslearning.wl.ui.ExerciseCommons;
import com.wordslearning.wl.ui.desktop.WLExercise;

public class ExampleExercise extends WLExercise {
	private JPanel examplePanel = new JPanel();
	private JLabel explanationLabel = new JLabel();
	private JButton checkButton = new JButton("Check");
	private JButton failButton = new JButton("Fail");
	private JButton forgotButton = new JButton("Forgot");
	private JButton accidentallyButton = new JButton("Accidentally");
	private ExerciseCommons commons = new ExerciseCommons();
	private JPanel checkButtonPanel;
	private JPanel assesmentButtonPanel;

	public ExampleExercise() {

		checkButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkAnswer();
			}
		});

		setLayout(new BorderLayout());
		explanationLabel.setBorder(BorderFactory
				.createEmptyBorder(20, 5, 20, 5));
		explanationLabel.setFont(explanationLabel.getFont().deriveFont(18.0F));
		add(explanationLabel, BorderLayout.NORTH);
		Box mainBox = Box.createVerticalBox();
		mainBox.setBackground(Color.WHITE);
		mainBox.setOpaque(true);
		mainBox.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		mainBox.add(Box.createVerticalGlue());
		examplePanel.setOpaque(false);
		mainBox.add(examplePanel);
		mainBox.add(Box.createVerticalGlue());
		add(mainBox);

		checkButtonPanel = new JPanel();
		checkButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		checkButtonPanel.add(checkButton);
		add(checkButtonPanel, BorderLayout.SOUTH);

		assesmentButtonPanel = new JPanel();
		assesmentButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5,
				5));
		failButton.addActionListener(new AnswerHandler(
				AnswerGrade.INCORRECT_FAIL));
		forgotButton.addActionListener(new AnswerHandler(
				AnswerGrade.INCORRECT_FORGET));
		accidentallyButton.addActionListener(new AnswerHandler(
				AnswerGrade.INCORRECT_ACCIDENTALY));
		assesmentButtonPanel.add(failButton);
		assesmentButtonPanel.add(forgotButton);
		assesmentButtonPanel.add(accidentallyButton);

	}

	private void checkAnswer() {
		if (isAnswerCorrect()) {
			finishAnswer(true);
		} else {
			showCorrectAnswer();
		}
	}

	private boolean isAnswerCorrect() {
		Component[] components = examplePanel.getComponents();
		for (Component component : components) {
			if (component instanceof WordTextField) {
				if (!((WordTextField) component).isAnsweredCorrect()) {
					return false;
				}
			}
		}
		return true;
	}

	private void showCorrectAnswer() {
		Component[] components = examplePanel.getComponents();
		for (Component component : components) {
			if (component instanceof WordTextField) {
				((WordTextField) component).setEditable(false);
				if (!((WordTextField) component).isAnsweredCorrect()) {
					((WordTextField) component).showProperAnswer();
				}
			}
		}
		remove(checkButtonPanel);
		add(assesmentButtonPanel, BorderLayout.SOUTH);
		revalidate();
		repaint();
	}

	@Override
	public String getExerciseName() {
		return "Example Exercise";
	}

	@Override
	public void showArticle(WLWord word) {
		setWord(word);
		cleanPanel();
		explanationLabel.setText("<html><body><p align=\"center\">"
				+ word.getWLArticle().getValue() + "</p></body></html>");
		List<String> examples = commons.getExamplesWithRequestedWord(word
				.getWLArticle().getRawExamples());
		if (examples.size() > 0) {
			Collections.shuffle(examples);
			String exampleToShow = examples.get(0);
			showExample(exampleToShow);
		}
	}

	private void cleanPanel() {
		remove(assesmentButtonPanel);
		add(checkButtonPanel, BorderLayout.SOUTH);
		examplePanel.removeAll();
	}

	private void showExample(String exampleToShow) {
		List<ExampleWord> parsedExample = commons.parseExample(exampleToShow);
		for (ExampleWord exWord : parsedExample) {
			if (exWord.isRequestedWord()) {
				String[] separateWords = exWord.getWord().split(" ");
				for (String separateWord : separateWords) {
					examplePanel.add(new WordTextField(separateWord));
				}
			} else {
				examplePanel.add(new JLabel(exWord.getWord()));
			}
		}
		revalidate();
		repaint();
	}

	@Override
	public boolean isApplicable(WLWord word) {
		List<String> examples = commons.getExamplesWithRequestedWord(word
				.getWLArticle().getRawExamples());
		return examples.size() > 0;
	}

	private class WordTextField extends JPanel {
		private String word;
		private JTextField wordTF;

		public WordTextField(String word) {
			this.word = word;
			wordTF = new JTextField(word.length());
			wordTF.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if (isLastTextField()) {
							checkAnswer();
						} else {
							KeyboardFocusManager manager = KeyboardFocusManager
									.getCurrentKeyboardFocusManager();
							manager.focusNextComponent();
						}
					}
					super.keyReleased(e);
				}
			});
			add(wordTF);
		}

		private boolean isLastTextField() {
			Component[] components = getParent().getComponents();
			boolean afterCurrent = false;
			boolean last = true;
			for (Component component : components) {
				if (component == this) {
					afterCurrent = true;
				} else if (afterCurrent && component instanceof WordTextField) {
					last = false;
					break;
				}
			}
			return last;
		}

		public boolean isAnsweredCorrect() {
			return word.equals(wordTF.getText());
		}

		public void showProperAnswer() {
			add(new JLabel("/" + word));
		}

		public void setEditable(boolean editable) {
			wordTF.setEditable(editable);
		}

	}

	private enum State {
		ANSWERED, NOT_ANSWERED
	}

}
