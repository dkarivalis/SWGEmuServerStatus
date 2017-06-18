package dave.swgemuserverstatus.util;

import java.util.concurrent.TimeUnit;

/**
 * Utils related to time.
 *
 * @author dave
 */
public class TimeUtils {

    private TimeUtils() {
    }

    private static final int HOURS_IN_A_DAY = 24;
    private static final int MINUTES_IN_AN_HOUR = 60;
    private static final int SECONDS_IN_A_MINUTE = 60;

    /**
     * Converts seconds -> Xdays Yhours Zmins Tsec
     * @param durationInSeconds Time elapsed in seconds.
     * @return Pretty print of the time elapsed.
     */
    public static String durationToPrettyPrint(final long durationInSeconds) {
        final int days = (int) TimeUnit.SECONDS.toDays(durationInSeconds);
        final long hours = TimeUnit.SECONDS.toHours(durationInSeconds) - (days * HOURS_IN_A_DAY);
        final long minutes = TimeUnit.SECONDS.toMinutes(durationInSeconds)
                - (TimeUnit.SECONDS.toHours(durationInSeconds) * MINUTES_IN_AN_HOUR);
        final long seconds = TimeUnit.SECONDS.toSeconds(durationInSeconds)
                - (TimeUnit.SECONDS.toMinutes(durationInSeconds) * SECONDS_IN_A_MINUTE);

        StringBuilder sb = new StringBuilder();

        if (days == 1) {
            sb.append(days);
            sb.append("day ");
        } else if (days > 1) {
            sb.append(days);
            sb.append("days ");
        }

        if (hours == 1) {
            sb.append(hours);
            sb.append("hr ");
        } else if (hours > 1) {
            sb.append(hours);
            sb.append("hrs ");
        }

        if (minutes == 1) {
            sb.append(minutes);
            sb.append("min ");
        } else if (minutes > 1) {
            sb.append(minutes);
            sb.append("mins ");
        }

        if (seconds == 1) {
            sb.append(seconds);
            sb.append("sec ");
        } else if (seconds > 1) {
            sb.append(seconds);
            sb.append("sec ");
        }

        if (sb.length() == 0) {
            sb.append("moments ago");
        }

        return sb.toString();
    }
}
