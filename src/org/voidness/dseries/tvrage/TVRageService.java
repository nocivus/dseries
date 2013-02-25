package org.voidness.dseries.tvrage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.voidness.dseries.data.Show;

import com.inforviegas.main.utils.log.LogUtils;

public class TVRageService {

    public static Show selectShow() {

        try {

            System.out.println("Show name: "); //$NON-NLS-1$
            String query = new BufferedReader(new InputStreamReader(System.in)).readLine();

            List<Show> results = findShow(query);
            for (Show s : results) {
                System.out.println(s);
            }
            System.out.println("Which one (id): "); //$NON-NLS-1$
            String id = new BufferedReader(new InputStreamReader(System.in)).readLine();

            for (Show s : results) {
                if (s.tvrageId == Integer.valueOf(id)) {
                    return s;
                }
            }

        } catch (Exception exc) {

            exc.printStackTrace();
        }
        return null;
    }

    public static List<Show> findShow(String query) {

        LogUtils.debug("Looking for shows named: " + query); //$NON-NLS-1$
        List<Show> results = new ArrayList<Show>();

        try {

            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://services.tvrage.com/feeds/search.php?show=" //$NON-NLS-1$
                    + URLEncoder.encode(query, "utf-8")); //$NON-NLS-1$
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                try {

                    SAXBuilder builder = new SAXBuilder();
                    Document doc = builder.build(instream);
                    Element root = doc.getRootElement();
                    List<Element> shows = root.getChildren("show"); //$NON-NLS-1$
                    LogUtils.debug("Found " + shows.size() + " shows"); //$NON-NLS-1$//$NON-NLS-2$
                    for (Element e : shows) {

                        Show s = new Show().setTvrageId(Integer.valueOf(e.getChildText("showid"))) //$NON-NLS-1$
                                .setTitle(e.getChildText("name")) //$NON-NLS-1$
                                .setYear(e.getChildText("started")) //$NON-NLS-1$
                                .setSeasons(Integer.valueOf(e.getChildText("seasons"))); //$NON-NLS-1$

                        results.add(s);
                    }

                } finally {

                    instream.close();
                }
            }

        } catch (Exception exc) {

            exc.printStackTrace();
        }

        return results;
    }

    public static List<Episode> getEpisodes(Show show) {

        List<Episode> results = new ArrayList<Episode>();

        try {

            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://services.tvrage.com/feeds/episode_list.php?sid=" + show.tvrageId); //$NON-NLS-1$
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                try {

                    SAXBuilder builder = new SAXBuilder();
                    Document doc = builder.build(instream);
                    Element root = doc.getRootElement().getChild("Episodelist"); //$NON-NLS-1$
                    List<Element> seasons = root.getChildren("Season"); //$NON-NLS-1$
                    for (Element s : seasons) {

                        List<Element> episodes = s.getChildren("episode"); //$NON-NLS-1$
                        for (Element e : episodes) {

                            Episode ep = new Episode().setShow(show).setTitle(e.getChildText("title")) //$NON-NLS-1$
                                    .setSeason(s.getAttribute("no").getIntValue()) //$NON-NLS-1$
                                    .setNumber(Integer.valueOf(e.getChildText("seasonnum"))) //$NON-NLS-1$
                                    .setAbsoluteNumber(Integer.valueOf(e.getChildText("epnum"))) //$NON-NLS-1$
                                    .setDate(new SimpleDateFormat("yyyy-MM-dd").parse(e.getChildText("airdate"))); //$NON-NLS-1$ //$NON-NLS-2$

                            results.add(ep);
                        }
                    }

                } finally {

                    instream.close();
                }
            }

        } catch (Exception exc) {

            exc.printStackTrace();
        }

        return results;
    }
}
