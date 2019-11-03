package com.wordslearning.wl.ui.desktop.prefdialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.wordslearning.wl.model.settings.RepeatingProfile;
import com.wordslearning.wl.model.settings.SettingsController;
import com.wordslearning.wl.ui.desktop.FileBrowser;
import com.wordslearning.wl.ui.desktop.FileBrowser.FileBrowserListener;
import com.wordslearning.wl.ui.desktop.prefdialog.repeatprofilespanel.RepeatProfilesPanel;

public class PrefDialog extends JDialog {
	private GeneralPreferencePanel genPrefPanel;
	private ProfilePanel profilePanel;
	private RepeatProfilesPanel repProfilesPanel;

	private SettingsController settingsController;

	public PrefDialog(JFrame parent, SettingsController settingsManager) {
		super(parent);
		setLocationByPlatform(true);
		this.settingsController = settingsManager;
		genPrefPanel = new GeneralPreferencePanel();
		profilePanel = new ProfilePanel();
		repProfilesPanel = new RepeatProfilesPanel(settingsController);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

		tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				PrefDialog.this.pack();
			}
		});

		tabbedPane.addTab(
				"General",
				new ImageIcon(PrefDialog.class
						.getResource("icons/genProperties.png")), genPrefPanel);
		tabbedPane.addTab(
				"WL Profile",
				new ImageIcon(PrefDialog.class
						.getResource("icons/wlProfile.png")), profilePanel);
		tabbedPane.addTab(
				"Profiles",
				new ImageIcon(PrefDialog.class
						.getResource("icons/profiles.png")), repProfilesPanel);

		add(tabbedPane);
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				storePreferences();
				dispose();
			}

		});
		JPanel closeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		closeButtonPanel.add(closeButton);
		add(closeButtonPanel, BorderLayout.SOUTH);
		setModal(true);
	}

	private void storePreferences() {
		settingsController.storePreferences();
	}

	public void showDialog() {
		pack();
		setVisible(true);
	}

	private class GeneralPreferencePanel extends JPanel {
		private FileBrowser cloudDirPathContainer;

		public GeneralPreferencePanel() {
			JLabel notificLabel = new JLabel("Notific. period");
			JSpinner notifPeriodSpinner = new JSpinner(new SpinnerNumberModel(
					0, 0, 1440, 1));
			notifPeriodSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					settingsController
							.setNotificationPeriod((Integer) ((JSpinner) e
									.getSource()).getValue());
				}
			});

			JLabel sessionWALabel = new JLabel("Questions amount");
			JSpinner sessionWASpinner = new JSpinner(new SpinnerNumberModel(0,
					0, 100, 1));
			sessionWASpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					settingsController
							.setSessionsWordAmount((Integer) ((JSpinner) e
									.getSource()).getValue());
				}
			});

			JLabel learningWALabel = new JLabel("Words amount");
			JSpinner learningWASpinner = new JSpinner(new SpinnerNumberModel(0,
					0, 100, 1));
			learningWASpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					settingsController
							.setLearnWordsAmounty((Integer) ((JSpinner) e
									.getSource()).getValue());
				}
			});

			JLabel cloudDirPathLabel = new JLabel("Path to the cloud dir");
			cloudDirPathContainer = new FileBrowser();
			cloudDirPathContainer.addListener(new FileBrowserListener() {

				@Override
				public void fileSelected(String filePath) {
//					settingsController.setDataDir(filePath);
				}
			});

			JLabel lrRatioLabel = new JLabel("Learn - Repeat");

			JSlider lrRatioSlider = new JSlider(0, 100,
					(int) (100 * settingsController.getLearnRepeatRatio()));
			lrRatioSlider.setPaintLabels(true);

			lrRatioSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						settingsController.setLearnRepeatRatio(source
								.getValue() / 100.0);
					}

				}
			});

			Dictionary<Integer, Component> labels = new Hashtable<Integer, Component>();
			labels.put(0, new JLabel("Learn"));
			labels.put(100, new JLabel("Repeat"));
			lrRatioSlider.setLabelTable(labels);

			GroupLayout layout = new GroupLayout(this);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);
			setLayout(layout);
			layout.setHorizontalGroup(layout
					.createParallelGroup()
					.addGroup(
							layout.createSequentialGroup()
									.addGroup(
											layout.createParallelGroup()
													.addComponent(notificLabel)
													.addComponent(
															sessionWALabel)
													.addComponent(
															learningWALabel)
													.addComponent(lrRatioLabel))
									.addGroup(
											layout.createParallelGroup()
													.addComponent(
															notifPeriodSpinner)
													// .addComponent(fileBrowser)
													.addComponent(
															sessionWASpinner)
													.addComponent(
															learningWASpinner)
													.addComponent(lrRatioSlider)))
					.addComponent(cloudDirPathLabel)
					.addComponent(cloudDirPathContainer));
			layout.setVerticalGroup(layout
					.createSequentialGroup()
					.addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.CENTER)
									.addComponent(notificLabel)
									.addComponent(notifPeriodSpinner, 0,
											GroupLayout.DEFAULT_SIZE,
											GroupLayout.PREFERRED_SIZE))
					// .addGroup(
					// layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					// // .addComponent(vocFileLabel)
					// // .addComponent(fileBrowser, 0,
					// // GroupLayout.DEFAULT_SIZE,
					// // GroupLayout.PREFERRED_SIZE)
					// )
					.addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.CENTER, false)
									.addComponent(sessionWALabel)
									.addComponent(sessionWASpinner))
					.addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.CENTER, false)
									.addComponent(learningWALabel)
									.addComponent(learningWASpinner))
					.addGroup(
							layout.createParallelGroup(
									GroupLayout.Alignment.CENTER, false)
									.addComponent(lrRatioLabel)
									.addComponent(lrRatioSlider))
					.addComponent(cloudDirPathLabel)
					.addComponent(cloudDirPathContainer));

			// boolean learn = Boolean.parseBoolean(manager
			// .getGeneralProperty(WLModel.P_MODE_LEARN));
			// boolean repeat = Boolean.parseBoolean(manager
			// .getGeneralProperty(WLModel.P_MODE_REPEAT));
			// lcb.setSelected(learn);
			// rcb.setSelected(repeat);
			notifPeriodSpinner.setValue(settingsController
					.getNotificationPeriod());
			// cloudDirPathContainer.setSelectedFilePath(settingsController
			// .getDataDir());
			sessionWASpinner.setValue(settingsController
					.getSessionWordsAmount());
			learningWASpinner
					.setValue(settingsController.getLearnWordsAmount());
		}

	}

	private class ProfilePanel extends JPanel {
		private Box repProfsPanel = Box.createVerticalBox();
		private JComboBox learnComboBox;

		public ProfilePanel() {
			setLayout(new GridBagLayout());
			JLabel learnLabel = new JLabel("Learn:");
			learnComboBox = new JComboBox();
			JLabel repeatsLabel = new JLabel("Repeats:");
			JButton addRepProfileButton = new JButton(new ImageIcon(
					PrefDialog.class.getResource("icons/addEx.png")));
			addRepProfileButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int max = -1;
					for (Integer interval : settingsController.getWLProfile()
							.getRepeatProfiles().keySet()) {
						max = Math.max(max, interval);
					}
					settingsController
							.getWLProfile()
							.getRepeatProfiles()
							.put(++max,
									settingsController.getWLProfile()
											.getRepeatProfiles().get(max - 1));
					addRepProfPanel(max, settingsController.getWLProfile()
							.getRepeatProfiles().get(max));
				}
			});
			add(learnLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(5, 5, 0, 0), 0, 0));
			add(learnComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(5, 0, 0, 5), 0, 0));
			add(repeatsLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(0, 5, 0, 0), 0, 0));
			add(addRepProfileButton, new GridBagConstraints(1, 1, 1, 1, 1.0, 0,
					GridBagConstraints.EAST, GridBagConstraints.NONE,
					new Insets(5, 0, 0, 5), 0, 0));
			add(new JScrollPane(repProfsPanel), new GridBagConstraints(0, 2, 2,
					1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));

			learnComboBox.setModel(new DefaultComboBoxModel(settingsController
					.getRepeatingProfiles()));
			learnComboBox.setSelectedItem(settingsController.getWLProfile()
					.getLearnProfile());
			Map<Integer, RepeatingProfile> repProfiles = settingsController
					.getWLProfile().getRepeatProfiles();
			for (Integer interval : repProfiles.keySet()) {
				addRepProfPanel(interval, repProfiles.get(interval));
			}
		}

		private void addRepProfPanel(int interval,
				RepeatingProfile repeatingProfile) {
			RepeatProfilePanel rpp = new RepeatProfilePanel(settingsController,
					interval, repeatingProfile);
			repProfsPanel.add(rpp);
			repProfsPanel.revalidate();
		}

	}
}