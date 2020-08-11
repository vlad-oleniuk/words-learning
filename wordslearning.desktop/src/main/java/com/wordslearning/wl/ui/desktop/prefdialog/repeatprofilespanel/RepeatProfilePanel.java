package com.wordslearning.wl.ui.desktop.prefdialog.repeatprofilespanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wordslearning.wl.model.ExerciseMeta;
import com.wordslearning.wl.model.settings.RepeatingProfile;
import com.wordslearning.wl.ui.desktop.prefdialog.PrefDialog;

class RepeatProfilePanel extends JPanel {
	private JTextField idTF = new JTextField();
	private Box exercisesList = Box.createVerticalBox();
	private RepeatingProfile profile;

	public RepeatProfilePanel() {
		idTF.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				profile.setId(((JTextField) e.getSource()).getText());
			}
		});

		JButton addExButton = new JButton(new ImageIcon(
				PrefDialog.class.getResource("/icons/addEx.png")));
		addExButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addNewExercise();
			}
		});

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(
						layout.createSequentialGroup().addComponent(idTF)
								.addComponent(addExButton))
				.addComponent(exercisesList));
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(idTF, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(addExButton))
				.addComponent(exercisesList));
	}

	private void addNewExercise() {
		profile.putExercise(ExerciseMeta.values()[0].getId(), new int[] { 0, 0,
				0 });
		rebuildExercisesList();
	}

	void rebuildExercisesList() {
		exercisesList.removeAll();

		List<String> profileExercises = profile.getExercises();
		for (int i = 0; i < profileExercises.size(); i++) {
			ExercisePanel label = new ExercisePanel(i, profile);
			// label.setBorder(BorderFactory
			// .createBevelBorder(BevelBorder.LOWERED));
			exercisesList.add(label);
		}
		exercisesList.revalidate();
	}

	public void setRepeatProfile(RepeatingProfile profile) {
		this.profile = profile;
		idTF.setText(profile.getId());
		rebuildExercisesList();
	}

}
