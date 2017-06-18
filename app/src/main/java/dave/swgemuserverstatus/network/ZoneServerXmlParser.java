package dave.swgemuserverstatus.network;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import dave.swgemuserverstatus.model.Users;
import dave.swgemuserverstatus.model.ZoneServer;

/**
 * Parses the server status xml page in to a {@link ZoneServer} model.
 * Pattern derived from https://developer.android.com/training/basics/network-ops/xml.html
 *
 * @author dave
 */
class ZoneServerXmlParser {
    private static final String ns = null;

    ZoneServer parse(final InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readZoneServer(parser);
        } finally {
            in.close();
        }
    }

    private ZoneServer readZoneServer(final XmlPullParser parser) throws XmlPullParserException,
                                                                         IOException {
        parser.require(XmlPullParser.START_TAG, ns, "zoneServer");
        String name = null;
        String status = null;
        Users users = null;
        long uptime = -1;
        long lastUpdated = -1;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String elementName = parser.getName();
            switch (elementName) {
                case "name":
                    name = readName(parser);
                    break;

                case "status":
                    status = readStatus(parser);
                    break;

                case "users":
                    users = readUsers(parser);
                    break;

                case "uptime":
                    uptime = readUptime(parser);
                    break;

                case "timestamp":
                    lastUpdated = readLastUpdated(parser);
                    break;

                default:
                    skip(parser);
                    break;
            }
        }
        return new ZoneServer(name, status, users, uptime, lastUpdated);
    }

    private String readName(final XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        final String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private String readStatus(final XmlPullParser parser) throws IOException,
                                                                 XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "status");
        String status = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "status");
        return status;
    }

    private Users readUsers(final XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "users");
        Long connected = null;
        Long cap = null;
        Long max = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String elementName = parser.getName();
            switch (elementName) {
                case "connected":
                    connected = readConnected(parser);
                    break;

                case "cap":
                    cap = readCap(parser);
                    break;

                case "max":
                    max = readMax(parser);
                    break;

                default:
                    skip(parser);
                    break;
            }
        }
        return (connected != null && cap != null && max != null)
                ? new Users(connected, cap, max)
                : null;
    }

    private Long readConnected(final XmlPullParser parser) throws IOException,
                                                                  XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "connected");
        final Long connected = readLong(parser);
        parser.require(XmlPullParser.END_TAG, ns, "connected");
        return connected;
    }

    private Long readCap(final XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "cap");
        final Long cap = readLong(parser);
        parser.require(XmlPullParser.END_TAG, ns, "cap");
        return cap;
    }

    private Long readMax(final XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "max");
        final Long max = readLong(parser);
        parser.require(XmlPullParser.END_TAG, ns, "max");
        return max;
    }

    private Long readUptime(final XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "uptime");
        final Long uptime = readLong(parser);
        parser.require(XmlPullParser.END_TAG, ns, "uptime");
        return uptime;
    }

    private Long readLastUpdated(final XmlPullParser parser) throws IOException,
                                                                    XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "timestamp");
        final Long lastUpdated = readLong(parser);
        parser.require(XmlPullParser.END_TAG, ns, "timestamp");
        return lastUpdated;
    }

    // For the tags name and status, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // For the tags connected, cap, max, uptime and timestamp, extracts their long values.
    private Long readLong(XmlPullParser parser) throws IOException, XmlPullParserException {
        Long result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            final String text = parser.getText();
            if (text != null && !text.isEmpty()) {
                result = Long.valueOf(text);
            }
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
