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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * .
 *
 *
 *
 * Copyright: General Public License
 */
public class AppleJuiceThreadGroup extends ThreadGroup {

    /** What happens when the GUI cannot continue. Replaceable so tests never reach {@code System.exit}. */
    interface FatalActions {
        void wrongPassword();

        void coreLost();
    }

    private static final FatalActions DEFAULT_ACTIONS = new FatalActions() {
        public void wrongPassword() {
            AppleJuiceDialog.getApp().informWrongPassword();
        }

        public void coreLost() {
            LanguageSelector languageSelector = LanguageSelector.getInstance();
            String message = languageSelector.getFirstAttributeByTagName("javagui.startup.connectionerror");

            message = message.replaceFirst("%s", OptionsManagerImpl.getInstance().getRemoteSettings().getHost());
            AppleJuiceDialog.closeWithErrormessage(message, true);
        }
    };

    private static volatile FatalActions fatalActions = DEFAULT_ACTIONS;
    /** Several failing threads report the same lost core; only the first one starts the shutdown. */
    private static final AtomicBoolean fatalHandled = new AtomicBoolean();

    public AppleJuiceThreadGroup() {
        super("AppleJuiceClientThreadGroup");
    }

    public void uncaughtException(Thread t, Throwable e) {
        handle(e);
    }

    /** Replaces the fatal actions (null restores the defaults) and re-arms the shutdown guard. */
    static void setFatalActions(FatalActions actions) {
        fatalActions = actions == null ? DEFAULT_ACTIONS : actions;
        fatalHandled.set(false);
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
        boolean wrongPassword = e instanceof WrongPasswordException;
        boolean coreLost = e.getClass() == CoreLostException.class;

        if (wrongPassword || coreLost) {
            if (!fatalHandled.compareAndSet(false, true)) {
                logger.debug("Shutdown already in progress, ignoring {}", e.getClass().getSimpleName());
                return;
            }
            try {
                if (wrongPassword) {
                    fatalActions.wrongPassword();
                } else {
                    fatalActions.coreLost();
                }
            } catch (RuntimeException failure) {
                logger.error("Shutdown after {} failed", e.getClass().getSimpleName(), failure);
            }
        } else {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }
}
