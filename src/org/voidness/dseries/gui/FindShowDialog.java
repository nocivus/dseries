package org.voidness.dseries.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.voidness.dseries.Show;
import org.voidness.dseries.tvrage.TVRageService;

import com.inforviegas.main.gui.custom.PowerTable;
import com.inforviegas.main.gui.custom.labeledcomponents.LabeledStringField;
import com.inforviegas.main.gui.dialogs.ResponseDialog;
import com.inforviegas.main.gui.models.BaseTableModel;
import com.inforviegas.main.utils.formatting.StringUtils;
import com.inforviegas.main.utils.ui.UIUtils;

public class FindShowDialog extends ResponseDialog {

    private JPanel             panel;
    private LabeledStringField showField;
    private JButton            searchButton;
    private PowerTable<Show>   powerTable;
    private JPanel             panel_1;
    private JButton            okButton;
    private JButton            cancelButton;
    private JPanel             mainPanel;
    private JScrollPane        scrollPane;

    public FindShowDialog() {

        initialize();
    }

    private void initialize() {

        setTitle("Find show"); //$NON-NLS-1$
        setSize(new Dimension(400, 500));
        setPreferredSize(new Dimension(400, 500));
        getContentPane().add(getMainPanel());
        UIUtils.centerWindow(this);
    }

    private JPanel getMainPanel() {

        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            mainPanel.setLayout(new BorderLayout(10, 10));
            mainPanel.add(getPanel(), BorderLayout.NORTH);
            mainPanel.add(getPanel_1(), BorderLayout.SOUTH);
            mainPanel.add(getScrollPane(), BorderLayout.CENTER);
        }
        return mainPanel;
    }

    public Show getShow() {

        return getPowerTable().getSelectedObject();
    }

    private JPanel getPanel() {

        if (panel == null) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.add(getShowField());
            panel.add(getSearchButton());
        }
        return panel;
    }

    private LabeledStringField getShowField() {

        if (showField == null) {
            showField = new LabeledStringField();
            showField.setLabelText("Name:"); //$NON-NLS-1$
            showField.setComponentSize(200);
            showField.getComponent().addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {

                    super.keyReleased(e);

                    getSearchButton().setEnabled(StringUtils.notEmpty(showField.getValue()));
                }

                @Override
                public void keyPressed(KeyEvent e) {

                    super.keyPressed(e);

                    if (e.getKeyCode() == KeyEvent.VK_ENTER && StringUtils.notEmpty(showField.getValue())) {

                        doSearch();
                    }
                }
            });
        }
        return showField;
    }

    private JButton getSearchButton() {

        if (searchButton == null) {
            searchButton = new JButton("Search"); //$NON-NLS-1$
            searchButton.setEnabled(false);
            searchButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    doSearch();
                }
            });
        }
        return searchButton;
    }

    protected void doSearch() {

        getSearchButton().setEnabled(false);

        new Thread(new Runnable() {

            public void run() {

                List<Show> shows = TVRageService.findShow(getShowField().getValue());
                Collections.sort(shows);
                getPowerTable().getTableModel().setData(shows);

                getSearchButton().setEnabled(true);
            }

        }).start();
    }

    private PowerTable<Show> getPowerTable() {

        if (powerTable == null) {
            powerTable = new PowerTable<Show>();
            powerTable.setModel(new BaseTableModel<Show>() {

                @Override
                protected List<String> getColumnNames() {

                    List<String> cols = new ArrayList<String>();
                    cols.add("Id"); //$NON-NLS-1$
                    cols.add("Title"); //$NON-NLS-1$
                    cols.add("Year"); //$NON-NLS-1$
                    return cols;
                }

                @Override
                protected Object getObjectValueAt(Show obj, int col) {

                    switch (col) {
                        case 0:
                            return obj.tvrageId;
                        case 1:
                            return obj.title;
                        case 2:
                            return obj.year;
                    }
                    return ""; //$NON-NLS-1$
                }
            });
        }
        return powerTable;
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

    private JScrollPane getScrollPane() {

        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getPowerTable());
        }
        return scrollPane;
    }
}
