package com.wordslearning.ve.serializers;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.wordslearning.ve.model.article.AbstractLocalWordsStorage;
import com.wordslearning.ve.model.article.Language;
import com.wordslearning.ve.model.article.WLArticle;
import com.wordslearning.ve.model.words_storages.XDXFDictionary;


public class XDXFParser implements AbstractVocabularyParser{

	private XDXFDictionary parseXDXFDictionaryFile(File file) {
		Document doc = parseDictFile(file);
		XDXFDictionary dictionary = parseDictionary(doc);
		return dictionary;
	}

	private XDXFDictionary parseDictionary(Document doc) {
		XDXFDictionary dict = new XDXFDictionary();
		Element xdxfEl = doc.getDocumentElement();
		dict.setLangFrom(Language.valueOf(xdxfEl.getAttribute("lang_from")));

		NodeList childNodes = xdxfEl.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i) instanceof Element) {
				Element child = (Element) childNodes.item(i);
				if (child.getNodeName().equals("full_name"))
					dict.setName(child.getTextContent());
				else if (child.getNodeName().equals("description"))
					dict.setDescription(child.getTextContent());
				else if (child.getNodeName().equals("ar"))
					dict.addArticle(parseArticle(child));
			}
		}
		return dict;
	}

	private WLArticle parseArticle(Element arEl) {
		WLArticle ar = new WLArticle();
		NodeList childNodes = arEl.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i) instanceof Element) {
				Element child = (Element) childNodes.item(i);
				if (child.getNodeName().equals("k")) {
					ar.setKey(child.getTextContent());
				}
			} else
				ar.setValue(childNodes.item(i).getTextContent().trim());
		}
		return ar;
	}

	private Document parseDictFile(File dictFile) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			return docBuilder.parse(dictFile);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AbstractLocalWordsStorage parseVocabularyFile(File file) {
		return parseXDXFDictionaryFile(file);
	}
}
