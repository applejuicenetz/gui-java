/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.exception.CoreLostException;
import de.applejuicenet.client.facade.exception.WrongPasswordException;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/AppleJuiceClientTG.java,v 1.12 2009/01/12 09:19:20 maj0r Exp $.
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class AppleJuiceThreadGroup extends ThreadGroup {

    public AppleJuiceThreadGroup() {
        super("AppleJuiceClientThreadGroup");
    }

    public void uncaughtException(Thread t, Throwable e) {
        handle(e);
    }

    /**
     * Shared error handling for threads of this group and for virtual threads that do not run in a
     * ThreadGroup. Dialogs and shutdown run on the EDT.
     */
    static void handle(Throwable e) {
        if (!java.awt.EventQueue.isDispatchThread()) {
            java.awt.EventQueue.invokeLater(() -> handle(e));
            return;
        }

        Logger logger = LoggerFactory.getLogger(AppleJuiceThreadGroup.class);
        if (e instanceof WrongPasswordException) {
            AppleJuiceDialog.getApp().informWrongPassword();
        } else if (e.getClass() == CoreLostException.class) {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            String message = languageSelector.getFirstAttributeByTagName("javagui.startup.connectionerror");

            message = message.replaceFirst("%s", OptionsManagerImpl.getInstance().getRemoteSettings().getHost());
            AppleJuiceDialog.closeWithErrormessage(message, true);
        } else {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }
}
