package de.applejuicenet.client.fassade.entity;

public interface ShareEntry {

	public enum SHAREMODE {
		NOT_SHARED(-1), SUBDIRECTORY(0), SINGLEDIRECTORY(1);

		SHAREMODE(int i) {
			wert = i;
		}

		private int wert;

		public int getWert() {
			return wert;
		}

		public static SHAREMODE getByValue(int value) {
			switch (value) {
				case -1:
					return NOT_SHARED;
				case 0:
					return SUBDIRECTORY;
				case 1:
					return SINGLEDIRECTORY;
				default: {
					throw new RuntimeException("invalid value for enum SHAREMODE");
				}
			}
		}
	}

	String getDir();

	SHAREMODE getShareMode();
}
