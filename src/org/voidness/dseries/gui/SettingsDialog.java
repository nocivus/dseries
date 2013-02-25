package org.voidness.dseries.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.voidness.dseries.Settings;

import com.inforviegas.main.gui.custom.labeledcomponents.LabeledLongField;
import com.inforviegas.main.gui.custom.labeledcomponents.LabeledSelectFolder;
import com.inforviegas.main.gui.dialogs.ResponseDialog;
import com.inforviegas.main.utils.formatting.StringUtils;
import com.inforviegas.main.utils.ui.UIUtils;

public class SettingsDialog extends ResponseDialog {

    private JPanel              mainPanel;
    private Settings            settings;
    private JPanel              panel;
    private JPanel              panel_1;
    private JButton             okButton;
    private JButton             cancelButton;
    private LabeledSelectFolder torrentDownloadFolderField;
    private LabeledLongField    checkIntervalField;

    public SettingsDialog(Settings settings) {

        this.settings = settings;
        initialize();
    }

    private void initialize() {

        setTitle("Settings"); //$NON-NLS-1$
        setSize(new Dimension(500, 200));
        setPreferredSize(new Dimension(500, 200));
        getContentPane().add(getMainPanel());
        UIUtils.centerWindow(this);
    }

    private JPanel getMainPanel() {

        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            mainPanel.setLayout(new BorderLayout(10, 10));
            mainPanel.add(getPanel(), BorderLayout.CENTER);
            mainPanel.add(getPanel_1(), BorderLayout.SOUTH);
        }
        return mainPanel;
    }

    private JPanel getPanel() {

        if (panel == null) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(getTorrentDownloadFolderField());
            panel.add(getCheckIntervalField());
        }
        return panel;
    }

    private JPanel getPanel_1() {

        if (panel_1 == null) {
            panel_1 = new JPanel();
            panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel_1.add(getOkButton());
            panel_1.add(getCancelButton());
        }
        return panel_1;
    }

    private JButton getOkButton() {

        if (okButton == null) {
            okButton = new JButton("Ok"); //$NON-NLS-1$
            okButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    // Save settings
                    if (StringUtils.notEmpty(getTorrentDownloadFolderField().getValue())) {

                        settings.saveTorrentDownloadFolder(getTorrentDownloadFolderField().getValue());
                        settings.saveCheckShowsInterval(getCheckIntervalField().getValue());
                    }

                    fireOk();
                }
            });
        }
        return okButton;
    }

    private JButton getCancelButton() {

        if (cancelButton == null) {
            cancelButton = new JButton("Cancel"); //$NON-NLS-1$
            cancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    fireCancel();
                }
            });
        }
        return cancelButton;
    }

    public LabeledSelectFolder getTorrentDownloadFolderField() {

        if (torrentDownloadFolderField == null) {
            torrentDownloadFolderField = new LabeledSelectFolder(120, "Torrent DL dir", 350, settings.getTorrentDownloadFolder()); //$NON-NLS-1$
            torrentDownloadFolderField.setTextFieldSize(300);
        }
        return torrentDownloadFolderField;
    }

    public LabeledLongField getCheckIntervalField() {

        if (checkIntervalField == null) {
            checkIntervalField = new LabeledLongField(120, "Check every (min.)", 150, settings.getCheckShowsInterval()); //$NON-NLS-1$
            checkIntervalField.setHelpText("in minutes"); //$NON-NLS-1$
        }
        return checkIntervalField;
    }
}
