package org.voidness.dseries.isohunt;

import java.util.ArrayList;
import java.util.List;

public class TorrentSearchResults {

    class Items {

        public List<Torrent> list;
    }

    public Items items;

    public List<Torrent> getTorrents() {

        List<Torrent> results = new ArrayList<Torrent>();

        if (items != null) {

            if (items.list != null) {

                for (Torrent t : items.list) {

                    if (t != null) {

                        results.add(t);
                    }
                }
            }
        }

        return results;
    }

    @Override
    public String toString() {

        StringBuffer b = new StringBuffer();
        for (Torrent t : getTorrents()) {
            b.append(t);
            b.append("\r\n"); //$NON-NLS-1$
        }
        return b.toString();
    }
}
