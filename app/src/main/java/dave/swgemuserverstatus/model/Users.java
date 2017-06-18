package dave.swgemuserverstatus.model;

/**
 * Users model.
 *
 * @author dave
 */
public class Users {
    public final long connected;
    public final long cap;
    public final long max;

    public Users(final long connected, final long cap, final long max) {
        this.connected = connected;
        this.cap = cap;
        this.max = max;
    }

    @Override
    public String toString() {
        return "Users{" +
                "connected=" + connected +
                ", cap=" + cap +
                ", max=" + max +
                '}';
    }
}
