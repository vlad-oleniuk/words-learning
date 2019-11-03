package com.wordslearning.wl.exercises;

import javax.swing.JButton;
import javax.swing.JLabel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


import com.wordslearning.ve.model.article.WLArticle;
import com.wordslearning.wl.exercises.WordConstructor;
import com.wordslearning.wl.model.WLWord;

public class WordConstructorTest {

	private WordConstructor wc;

	@Before
	public void init() {
		wc = new WordConstructor();
//		WLWord word=new WLWord();
//		WLArticle article=new WLArticle();
//		article.setKey("word_key");
//		article.setValue("word_value");
//		wc.showArticle(word);
	}


	@Test
	public void testActivateLetterButton() {
		JButton button = new JButton("A");
		button.setEnabled(false);
		wc.getLettersPanel().add(button);
		wc.changeLetterButtonState("A", true);
		Assert.assertEquals(1, wc.getLettersPanel().getComponentCount());
		Assert.assertTrue(((JButton) wc.getLettersPanel().getComponent(0))
				.isEnabled());
	}
	
	@Test
	public void testShowArticle() {

		WLWord word = new WLWord();
		WLArticle article = new WLArticle();
		article.setKey("key");
		article.setValue("value");
		word.setWlArticle(article);
		wc.showArticle(word);

		Assert.assertEquals(3, wc.getLettersPanel().getComponents().length);
		Assert.assertEquals("<html><body><p>value</p></body></html>", wc.getTransLabel().getText());
	}
}
