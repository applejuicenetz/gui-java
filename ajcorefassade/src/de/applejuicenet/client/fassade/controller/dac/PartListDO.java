package de.applejuicenet.client.fassade.controller.dac;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/controller/dac/Attic/PartListDO.java,v 1.2 2004/12/03 15:51:44 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PartListDO {
	public static final Color COLOR_TYPE_UEBERPRUEFT = Color.GREEN;

	public static final Color COLOR_TYPE_OK = Color.BLACK;

	public static final Color COLOR_TYPE_0 = Color.RED;

	public static final Color COLOR_TYPE_9 = new Color(25, 25, 250);

	public static final Color COLOR_TYPE_8 = new Color(50, 50, 250);

	public static final Color COLOR_TYPE_7 = new Color(75, 75, 250);

	public static final Color COLOR_TYPE_6 = new Color(100, 100, 250);

	public static final Color COLOR_TYPE_5 = new Color(125, 125, 250);

	public static final Color COLOR_TYPE_4 = new Color(150, 150, 250);

	public static final Color COLOR_TYPE_3 = new Color(175, 175, 250);

	public static final Color COLOR_TYPE_2 = new Color(200, 200, 250);

	public static final Color COLOR_TYPE_1 = new Color(225, 225, 250);

	public static final Color COLOR_TYPE_10 = Color.BLUE;

	public static final Color COLOR_READY_10 = new Color(255, 255, 132);

	public static final Color COLOR_READY_30 = new Color(255, 255, 98);

	public static final Color COLOR_READY_50 = new Color(255, 255, 0);

	public static final Color COLOR_READY_70 = new Color(210, 210, 0);

	public static final Color COLOR_READY_90 = new Color(176, 176, 0);

	public static final Color COLOR_READY_100 = new Color(157, 157, 0);

	public static final int MAIN_PARTLIST = 0;

	public static final int SOURCE_PARTLIST = 1;

	private Object valueHolderDO;

	private long groesse;

	private List parts = new ArrayList();

	private int type;

	public PartListDO(DownloadDO downloadDO) {
		valueHolderDO = downloadDO;
		type = MAIN_PARTLIST;
	}

	public PartListDO(DownloadSourceDO downloadSourceDO) {
		valueHolderDO = downloadSourceDO;
		type = SOURCE_PARTLIST;
	}

	public int getPartListType() {
		return type;
	}

	public long getGroesse() {
		return groesse;
	}

	public void addPart(Part aPart) {
		parts.add(aPart);
	}

	public void removeAllParts() {
		parts.clear();
	}

	public Part[] getParts() {
		return (Part[]) parts.toArray(new Part[parts.size()]);
	}

	public void setGroesse(long groesse) {
		this.groesse = groesse;
	}

	public Object getValueDO() {
		return valueHolderDO;
	}

	public double getProzentVerfuegbar() {
		Part[] allParts = getParts();
		if (allParts == null || allParts.length == 0) {
			return -1;
		}
		long available = 0;
		for (int i = 0; i < allParts.length - 1; i++) {
			if (allParts[i].getType() != 0) {
				available += allParts[i + 1].getFromPosition()
						- allParts[i].getFromPosition();
			}
		}
		if (allParts[allParts.length - 1].getType() != 0) {
			available += groesse
					- allParts[allParts.length - 1].getFromPosition();
		}
		return ((available * 100.0) / groesse);
	}

	public class Part {
		private long fromPosition;

		private int type;

		public Part(long fromPosition, int type) {
			this.fromPosition = fromPosition;
			this.type = type;
		}

		public long getFromPosition() {
			return fromPosition;
		}

		public int getType() {
			return type;
		}

		public boolean equals(Object obj) {
			if (obj.getClass() != Part.class) {
				return false;
			}
			if (((Part) obj).getFromPosition() != fromPosition) {
				return false;
			}
			if (((Part) obj).getType() != type) {
				return false;
			}
			return true;
		}
	}
}
