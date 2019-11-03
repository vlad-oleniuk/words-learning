package com.wordslearning.wl.exercises;

import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.ui.WordsLearningUI;


public class TranslationBasedSpelling extends SpellingExercise {

	private JLabel explanationArea;

	@Override
	public void showArticle(WLWord word) {
		super.showArticle(word);
		explanationArea.setFont(explanationArea.getFont().deriveFont(25.0F));
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("<p align=\"center\">");

		sb.append(word.getWLArticle().getValue());

		sb.append("</p>");

		sb.append("</html></body>");

		explanationArea.setText(sb.toString());

	}

	@Override
	public boolean isApplicable(WLWord word) {
		return true;
	}

	@Override
	protected void showAnswerExplanation() {
		StringBuilder sb = new StringBuilder();
		explanationArea.setFont(explanationArea.getFont().deriveFont(17.0F));
		sb.append("<html><body>");
		sb.append("<p align=\"center\">");

		sb.append(getWord().getWLArticle().getValue());

		sb.append("</p>");

		sb.append("<hr/>");

		Set<String> examples = getWord().getWLArticle().getExamples();

		for (String example : examples) {
			sb.append("<p>");
			sb.append(example);
			sb.append("</p>");
		}

		sb.append("</html></body>");
		explanationArea.setText(sb.toString());
	}

	@Override
	protected JComponent createExplanationArea() {
		explanationArea = new JLabel("", JLabel.CENTER);
		return explanationArea;
	}
}
