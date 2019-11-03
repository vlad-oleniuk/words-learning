package com.wordslearning.wl.exercises;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.wordslearning.wl.exercises.widgets.ImageViewer;
import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.model.WordsLearning;

public class PictureBasedSpelling extends SpellingExercise {

	private ImageViewer pictureLabel;
	private JPanel explanationArea;
	private JLabel translationLabel;
	private static Logger LOG = Logger.getLogger(PictureBasedSpelling.class
			.getName());

	@Override
	public void showArticle(WLWord word) {
		super.showArticle(word);
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("<p align=\"center\">");
		sb.append(word.getWLArticle().getValue());
		sb.append("</p>");
		sb.append("</html></body>");
		translationLabel.setText(sb.toString());
		Image image = Toolkit.getDefaultToolkit().getImage(
				getCachedFile(word.getWLArticle().getIllustrationURL())
						.getAbsolutePath());
		pictureLabel.setImage(image);
	}

	@Override
	public boolean isApplicable(WLWord word) {
		String url = word.getWLArticle().getIllustrationURL();
		boolean isUrlSet = url != null && !url.equals("");
		if (!isUrlSet) {
			return false;
		}
		File cachedPictureFile = getCachedFile(url);
		if (cachedPictureFile.exists()) {
			return true;
		}
		try {
			storePictureToFile(url, cachedPictureFile);
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			LOG.warning("IOException while a picture file is beening stored. "
					+ e.getMessage());
			return false;
		}
	}

	private File getCachedFile(String url) {
		return new File(getFilesDirectory(), String.valueOf(url.hashCode()));
	}

	private void storePictureToFile(String url, File cachedPictureFile)
			throws MalformedURLException, IOException {
		URLConnection streamConnection = new URL(url).openConnection();
		streamConnection.setConnectTimeout(3000);
		streamConnection.setReadTimeout(4000);
		streamConnection.connect();
		InputStream intputStream = streamConnection.getInputStream();
		FileOutputStream fos = new FileOutputStream(cachedPictureFile);
		byte[] buffer = new byte[10240];
		int read = -1;
		while ((read = intputStream.read(buffer)) != -1) {
			fos.write(buffer, 0, read);
		}
		fos.close();
		intputStream.close();
	}

	@Override
	protected void showAnswerExplanation() {

	}

	@Override
	protected JComponent createExplanationArea() {
		explanationArea = new JPanel();
		explanationArea.setLayout(new BorderLayout());
		translationLabel = new JLabel();
		translationLabel.setFont(translationLabel.getFont().deriveFont(25.0F));
		translationLabel.setHorizontalTextPosition(JLabel.CENTER);
		pictureLabel = new ImageViewer();
		explanationArea.add(translationLabel, BorderLayout.NORTH);
		explanationArea.add(pictureLabel);
		return explanationArea;
	}

	@Override
	public String getExerciseName() {
		return "Picture Spelling";
	}
}
