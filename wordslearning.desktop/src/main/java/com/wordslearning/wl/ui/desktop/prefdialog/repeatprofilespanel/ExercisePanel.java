package com.wordslearning.wl.ui.desktop.prefdialog.repeatprofilespanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.wordslearning.wl.model.ExerciseMeta;
import com.wordslearning.wl.model.settings.RepeatingProfile;
import com.wordslearning.wl.ui.desktop.prefdialog.PrefDialog;

class ExercisePanel extends JPanel {

	private RepeatingProfile profile;
	private RepeatProfilePanel repeatProfilePanel;

	public ExercisePanel(final int i,
			RepeatingProfile prof) {
		// Map<String, WLExercise> exercisesMap = new HashMap<String,
		// WLExercise>();
		// for (WLExercise wlExercise : exercises) {
		// exercisesMap.put(wlExercise.getExerciseId(), wlExercise);
		// }
		ExerciseMeta[] exerciseIds = ExerciseMeta.values();
		this.profile = prof;
		JComboBox exercisesComboBox = new JComboBox(exerciseIds);
		exercisesComboBox.setSelectedItem(ExerciseMeta.getById(profile
				.getExercises().get(i)));
		exercisesComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				profile.getExercises().set(
						i,
						((ExerciseMeta) ((JComboBox) e.getSource())
								.getSelectedItem()).getId());
			}
		});
		JButton remExButton = new JButton(new ImageIcon(
				PrefDialog.class.getResource("icons/delEx.png")));
		remExButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				profile.getExercises().remove(i);
				repeatProfilePanel.rebuildExercisesList();
			}
		});

		int[] exPoints = profile.getExercisePoints(i);
		JSpinner sucPointsSpinner = new JSpinner(new SpinnerNumberModel(
				exPoints[0], -100, 100, 1));
		sucPointsSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ev) {
				profile.getExercisePoints(i)[0] = (Integer) ((JSpinner) ev
						.getSource()).getValue();
			}
		});

		JSpinner posPointsSpinner = new JSpinner(new SpinnerNumberModel(
				exPoints[1], -100, 100, 1));
		posPointsSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ev) {
				profile.getExercisePoints(i)[1] = (Integer) ((JSpinner) ev
						.getSource()).getValue();
			}
		});
		JSpinner negPointsSpinner = new JSpinner(new SpinnerNumberModel(
				exPoints[2], -100, 100, 1));
		negPointsSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ev) {
				profile.getExercisePoints(i)[2] = (Integer) ((JSpinner) ev
						.getSource()).getValue();
			}
		});

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(exercisesComboBox).addComponent(sucPointsSpinner)
				.addComponent(posPointsSpinner).addComponent(negPointsSpinner)
				.addComponent(remExButton));

		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.CENTER, false)
				.addComponent(exercisesComboBox).addComponent(sucPointsSpinner)
				.addComponent(posPointsSpinner).addComponent(negPointsSpinner)
				.addComponent(remExButton));
	}

}
