package com.wordslearning.wl.exercises;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.ui.WordsLearningUI;
import com.wordslearning.wl.ui.desktop.WLExercise;


public class Card extends WLExercise {
	
	private enum State {
		KEY_SHOWN, ANSWERED_TRANSLATION_SHOWN, NOT_ANSWERED_TRANSLATION_SHOWN
	}

	private State currentState;
	private final String WORD_SIDE = "word_side";
	private final String TRANS_SIDE = "trans_side";

	private JButton knowButton = new JButton("I know");
	private JButton doNotKnowButton = new JButton("I don't know");
	private JButton checkButton;
	private JLabel keyLabel;
	private JLabel transLabel;
	private JPanel cardPanel;
	private boolean correctAnswer;
	private JPanel examplesDisplayer;
	private CardLayout examplesCardLayout;

	public Card() {
		setLayout(new BorderLayout());
		cardPanel = createCardPanel();
		add(cardPanel);
		add(createButtonsPanel(), BorderLayout.LINE_END);
	}

	private void setState(State state) {
		currentState = state;
		switch (currentState) {
		case KEY_SHOWN:
			checkButton.setEnabled(true);
			knowButton.setEnabled(true);
			doNotKnowButton.setEnabled(true);
			((CardLayout) cardPanel.getLayout()).show(cardPanel, WORD_SIDE);
			break;
		case ANSWERED_TRANSLATION_SHOWN:
			checkButton.setEnabled(true);
			knowButton.setEnabled(false);
			doNotKnowButton.setEnabled(false);
			((CardLayout) cardPanel.getLayout()).show(cardPanel, TRANS_SIDE);
			break;
		case NOT_ANSWERED_TRANSLATION_SHOWN:
			checkButton.setEnabled(false);
			knowButton.setEnabled(true);
			doNotKnowButton.setEnabled(true);
			((CardLayout) cardPanel.getLayout()).show(cardPanel, TRANS_SIDE);
			break;
		}
	}

	private JComponent createButtonsPanel() {

		knowButton = new JButton(new ImageIcon(
				Card.class.getResource("/icons/knowIcon.png")));

		knowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				correctAnswer = true;
				if (currentState == State.KEY_SHOWN)
					setState(State.ANSWERED_TRANSLATION_SHOWN);
				else if (currentState == State.NOT_ANSWERED_TRANSLATION_SHOWN)
					finishAnswer(correctAnswer);
			}
		});

		doNotKnowButton = new JButton(new ImageIcon(
				Card.class.getResource("/icons/dontKnowIcon.png")));

		doNotKnowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				correctAnswer = false;
				if (currentState == State.KEY_SHOWN)
					setState(State.ANSWERED_TRANSLATION_SHOWN);
				else if (currentState == State.NOT_ANSWERED_TRANSLATION_SHOWN)
					finishAnswer(correctAnswer);
			}
		});

		checkButton = new JButton(new ImageIcon(
				Card.class.getResource("/icons/checkIcon.png")));

		checkButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentState == State.KEY_SHOWN)
					setState(State.NOT_ANSWERED_TRANSLATION_SHOWN);
				else if (currentState == State.ANSWERED_TRANSLATION_SHOWN)
					finishAnswer(correctAnswer);
			}
		});

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(knowButton, GroupLayout.DEFAULT_SIZE,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(doNotKnowButton, GroupLayout.DEFAULT_SIZE,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(checkButton, GroupLayout.DEFAULT_SIZE,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(knowButton).addComponent(doNotKnowButton)
				.addComponent(checkButton));
		return panel;
	}

	private JPanel createCardPanel() {
		transLabel = new JLabel("", JLabel.CENTER);
		transLabel.setBackground(Color.WHITE);
		transLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		transLabel.setOpaque(true);
		transLabel.setFont(transLabel.getFont().deriveFont(25.0F));
		CardLayout cardLayout = new CardLayout();
		JPanel panel = new JPanel(cardLayout);
		panel.add(createKeyPanel(), WORD_SIDE);
		panel.add(transLabel, TRANS_SIDE);
		return panel;
	}

	private JPanel createKeyPanel() {
		JPanel keyPanel = new JPanel(new GridLayout(2, 1));
		keyLabel = new JLabel("", JLabel.CENTER);
		keyLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		keyLabel.setBackground(Color.WHITE);
		keyLabel.setOpaque(true);
		keyLabel.setFont(keyLabel.getFont().deriveFont(40.0F));

		JPanel examplesPanel = new JPanel(new BorderLayout());

		JButton prevExampleButton = new JButton(new ImageIcon(
				Card.class.getResource("/icons/prevExample.png")));
		prevExampleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				examplesCardLayout.next(examplesDisplayer);
			}
		});

		JButton nextExampleButton = new JButton(new ImageIcon(
				Card.class.getResource("/icons/nextExample.png")));
		nextExampleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				examplesCardLayout.previous(examplesDisplayer);
			}
		});

		examplesPanel.add(prevExampleButton, BorderLayout.LINE_START);
		examplesPanel.add(nextExampleButton, BorderLayout.LINE_END);
		examplesDisplayer = new JPanel();
		examplesCardLayout = new CardLayout();
		examplesDisplayer.setLayout(examplesCardLayout);
		examplesPanel.add(examplesDisplayer);

		keyPanel.add(keyLabel);
		keyPanel.add(examplesPanel);

		return keyPanel;
	}

	@Override
	public String getExerciseName() {
		return "Card Exercise";
	}

	@Override
	public void showArticle(WLWord word) {
		setWord(word);
		keyLabel.setText("<html><body><p align=\"center\">"
				+ word.getWLArticle().getKey() + "</p></body></html>");
		transLabel.setText("<html><body><p align=\"center\">"
				+ word.getWLArticle().getValue() + "</p></body></html>");
		examplesDisplayer.removeAll();
		Set<String> examples = word.getWLArticle().getExamples();
		if (examples != null)
			for (String example : examples) {
				JLabel exampleLabel = createExampleLabel();
				exampleLabel.setText("<html><body><p align=\"center\">"
						+ example + "</p></body></html>");
				examplesDisplayer.add(exampleLabel, "ex" + example.hashCode());
			}
		setState(State.KEY_SHOWN);
	}

	private JLabel createExampleLabel() {
		JLabel exampleLabel = new JLabel("", JLabel.CENTER);
		exampleLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		exampleLabel.setBackground(Color.WHITE);
		exampleLabel.setOpaque(true);
		exampleLabel.setFont(exampleLabel.getFont().deriveFont(28.0F));
		return exampleLabel;
	}

	@Override
	public boolean isApplicable(WLWord word) {
		return true;
	}

}
