package com.wordslearning.wl.model.settings;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public abstract class WLProfileSerializer {

	protected final static String TAG_LEARN = "learn";
	protected final static String TAG_REPEAT = "repeat";
	protected final static String TAG_PROFILE_ID = "rpf_id";
	protected final static String TAG_PROFILE_INTERVAL = "interval";
	protected final static String TAG_ROOT = "wl_profile";
	
	public WLProfile parseWLProfile(File file, List<RepeatingProfile> profiles) {
		Map<String, RepeatingProfile> profilesMap = new HashMap<String, RepeatingProfile>();
		for (RepeatingProfile repeatingProfile : profiles) {
			profilesMap.put(repeatingProfile.getId(), repeatingProfile);
		}

		try {
			WLProfile profile = new WLProfile();
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(file);
			Element rootEl = doc.getDocumentElement();
			NodeList profileChildNodes = rootEl.getChildNodes();
			for (int i = 0; i < profileChildNodes.getLength(); i++) {
				Node profileChild = profileChildNodes.item(i);
				if (TAG_LEARN.equals(profileChild.getNodeName())) {
					Node idElem = ((Element) profileChild)
							.getElementsByTagName(TAG_PROFILE_ID).item(0);
					profile.setLearnProfile(profilesMap.get(idElem
							.getFirstChild().getNodeValue().trim()));
				} else if (TAG_REPEAT.equals(profileChild.getNodeName())) {
					NodeList repeatProfileChildNodes = profileChild
							.getChildNodes();
					int interval = 0;
					String rpfId = null;
					for (int j = 0; j < repeatProfileChildNodes.getLength(); j++) {
						Node repeatProfileChild = repeatProfileChildNodes
								.item(j);
						if (TAG_PROFILE_INTERVAL.equals(repeatProfileChild
								.getNodeName())) {
							interval = Integer.parseInt(repeatProfileChild
									.getFirstChild().getNodeValue().trim());
						} else if (TAG_PROFILE_ID.equals(repeatProfileChild
								.getNodeName())) {
							rpfId = repeatProfileChild.getFirstChild()
									.getNodeValue().trim();

						}
					}
					profile.putRepeatProfile(interval, profilesMap.get(rpfId));
				}
			}
			return profile;
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

	public abstract void serializeWLProfile(File file, WLProfile profile);

}