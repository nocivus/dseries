package org.voidness.dseries.isohunt;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.voidness.dseries.DownloadService;

import com.google.gson.Gson;
import com.inforviegas.main.utils.formatting.StringUtils;
import com.inforviegas.main.utils.log.LogUtils;

/*
{
  "title": "isoHunt > All > californication s06e04",
  "link": "http://isohunt.com",
  "description": "BitTorrent Search > All > californication s06e04",
  "language": "en-us",
  "category": "All",
  "max_results": 1000,
  "ttl": 15,
  "image": {"title": "isoHunt > All > californication s06e04",
            "url": "http://isohunt.com/img/buttons/isohunt-02.gif",
            "link": "http://isohunt.com/",
            "width": 157,
            "height": 45},
  "lastBuildDate": "Wed, 20 Feb 2013 18:42:11 GMT","pubDate": "Wed, 20 Feb 2013 18:42:11 GMT","total_results":55, "censored":0, 
  "items": {
    "list":  [
      {"title":"[Torrentfrancais.com]-<b>californication-s06e04</b>-vostfr-hdtv","link":"http:\/\/isohunt.com\/torrent_details\/453346486\/californication+s06e04?tab=summary","guid":"453346486","enclosure_url":"http://ca.isohunt.com/download/453346486/californication+s06e04.torrent","length":242498929,"tracker":"tracker.publicbt.com","tracker_url":"http:\/\/tracker.publicbt.com:80\/announce","kws":"","exempts":" ... [Torrentfrancais.com]-<b>californication-s06e04</b>-vostfr-hdtv \/ [www.Torrentfrancais.com] <b>Californication.S06E04</b>.FASTSUB.VOSTFR.HDTV.XviD-MiND.avi","category":"TV","original_site":"www.fulldls.com","original_link":"http:\/\/www.fulldls.com\/download-tv-5567712-%5BTorrentfrancais+com%5Dcalifornications06e04vostfrhdtv.torrent","size":"231.27 MB","files":4,"Seeds":6,"leechers":0,"downloads":10,"votes":0,"comments":0,"hash":"8508cc7117cc62504c7708404ee33b71af3aa241","pubDate":"Mon, 11 Feb 2013 19:55:22 GMT"},
      {"title":"<b>Californication.S06E04</b>.Hell Bent for Leather.HDTV.x264-LOL.[VTV]","link":"http:\/\/isohunt.com\/torrent_details\/449723861\/californication+s06e04?tab=summary","guid":"449723861","enclosure_url":"http://ca.isohunt.com/download/449723861/californication+s06e04.torrent","length":360177467,"tracker":"tracker.ccc.de","tracker_url":"http:\/\/tracker.ccc.de:80\/announce","kws":"","exempts":" ... <b>Californication.S06E04</b>.Hell Bent for Leather.HDTV.x264-LOL.[VTV].txt ... <b>Californication.S06E04</b>.Hell Bent for Leather.HDTV.x264-LOL.[VTV].rar","category":"TV","original_site":"www.torrentfunk.com","original_link":"http:\/\/www.torrentfunk.com\/tor\/5369333.torrent","size":"343.49 MB","files":2,"Seeds":4,"leechers":0,"downloads":10,"votes":0,"comments":0,"hash":"1dc61819117fb8264b709d478edba708fb0e6b07","pubDate":"Sat, 02 Feb 2013 13:41:30 GMT"},
    ]
  }
}
*/
public class IsoHuntService {

    public static Torrent findTorrent(String query) {

        try {

            HttpClient client = DownloadService.getClient();
            String httpQuery = "http://ca.isohunt.com/js/json.php?ihq=" + query + "&rows=20&sort=seeds"; //$NON-NLS-1$ //$NON-NLS-2$
            LogUtils.debug("Querying isohunt with: " + httpQuery); //$NON-NLS-1$
            HttpGet httpget = new HttpGet(httpQuery);
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                try {

                    TorrentSearchResults results = new Gson().fromJson(new InputStreamReader(instream), TorrentSearchResults.class);
                    List<Torrent> torrents = results.getTorrents();

                    // Remove all down voted or that contain crap
                    List<Torrent> toDelete = new ArrayList<Torrent>();
                    for (Torrent t : torrents) {
                        if ((StringUtils.notEmpty(t.votes) && Integer.valueOf(t.votes) < 0) || t.containsCrap()) {
                            toDelete.add(t);
                        }
                    }
                    torrents.removeAll(toDelete);

                    // Sort using the algorithm
                    Collections.sort(torrents);

                    if (torrents.size() > 0) {

                        Torrent t = torrents.get(0);
                        LogUtils.debug("Picking torrent with " + t.Seeds + "/" + t.leechers + " s/l and " + t.votes + " votes"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        return t;
                    }

                } finally {

                    instream.close();
                }
            }

        } catch (Exception exc) {

            exc.printStackTrace();
        }

        return null;
    }

    public static List<Torrent> testFindTorrent() {

        List<Torrent> results = new ArrayList<Torrent>();

        for (int i = 0; i < 20; i++) {
            results.add(random());
        }

        // Remove all down voted or that contain crap
        List<Torrent> toDelete = new ArrayList<Torrent>();
        for (Torrent t : results) {
            if ((StringUtils.notEmpty(t.votes) && Integer.valueOf(t.votes) < 0) || t.containsCrap()) {
                toDelete.add(t);
            }
        }
        results.removeAll(toDelete);

        // Sort using the algorithm
        Collections.sort(results);

        return results;
    }

    private static Torrent random() {

        Torrent t = new Torrent();
        t.enclosure_url = "faren"; //$NON-NLS-1$
        t.Seeds = "" + rand(0, 1000); //$NON-NLS-1$
        t.leechers = "" + rand(0, 1000); //$NON-NLS-1$
        t.exempts = Math.random() > 0.5 ? "something.zip" : "something.avi"; //$NON-NLS-1$ //$NON-NLS-2$
        t.votes = "" + rand(-100, 100); //$NON-NLS-1$
        t.comments = "" + rand(0, 200); //$NON-NLS-1$
        t.downloads = "" + rand(0, 300); //$NON-NLS-1$
        return t;
    }

    private static int rand(int between, int and) {

        return (int) (Math.random() * (and - between) + between);
    }
}
