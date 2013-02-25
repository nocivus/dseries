package org.voidness.dseries.isohunt;

import com.inforviegas.main.utils.formatting.StringUtils;

/*
 * title: "[Torrentfrancais.com]-<b>californication-s06e04</b>-vostfr-hdtv",
 * link: "http:\\/\\/isohunt.com\\/torrent_details\\/453346486\\/californication+s06e04?tab=summary",
 * guid: "453346486",
 * enclosure_url: "http://ca.isohunt.com/download/453346486/californication+s06e04.torrent",
 * length: 242498929,
 * tracker: "tracker.publicbt.com",
 * tracker_url: "http:\\/\\/tracker.publicbt.com:80\\/announce\",
 * kws: "",
 * exempts: " ... [Torrentfrancais.com]-<b>californication-s06e04</b>-vostfr-hdtv \\/ [www.Torrentfrancais.com] <b>Californication.S06E04</b>.FASTSUB.VOSTFR.HDTV.XviD-MiND.avi",
 * category: "TV",
 * original_site: "www.fulldls.com",
 * original_link: "http:\\/\\/www.fulldls.com\\/download-tv-5567712-%5BTorrentfrancais+com%5Dcalifornications06e04vostfrhdtv.torrent",
 * size:"231.27 MB",
 * files:4,
 * Seeds:6, 
 * leechers:0,
 * downloads:10,
 * votes:0,
 * comments:0,
 * hash:"8508cc7117cc62504c7708404ee33b71af3aa241",
 * pubDate:"Mon, 11 Feb 2013 19:55:22 GMT"
 */
public class Torrent implements Comparable<Torrent> {

    public String files;

    public String Seeds;

    public String leechers;

    public String downloads;

    public String votes;

    public String comments;

    public String exempts;

    public String enclosure_url;

    public boolean containsCrap() {

        return exempts != null && (exempts.toLowerCase().contains(".zip") //$NON-NLS-1$
                || exempts.toLowerCase().contains(".rar") //$NON-NLS-1$
                || exempts.toLowerCase().contains(".gz") //$NON-NLS-1$
                || exempts.toLowerCase().contains(".exe") //$NON-NLS-1$
        || exempts.toLowerCase().contains(".wmv")); //$NON-NLS-1$
    }

    @Override
    public String toString() {

        return "URL: " + enclosure_url + " Files: " + files + " Seeds: " + Seeds //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + " Leechers: " + leechers + " Downloads: " + downloads + " Votes: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + votes + " Comments: " + comments + " Contains crap: " + containsCrap(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected float rating() {

        int rating = 0;
        if (StringUtils.notEmpty(Seeds)) {
            rating += 0.3f * Integer.valueOf(Seeds);
        }
        if (StringUtils.notEmpty(leechers)) {
            rating += 0.2f * Integer.valueOf(leechers);
        }
        if (StringUtils.notEmpty(votes)) {
            rating += 0.5f * Integer.valueOf(votes);
        }
        return rating;
    }

    @Override
    public int compareTo(Torrent o) {

        // First null check
        if (o == null) {
            return 1;
        }

        // After that, give some percentage of importance to the 3 final parameters (seeds, leechers, and votes)
        float myRating = rating();
        float otherRating = o.rating();
        if (myRating > otherRating) {
            return -1;
        } else if (myRating < otherRating) {
            return 1;
        }

        return 0;
    }
}
