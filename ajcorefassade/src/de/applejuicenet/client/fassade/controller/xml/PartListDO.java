package de.applejuicenet.client.fassade.controller.xml;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xml/PartListDO.java,v 1.1 2005/01/19 11:03:25 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

import java.util.ArrayList;
import java.util.List;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.entity.Part;
import de.applejuicenet.client.fassade.entity.PartList;

class PartListDO extends PartList{

	private Object valueHolder;
	private long groesse;
	private List<PartDO> parts = new ArrayList<PartDO>();
	private int type;

	public PartListDO(Download download) {
		valueHolder = download;
		type = MAIN_PARTLIST;
	}

	public PartListDO(DownloadSource downloadSource) {
		valueHolder = downloadSource;
		type = SOURCE_PARTLIST;
	}

	public int getPartListType() {
		return type;
	}

	public long getGroesse() {
		return groesse;
	}

	public void addPart(PartDO aPart) {
		parts.add(aPart);
	}

	public void removeAllParts() {
		parts.clear();
	}

	public PartDO[] getParts() {
		return (PartDO[]) parts.toArray(new PartDO[parts.size()]);
	}

	public void setGroesse(long groesse) {
		this.groesse = groesse;
	}

	public Object getValueObject() {
		return valueHolder;
	}

	class PartDO extends Part{
		private long fromPosition;
		private int type;

		public PartDO(long fromPosition, int type) {
			this.fromPosition = fromPosition;
			this.type = type;
		}

		public long getFromPosition() {
			return fromPosition;
		}

		public int getType() {
			return type;
		}
	}
}
