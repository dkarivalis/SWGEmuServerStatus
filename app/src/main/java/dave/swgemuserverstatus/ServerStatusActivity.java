package dave.swgemuserverstatus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import dave.swgemuserverstatus.model.ZoneServer;
import dave.swgemuserverstatus.network.DownloadUrlTask;
import dave.swgemuserverstatus.util.TimeUtils;

/**
 * Main screen of our application. Responsible for displaying the server status of SWGEmu's
 * Basilisk & TC-Nova servers, while providing an easy way to get to SWGEmu's forums.
 *
 * @author dave
 */
public class ServerStatusActivity extends AppCompatActivity implements
                                                               DownloadUrlTask.DownloadUrlListener {

    /** Main view group of our activity where we display the server status cards. */
    private ViewGroup content;
    /** Shared prefs to persist the last refreshed timestamp. */
    private SharedPreferences sharedPref;
    /** List zone server statuses returned from server. */
    @NonNull
    private final List<ZoneServer> zoneServers = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_status);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.gold));
        setSupportActionBar(toolbar);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            final Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BROWSER_FORUMS_URL));
            startActivity(browserIntent);
            }
        });

        content = (ViewGroup) findViewById(R.id.content);
        sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        FirebaseMessaging.getInstance().subscribeToTopic("server-status");
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFromNetwork();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_server_status, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        //get the last time we refreshed from network
        final long lastRefreshedTimestamp =
                sharedPref.getLong(Constants.PREF_LAST_REFRESHED, System.currentTimeMillis());

        //make it pretty
        final long durationSinceLastRefresh =
                (System.currentTimeMillis() - lastRefreshedTimestamp) / 1000;
        final String durationPrettyPrint =
                TimeUtils.durationToPrettyPrint(durationSinceLastRefresh);

        //set it in our menu item
        menu.getItem(0).setTitle("Last Refreshed: " + durationPrettyPrint);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_last_refreshed) {
            refreshFromNetwork();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Refreshes the Basilisk & TC-Nova server status from the network.
     */
    private void refreshFromNetwork() {
        //clear previous fetched server statuses
        zoneServers.clear();

        //fetch basilisk server status
        new DownloadUrlTask(new WeakReference<DownloadUrlTask.DownloadUrlListener>(this),
                Constants.ZONE_SERVER_BASILISK)
                .execute();

        //fetch tc-nova server status
        new DownloadUrlTask(new WeakReference<DownloadUrlTask.DownloadUrlListener>(this),
                Constants.ZONE_SERVER_TC_NOVA)
                .execute();

        //write down the last time we refreshed from network
        sharedPref.edit()
                .putLong(Constants.PREF_LAST_REFRESHED, System.currentTimeMillis())
                .apply();
    }

    /**
     * Refreshes the UI with our zone servers.
     */
    private void refreshZoneServers() {
        content.removeAllViews();

        for (final ZoneServer zoneServer : zoneServers) {
            final View zoneServerCard =
                    LayoutInflater.from(this).inflate(R.layout.card_zone_server, content, false);

            final TextView cardHeaderText =
                    (TextView) zoneServerCard.findViewById(R.id.card_header_text);
            cardHeaderText.setText(zoneServer.name);

            final TextView cardBodyText =
                    (TextView) zoneServerCard.findViewById(R.id.card_body_text);

            final String body;
            if (zoneServer.status.equals(Constants.SERVER_STATUS_UP)
                    || zoneServer.status.equals(Constants.SERVER_STATUS_DOWN)) {
                body = "<b>Status:</b> " + zoneServer.status + "<br>"
                        + "<b>Population:</b> " + zoneServer.users.connected + "<br>"
                        + "<b>Highest Population:</b> " + zoneServer.users.max + "<br>"
                        + "<b>Maximum Capacity:</b> " + zoneServer.users.cap + "<br>"
                        + "<b>Uptime:</b> "
                        + TimeUtils.durationToPrettyPrint(zoneServer.uptime);
                setHtmlToTextView(body, cardBodyText);

            } else if (zoneServer.status.equals(Constants.SERVER_STATUS_LOADING)
                    || zoneServer.status.equals(Constants.SERVER_STATUS_LOCKED)){
                body = "<b>Status:</b> " + zoneServer.status;
                setHtmlToTextView(body, cardBodyText);
            }

            zoneServerCard.setTag(zoneServer.name);
            content.addView(zoneServerCard);
        }
    }

    @SuppressWarnings("deprecation")
    private static void setHtmlToTextView(@NonNull final String html,
                                          @NonNull final TextView textView) {
        final Spanned spanned;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = Html.fromHtml(html);
        }
        textView.setText(spanned);
    }

    @Override
    public void updateZoneServer(final ZoneServer zoneServer) {
        zoneServers.add(zoneServer);
        refreshZoneServers();
    }
}
