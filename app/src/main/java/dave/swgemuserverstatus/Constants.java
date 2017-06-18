package dave.swgemuserverstatus;

/**
 * Convenient spot for our constants.
 *
 * @author dave
 */
class Constants {

    private Constants() {
    }

    static final String ZONE_SERVER_BASILISK = "http://www.swgemu.com/status/basilisk.xml";
    static final String ZONE_SERVER_TC_NOVA = "http://www.swgemu.com/status/nova.xml";
    static final String SERVER_STATUS_UP = "up";
    static final String SERVER_STATUS_DOWN = "down";
    static final String SERVER_STATUS_LOADING = "loading";
    static final String SERVER_STATUS_LOCKED = "locked";
    static final String BROWSER_FORUMS_URL = "http://www.swgemu.com/forums/forum.php";
    static final String PREF_LAST_REFRESHED = "last_refreshed";
}
