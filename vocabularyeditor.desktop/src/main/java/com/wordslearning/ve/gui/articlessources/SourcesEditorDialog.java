package com.wordslearning.ve.gui.articlessources;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordslearning.ve.gui.vocabulary.WLVocPropertiesDialog;
import com.wordslearning.ve.model.VEModel;
import com.wordslearning.ve.model.VEModelListener;
import com.wordslearning.ve.model.article.Language;
import com.wordslearning.ve.model.article.WLArticle;
import com.wordslearning.ve.model.article.WLVocabulary;
import com.wordslearning.ve.model.article.WordsStorage;

public class SourcesEditorDialog extends JDialog implements VEModelListener {

	private final static String TAB_TITLE_VOCABULARIES = "Vocabularies";
	private final static String TAB_TITLE_SOURCES = "Sources";
	private VEModel model;
	private Language currentLanguage = null;
	private JList sourcesList = new JList(new SourcesListModel());
	private JList vocabulariesList = new JList(new VocabulariesListModel());

	private OperationsApplicabilityController aplicabilityController = null;
	private JButton createButton;
	private JButton addSourceButton;
	private JButton removeSourceButton;

	private ObjectMapper mapper = new ObjectMapper();

	public SourcesEditorDialog(Frame parent, VEModel model) {
		super(parent, "Sources Editor", true);
		setLocationByPlatform(true);
		this.model = model;
		model.addListener(this);
		JComboBox langToComboBox = new JComboBox(Language.values());
		langToComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentLanguage = (Language) ((JComboBox) e.getSource())
						.getSelectedItem();
				((SourcesListModel) sourcesList.getModel()).fireModelChanged();
				((VocabulariesListModel) vocabulariesList.getModel())
						.fireModelChanged();
			}
		});

		langToComboBox.setSelectedIndex(-1);

		JScrollPane vocabulariesListSP = new JScrollPane(vocabulariesList);
		JScrollPane sourcesListSP = new JScrollPane(sourcesList);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(TAB_TITLE_VOCABULARIES, vocabulariesListSP);
		tabbedPane.addTab(TAB_TITLE_SOURCES, sourcesListSP);

		createButton = new JButton(new CreateAction());
		addSourceButton = new JButton(new AdditionAction());
		removeSourceButton = new JButton(new RemoveAction());

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				actualizeFileOperationsProcessor((JTabbedPane) e.getSource());
			}
		});
		actualizeFileOperationsProcessor(tabbedPane);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SourcesEditorDialog.this.dispose();
			}
		});
		GroupLayout gl = new GroupLayout(getContentPane());
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		setLayout(gl);
		gl.setHorizontalGroup(gl
				.createSequentialGroup()
				.addGroup(
						gl.createParallelGroup()
								.addComponent(langToComboBox)
								.addComponent(tabbedPane)
								.addComponent(closeButton,
										GroupLayout.Alignment.TRAILING))
				.addGroup(
						gl.createParallelGroup().addComponent(createButton)
								.addComponent(addSourceButton)
								.addComponent(removeSourceButton)));
		gl.setVerticalGroup(gl
				.createSequentialGroup()
				.addComponent(langToComboBox)
				.addGroup(
						gl.createParallelGroup()
								.addComponent(tabbedPane)
								.addGroup(
										gl.createSequentialGroup()
												.addComponent(createButton)
												.addComponent(addSourceButton)
												.addComponent(
														removeSourceButton)))
				.addComponent(closeButton));
		setSize(320, 240);
	}

	protected void actualizeFileOperationsProcessor(JTabbedPane pane) {
		if (pane.getTitleAt(pane.getSelectedIndex()).equals(
				TAB_TITLE_VOCABULARIES)) {
			createButton.setEnabled(true);
			addSourceButton.setEnabled(true);
			removeSourceButton.setEnabled(true);
		} else if (pane.getTitleAt(pane.getSelectedIndex()).equals(
				TAB_TITLE_SOURCES)) {
			createButton.setEnabled(false);
			addSourceButton.setEnabled(false);
			removeSourceButton.setEnabled(false);
		}
	}

	public void showEditor() {
		setVisible(true);
	}

	private class SourcesListModel extends DefaultListModel {

		@Override
		public Object getElementAt(int index) {
			if (currentLanguage == null) {
				List<WordsStorage> sourceVocabularies = model
						.getSourceVocabularies();
				return sourceVocabularies.get(index);
			} else {
				return getFilteredVocabularies(currentLanguage).get(index);
			}
		}

		public void fireModelChanged() {
			fireContentsChanged(this, 0, getSize());
		}

		@Override
		public int getSize() {
			List<WordsStorage> sourceVocabularies = model
					.getSourceVocabularies();
			if (currentLanguage == null)
				return sourceVocabularies.size();
			else
				return getFilteredVocabularies(currentLanguage).size();
		}

		private List<WordsStorage> getFilteredVocabularies(Language filterLang) {
			List<WordsStorage> sourceVocabularies = model
					.getSourceVocabularies();
			List<WordsStorage> filteredVocabularies = new ArrayList<WordsStorage>();
			for (WordsStorage sourceVoc : sourceVocabularies) {
				if (sourceVoc.getLangFrom() == filterLang) {
					filteredVocabularies.add(sourceVoc);
				}
			}
			return filteredVocabularies;
		}

	}

	private class VocabulariesListModel extends DefaultListModel {

		@Override
		public Object getElementAt(int index) {
			if (currentLanguage == null) {
				List<WLVocabulary> sourceVocabularies = model
						.getWlVocabularies();
				return sourceVocabularies.get(index);
			} else {
				return getFilteredVocabularies(currentLanguage).get(index);
			}
		}

		public void fireModelChanged() {
			fireContentsChanged(this, 0, getSize());
		}

		@Override
		public int getSize() {
			List<WLVocabulary> sourceVocabularies = model.getWlVocabularies();
			if (currentLanguage == null)
				return sourceVocabularies.size();
			else
				return getFilteredVocabularies(currentLanguage).size();
		}

		private List<WLVocabulary> getFilteredVocabularies(Language filterLang) {
			List<WLVocabulary> sourceVocabularies = model.getWlVocabularies();
			List<WLVocabulary> filteredVocabularies = new ArrayList<WLVocabulary>();
			for (WLVocabulary sourceVoc : sourceVocabularies) {
				if (sourceVoc.getLangFrom() == filterLang) {
					filteredVocabularies.add(sourceVoc);
				}
			}
			return filteredVocabularies;
		}

	}

	@Override
	public void modelChanged() {
		((SourcesListModel) sourcesList.getModel()).fireModelChanged();
		((VocabulariesListModel) vocabulariesList.getModel())
				.fireModelChanged();
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
	public void editedVocabularyChanged() {
		// TODO Auto-generated method stub

	}

	private abstract class OperationsApplicabilityController {

		abstract boolean isCreationSupported();

		abstract boolean isAdditionSupported();

		abstract boolean isRemovalSupported();

	}

	private class CreateAction extends AbstractAction {

		public CreateAction() {
			putValue(Action.LARGE_ICON_KEY, new ImageIcon(getClass()
					.getResource("create.png")));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			WLVocPropertiesDialog dialog = new WLVocPropertiesDialog(
					SourcesEditorDialog.this);
			int res = dialog.showDialog();
			if (res == WLVocPropertiesDialog.APPROVE_OPTION) {
				WLVocabulary voc = dialog.getWLVocabulary();
				model.createVocabulary(voc);
				model.setEditedVocabulary(voc);
			}
		}
	}

	private class RemoveAction extends AbstractAction {

		public RemoveAction() {
			putValue(Action.LARGE_ICON_KEY, new ImageIcon(getClass()
					.getResource("remove.png")));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SourcesEditorDialog.this.model
					.removeWLVocabulary((WLVocabulary) vocabulariesList
							.getSelectedValue());
		}
	}

	private class AdditionAction extends AbstractAction {

		public AdditionAction() {
			putValue(Action.LARGE_ICON_KEY, new ImageIcon(getClass()
					.getResource("import.png")));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser dictFileOpenDialog = new JFileChooser();
			dictFileOpenDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
			dictFileOpenDialog.setMultiSelectionEnabled(true);
			int res = dictFileOpenDialog
					.showOpenDialog(SourcesEditorDialog.this);
			if (res == JFileChooser.APPROVE_OPTION) {
				File[] dictFiles = dictFileOpenDialog.getSelectedFiles();
				for (File file : dictFiles) {
					try {
						WLVocabulary vocabulary = mapper.readValue(file,
								WLVocabulary.class);
						model.addWLVocabulary(vocabulary);
					} catch (JsonParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JsonMappingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void vocabulariesInitialized() {
		// TODO Auto-generated method stub
		
	}

}
