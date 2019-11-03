package com.wordslearning.wl.model.learnprocess;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.wordslearning.wl.model.WLWord;

public class StatisticSerializer {

	protected static final String NODE_NAME_ROOT = "learn_statistic";
	protected static final String NODE_NAME_ARTICLE_STATISTIC = "ar_stat";
	protected static final String NODE_NAME_ARTICLE_ID = "ar_id";
	protected static final String NODE_NAME_LEARN_DATE = "learn_date";
	protected static final String NODE_NAME_LAST_REPEAT_DATE = "last_repeat_date";
	protected static final String NODE_NAME_LAST_REPEAT_INTERVAL = "last_repeat_interv";
	protected static final String NODE_NAME_DIFFICULTY = "difficulty";
	protected static final String NODE_NAME_CURRENT_POINTS = "cur_points";

	protected SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	public Document synchronizeStatistic(Document doc,
			Map<Integer, WLWord> words) {
		NodeList existingStatistic = doc
				.getElementsByTagName(NODE_NAME_ARTICLE_STATISTIC);

		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document newStatistic = docBuilder.newDocument();
			Element newStatisticRootEl = newStatistic
					.createElement(NODE_NAME_ROOT);
			newStatistic.appendChild(newStatisticRootEl);

			// add statistic, which was not modified
			for (int i = 0; i < existingStatistic.getLength(); i++) {
				Element existingStatEl = (Element) existingStatistic.item(i);
				Integer id = Integer.parseInt(existingStatEl
						.getElementsByTagName(NODE_NAME_ARTICLE_ID).item(0)
						.getFirstChild().getNodeValue().trim());
				if (!words.containsKey(id)) {
					Node newStatEl = newStatistic.importNode(existingStatEl,
							true);
					newStatisticRootEl.appendChild(newStatEl);
				}

			}

			for (Integer wordId : words.keySet()) {
				newStatisticRootEl.appendChild(serializeStatisticNode(
						words.get(wordId), newStatistic));
			}

			return newStatistic;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Element serializeStatisticNode(WLWord wlWord, Document parent) {

		Element articleIdNode = parent.createElement(NODE_NAME_ARTICLE_ID);
		articleIdNode.appendChild(parent.createTextNode(String.valueOf(wlWord
				.getId())));

		Element learnDateNode = parent.createElement(NODE_NAME_LEARN_DATE);
		if (wlWord.getLearnDate() != null)
			learnDateNode.appendChild(parent.createTextNode(sdf.format(wlWord
					.getLearnDate())));

		Element lastRepeatDateNode = parent
				.createElement(NODE_NAME_LAST_REPEAT_DATE);
		if (wlWord.getLastRepeatDate() != null)
			lastRepeatDateNode.appendChild(parent.createTextNode(sdf
					.format(wlWord.getLastRepeatDate())));

		Element lastRepeatIntervalNode = parent
				.createElement(NODE_NAME_LAST_REPEAT_INTERVAL);
		lastRepeatIntervalNode.appendChild(parent.createTextNode(String
				.valueOf(wlWord.getLastRepeatInterval())));

		Element currentPointsNode = parent
				.createElement(NODE_NAME_CURRENT_POINTS);
		currentPointsNode.appendChild(parent.createTextNode(String
				.valueOf(wlWord.getCurrentPoints())));

		Element difficultyNode = parent.createElement(NODE_NAME_DIFFICULTY);
		difficultyNode.appendChild(parent.createTextNode(String.valueOf(wlWord
				.getDifficulty())));

		Element arStatEl = parent.createElement(NODE_NAME_ARTICLE_STATISTIC);
		arStatEl.appendChild(articleIdNode);
		arStatEl.appendChild(learnDateNode);
		arStatEl.appendChild(lastRepeatDateNode);
		arStatEl.appendChild(lastRepeatIntervalNode);
		arStatEl.appendChild(currentPointsNode);
		arStatEl.appendChild(difficultyNode);
		return arStatEl;
	}

	public LearnStatistic extractRelStatistic(Document statisticDoc,
			List<Integer> repeatIntervals) {
		NodeList statisticEls = statisticDoc
				.getElementsByTagName(NODE_NAME_ARTICLE_STATISTIC);
		return extractRelStatistic(statisticEls, repeatIntervals);
	}

	private LearnStatistic extractRelStatistic(NodeList statisticEls,
			List<Integer> repeatIntervals) {

		LearnStatistic response = new LearnStatistic();

		for (int j = 0; j < statisticEls.getLength(); j++) {
			Element statEl = (Element) statisticEls.item(j);
			int articleId = 0;
			Date learnDate = null;
			Date lastRepeatDate = null;
			int currentPoints = 0;
			int lastRepeatInterval = 0;
			double difficulty = 0.0;

			GregorianCalendar today = new GregorianCalendar();
			today.clear(Calendar.MILLISECOND);
			today.clear(Calendar.MINUTE);
			today.clear(Calendar.HOUR_OF_DAY);
			NodeList statElChildren = statEl.getChildNodes();
			for (int i = 0; i < statElChildren.getLength(); i++) {
				Node statElChild = statElChildren.item(i);
				if (NODE_NAME_ARTICLE_ID.equals(statElChild.getNodeName()))
					articleId = Integer.parseInt(statElChild.getFirstChild()
							.getNodeValue().trim());
				else if (NODE_NAME_LEARN_DATE.equals(statElChild.getNodeName())) {
					if (statElChild.getFirstChild() != null) {
						String dateStr = statElChild.getFirstChild()
								.getNodeValue().trim();
						try {
							learnDate = sdf.parse(dateStr);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				} else if (NODE_NAME_CURRENT_POINTS.equals(statElChild
						.getNodeName())) {
					String valueStr = statElChild.getFirstChild()
							.getNodeValue().trim();
					if (!valueStr.equals("")) {
						currentPoints = Integer.parseInt(valueStr);
					}
				} else if (NODE_NAME_LAST_REPEAT_INTERVAL.equals(statElChild
						.getNodeName())) {
					String valueStr = statElChild.getFirstChild()
							.getNodeValue().trim();
					if (!valueStr.equals("")) {
						lastRepeatInterval = Integer.parseInt(valueStr);
					}
				} else if (NODE_NAME_DIFFICULTY.equals(statElChild
						.getNodeName())) {
					String valueStr = statElChild.getFirstChild()
							.getNodeValue().trim();
					if (!valueStr.equals("")) {
						difficulty = Double.parseDouble(valueStr);
					}
				} else if (NODE_NAME_LAST_REPEAT_DATE.equals(statElChild
						.getNodeName())) {
					if (statElChild.getFirstChild() != null) {
						String dateStr = statElChild.getFirstChild()
								.getNodeValue().trim();
						try {
							lastRepeatDate = sdf.parse(dateStr);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
			if (learnDate == null) {
				response.addWordForLearning(articleId, currentPoints);
			} else {

				GregorianCalendar nextRepeatDate = new GregorianCalendar();
				if (lastRepeatDate != null) {
					nextRepeatDate.setTime(lastRepeatDate);
				} else {
					nextRepeatDate.setTime(learnDate);
				}
				nextRepeatDate.add(
						Calendar.DAY_OF_YEAR,
						getRepeatMargin(lastRepeatInterval, repeatIntervals,
								difficulty));

				if (today.after(nextRepeatDate)) {
					response.addWordForRepeating(articleId, learnDate,
							lastRepeatDate, lastRepeatInterval, currentPoints,
							difficulty);
				} else
					response.addToIgnore(articleId);
			}
		}

		return response;
	}

	private int getRepeatMargin(int lastInterv, List<Integer> repeatIntervals,
			double difficulty) {
		for (Integer interval : repeatIntervals) {
			if (interval > lastInterv)
				return (int) ((interval - lastInterv) * (1 - difficulty));
		}
		return 36500;
	}

}