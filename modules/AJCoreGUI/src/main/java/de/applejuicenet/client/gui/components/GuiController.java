package de.applejuicenet.client.gui.components;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.listener.DataUpdateListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.listener.LanguageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/GuiController.java,v 1.8 2005/01/19 16:22:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
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
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public final void fireContentChanged(DATALISTENER_TYPE type, Object content) {
        try {
            contentChanged(type, content);
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    protected abstract void languageChanged();

    protected abstract void contentChanged(DATALISTENER_TYPE type, Object content);
}
