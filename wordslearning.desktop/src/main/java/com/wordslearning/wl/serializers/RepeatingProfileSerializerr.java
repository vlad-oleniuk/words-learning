package com.wordslearning.wl.serializers;

import java.io.File;
import java.util.List;

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
import org.w3c.dom.Element;

import com.wordslearning.wl.model.settings.RepeatingProfile;
import com.wordslearning.wl.model.settings.RepeatingProfileSerializer;

public class RepeatingProfileSerializerr extends RepeatingProfileSerializer {

	@Override
	public void serializeRepeatingProfiles(File file,
			List<RepeatingProfile> profiles) {
		try {
			Document document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element rootElement = document.createElement(TAG_ROOT_ELEMENT);
			document.appendChild(rootElement);
			for (RepeatingProfile profile : profiles) {
				Element profileEl = document.createElement(TAG_PROFILE_ELEMENT);
				rootElement.appendChild(profileEl);
				Element profileIdEl = document
						.createElement(TAG_PROFILE_ID_ELEMENT);
				profileEl.appendChild(profileIdEl);
				profileIdEl
						.appendChild(document.createTextNode(profile.getId()));
				List<String> exercises = profile.getExercises();
				for (int i = 0; i < exercises.size(); i++) {
					Element exerciseEl = document
							.createElement(TAG_PROFILE_EXERCISE);
					profileEl.appendChild(exerciseEl);
					Element exIdEl = document
							.createElement(TAG_EXERCISE_ID_ELEMENT);
					exerciseEl.appendChild(exIdEl);
					exIdEl.appendChild(document.createTextNode(exercises.get(i)));
					int[] exPoints = profile.getExercisePoints(i);
					Element exSucPoints = document
							.createElement(TAG_EX_SUC_POINTS);
					exerciseEl.appendChild(exSucPoints);
					exSucPoints.appendChild(document.createTextNode(String
							.valueOf(exPoints[0])));

					Element exPosPoints = document
							.createElement(TAG_EX_POS_POINTS);
					exerciseEl.appendChild(exPosPoints);
					exPosPoints.appendChild(document.createTextNode(String
							.valueOf(exPoints[1])));

					Element exNegPoints = document
							.createElement(TAG_EX_NEG_POINTS);
					exerciseEl.appendChild(exNegPoints);
					exNegPoints.appendChild(document.createTextNode(String
							.valueOf(exPoints[2])));
				}
			}
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(
					file));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}