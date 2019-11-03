package com.wordslearning.wl.model.learnprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordslearning.ve.model.vocabularies.ResultReceiver;

public class LPDataLocalFileAccessor extends LearnProcessDataAccessor {

	private String statisticFilePath;
	private String learningWordsFilePath;
	private String learnDataFilePath;

	private static final String P_LAST_LEARNT_VOC = "learn.vocabulary.last";

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void retrieveLPData(ResultReceiver<LearnProcessData> holder) {
		File statFile = new File(statisticFilePath);
		try {
			Document statisticDoc = null;
			if (statFile.exists()) {
				statisticDoc = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().parse(statFile);
			}
			LearnProcessData lpData = new LearnProcessData();
			lpData.setStatistic(statisticDoc);
			lpData.setWordsBeingLearnt(readWordsBeingLearnt());
			lpData.setLastLearntVoc(readLastLearntVoc());
			holder.setResult(lpData);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private String readLastLearntVoc() {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(learnDataFilePath));
			return props.getProperty(P_LAST_LEARNT_VOC);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Set<Integer>> readWordsBeingLearnt() {
		File lwFile = new File(learningWordsFilePath);
		if (lwFile.exists())
			try {
				return mapper.readValue(lwFile,
						new TypeReference<Map<String, Set<Integer>>>() {
						});
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return new HashMap<String, Set<Integer>>();
	}

	public void setStatisticFilePath(String statisticFilePath) {
		this.statisticFilePath = statisticFilePath;
	}

	@Override
	public void storeLPData(LearnProcessData data) {
		try {
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();

			transformer.transform(new DOMSource(data.getStatistic()),
					new StreamResult(new File(statisticFilePath)));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		storeWordsBeingLearnt(data.getWordsBeingLearnt());

		storeLastLearntVoc(data.getLastLearntVoc());

	}

	private void storeLastLearntVoc(String lastLearntVoc) {
		try {
			Properties props=new Properties();
			props.put(P_LAST_LEARNT_VOC, lastLearntVoc);
			props.store(new FileOutputStream(learnDataFilePath), "Learn Data");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getLearningWordsFilePath() {
		return learningWordsFilePath;
	}

	public void setLearningWordsFilePath(String learningWordsFilePath) {
		this.learningWordsFilePath = learningWordsFilePath;
	}

	private void storeWordsBeingLearnt(Map<String, Set<Integer>> wordsBeingLernt) {
		Map<String, Set<Integer>> res = readWordsBeingLearnt();
		try {
			res.putAll(wordsBeingLernt);
			mapper.writeValue(new File(learningWordsFilePath), res);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStatisticFilePath() {
		return statisticFilePath;
	}

	public String getLearnDataFilePath() {
		return learnDataFilePath;
	}

	public void setLearnDataFilePath(String learnDataFilePath) {
		this.learnDataFilePath = learnDataFilePath;
	}

}
