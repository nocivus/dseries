package org.voidness.dseries;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import org.voidness.dseries.gui.ShowsList;

import com.inforviegas.main.utils.log.LogUtils;

//import org.voidness.dseries.gui.ShowsList;

public class DSeries {

    private ShowsList gui;

    private void go() {

        Database d = new Database();
        final Settings s = new Settings();

        setupSysTray();

        try {

            // Show the gui
            gui = new ShowsList(s, d);
            gui.setVisible(true);

        } finally {

            d.close();
        }
    }

    private void setupSysTray() {

        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported"); //$NON-NLS-1$
            return;
        }
        //final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(new ImageIcon(getClass().getClassLoader().getResource("tray.png")).getImage()); //$NON-NLS-1$
        final SystemTray tray = SystemTray.getSystemTray();

        //trayIcon.setPopupMenu(popup);
        trayIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                super.mouseClicked(e);

                gui.setVisible(!gui.isVisible());
            }
        });

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added."); //$NON-NLS-1$
        }
    }

    public static void main(String[] args) throws Exception {

        LogUtils.setLogLevel(LogUtils.DEBUG);

        new DSeries().go();
    }

}
