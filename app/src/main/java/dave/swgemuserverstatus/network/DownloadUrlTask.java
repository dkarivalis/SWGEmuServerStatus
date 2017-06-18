package dave.swgemuserverstatus.network;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import dave.swgemuserverstatus.model.ZoneServer;

/**
 * Downloads & parses the xml content of the inputted url, returning a {@link ZoneServer}.
 *
 * @author dave
 */
public class DownloadUrlTask extends AsyncTask<Void, Void, ZoneServer> {
    private static final String TAG = DownloadUrlTask.class.getSimpleName();

    private final String url;
    private final WeakReference<DownloadUrlListener> weakListener;

    public DownloadUrlTask(final WeakReference<DownloadUrlListener> weakListener,
                           final String url) {
        this.weakListener = weakListener;
        this.url = url;
    }

    protected ZoneServer doInBackground(Void... voids) {
        InputStream stream = null;
        ZoneServerXmlParser zoneServerXmlParser = new ZoneServerXmlParser();
        ZoneServer zoneServer = null;
        try {
            if (this.url != null && !this.url.isEmpty()) {
                stream = downloadUrl(this.url);
                zoneServer = zoneServerXmlParser.parse(stream);
            }
        } catch (final MalformedURLException mue) {
            Log.d(TAG, "URL MalformedURLException in DownloadUrl: " + mue);
        } catch (final IOException ioe) {
            Log.d(TAG, "IOException in DownloadUrl: " + ioe);
        } catch (final XmlPullParserException xppe) {
            Log.d(TAG, "XmlPullParserException in DownloadUrl: " + xppe);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ioe) {
                    Log.d(TAG, "IOException in DownloadUrl when closing stream: " + ioe);
                }
            }
        }
        return zoneServer;
    }

    private InputStream downloadUrl(final String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    protected void onPostExecute(final ZoneServer zoneServer) {
        if (zoneServer == null) return;

        DownloadUrlListener listener = weakListener.get();
        if (listener != null) {
            listener.updateZoneServer(zoneServer);
        }
    }

    public interface DownloadUrlListener {
        /**
         * Called when a {@link ZoneServer} model is updated.
         * @param zoneServer Updated zone server.
         */
        void updateZoneServer(ZoneServer zoneServer);
    }
}
