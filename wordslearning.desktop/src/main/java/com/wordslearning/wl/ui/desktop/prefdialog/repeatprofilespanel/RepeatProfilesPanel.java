package com.wordslearning.wl.ui.desktop.prefdialog.repeatprofilespanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.wordslearning.wl.model.settings.RepeatingProfile;
import com.wordslearning.wl.model.settings.SettingsController;
import com.wordslearning.wl.ui.desktop.prefdialog.PrefDialog;

public class RepeatProfilesPanel extends JPanel {
	private JComboBox repProfiesComboBox = new JComboBox();
	private RepeatProfilePanel repProfPanel;
	private SettingsController settingsController;

	public RepeatProfilesPanel(SettingsController settingsController) {
		this.settingsController = settingsController;
		repProfPanel = new RepeatProfilePanel();
		repProfiesComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (((JComboBox) e.getSource()).getSelectedItem() != null) {
					repProfPanel
							.setRepeatProfile((RepeatingProfile) ((JComboBox) e
									.getSource()).getSelectedItem());
				}
			}
		});
		JButton addRepProfileButton = new JButton(new ImageIcon(
				PrefDialog.class.getResource("icons/addProf.png")));
		addRepProfileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				addNewRepeatingProfile();
			}
		});

		JButton remRepProfileButton = new JButton(new ImageIcon(
				PrefDialog.class.getResource("icons/delProf.png")));
		remRepProfileButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				RepeatProfilesPanel.this.settingsController
						.removeRepeatProfile((RepeatingProfile) repProfiesComboBox
								.getSelectedItem());
				refreshRepProfsCB();
			}
		});
		refreshRepProfsCB();
		JPanel northPanel = new JPanel();
		GroupLayout layout = new GroupLayout(northPanel);
		northPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(repProfiesComboBox)
				.addComponent(addRepProfileButton)
				.addComponent(remRepProfileButton));
		layout.setVerticalGroup(layout.createParallelGroup()
				.addComponent(repProfiesComboBox)
				.addComponent(addRepProfileButton)
				.addComponent(remRepProfileButton));
		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);

		JScrollPane repProfPanelScrollPane = new JScrollPane(repProfPanel);
		repProfPanelScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(repProfPanelScrollPane);
	}

	private void addNewRepeatingProfile() {
		RepeatingProfile newProfile = new RepeatingProfile();
		newProfile.setId("unnamed_profile");
		repProfPanel.setRepeatProfile(newProfile);
		settingsController.addRepeatProfile(newProfile);
		refreshRepProfsCB();
	}

	private void refreshRepProfsCB() {
		repProfiesComboBox.setModel(new DefaultComboBoxModel(settingsController
				.getRepeatingProfiles()));
		repProfiesComboBox.setSelectedIndex(repProfiesComboBox.getModel()
				.getSize() - 1);
	}

}
