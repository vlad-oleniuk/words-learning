package com.wordslearning.wl.model.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public abstract class RepeatingProfileSerializer {

	protected static final String TAG_ROOT_ELEMENT = "profiles";
	protected static final String TAG_PROFILE_ELEMENT = "rpf";
	protected static final String TAG_PROFILE_ID_ELEMENT = "id";
	protected static final String TAG_PROFILE_EXERCISE = "exercise";
	protected static final String TAG_EXERCISE_ID_ELEMENT = "ex_id";
	protected static final String TAG_EX_SUC_POINTS = "suc_points";
	protected static final String TAG_EX_POS_POINTS = "pos_points";
	protected static final String TAG_EX_NEG_POINTS = "neg_points";
	
	public abstract void serializeRepeatingProfiles(File file,
			List<RepeatingProfile> profiles);

	public List<RepeatingProfile> deserializeRepeatingProfiles(File file) {
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(file);
			NodeList profileNodeList = doc.getDocumentElement()
					.getElementsByTagName(TAG_PROFILE_ELEMENT);
			List<RepeatingProfile> profiles = new ArrayList<RepeatingProfile>();
			for (int i = 0; i < profileNodeList.getLength(); i++) {
				Element profileElement = (Element) profileNodeList.item(i);
				NodeList profileChildElems = profileElement.getChildNodes();
				RepeatingProfile profile = new RepeatingProfile();
				profiles.add(profile);
				for (int j = 0; j < profileChildElems.getLength(); j++) {
					Node profileChild = profileChildElems.item(j);
					if (TAG_PROFILE_ID_ELEMENT.equals(profileChild
							.getNodeName())) {
						profile.setId(profileChild.getFirstChild()
								.getNodeValue().trim());
					} else if (TAG_PROFILE_EXERCISE.equals(profileChild
							.getNodeName())) {
						String exId = null;
						int sucPoints = 0;
						int negPoints = 0;
						int posPoints = 0;
						NodeList exerciseChildNodes = profileChild
								.getChildNodes();
						for (int k = 0; k < exerciseChildNodes.getLength(); k++) {
							Node exerciseChild = exerciseChildNodes.item(k);
							if (TAG_EXERCISE_ID_ELEMENT.equals(exerciseChild
									.getNodeName())) {
								exId = exerciseChild.getFirstChild()
										.getNodeValue().trim();
							} else if (TAG_EX_SUC_POINTS.equals(exerciseChild
									.getNodeName())) {
								sucPoints = Integer.parseInt(exerciseChild
										.getFirstChild().getNodeValue());
							} else if (TAG_EX_POS_POINTS.equals(exerciseChild
									.getNodeName())) {
								posPoints = Integer.parseInt(exerciseChild
										.getFirstChild().getNodeValue());
							} else if (TAG_EX_NEG_POINTS.equals(exerciseChild
									.getNodeName())) {
								negPoints = Integer.parseInt(exerciseChild
										.getFirstChild().getNodeValue());
							}
						}
						profile.putExercise(exId, new int[] { sucPoints,
								posPoints, negPoints });
					}
				}

			}
			return profiles;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
