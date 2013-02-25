package org.voidness.dseries;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.inforviegas.main.utils.formatting.StringUtils;

public class Settings {

    private final static String  propsFile           = StringUtils.notEmpty(System.getProperty("ApplicationSupportDirectory")) //$NON-NLS-1$
                                                     ? System.getProperty("ApplicationSupportDirectory") + "/dseries/dseries.properties" //$NON-NLS-1$ //$NON-NLS-2$
                                                             : "dseries.properties"; //$NON-NLS-1$

    private static final String  DOWNLOAD_FOLDER_KEY = "torrent_download_folder";   //$NON-NLS-1$
    private static final String  CHECK_INTERVAL_KEY  = "check_interval";            //$NON-NLS-1$

    private java.util.Properties props;

    public Settings() {

        readProps();
    }

    public String getTorrentDownloadFolder() {

        return props.getProperty(DOWNLOAD_FOLDER_KEY);
    }

    public void saveTorrentDownloadFolder(String location) {

        props.setProperty(DOWNLOAD_FOLDER_KEY, location);
        saveProps();
    }

    private void readProps() {

        try {

            props = new Properties();
            if (!new File(propsFile).exists()) {
                new File(propsFile).createNewFile();
            }
            props.load(new FileInputStream(propsFile));

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void saveProps() {

        try {

            props.store(new FileOutputStream(propsFile), ""); //$NON-NLS-1$

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void saveCheckShowsInterval(long value) {

        props.setProperty(CHECK_INTERVAL_KEY, value + ""); //$NON-NLS-1$
        saveProps();
    }

    public long getCheckShowsInterval() {

        long checkInterval = 3600;
        try {

            checkInterval = Long.valueOf(props.getProperty(CHECK_INTERVAL_KEY));

        } catch (Exception exc) {

            saveCheckShowsInterval(checkInterval);
        }

        return checkInterval;
    }
}
