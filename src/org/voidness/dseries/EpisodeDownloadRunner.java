package org.voidness.dseries;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.JTextPane;

import org.voidness.dseries.data.Database;
import org.voidness.dseries.data.Settings;
import org.voidness.dseries.data.Show;
import org.voidness.dseries.isohunt.IsoHuntService;
import org.voidness.dseries.isohunt.Torrent;
import org.voidness.dseries.tvrage.Episode;
import org.voidness.dseries.tvrage.TVRageService;

import com.inforviegas.main.utils.datetime.DateTimeUtils;
import com.inforviegas.main.utils.log.LogUtils;

public class EpisodeDownloadRunner {

    Semaphore         binary = new Semaphore(1);
    private Database  database;
    private Settings  settings;
    private JTextPane logArea;

    public EpisodeDownloadRunner(JTextPane logArea, Database database, Settings settings) {

        this.logArea = logArea;
        this.database = database;
        this.settings = settings;
    }

    public synchronized void dowork() {

        final Worker worker = new Worker();

        for (final Show s : database.getShows()) {

            new Thread() {

                public void run() {

                    System.out.println("Working on show: " + s); //$NON-NLS-1$
                    worker.doWork(s);
                    System.out.println("Work on show " + s + " done."); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }.start();
        }

        try {

            // Wait for all threads to try to acquire the semaphore
            Thread.sleep(1000);

            System.out.println("Waiting for work to be complete on all shows"); //$NON-NLS-1$
            while (binary.hasQueuedThreads()) {

                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {

            // handle
            e.printStackTrace();

        }

        System.out.println("All work complete!"); //$NON-NLS-1$
    }

    class Worker {

        public void doWork(Show show) {

            try {

                binary.acquire();
                findAndDownloadEpisodeFor(show);
                binary.release();

            } catch (InterruptedException e) {

                // handle
                e.printStackTrace();
            }
        }
    }

    public void findAndDownloadEpisodeFor(Show s) {

        if (DateTimeUtils.isAfter(s.nextCheckDate, new Date())) {

            log("Not checking " + s + ". Episode " + s.getNextSeasonEpisode() + " not out yet."); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            return;
        }

        log("Loading episodes for show " + s.title); //$NON-NLS-1$

        // Check for episode availability
        List<Episode> episodes = TVRageService.getEpisodes(s);
        Episode nextEpisodeToDL = getNextEpisodeToDownload(s, episodes);

        log("Next episode is " + nextEpisodeToDL.title); //$NON-NLS-1$

        // Check if the airdate has passed
        if (nextEpisodeToDL != null && DateTimeUtils.isBefore(nextEpisodeToDL.date, new Date())) {

            log("Will search for torrent: " + nextEpisodeToDL.getTorrentSearchQuery()); //$NON-NLS-1$

            // Look for the torrents
            log("Looking for torrent for episode " + nextEpisodeToDL.toString()); //$NON-NLS-1$
            Torrent t = IsoHuntService.findTorrent(nextEpisodeToDL.getTorrentSearchQuery());

            if (t != null) {

                log("Found torrent for episode: " + t); //$NON-NLS-1$

                // Download torrent to target folder
                try {

                    log("Downloading..."); //$NON-NLS-1$
                    DownloadService.downloadFile(t.enclosure_url, settings.getTorrentDownloadFolder());

                    // If all goes well, update the episode number
                    updateShow(s, episodes, nextEpisodeToDL);

                } catch (Exception exc) {

                    exc.printStackTrace();
                }

            } else {

                log("No torrents found."); //$NON-NLS-1$
            }

        } else {

            log("Episode isn't out yet."); //$NON-NLS-1$
            s.nextCheckDate = nextEpisodeToDL.date;
            s.nextEpisodeDate = nextEpisodeToDL.date;
            database.saveShow(s);
        }
    }

    protected void updateShow(Show show, List<Episode> episodes, Episode lastDownloaded) {

        int currentNumber = lastDownloaded.absoluteNumber;
        log("Current episode absolute number is: " + currentNumber); //$NON-NLS-1$

        // Find the next one
        boolean found = false;
        for (Episode ep : episodes) {

            if (ep.absoluteNumber == currentNumber + 1) {

                log("Found next episode: " + ep); //$NON-NLS-1$
                show.currentSeason = ep.season;
                show.currentEpisode = ep.number;
                show.nextCheckDate = ep.date;
                show.nextEpisodeDate = ep.date;
                found = true;
                break;
            }
        }

        // If it could not find it, just increase the number (assume next one)
        if (!found) {

            log("Did not find next episode, assuming + 1..."); //$NON-NLS-1$
            show.currentEpisode++;
        }

        // Save it
        database.saveShow(show);
    }

    protected Episode getNextEpisodeToDownload(Show show, List<Episode> episodes) {

        for (Episode e : episodes) {

            if (e.season == show.currentSeason && e.number == show.currentEpisode) {

                return e;
            }
        }
        return null;
    }

    private void log(String str) {

        LogUtils.debug(str);
        logArea.setText(logArea.getText() + "\r\n" + str); //$NON-NLS-1$
    }
}
