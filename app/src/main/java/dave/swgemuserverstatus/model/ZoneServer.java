package dave.swgemuserverstatus.model;

import android.text.TextUtils;

/**
 * ZoneServer model.
 *
 * @author dave
 */
public class ZoneServer {
    public final String name;
    public final String status;
    public final Users users;
    public final long uptime;
    public final long lastUpdated;

    public ZoneServer(final String name,
                       final String status,
                       final Users users,
                       final long uptime,
                       final long lastUpdated) {
        this.name = name;
        this.status = status;
        this.users = users;
        this.uptime = uptime;
        this.lastUpdated = lastUpdated;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(name) && !TextUtils.isEmpty(status) && users != null;
    }

    @Override
    public String toString() {
        return "ZoneServer{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", users=" + users +
                ", uptime=" + uptime +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
