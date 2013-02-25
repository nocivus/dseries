package org.voidness.dseries.tvrage;

import java.net.URLEncoder;
import java.util.Date;

import org.voidness.dseries.Show;

import com.inforviegas.main.utils.formatting.FormattingUtils;

public class Episode {

    public Show   show;
    public String title;
    public int    season;
    public int    number;
    public int    absoluteNumber;
    public Date   date;

    public Episode setShow(Show show) {

        this.show = show;
        return this;
    }

    public Episode setTitle(String title) {

        this.title = title;
        return this;
    }

    public Episode setSeason(int season) {

        this.season = season;
        return this;
    }

    public Episode setNumber(int number) {

        this.number = number;
        return this;
    }

    public Episode setAbsoluteNumber(int number) {

        this.absoluteNumber = number;
        return this;
    }

    public Episode setDate(Date date) {

        this.date = date;
        return this;
    }

    public String getTorrentSearchQuery() {

        try {

            return URLEncoder.encode(getShowTitleAndEpisode(), "utf-8"); //$NON-NLS-1$

        } catch (Exception exc) {

            exc.printStackTrace();
        }
        return ""; //$NON-NLS-1$
    }

    private String getSeasonEpisode() {

        return "S" + FormattingUtils.addZerosToLeft(season, 2) + "E" + FormattingUtils.addZerosToLeft(number, 2); //$NON-NLS-1$//$NON-NLS-2$
    }

    private String getShowTitleAndEpisode() {

        return show.title + " " + getSeasonEpisode(); //$NON-NLS-1$
    }

    @Override
    public String toString() {

        return getShowTitleAndEpisode() + " - " + title; //$NON-NLS-1$
    }
}
