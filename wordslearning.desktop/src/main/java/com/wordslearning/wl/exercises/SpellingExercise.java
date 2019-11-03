package com.wordslearning.wl.exercises;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.model.learnprocess.LearnProcessEngine.AnswerGrade;
import com.wordslearning.wl.ui.desktop.WLExercise;

public abstract class SpellingExercise extends WLExercise {

	private static final String INPUT_PANEL = "input_panel";
	private static final String ANSWER_ASSESSMENT_PANEL = "answer_assessment";
	private JLabel correctAnswerLabel = new JLabel("", JLabel.CENTER);
	private JTextField answerTF = new JTextField(20);
	/**
	 * specifies whether the answer was already given
	 */
	private boolean answered = false;
	private JPanel bottomPanel;
	private JTextField typedAnswerTF = new JTextField(20);
	private DifficultyIndicatorAction difficultyIndicatorAction = new DifficultyIndicatorAction();
	private JToggleButton difficultyIndicator = new JToggleButton(
			difficultyIndicatorAction);
	private JButton failButton;
	private ForgetAction forgetAction = new ForgetAction();
	private JButton forgetButton = new JButton(forgetAction);

	public SpellingExercise() {
		JComponent explanationArea = createExplanationArea();

		explanationArea.setBorder(BorderFactory.createLoweredBevelBorder());
		explanationArea.setBackground(Color.WHITE);
		explanationArea.setOpaque(true);
		setLayout(new BorderLayout());
		add(explanationArea);

		bottomPanel = initBottomPanel();
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private JPanel initBottomPanel() {
		JPanel bottomPanel = new JPanel(new CardLayout());

		bottomPanel.add(createCentralizingVerticalBox(createInputPanel()),
				INPUT_PANEL);
		JPanel answerAssessmnetPanel = createAnswerAssessmentPanel();
		bottomPanel.add(createCentralizingVerticalBox(answerAssessmnetPanel),
				ANSWER_ASSESSMENT_PANEL);

		InputMap keyStrokesMap = bottomPanel
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		keyStrokesMap.put(KeyStroke.getKeyStroke("ctrl D"),
				"difficulty.indikator");
		keyStrokesMap.put(KeyStroke.getKeyStroke("ctrl F"), "forget.button");
		bottomPanel.getActionMap().put("difficulty.indikator",
				difficultyIndicatorAction);
		bottomPanel.getActionMap().put("forget.button", forgetAction);
		bottomPanel.setPreferredSize(new Dimension(bottomPanel
				.getPreferredSize().width, (int) (1.5 * bottomPanel
				.getPreferredSize().height)));
		return bottomPanel;
	}

	private Box createCentralizingVerticalBox(JPanel panel) {
		Box box = Box.createVerticalBox();
		box.add(Box.createVerticalGlue());
		box.add(panel);
		// box.add(Box.createVerticalGlue());
		return box;
	}

	private JPanel createAnswerAssessmentPanel() {
		JPanel answerAssessmnetPanel = new JPanel();
		typedAnswerTF.setEditable(false);
		typedAnswerTF.setHorizontalAlignment(JTextField.CENTER);
		answerAssessmnetPanel.add(correctAnswerLabel);
		answerAssessmnetPanel.add(typedAnswerTF);
		failButton = new JButton("Fail");
		failButton.addActionListener(new AnswerHandler(
				AnswerGrade.INCORRECT_FAIL));
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(failButton);
		buttonsPanel.add(forgetButton);
		JButton accidentallyButton = new JButton("Accidentally");
		accidentallyButton.addActionListener(new AnswerHandler(
				AnswerGrade.INCORRECT_ACCIDENTALY));
		buttonsPanel.add(accidentallyButton);
		answerAssessmnetPanel.add(buttonsPanel);
		return answerAssessmnetPanel;
	}

	private JPanel createInputPanel() {
		JPanel inputPanel = new JPanel();

		inputPanel.add(difficultyIndicator);
		inputPanel.add(answerTF);

		JButton checkButton = new JButton(new ImageIcon(
				SpellingExercise.class.getResource("/icons/spellCheck.png")));
		checkButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				performCheck();
			}
		});
		inputPanel.add(checkButton);
		answerTF.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					performCheck();
				}
			}
		});
		return inputPanel;
	}

	protected abstract JComponent createExplanationArea();

	private void performCheck() {
		if (!answered) {
			answered = true;
			if (answerTF.getText().equals(getWord().getWLArticle().getKey())) {
				finishAnswer(difficultyIndicator.isSelected() ? AnswerGrade.CORRECT_DIFFICULT
						: AnswerGrade.CORRECT_EASY);
			} else if (getWord().getWLArticle().getSynonyms() != null
					&& getWord().getWLArticle().getSynonyms()
							.contains(answerTF.getText())) {
				answered = false;
				JOptionPane.showMessageDialog(SpellingExercise.this,
						"You have answered with a synonym. Try again.");
			} else {
				char[] commonChars = getLongestCommonSubsequence(
						answerTF.getText(), getWord().getWLArticle().getKey());
				char[] correctChars = getWord().getWLArticle().getKey()
						.toCharArray();
				StringBuilder sb = new StringBuilder();
				sb.append("<html><body>");
				int i = 0;
				for (char c : correctChars) {
					if (i < commonChars.length && c == commonChars[i]) {
						sb.append(c);
						i++;
					} else {
						sb.append("<span style=\"color: red\">" + c + "</span>");
					}
				}
				sb.append("</body></html>");
				correctAnswerLabel.setText(sb.toString());
				// correctAnswerLabel.setText(getWord().getWLArticle().getKey());
				// correctAnswerLabel.setForeground(Color.RED);
				showAnswerAssesmentPanel();
				showAnswerExplanation();
			}
		}
	}

	private char[] getLongestCommonSubsequence(String str1, String str2) {
		char[] c1 = str1.toCharArray();
		char[] c2 = str2.toCharArray();
		int[][] seqLengthes = new int[c1.length + 1][c2.length + 1];
		for (int i = 0; i < c1.length + 1; i++) {
			seqLengthes[i][0] = 0;
		}
		for (int i = 0; i < c2.length + 1; i++) {
			seqLengthes[0][i] = 0;
		}
		for (int i = 1; i < c1.length + 1; i++) {
			for (int j = 1; j < c2.length + 1; j++) {
				if (c1[i - 1] == c2[j - 1]) {
					seqLengthes[i][j] = seqLengthes[i - 1][j - 1] + 1;
				} else {
					seqLengthes[i][j] = Math.max(seqLengthes[i][j - 1],
							seqLengthes[i - 1][j]);
				}
			}
		}
		List<Character> res = new ArrayList<Character>();
		int i = c1.length;
		int j = c2.length;
		while (i != 0 && j != 0) {
			if (seqLengthes[i - 1][j] == seqLengthes[i][j]) {
				i -= 1;
			} else if (seqLengthes[i][j - 1] == seqLengthes[i][j]) {
				j -= 1;
			} else {
				i -= 1;
				j -= 1;
				res.add(c1[i]);
			}
		}
		char[] charArray = new char[res.size()];
		for (int k = 0; k < charArray.length; k++) {
			charArray[k] = res.get(res.size() - k - 1);
		}
		return charArray;
	}

	private void showAnswerAssesmentPanel() {
		if (getWord().getLearnDate() == null) {
			failButton.setEnabled(false);
		} else {
			failButton.setEnabled(true);
		}
		typedAnswerTF.setText(answerTF.getText());
		((CardLayout) bottomPanel.getLayout()).show(bottomPanel,
				ANSWER_ASSESSMENT_PANEL);
		forgetButton.requestFocus();
	}

	protected abstract void showAnswerExplanation();

	@Override
	public String getExerciseName() {
		return "Spelling";
	}

	@Override
	public void showArticle(WLWord word) {
		cleanPanel();
		setWord(word);
		if (word.getLearnDate() == null) {
			difficultyIndicator.setEnabled(false);
		} else {
			difficultyIndicator.setEnabled(true);
			difficultyIndicator
					.setSelected(word.getDifficulty() != AnswerGrade.CORRECT_EASY
							.getDifficultyFactor());
		}

	}

	protected void cleanPanel() {
		answerTF.setText("");
		answered = false;
		correctAnswerLabel.setForeground(Color.BLACK);
		correctAnswerLabel.setText("**********");
		answerTF.requestFocusInWindow();
		difficultyIndicator.setSelected(false);

		((CardLayout) bottomPanel.getLayout()).show(bottomPanel, INPUT_PANEL);
	}

	private class DifficultyIndicatorAction extends AbstractAction {

		public DifficultyIndicatorAction() {
			putValue(NAME, "Difficult");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JPanel) {
				difficultyIndicator.setSelected(!difficultyIndicator
						.isSelected());
			}
		}

	}

	private class ForgetAction extends AbstractAction {

		public ForgetAction() {
			putValue(NAME, "Forgot");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			finishAnswer(AnswerGrade.INCORRECT_FORGET);
		}

	}

}
