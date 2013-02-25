package org.voidness.dseries;

import org.voidness.dseries.gui.ShowsList;

public class CheckShowsThread extends Thread {

    private ShowsList gui;
    private Settings  settings;
    private boolean   askedToDie = false;

    public CheckShowsThread(ShowsList gui, Settings settings) {

        this.gui = gui;
        this.settings = settings;
    }

    @Override
    public void run() {

        while (!askedToDie) {

            try {

                // Check the shows
                gui.checkShows();

                // Sleep for whatever time the user specified (in minutes)
                Thread.sleep(settings.getCheckShowsInterval() * 60000);

            } catch (InterruptedException exc) {

                // Do nothing, it's normal

            } catch (Exception exc) {

                exc.printStackTrace();
            }
        }
    }

    public void pleaseStop() {

        askedToDie = true;
    }
}
