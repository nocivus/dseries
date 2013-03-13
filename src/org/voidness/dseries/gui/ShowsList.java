package org.voidness.dseries.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import org.voidness.dseries.CheckShowsThread;
import org.voidness.dseries.data.Database;
import org.voidness.dseries.data.Settings;
import org.voidness.dseries.data.Show;

import com.inforviegas.main.gui.custom.PowerTable;
import com.inforviegas.main.gui.custom.error.Notifications;
import com.inforviegas.main.gui.listeners.DialogResponseListener;
import com.inforviegas.main.gui.models.BaseTableModel;
import com.inforviegas.main.utils.formatting.StringUtils;
import com.inforviegas.main.utils.ui.UIUtils;

public class ShowsList extends JFrame {

    private PowerTable<Show> powerTable;
    private JPanel           panel;
    private JButton          addShowButton;
    private Database         database;
    private JPanel           mainPanel;
    private JScrollPane      scrollPane;
    private JButton          checkUpdatesButton;
    private JButton          settingsButton;
    private Settings         settings;
    private CheckShowsThread thread;
    private JPanel           centerPanel;
    private JScrollPane      scrollPane_1;
    private JTextPane        logArea;
    private JButton          aboutButton;
    private JButton          btnDelete;
    private JSeparator       separator;
    private JSeparator       separator_1;
    private JSeparator       separator_2;

    public ShowsList(Settings s, Database d) {

        database = d;
        settings = s;

        initialize();

        updateTable();

        // Launch a thread to check for new episodes every X time
        launchCheckThread();
    }

    private void launchCheckThread() {

        // Stop it, if running
        if (thread != null) {

            // Ask the thread to die
            thread.pleaseStop();
            // Interrupt the sleep inside
            thread.interrupt();
            // Wait for it to die
            try {
                thread.join();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

        // And relaunch with a different time
        thread = new CheckShowsThread(getCheckUpdatesButton(), getLogArea(), database, settings);
        thread.start();
    }

    private void updateTable() {

        getPowerTable().getTableModel().setData(database.getShows());
    }

    private void initialize() {

        setPreferredSize(new Dimension(400, 500));
        setSize(new Dimension(400, 500));
        setTitle("Download Series"); //$NON-NLS-1$
        setContentPane(getMainPanel());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UIUtils.centerWindow(this);
    }

    private JPanel getMainPanel() {

        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            mainPanel.setLayout(new BorderLayout(10, 10));
            mainPanel.add(getPanel(), BorderLayout.SOUTH);
            mainPanel.add(getCenterPanel(), BorderLayout.CENTER);
        }
        return mainPanel;
    }

    private PowerTable<Show> getPowerTable() {

        if (powerTable == null) {
            powerTable = new PowerTable<Show>();
            powerTable.setModel(new BaseTableModel<Show>() {

                @Override
                protected List<String> getColumnNames() {

                    List<String> cols = new ArrayList<String>();
                    cols.add("Title"); //$NON-NLS-1$
                    cols.add("Next episode"); //$NON-NLS-1$
                    cols.add("Next air date"); //$NON-NLS-1$
                    return cols;
                }

                @Override
                protected Object getObjectValueAt(Show obj, int col) {

                    switch (col) {
                        case 0:
                            return obj.title;
                        case 1:
                            return obj.getNextSeasonEpisode();
                        case 2:
                            return new SimpleDateFormat("yyyy-MM-dd").format(obj.nextCheckDate); //$NON-NLS-1$
                    }
                    return ""; //$NON-NLS-1$
                }
            });
            powerTable.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    super.mouseClicked(e);

                    if (e.getClickCount() == 2) {

                        final Show selected = getPowerTable().getSelectedObject();
                        if (selected != null) {
                            final EditShowDialog fsd = new EditShowDialog(selected);
                            fsd.addDialogResponseListener(new DialogResponseListener() {

                                @Override
                                public void onOk() {

                                    try {

                                        selected.currentSeason = fsd.getSeasonField().getValue();
                                        selected.currentEpisode = fsd.getEpisodeField().getValue();
                                        selected.nextCheckDate = new Date();
                                        selected.nextEpisodeDate = null;
                                        database.saveShow(selected);
                                        updateTable();

                                    } catch (Exception exc) {
                                        exc.printStackTrace();
                                    }
                                    fsd.dispose();
                                }

                                @Override
                                public void onCancel() {

                                    fsd.dispose();
                                }

                                @Override
                                public void onReset() {

                                }
                            });
                            fsd.setVisible(true);
                        }
                    }
                }
            });
        }
        return powerTable;
    }

    private JPanel getPanel() {

        if (panel == null) {
            panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.add(getAddShowButton());
            panel.add(getBtnDelete());
            panel.add(getSeparator());
            panel.add(getCheckUpdatesButton());
            panel.add(getSeparator_1());
            panel.add(getSettingsButton());
            panel.add(getSeparator_2());
            panel.add(getAboutButton());
        }
        return panel;
    }

    private JButton getAddShowButton() {

        if (addShowButton == null) {
            addShowButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("add.png"))); //$NON-NLS-1$
            addShowButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    final FindShowDialog fsd = new FindShowDialog();
                    fsd.addDialogResponseListener(new DialogResponseListener() {

                        @Override
                        public void onOk() {

                            Show found = fsd.getShow();

                            if (found != null) {

                                if (database.findShow(found.tvrageId) == null) {

                                    database.saveShow(found);
                                    updateTable();
                                }
                                fsd.dispose();
                            }
                        }

                        @Override
                        public void onCancel() {

                            fsd.dispose();
                        }

                        @Override
                        public void onReset() {

                        }
                    });
                    fsd.setVisible(true);
                }
            });
        }
        return addShowButton;
    }

    private JScrollPane getScrollPane() {

        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getPowerTable());
        }
        return scrollPane;
    }

    private JButton getCheckUpdatesButton() {

        if (checkUpdatesButton == null) {
            checkUpdatesButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("refresh.png"))); //$NON-NLS-1$
            checkUpdatesButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    checkShows();
                }
            });
        }
        return checkUpdatesButton;
    }

    public void checkShows() {

        if (StringUtils.isEmpty(settings.getTorrentDownloadFolder())) {

            Notifications.showError("Torrent download folder not set. Try again after setting it."); //$NON-NLS-1$
            showSettings();
            getCheckUpdatesButton().setEnabled(true);
            return;
        }

        // Go!
        launchCheckThread();

        updateTable();
    }

    private JButton getSettingsButton() {

        if (settingsButton == null) {
            settingsButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("cog.png"))); //$NON-NLS-1$
            settingsButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    showSettings();
                }
            });
        }
        return settingsButton;
    }

    private void showSettings() {

        final long oldCheckInterval = settings.getCheckShowsInterval();
        final SettingsDialog fsd = new SettingsDialog(settings);
        fsd.addDialogResponseListener(new DialogResponseListener() {

            @Override
            public void onOk() {

                if (settings.getCheckShowsInterval() != oldCheckInterval) {

                    launchCheckThread();
                }
                fsd.dispose();
            }

            @Override
            public void onCancel() {

                fsd.dispose();
            }

            @Override
            public void onReset() {

            }
        });
        fsd.setVisible(true);
    }

    private JPanel getCenterPanel() {

        if (centerPanel == null) {
            centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout(0, 0));
            centerPanel.add(getScrollPane_1(), BorderLayout.SOUTH);
            centerPanel.add(getScrollPane(), BorderLayout.CENTER);
        }
        return centerPanel;
    }

    private JScrollPane getScrollPane_1() {

        if (scrollPane_1 == null) {
            scrollPane_1 = new JScrollPane();
            scrollPane_1.setViewportView(getLogArea());
            scrollPane_1.setMinimumSize(new Dimension(0, 100));
            scrollPane_1.setMaximumSize(new Dimension(0, 100));
            scrollPane_1.setPreferredSize(new Dimension(0, 100));
            scrollPane_1.setSize(new Dimension(0, 100));
        }
        return scrollPane_1;
    }

    private JTextPane getLogArea() {

        if (logArea == null) {
            logArea = new JTextPane();
            logArea.setText("Idle."); //$NON-NLS-1$
            logArea.setForeground(new Color(0, 182, 37));
            logArea.setBackground(new Color(52, 52, 52));
            logArea.setFont(new Font("Courier New", Font.BOLD, 10)); //$NON-NLS-1$
            DefaultCaret caret = (DefaultCaret) logArea.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        }
        return logArea;
    }

    private JButton getAboutButton() {

        if (aboutButton == null) {
            aboutButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("information.png"))); //$NON-NLS-1$
            aboutButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    Notifications.showInfo("DSeries was developed by Pedro Assuncao (http://pedroassuncao.com).\r\nIf you like it, please donate :)"); //$NON-NLS-1$
                }
            });
        }
        return aboutButton;
    }

    private JButton getBtnDelete() {

        if (btnDelete == null) {
            btnDelete = new JButton(new ImageIcon(getClass().getClassLoader().getResource("delete.png"))); //$NON-NLS-1$
            btnDelete.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    Show selected = getPowerTable().getSelectedObject();
                    if (selected != null && UIUtils.confirm("Are you sure you want to remove that show?")) { //$NON-NLS-1$

                        database.removeShow(selected);
                        updateTable();
                    }
                }
            });
        }
        return btnDelete;
    }

    private JSeparator getSeparator() {

        if (separator == null) {
            separator = new JSeparator();
        }
        return separator;
    }

    private JSeparator getSeparator_1() {

        if (separator_1 == null) {
            separator_1 = new JSeparator();
        }
        return separator_1;
    }

    private JSeparator getSeparator_2() {

        if (separator_2 == null) {
            separator_2 = new JSeparator();
        }
        return separator_2;
    }
}
