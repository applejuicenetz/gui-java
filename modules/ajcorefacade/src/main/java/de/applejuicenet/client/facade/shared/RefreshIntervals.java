package de.applejuicenet.client.facade.shared;

/**
 * Central refresh intervals in milliseconds.
 *
 * The two intervals shown in the options (core polling and part list) have a default and an allowed
 * range. The part list interval is read by running workers on every cycle, so a change in the options
 * takes effect without a restart.
 */
public final class RefreshIntervals
{
   /** Default query interval of the core polling (downloads, uploads, status bar). */
   public static final long CORE_POLL_DEFAULT_MS = 1000;
   public static final long CORE_POLL_MIN_MS = 500;
   public static final long CORE_POLL_MAX_MS = 10000;

   /** Default refresh of the part list of a single download. */
   public static final long PARTLIST_REFRESH_DEFAULT_MS = 2000;
   public static final long PARTLIST_REFRESH_MIN_MS = 1000;
   public static final long PARTLIST_REFRESH_MAX_MS = 30000;

   /** Minimum age of a search before it is cancelled (wait time before that). */
   public static final long SEARCH_CANCEL_MIN_AGE_MS = 10000;

   /** Wait time between retries of a failed search cancellation. */
   public static final long SEARCH_CANCEL_RETRY_MS = 4000;

   private static volatile long partlistRefreshMs = PARTLIST_REFRESH_DEFAULT_MS;

   private RefreshIntervals()
   {
   }

   public static long clamp(long value, long min, long max)
   {
      return Math.max(min, Math.min(max, value));
   }

   /** Current refresh interval of the part list; read by the workers on every cycle. */
   public static long partlistRefreshMs()
   {
      return partlistRefreshMs;
   }

   public static void setPartlistRefreshMs(long millis)
   {
      partlistRefreshMs = clamp(millis, PARTLIST_REFRESH_MIN_MS, PARTLIST_REFRESH_MAX_MS);
   }
}
