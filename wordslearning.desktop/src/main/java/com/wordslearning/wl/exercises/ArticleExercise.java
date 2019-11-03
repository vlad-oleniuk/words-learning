package com.wordslearning.wl.exercises;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.wordslearning.ve.model.article.Language;
import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.ui.desktop.WLExercise;


public class ArticleExercise extends WLExercise {

	private JLabel wordLabel = new JLabel();

	private Map<Language, String[]> languageArticles = new HashMap<Language, String[]>();

	private JPanel panel;
	private State state = State.NOT_ANSWERED;
	private String correctAnswer;
	private JButton checkNextButton;

	{
		languageArticles
				.put(Language.GER, new String[] { "der", "die", "das" });
		languageArticles.put(Language.ESP, new String[] { "el", "la" });
	}

	public ArticleExercise() {
		setLayout(new BorderLayout());

		wordLabel.setHorizontalAlignment(JLabel.CENTER);
		wordLabel.setFont(wordLabel.getFont().deriveFont(25.0F));

		add(wordLabel, BorderLayout.NORTH);

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setOpaque(true);
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		CardLayout layout = new CardLayout();
		panel.setLayout(layout);

		for (Language language : languageArticles.keySet()) {
			panel.add(new ArticlesPanel(language), language.toString());
		}
		add(panel);

		JPanel buttonPanel = new JPanel();
		checkNextButton = new JButton();
		checkNextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArticlesPanel articlesPanel = getCurrentArticlesPanel();
				if (articlesPanel.getAnswer() == null)
					return;
				if (state == State.ANSWERED) {
					finishAnswer(false);
				} else {
					state = State.ANSWERED;
					if (getWord().getWLArticle().getKey()
							.startsWith(articlesPanel.getAnswer())) {
						finishAnswer(true);
					} else {
						articlesPanel.setEditable(false);
						articlesPanel.setAnswer(correctAnswer);
						checkNextButton.setText("Show Next");

					}
				}
			}
		});
		buttonPanel.add(checkNextButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private ArticlesPanel getCurrentArticlesPanel() {
		for (Component component : panel.getComponents()) {
			if (component.isVisible())
				return (ArticlesPanel) component;
		}
		return null;
	}

	@Override
	public String getExerciseName() {
		return "Articles Exercise";
	}

	@Override
	public void showArticle(WLWord word) {
		setWord(word);
		checkNextButton.setText("Check Answer");
		state = State.NOT_ANSWERED;
		correctAnswer = word.getWLArticle().getKey()
				.substring(0, word.getWLArticle().getKey().indexOf(" "));
		wordLabel.setText(word.getWLArticle().getKey()
				.substring(correctAnswer.length() + 1));
		((CardLayout) panel.getLayout()).show(panel, getCurrentLanguage()
				.toString());
		ArticlesPanel currentArticlePanel = getCurrentArticlesPanel();
		currentArticlePanel.setEditable(true);
		currentArticlePanel.deselect();
	}

	@Override
	public boolean isApplicable(WLWord word) {
		String key = word.getWLArticle().getKey();
		String[] articles = languageArticles.get(getCurrentLanguage());
		if (articles != null) {
			for (String article : articles) {
				if (key.startsWith(article + " "))
					return true;
			}
		}
		return false;
	}

	private enum State {
		ANSWERED, NOT_ANSWERED
	}

	private class ArticlesPanel extends Box {

		private ButtonGroup group;

		public ArticlesPanel(Language lang) {
			super(BoxLayout.X_AXIS);

			Box articlesPanel = Box.createVerticalBox();
			articlesPanel.add(Box.createVerticalGlue());
			group = new ButtonGroup();
			String[] articles = languageArticles.get(lang);
			for (String article : articles) {
				JRadioButton radioButton = new JRadioButton(article);
				radioButton.setOpaque(false);
				radioButton.setFont(wordLabel.getFont().deriveFont(20.0F));
				group.add(radioButton);
				articlesPanel.add(radioButton);
			}
			articlesPanel.add(Box.createVerticalGlue());

			add(Box.createHorizontalGlue());
			add(articlesPanel);
			add(Box.createHorizontalGlue());
		}

		public void setAnswer(String answer) {
			Enumeration<AbstractButton> buttons = group.getElements();
			while (buttons.hasMoreElements()) {
				AbstractButton button = buttons.nextElement();
				if (button.getText().equals(answer)) {
					button.setSelected(true);
					break;
				}
			}
		}

		public void deselect() {
			Enumeration<AbstractButton> buttons = group.getElements();
			while (buttons.hasMoreElements()) {
				AbstractButton button = buttons.nextElement();
				button.setSelected(false);
			}
		}

		public void setEditable(boolean b) {
			Enumeration<AbstractButton> buttons = group.getElements();
			while (buttons.hasMoreElements()) {
				AbstractButton button = buttons.nextElement();
				button.setEnabled(b);
			}
		}

		public String getAnswer() {
			Enumeration<AbstractButton> buttons = group.getElements();
			while (buttons.hasMoreElements()) {
				AbstractButton button = buttons.nextElement();
				if (button.isSelected()) {
					return button.getText();
				}
			}
			return null;
		}

	}

}
