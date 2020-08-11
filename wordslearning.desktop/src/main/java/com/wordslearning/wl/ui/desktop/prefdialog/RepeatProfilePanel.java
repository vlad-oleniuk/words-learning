package com.wordslearning.wl.ui.desktop.prefdialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.wordslearning.wl.model.settings.RepeatingProfile;
import com.wordslearning.wl.model.settings.SettingsController;


class RepeatProfilePanel extends JPanel {
	private JSpinner periodTF = new JSpinner(new SpinnerNumberModel(1, 1, 1000,
			1));
	private JComboBox repProfilesComboBox;
	private int interval;

	private SettingsController settingsController;

	public RepeatProfilePanel(SettingsController sc, int interval,
			RepeatingProfile repeatingProfile) {
		this.interval = interval;
		this.settingsController = sc;
		repProfilesComboBox = new JComboBox(
				settingsController.getRepeatingProfiles());
		JButton removeButton = new JButton(new ImageIcon(
				PrefDialog.class.getResource("/icons/delEx.png")));
		removeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settingsController.getWLProfile().getRepeatProfiles()
						.remove(RepeatProfilePanel.this.interval);
				JComponent parent = (JComponent) RepeatProfilePanel.this
						.getParent();
				parent.remove(RepeatProfilePanel.this);
				parent.revalidate();
			}
		});
		periodTF.setValue(interval);
		periodTF.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ev) {
				int value = (Integer) ((JSpinner) ev.getSource()).getValue();
				if (settingsController.getWLProfile().getRepeatProfiles()
						.containsKey(value)) {
					((JSpinner) ev.getSource()).setValue(++value);
				} else {
					RepeatingProfile prof = settingsController.getWLProfile()
							.getRepeatProfiles()
							.get(RepeatProfilePanel.this.interval);
					settingsController.getWLProfile().getRepeatProfiles()
							.remove(RepeatProfilePanel.this.interval);
					RepeatProfilePanel.this.interval = value;
					settingsController.getWLProfile().getRepeatProfiles()
							.put(RepeatProfilePanel.this.interval, prof);

				}
			}
		});
		repProfilesComboBox.setSelectedItem(repeatingProfile);

		repProfilesComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settingsController
						.getWLProfile()
						.getRepeatProfiles()
						.put(RepeatProfilePanel.this.interval,
								(RepeatingProfile) ((JComboBox) e.getSource())
										.getSelectedItem());
			}
		});
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addComponent(periodTF, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(repProfilesComboBox)
				.addComponent(removeButton, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE));

		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(periodTF).addComponent(repProfilesComboBox)
				.addComponent(removeButton));
	}

}
