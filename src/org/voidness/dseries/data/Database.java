package org.voidness.dseries.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.inforviegas.main.utils.formatting.StringUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class Database {

    private final static String  DATABASE_URL = StringUtils.notEmpty(System.getProperty("ApplicationSupportDirectory")) //$NON-NLS-1$
                                              ? "jdbc:h2:" + System.getProperty("ApplicationSupportDirectory") + "/dseries/data" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                      : "jdbc:h2:data"; //$NON-NLS-1$

    public Dao<Show, Integer>    showDao;
    private JdbcConnectionSource connectionSource;

    public Database() {

        try {

            // create our data-source for the database
            connectionSource = new JdbcConnectionSource(DATABASE_URL);

            // setup our database and DAOs
            setupDatabase();

        } catch (Exception exc) {

            exc.printStackTrace();

        }
    }

    /**
     * Setup our database and DAOs
     */
    private void setupDatabase() throws Exception {

        showDao = DaoManager.createDao(connectionSource, Show.class);

        // if you need to create the table
        TableUtils.createTableIfNotExists(connectionSource, Show.class);

        // Add column next check
        //showDao.executeRaw("ALTER TABLE `shows` ADD COLUMN next_episode_date datetime;"); //$NON-NLS-1$
    }

    public void close() {

        try {

            connectionSource.close();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void saveShow(Show show) {

        try {

            showDao.createOrUpdate(show);

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public List<Show> getShows() {

        try {

            return showDao.queryForAll();

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return new ArrayList<Show>();
    }

    public Show findShow(int tvrageId) {

        try {

            List<Show> results = showDao.queryForEq("tvrage_id", tvrageId); //$NON-NLS-1$
            return results.size() > 0 ? results.get(0) : null;

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;
    }

    public void removeShow(Show show) {

        try {

            showDao.delete(show);

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}
