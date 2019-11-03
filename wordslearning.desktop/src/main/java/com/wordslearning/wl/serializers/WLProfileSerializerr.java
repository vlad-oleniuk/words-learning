package com.wordslearning.wl.serializers;

import java.io.File;
import java.util.Map;

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
import com.wordslearning.wl.model.settings.WLProfile;
import com.wordslearning.wl.model.settings.WLProfileSerializer;


public class WLProfileSerializerr extends WLProfileSerializer {
	

	@Override
	public void serializeWLProfile(File file, WLProfile profile) {
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			Element root = doc.createElement(TAG_ROOT);
			doc.appendChild(root);
			Element learnProfileEl = doc.createElement(TAG_LEARN);
			root.appendChild(learnProfileEl);
			Element learnProfileIdEl = doc.createElement(TAG_PROFILE_ID);
			learnProfileEl.appendChild(learnProfileIdEl);
			learnProfileIdEl.appendChild(doc.createTextNode(profile
					.getLearnProfile().getId()));
			Map<Integer, RepeatingProfile> repProfiles = profile
					.getRepeatProfiles();
			for (Integer interval : repProfiles.keySet()) {
				Element repeatingProfileEl = doc.createElement(TAG_REPEAT);
				root.appendChild(repeatingProfileEl);
				Element profileIntervalEl = doc
						.createElement(TAG_PROFILE_INTERVAL);
				repeatingProfileEl.appendChild(profileIntervalEl);
				profileIntervalEl.appendChild(doc.createTextNode(String
						.valueOf(interval)));
				Element repeatingProfileId = doc.createElement(TAG_PROFILE_ID);
				repeatingProfileEl.appendChild(repeatingProfileId);
				repeatingProfileId.appendChild(doc.createTextNode(repProfiles
						.get(interval).getId()));
			}
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(file));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
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

// def parseWLProfile(sourceStream: java.io.InputStream, repeatingProfiles:
// java.util.List[RepeatingProfile]): WLProfile = {
// val profile = new WLProfile();
// val wlProfileEl = XML.load(sourceStream)
// val learnRPId = ((wlProfileEl \ "learn") \ "rpf_id").text
// profile.setLearnProfile(repeatingProfiles.find(_.getId == learnRPId) match {
// case Some(rp) => rp })
// val repeatRPEls = wlProfileEl \ "repeat"
// repeatRPEls.foreach(repeatRPEl =>
// profile.putRepeatProfile((repeatRPEl \ "interval").text.toInt,
// repeatingProfiles.find(_.getId == (repeatRPEl \ "rpf_id").text) match { case
// Some(rp) => rp }))
// profile
// }