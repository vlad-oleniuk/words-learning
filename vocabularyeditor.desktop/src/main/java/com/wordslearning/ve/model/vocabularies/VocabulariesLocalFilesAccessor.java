package com.wordslearning.ve.model.vocabularies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordslearning.ve.model.article.WLVocabulary;

public class VocabulariesLocalFilesAccessor extends VocabulariesAccessor {
	private String vocabulariesDir;

	public void setVocabulariesDir(String vocabulariesDir) {
		this.vocabulariesDir = vocabulariesDir;
	}

	public String getVocabulariesDir() {
		return this.vocabulariesDir;
	}

	private ObjectMapper mapper = new ObjectMapper();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wordslearning.ve.model.VocabulariesAccessor#saveVocabularies(java
	 * .util.List)
	 */
	@Override
	public void saveVocabularies(List<WLVocabulary> vocs) {
		removeSuperfluousVocs(vocs);
		for (WLVocabulary wlVocabulary : vocs) {
			String vocFilePath = vocabulariesDir + "/"
					+ getFileNameForWLVocabulary(wlVocabulary.getName());
			File jsonVocFile = new File(vocFilePath);
			try {
				mapper.writeValue(jsonVocFile, wlVocabulary);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void removeSuperfluousVocs(List<WLVocabulary> vocs) {
		Set<String> vocFileNames = new HashSet<String>();
		for (WLVocabulary wlVocabulary : vocs) {
			vocFileNames
					.add(getFileNameForWLVocabulary(wlVocabulary.getName()));
		}
		File[] files = new File(vocabulariesDir).listFiles();
		if (files != null)
			for (File file : files) {
				if (!vocFileNames.contains(file.getName())) {
					file.delete();
				}
			}
	}

	private String getFileNameForWLVocabulary(String vocabName) {
		return vocabName.toLowerCase() + ".wldf.json";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wordslearning.ve.model.VocabulariesAccessor#readWLVocabularies()
	 */
	@Override
	public void retrieveVocabularies(ResultReceiver<List<WLVocabulary>> vocsReceiver) {
		List<WLVocabulary> vocabularies;
		File[] vocFiles = new File(vocabulariesDir).listFiles();
		if (vocFiles != null) {
			vocabularies = new ArrayList<WLVocabulary>();
			for (File vocFile : vocFiles) {
				try {
					WLVocabulary vocabulary = mapper.readValue(vocFile,
							WLVocabulary.class);
					vocabularies.add(vocabulary);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			vocabularies = Collections.<WLVocabulary> emptyList();
		}
		vocsReceiver.setResult(vocabularies);
	}
}
