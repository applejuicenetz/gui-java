package de.applejuicenet.client.facade.shared;

/**
 * Zentrale Aktualisierungsintervalle in Millisekunden.
 */
public final class RefreshIntervals
{
   /** Query interval of the core polling (downloads, uploads, status bar). */
   public static final long CORE_POLL_MS = 1000;

   /** Refresh of the part list of a single download. */
   public static final long PARTLIST_REFRESH_MS = 2000;

   /** Minimum age of a search before it is cancelled (wait time before that). */
   public static final long SEARCH_CANCEL_MIN_AGE_MS = 10000;

   /** Wait time between retries of a failed search cancellation. */
   public static final long SEARCH_CANCEL_RETRY_MS = 4000;

   private RefreshIntervals()
   {
   }
}
