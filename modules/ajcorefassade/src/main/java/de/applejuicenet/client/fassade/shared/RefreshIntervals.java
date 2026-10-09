package de.applejuicenet.client.fassade.shared;

/**
 * Zentrale Aktualisierungsintervalle in Millisekunden.
 */
public final class RefreshIntervals
{
   /** Abfrageintervall des Core-Pollings (Downloads, Uploads, Statusleiste). */
   public static final long CORE_POLL_MS = 1000;

   /** Aktualisierung der Partliste eines einzelnen Downloads. */
   public static final long PARTLIST_REFRESH_MS = 2000;

   /** Mindestalter einer Suche, bevor sie abgebrochen wird (Wartezeit davor). */
   public static final long SEARCH_CANCEL_MIN_AGE_MS = 10000;

   /** Wartezeit zwischen Wiederholungen eines fehlgeschlagenen Suchabbruchs. */
   public static final long SEARCH_CANCEL_RETRY_MS = 4000;

   private RefreshIntervals()
   {
   }
}
