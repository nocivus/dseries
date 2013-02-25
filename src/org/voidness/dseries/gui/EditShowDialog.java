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

import org.voidness.dseries.data.Show;

import com.inforviegas.main.gui.custom.labeledcomponents.LabeledIntegerField;
import com.inforviegas.main.gui.dialogs.ResponseDialog;
import com.inforviegas.main.utils.ui.UIUtils;

public class EditShowDialog extends ResponseDialog {

    private JPanel              mainPanel;
    private Show                show;
    private JPanel              panel;
    private JPanel              panel_1;
    private LabeledIntegerField seasonField;
    private JButton             okButton;
    private JButton             cancelButton;
    private LabeledIntegerField episodeField;

    public EditShowDialog(Show show) {

        this.show = show;
        initialize();
    }

    private void initialize() {

        setTitle("Edit next season and episode"); //$NON-NLS-1$
        setSize(new Dimension(300, 200));
        setPreferredSize(new Dimension(300, 200));
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
            panel.add(getSeasonField());
            panel.add(getEpisodeField());
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

    public LabeledIntegerField getSeasonField() {

        if (seasonField == null) {
            seasonField = new LabeledIntegerField(50, "Season", 100, show.currentSeason); //$NON-NLS-1$
        }
        return seasonField;
    }

    public LabeledIntegerField getEpisodeField() {

        if (episodeField == null) {
            episodeField = new LabeledIntegerField(50, "Episode", 100, show.currentEpisode); //$NON-NLS-1$
        }
        return episodeField;
    }
}
