package com.wordslearning.ve.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.wordslearning.ve.gui.articlessources.SourceVocabulariesPanel;
import com.wordslearning.ve.gui.articlessources.SourcesEditorDialog;
import com.wordslearning.ve.gui.vocabulary.WLVocabularyPanel;
import com.wordslearning.ve.model.VEModel;
import com.wordslearning.ve.model.VEModelListener;
import com.wordslearning.ve.model.article.WLArticle;
import com.wordslearning.ve.model.vocabularies.VocabulariesLocalFilesAccessor;

public class VocabularyEditor extends JFrame implements VEModelListener {
	private static final String PROPERTIES_FILE_NAME = "vocabulary_editor.properties";
	private static final String P_UI_DATA_DIR = "data.dir";
	private static final String VOCS_DIR_NAME = "vocs";
	// private static final String FILE_PATHES_FILE_NAME =
	// "file_pathes.properties";

	private String propsFilePath;

	private static Logger LOG = Logger.getLogger(VocabularyEditor.class
			.getName());

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File propertiesFile = new File(System.getProperty("user.home")
				+ "/Dropbox/Apps/words-learning/" + PROPERTIES_FILE_NAME);
		if (!propertiesFile.exists()) {
			createDefaultPropertiesFile(propertiesFile);
		}

		File baseDir = new File(System.getProperty("user.home")
				+ "/Dropbox/Apps/words-learning");

		if (args.length > 0)
			baseDir = new File(args[0]);

		LOG.info("Base Dir == " + baseDir.getAbsolutePath());

		if (!baseDir.exists()) {
			baseDir.mkdir();
		}
		// File filePathesFile = new File(baseDir, FILE_PATHES_FILE_NAME);
		// if (!filePathesFile.exists()) {
		// createDefaultFilePathes(filePathesFile, baseDir);
		// }

		Properties filePathes = new Properties();
		// filePathes.load(new FileInputStream(filePathesFile));
		filePathes.put("data.dir", baseDir.getAbsolutePath());
		Properties props = new Properties();
		props.load(new FileInputStream(propertiesFile));

		VocabularyEditor vocEditor = new VocabularyEditor();
		vocEditor.initUI();
		vocEditor.initModel(props, filePathes);
		vocEditor.propsFilePath = propertiesFile.getAbsolutePath();
		vocEditor.setVisible(true);
	}

	private void initModel(Properties props, Properties filePathes) {
		VocabulariesLocalFilesAccessor vocFilesManager = new VocabulariesLocalFilesAccessor();
		File vocsDir = new File(filePathes.getProperty(P_UI_DATA_DIR) + "/"
				+ VOCS_DIR_NAME);
		if (!vocsDir.exists())
			vocsDir.mkdir();
		vocFilesManager.setVocabulariesDir(vocsDir.getAbsolutePath());
		model.setVocabulariesAccessor(vocFilesManager);
		model.setProperties(props);
		model.init();
	}

	private static void createDefaultFilePathes(File filePathesFile,
			File baseDir) {
		Properties filePathes = new Properties();
		filePathes.put(P_UI_DATA_DIR, baseDir.getAbsolutePath());
		try {
			filePathes.store(new FileOutputStream(filePathesFile),
					"Words Learning Default File Pathes. ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createDefaultPropertiesFile(File propertiesFile) {
		Properties filePathes = new Properties();
		try {
			filePathes.store(new FileOutputStream(propertiesFile),
					"Vocabulary Editor default properties. ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private SourceVocabulariesPanel sourcesPanel;
	private WLVocabularyPanel wlVocPanel;
	private VEModel model;

	private JButton addWordFromDictButton;

	public VocabularyEditor() {
		setTitle("WL Vocabulary Editor ");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				VocabularyEditor.class.getResource("trayIcon.png")));
		model = new VEModel();
		model.addListener(this);
		addWindowListener(new VEWindowListener());
	}

	private void initUI() {
		setLocationByPlatform(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sourcesPanel = new SourceVocabulariesPanel(model);
		JComponent buttonsPanel = createButtonsPanel();
		wlVocPanel = new WLVocabularyPanel(this, model);
		GroupLayout layout = new GroupLayout(this.getContentPane());
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addComponent(sourcesPanel, GroupLayout.PREFERRED_SIZE,
						GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(buttonsPanel)
				.addComponent(wlVocPanel, GroupLayout.PREFERRED_SIZE,
						GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE));

		layout.setVerticalGroup(layout.createParallelGroup()
				.addComponent(sourcesPanel).addComponent(buttonsPanel)
				.addComponent(wlVocPanel));
		setSize(getPreferredSize().width, 480);
		setResizable(false);
	}

	public void addWordsToVocabulary() {
		WLArticle ar = sourcesPanel.getSelectedWord();
		EditWordDialog ewd = new EditWordDialog(this);
		ewd.setEditedArticle(ar);
		int res = ewd.showDialog();
		if (res == EditWordDialog.APPROVE_OPTION)
			model.addWLArticle(ewd.getEditedWord());
	}

	private JComponent createButtonsPanel() {
		Box vBox = Box.createVerticalBox();

		JButton editSourcesButton = new JButton(new ImageIcon(
				VocabularyEditor.class.getResource("openSourcesDialog.png")));
		editSourcesButton.addActionListener(new OpenDictionaryActionListener());

		addWordFromDictButton = new JButton(new ImageIcon(
				VocabularyEditor.class.getResource("arrowRight.png")));
		addWordFromDictButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addWordsToVocabulary();
			}
		});
		addWordFromDictButton.setEnabled(false);
		vBox.add(editSourcesButton);
		vBox.add(Box.createGlue());
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(addWordFromDictButton);
		vBox.add(Box.createGlue());
		return vBox;
	}

	private class VEWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent ev) {
			model.flush();
			try {
				if (model.getProperties() != null)
					model.getProperties().store(
							new FileOutputStream(propsFilePath),
							"Vocabulary Editor Properties");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class OpenDictionaryActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			SourcesEditorDialog sourcesEditor = new SourcesEditorDialog(
					VocabularyEditor.this, model);
			sourcesEditor.showEditor();
		}
	}

	@Override
	public void modelChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void editedVocabularyChanged() {
		addWordFromDictButton.setEnabled(true);
	}

	@Override
	public void homonymsRevealed(WLArticle wlArticle, List<WLArticle> homonyms) {
		// TODO Auto-generated method stub

	}

	@Override
	public void synonymsRevealed(WLArticle wlArticle, List<WLArticle> synonyms) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vocabulariesInitialized() {
		// TODO Auto-generated method stub

	}
}
