package org.voidness.dseries;

import javax.swing.JButton;
import javax.swing.JTextPane;

import org.voidness.dseries.data.Database;
import org.voidness.dseries.data.Settings;

import com.inforviegas.main.utils.log.LogUtils;

public class CheckShowsThread extends Thread {

    private Settings              settings;
    private boolean               askedToDie = false;
    private EpisodeDownloadRunner runner;
    private JButton               checkButton;
    private JTextPane             logArea;

    public CheckShowsThread(JButton checkButton, JTextPane logArea, Database database, Settings settings) {

        this.checkButton = checkButton;
        this.settings = settings;
        this.logArea = logArea;
        this.runner = new EpisodeDownloadRunner(logArea, database, settings);
    }

    @Override
    public void run() {

        while (!askedToDie) {

            try {

                // Check the shows
                checkButton.setEnabled(false);
                runner.dowork();
                checkButton.setEnabled(true);
                log("Idle."); //$NON-NLS-1$

                // Sleep for whatever time the user specified (in minutes)
                Thread.sleep(settings.getCheckShowsInterval() * 60000);

            } catch (InterruptedException exc) {

                // Do nothing, it's normal

            } catch (Exception exc) {

                exc.printStackTrace();
            }
        }
    }

    private void log(String str) {

        LogUtils.debug(str);
        logArea.setText(logArea.getText() + "\r\n" + str); //$NON-NLS-1$
    }

    public void pleaseStop() {

        askedToDie = true;
    }
}
