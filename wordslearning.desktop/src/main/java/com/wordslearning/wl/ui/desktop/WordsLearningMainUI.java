package com.wordslearning.wl.ui.desktop;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.*;

import com.wordslearning.ve.model.vocabularies.VocabulariesLocalFilesAccessor;
import com.wordslearning.wl.exercises.*;
import com.wordslearning.wl.model.ExerciseAction;
import com.wordslearning.wl.model.ExerciseMeta;
import com.wordslearning.wl.model.WLWord;
import com.wordslearning.wl.model.WordsLearning;
import com.wordslearning.wl.model.exceptions.VocabularyNotAvailableException;
import com.wordslearning.wl.model.learnprocess.LPDataLocalFileAccessor;
import com.wordslearning.wl.model.learnprocess.LearnProcessController;
import com.wordslearning.wl.model.learnprocess.WLLearnEngine;
import com.wordslearning.wl.model.settings.SettingsController;
import com.wordslearning.wl.model.settings.SettingsLocalFilesAccessor;
import com.wordslearning.wl.serializers.RepeatingProfileSerializerr;
import com.wordslearning.wl.serializers.WLProfileSerializerr;
import com.wordslearning.wl.ui.WordsLearningUI;
import com.wordslearning.wl.ui.desktop.prefdialog.PrefDialog;
import dorkbox.systemTray.MenuItem;

import javax.swing.*;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class WordsLearningMainUI extends JFrame implements WordsLearningUI {


    public static void main(String[] args)
            throws VocabularyNotAvailableException {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        String baseDir = System.getProperty("user.home")
                + "/Dropbox/Apps/words-learning/";
        if (args.length > 0)
            baseDir = args[0];

        File baseDirFile = new File(baseDir);
        if (!baseDirFile.exists()) {
            throw new IllegalArgumentException("Base dir does not exist: " + baseDirFile.getAbsolutePath());
        }


        VocabulariesLocalFilesAccessor vocsAccessor = new VocabulariesLocalFilesAccessor();
        vocsAccessor.setVocabulariesDir(new File(baseDirFile, "vocs").getAbsolutePath());

        SettingsLocalFilesAccessor settingsFilesAccessor = new SettingsLocalFilesAccessor();
        settingsFilesAccessor.setWorkDir(new File(baseDirFile, "conf").getAbsolutePath());
        settingsFilesAccessor
                .setRepeatingProfileSerializer(new RepeatingProfileSerializerr());
        settingsFilesAccessor
                .setWlProfileSerializer(new WLProfileSerializerr());

        // TODO remove after everyone has updated
        File statisticFile = new File(baseDir + "/learn_data/statistic.xml");
        if (!statisticFile.exists()) {
            moveFile(new File(baseDirFile, "statistic.xml"), statisticFile);
        }
        File learningWordsFile = new File(baseDir
                + "/learn_data/learning_words.json");
        if (!learningWordsFile.exists()) {
            moveFile(new File(baseDirFile, "learning_words.json"),
                    learningWordsFile);
        }

        LPDataLocalFileAccessor statFileAccessor = new LPDataLocalFileAccessor();
        statFileAccessor.setStatisticFilePath(statisticFile.getAbsolutePath());
        statFileAccessor.setLearningWordsFilePath(learningWordsFile
                .getAbsolutePath());
        statFileAccessor.setLearnDataFilePath(baseDir
                + "/learn_data/learn_data.properties");

        WordsLearningMainUI ui = new WordsLearningMainUI();
        ui.wordsLearning = new WordsLearning();
        ui.wordsLearning.setWlUI(ui);
        ui.wordsLearning.setStatisticFileAccessor(statFileAccessor);
        ui.wordsLearning.setSettingsFilesAccessor(settingsFilesAccessor);
        ui.wordsLearning.setVocsAccessor(vocsAccessor);
        ui.init();
    }

    private static void moveFile(File source, File target) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (source.exists()) {
                fis = new FileInputStream(source);
                fos = new FileOutputStream(target);
                byte[] buffer = new byte[1024];
                int read = -1;
                while ((read = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
                fis.close();
                source.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, Class<? extends WLExercise>> exerciseClasses = new HashMap<String, Class<? extends WLExercise>>();

    {
        exerciseClasses.put(ExerciseMeta.WORD_CONSTRUCTOR_EX.getId(),
                WordConstructor.class);
        exerciseClasses.put(ExerciseMeta.SELECT_EX.getId(),
                SelectTranslation.class);
        exerciseClasses.put(ExerciseMeta.SPELLING_EX.getId(),
                TranslationBasedSpelling.class);
        exerciseClasses.put(ExerciseMeta.PICTURE_SPELLING_EX.getId(),
                PictureBasedSpelling.class);
        exerciseClasses.put(ExerciseMeta.CARD_EX.getId(), Card.class);
        exerciseClasses.put(ExerciseMeta.SELECT_SYNONYMS.getId(),
                SelectSynonyms.class);
        exerciseClasses.put(ExerciseMeta.EXAMPLE_EX.getId(),
                ExampleExercise.class);
        exerciseClasses.put(ExerciseMeta.ARTICLE_EX.getId(),
                ArticleExercise.class);
    }

    private Image iconImage = Toolkit.getDefaultToolkit().getImage(
            getClass().getResource("/icons/trayIcon.png"));
    private JPanel exercisesPanel = new JPanel();
    private WLLearnEngine learnEngineAccessor;
    private LearnProcessController learnProcessController;
    private SettingsController settingsManager;

    private Timer timer = new Timer();
    private TimerTask startNextSessionTask;
    private WordsLearning wordsLearning;
    private String localDataDir = System.getProperty("user.home")
            + "/.words_learning/";

    public WordsLearningMainUI() {

        // init UI
        setTitle("Words Learning");
        setIconImage(iconImage);
        setSize(640, 480);
        addWindowListener(new WLWindowListener());
        Toolkit defToolkit = Toolkit.getDefaultToolkit();
        setLocation((defToolkit.getScreenSize().width - getWidth()) / 2,
                (defToolkit.getScreenSize().height - getHeight()) / 2);

        JButton prefButton = new JButton(new ImageIcon(getClass().getResource(
                "/icons/preferences.png")));
        prefButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PrefDialog pDialog = new PrefDialog(WordsLearningMainUI.this,
                        settingsManager);
                pDialog.showDialog();
            }

            private List<WLExercise> getExercisePanels() {
                Component[] components = exercisesPanel.getComponents();
                List<WLExercise> exercisePanels = new ArrayList<WLExercise>();
                for (Component component : components) {
                    if (component instanceof WLExercise)
                        exercisePanels.add((WLExercise) component);
                }
                return Arrays.asList(exercisePanels
                        .toArray(new WLExercise[exercisePanels.size()]));
            }
        });

        JButton learnAnewButton = new JButton(new ImageIcon(getClass()
                .getResource("/icons/reset.png")));
        learnAnewButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                learnProcessController.resetCurrentWord();
                showNextExercise();
            }
        });

        learnAnewButton.setToolTipText("Learn this word anew");

        JButton pronuncButton = new JButton(new ImageIcon(getClass()
                .getResource("/icons/pronunc.png")));
        pronuncButton.addActionListener(new PlayButtonActionListener());

        pronuncButton.setToolTipText("Pronounce the word");

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(prefButton);
        toolBar.add(learnAnewButton);
        toolBar.add(pronuncButton);

        add(toolBar, BorderLayout.NORTH);

        add(exercisesPanel, BorderLayout.CENTER);
        setLocation(100, 100);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void initUI() {
        exercisesPanel.setLayout(new CardLayout());
        for (String exId : exerciseClasses.keySet()) {
            try {
                Class<? extends WLExercise> exClass = exerciseClasses.get(exId);
                WLExercise exercPanel = exClass.newInstance();
                exercPanel.setWlMainUI(this);
                exercPanel.setLearnEngineAccessor(learnEngineAccessor);
                exercPanel.setDataDirectory(localDataDir + "/exercise_data/"
                        + exId);
                exercisesPanel.add(exercPanel, exId);
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (startNextSessionTask != null)
            startNextSessionTask.cancel();
        setVisible(true);
        revalidate();
        repaint();
    }

    public void passivate() {
        learnProcessController.stopLearnSession();
        exercisesPanel.removeAll();
        startNextSessionTask = new TimerTask() {
            @Override
            public void run() {
                init();
            }
        };
        timer.schedule(startNextSessionTask,
                settingsManager.getNotificationPeriod() * 60000);
        setVisible(false);
    }

    public void showNextExercise() {
        ExerciseAction exercise = learnProcessController.getNextExercise();
        switch (exercise.getStatus()) {
            case READY:
                getExercisePanelByExerciseId(exercise.getExercise().getId())
                        .showArticle(exercise.getWord());
                ((CardLayout) exercisesPanel.getLayout()).show(exercisesPanel,
                        exercise.getExercise().getId());
                break;
            case NO_FURTHER_WORDS:
                JOptionPane.showMessageDialog(this, "There are no words to learn");
            case SESSION_WORDS_LIMIT_REACHED:
                WindowEvent closeEvent = new WindowEvent(this,
                        WindowEvent.WINDOW_CLOSING);
                Toolkit.getDefaultToolkit().getSystemEventQueue()
                        .postEvent(closeEvent);
                break;
        }
    }

    public void exerciseFinished() {
        showNextExercise();
    }

    private WLExercise getExercisePanelByExerciseId(String exId) {
        Component[] components = exercisesPanel.getComponents();
        Class<? extends WLExercise> exercClass = exerciseClasses.get(exId);
        for (Component component : components) {
            if (component.getClass() == exercClass)
                return (WLExercise) component;
        }
        return null;
    }

    private class WLWindowListener extends WindowAdapter {

        private final MenuItem openItem;

        public WLWindowListener() {


            dorkbox.systemTray.SystemTray systemTray = dorkbox.systemTray.SystemTray.get();
            if (systemTray == null) {
                throw new RuntimeException("Unable to load SystemTray!");
            }
            systemTray.setImage(WordsLearningMainUI.class.getResourceAsStream("/icons/trayIcon.png"));

            openItem = new dorkbox.systemTray.MenuItem("Show", new ShowWindowActionListener());

            dorkbox.systemTray.MenuItem exitItem = new dorkbox.systemTray.MenuItem("Exit", new ExitWindowActionListener());

            dorkbox.systemTray.Menu menu = systemTray.getMenu();
            menu.add(openItem);
            menu.add(exitItem);

        }

        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            passivate();
            openItem.setEnabled(true);
        }

        @Override
        public void windowActivated(WindowEvent e) {
            super.windowActivated(e);
            openItem.setEnabled(false);
        }

        private class ShowWindowActionListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                init();
            }
        }

        private class ExitWindowActionListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        }
    }

    @Override
    public boolean isExerciseApplicable(String curExId, WLWord lWord) {
        return getExercisePanelByExerciseId(curExId).isApplicable(lWord);
    }

    private void init() {
        wordsLearning.init();
        learnEngineAccessor = wordsLearning;
        learnProcessController = wordsLearning;
        settingsManager = wordsLearning.getSettingsController();
        initUI();
        wordsLearning.startLearnSession();
        showNextExercise();
    }

    @Override
    public void notifyNoVocabularies() {
        JOptionPane.showMessageDialog(this, "No vocabularies found");
    }

    private class PlayButtonActionListener implements ActionListener {

        private File soundDir = new File(localDataDir, "pronunciations");

        {
            if (!soundDir.exists()) {
                soundDir.mkdirs();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            WLWord currentWord = learnProcessController.getCurrentWord();
            String pronunciation = currentWord.getWLArticle()
                    .getPronunciation();
            if (pronunciation != null) {
                File file = getSoundFile(pronunciation);
                play(file);
            }
        }

        private File getSoundFile(String pronuncUrl) {
            final String pronuncFileName = pronuncUrl.hashCode() + ".mp3";
            String[] fileNames = soundDir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.equals(pronuncFileName);
                }
            });
            File soundFile = null;
            if (fileNames != null && fileNames.length > 0) {
                soundFile = new File(soundDir, fileNames[0]);
            } else {
                FileChannel fc = null;
                try {
                    soundFile = new File(soundDir, pronuncFileName);
                    fc = new FileOutputStream(soundFile).getChannel();
                    URLConnection connection = new URL(pronuncUrl)
                            .openConnection();
                    ReadableByteChannel channel = Channels
                            .newChannel(connection.getInputStream());
                    ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                    while (channel.read(buffer) != -1) {
                        buffer.flip();
                        fc.write(buffer);
                        buffer.compact();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fc != null) {
                        try {
                            fc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return soundFile;
        }

        private void play(File file) {
            try {
                Player player = new Player(new FileInputStream(file));
                player.play();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }

    }

}
