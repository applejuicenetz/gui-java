package de.applejuicenet.client.gui.components;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.listener.LanguageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Copyright: General Public License
 */

public abstract class GuiController implements LanguageListener, DataUpdateListener {
    protected final Logger logger;

    public GuiController() {
        logger = LoggerFactory.getLogger(getClass());
    }

    public abstract void fireAction(int actionId, Object source);

    public abstract JComponent getComponent();

    public abstract void componentSelected();

    public abstract void componentLostSelection();

    public abstract Value[] getCustomizedValues();

    public final String getName() {
        return getClass().getName();
    }

    public final void fireLanguageChanged() {
        try {
            languageChanged();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public final void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        try {
            contentChanged(type, content);
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    protected abstract void languageChanged();

    protected abstract void contentChanged(DATALISTENER_TYPE type, Object content);
}
