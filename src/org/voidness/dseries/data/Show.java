package org.voidness.dseries.data;

import java.util.Date;

import com.inforviegas.main.utils.formatting.FormattingUtils;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "shows")
public class Show implements Comparable<Show> {

    @DatabaseField(generatedId = true)
    public int    id;

    @DatabaseField(columnName = "tvrage_id", canBeNull = false)
    public int    tvrageId;

    @DatabaseField(columnName = "title", canBeNull = false)
    public String title;

    @DatabaseField(columnName = "year", canBeNull = false)
    public String year;

    @DatabaseField(columnName = "seasons")
    public int    seasons;

    @DatabaseField(columnName = "current_season")
    public int    currentSeason  = 1;

    @DatabaseField(columnName = "current_episode")
    public int    currentEpisode = 1;

    @DatabaseField(columnName = "next_check_date")
    public Date   nextCheckDate  = new Date();

    @DatabaseField(columnName = "next_episode_date")
    public Date   nextEpisodeDate;

    public Show() {

        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public Show setTvrageId(int tvrageId) {

        this.tvrageId = tvrageId;
        return this;
    }

    public Show setTitle(String title) {

        this.title = title;
        return this;
    }

    public Show setYear(String year) {

        this.year = year;
        return this;
    }

    public Show setSeasons(int seasons) {

        this.seasons = seasons;
        return this;
    }

    public Show setNextEpisodeDate(Date nextEpisodeDate) {

        this.nextEpisodeDate = nextEpisodeDate;
        return this;
    }

    public String getNextSeasonEpisode() {

        return "S" + FormattingUtils.addZerosToLeft(currentSeason, 2) + "E" + FormattingUtils.addZerosToLeft(currentEpisode, 2); //$NON-NLS-1$//$NON-NLS-2$
    }

    @Override
    public String toString() {

        return tvrageId + " - " + title + " (" + year + ") - " + seasons + " seasons"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    @Override
    public int hashCode() {

        return title.hashCode();
    }

    @Override
    public boolean equals(Object other) {

        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        return title.equals(((Show) other).title);
    }

    @Override
    public int compareTo(Show o) {

        if (o == null) {
            return 1;
        }
        return tvrageId < o.tvrageId ? -1 : (tvrageId == o.tvrageId ? 0 : 1);
    }
}
