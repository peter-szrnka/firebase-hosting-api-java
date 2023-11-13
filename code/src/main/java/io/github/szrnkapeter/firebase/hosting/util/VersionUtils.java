package io.github.szrnkapeter.firebase.hosting.util;

import io.github.szrnkapeter.firebase.hosting.config.FirebaseHostingApiConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.szrnkapeter.firebase.hosting.util.Constants.SITES;
import static io.github.szrnkapeter.firebase.hosting.util.Constants.VERSIONS;

/**
 * Version scraping utilities
 *
 * @author Peter Szrnka
 * @since 0.8
 */
public class VersionUtils {

    private VersionUtils() {}

    public static String getVersionId(String version) {
        if(version == null || version.isEmpty()) {
            return null;
        }

        return version.substring(version.lastIndexOf("/") + 1);
    }

    public static String getVersionName(FirebaseHostingApiConfig config, String version) {
        if(version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Version field is mandatory!");
        }

        String versionName = SITES + config.getSiteId() + VERSIONS + version;

        Pattern p = Pattern.compile(SITES + config.getSiteId() + VERSIONS + ".*");
        Matcher m = p.matcher(version);

        if (m.matches()) {
            versionName = version;
        }

        return versionName;
    }
}
