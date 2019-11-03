package com.wordslearning.wl.model;

import java.util.Date;

import com.wordslearning.ve.model.article.WLArticle;


public class WLWord {
	private WLArticle wlArticle;
	private Date learnDate;
	private Date lastRepeatDate;
	private int currentPoints;
	private int lastRepInterval;
	private double difficulty;

	public WLArticle getWLArticle() {
		return wlArticle;
	}

	public void setWlArticle(WLArticle wlArticle) {
		this.wlArticle = wlArticle;
	}

	public Date getLearnDate() {
		return learnDate;
	}

	public void setLearnDate(Date learnDate) {
		this.learnDate = learnDate;
	}

	public Date getLastRepeatDate() {
		return lastRepeatDate;
	}

	public void setLastRepeatDate(Date lastRepeatDate) {
		this.lastRepeatDate = lastRepeatDate;
	}

	public int getCurrentPoints() {
		return currentPoints;
	}

	public void setCurrentPoints(int currentPoints) {
		this.currentPoints = currentPoints;
	}

	public void setLastRepeatInterval(int lastRepInterval) {
		this.lastRepInterval = lastRepInterval;
	}

	public int getLastRepeatInterval() {
		return lastRepInterval;
	}

	public double getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(double difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public int hashCode() {
		if (wlArticle != null)
			return generateId(wlArticle);
		else
			return super.hashCode();
	}

	public int getId() {
		return hashCode();
	}
	
	public static int generateId(WLArticle article){
		return (article.getKey() + article.getValue()).hashCode();
	}

}
