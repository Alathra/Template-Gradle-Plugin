package io.github.exampleuser.exampleplugin.updatechecker;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Semantic version.
 */
public class SemanticVersion {
    private final static Pattern SEMVER_REGEX = Pattern.compile("^(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)(?:-(?<prerelease>(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+(?<meta>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");

    // Base fields
    private final long major; // The Major version
    private final long minor; // The Minor version
    private final long patch; // The Patch version
    private final String preRelease; // The pre-release data like "SNAPSHOT-1" or "RC-3"
    private final String meta; // The build-metadata

    // Generated fields
    private final String version; // The version consisting of only Major.Minor.Patch
    private final String versionFull; // The entire version string
    private final boolean isReleaseCandidate; // Whether this is a pre-release
    private final boolean isSnapshot; // Whether this is a pre-release

    /**
     * Creates a SemanticVersion object from a version string
     * </p>
     * @param versionString the string to parse version from
     * @throws NullPointerException thrown when major, minor or patch version are missing
     * @throws IllegalStateException thrown when the previous match operation failed
     * @throws IllegalArgumentException thrown when regex groups cant be found
     */
    private SemanticVersion(String versionString) throws NullPointerException, IllegalStateException, IllegalArgumentException {
        // Strip preceding version "V" in version
        if (versionString.toUpperCase().startsWith("V"))
            versionString = versionString.substring(1);

        // Grab version details from string
        final Matcher matcher = SEMVER_REGEX.matcher(versionString);
        if (!matcher.matches())
            throw new NullPointerException("Version could not be parsed from version string when constructing SemanticVersion object.");

        final Optional<Long> major = Optional.ofNullable(matcher.group("major")).map(Long::parseLong);
        final Optional<Long> minor = Optional.ofNullable(matcher.group("minor")).map(Long::parseLong);
        final Optional<Long> patch = Optional.ofNullable(matcher.group("patch")).map(Long::parseLong);
        final Optional<String> preRelease = Optional.ofNullable(matcher.group("prerelease"));
        final Optional<String> meta = Optional.ofNullable(matcher.group("meta"));

        // Check for missing data
        if (major.isEmpty())
            throw new NullPointerException("Major version could not be parsed from version string when constructing SemanticVersion object.");

        if (minor.isEmpty())
            throw new NullPointerException("Major version could not be parsed from version string when constructing SemanticVersion object.");

        if (patch.isEmpty())
            throw new NullPointerException("Major version could not be parsed from version string when constructing SemanticVersion object.");

        // Base fields
        this.major = major.get();
        this.minor = minor.get();
        this.patch = patch.get();
        this.preRelease = preRelease.orElse("");
        this.meta = meta.orElse("");

        // Generated fields
        this.version = concatenateVersionString(this.major, this.minor, this.patch);
        this.versionFull = concatenateVersionFullString(this.major, this.minor, this.patch, this.preRelease, this.meta);
        this.isReleaseCandidate = versionFull.contains("-RC");
        this.isSnapshot = versionFull.contains("-SNAPSHOT");
    }

    /**
     * Create a SemanticVersion object from a version string
     *
     * @param version a string containing a semantic version
     * @return SemanticVersion or null
     */
    public static @Nullable SemanticVersion of(String version) {
        try {
            return new SemanticVersion(version);
        } catch (NullPointerException | IllegalStateException | IllegalArgumentException e) {
            return null;
        }
    }


    /**
     * Of semantic version.
     *
     * @param major      the major version
     * @param minor      the minor version
     * @param patch      the patch version
     * @param preRelease the pre-release version
     * @param meta       the build-meta
     * @return the semantic version
     */
    public static @Nullable SemanticVersion of(long major, long minor, long patch, String preRelease, String meta) {
        return SemanticVersion.of(concatenateVersionFullString(major, minor, patch, preRelease, meta));
    }

    /**
     * Contains version comparison results.
     */
    public enum VersionCheckResult {
        /**
         * Newer version check result.
         */
        NEWER,
        /**
         * Equal version check result.
         */
        EQUAL,
        /**
         * Older version check result.
         */
        OLDER
    }

    /**
     * Check if two versions are the same.
     *
     * @param newVersion the new version
     * @param oldVersion the old version
     * @return if newVersion is same as oldVersion
     */
    public static boolean isEqual(SemanticVersion newVersion, SemanticVersion oldVersion) {
        return compare(newVersion, oldVersion).equals(VersionCheckResult.EQUAL);
    }

    /**
     * Check if one version is newer.
     *
     * @param newVersion the new version
     * @param oldVersion the old version
     * @return if newVersion is newer
     */
    public static boolean isNewer(SemanticVersion newVersion, SemanticVersion oldVersion) {
        return compare(newVersion, oldVersion).equals(VersionCheckResult.NEWER);
    }

    /**
     * Check if one version is older.
     *
     * @param newVersion the new version
     * @param oldVersion the old version
     * @return if newVersion is older
     */
    public static boolean isOlder(SemanticVersion newVersion, SemanticVersion oldVersion) {
        return compare(newVersion, oldVersion).equals(VersionCheckResult.OLDER);
    }

    /**
     * Check if one version is newer or same.
     *
     * @param newVersion the new version
     * @param oldVersion the old version
     * @return if newVersion is newer or equal to
     */
    public static boolean isNewerOrEqual(SemanticVersion newVersion, SemanticVersion oldVersion) {
        return isNewer(newVersion, oldVersion) || isEqual(newVersion, oldVersion);
    }

    /**
     * Check if one version is older or same.
     *
     * @param newVersion the new version
     * @param oldVersion the old version
     * @return if newVersion is older or equal to
     */
    public static boolean isOlderOrEqual(SemanticVersion newVersion, SemanticVersion oldVersion) {
        return isOlder(newVersion, oldVersion) || isEqual(newVersion, oldVersion);
    }

    /**
     * Compare major, minor and patch version.
     *
     * @param newVersion the new version
     * @param oldVersion the old version
     * @return the version check result
     */
    public static VersionCheckResult compare(SemanticVersion newVersion, SemanticVersion oldVersion) {
        double majorChange = newVersion.getMajor() - oldVersion.getMajor();
        double minorChange = newVersion.getMinor() - oldVersion.getMinor();
        double patchChange = newVersion.getPatch() - oldVersion.getPatch();

        // If there are any changes in versioning we know whether the version is newer or not
        if (majorChange != 0)
            return majorChange < 0 ? VersionCheckResult.OLDER : VersionCheckResult.NEWER;

        if (minorChange != 0)
            return minorChange < 0 ? VersionCheckResult.OLDER : VersionCheckResult.NEWER;

        if (patchChange != 0)
            return patchChange < 0 ? VersionCheckResult.OLDER : VersionCheckResult.NEWER;

        return compareMetadata(newVersion, oldVersion);
    }

    /**
     * Compare build-metadata version.
     *
     * @param newVersion the new version
     * @param oldVersion the old version
     * @return the version check result
     */
    public static VersionCheckResult compareMetadata(SemanticVersion newVersion, SemanticVersion oldVersion) {
        if (newVersion.isReleaseCandidate() && oldVersion.isReleaseCandidate()) {
            // Compare release candidate build number
            long rcBuild1 = Long.parseLong(newVersion.getPreRelease().substring(3));
            long rcBuild2 = Long.parseLong(oldVersion.getPreRelease().substring(3));

            if (rcBuild1 == rcBuild2)
                return VersionCheckResult.EQUAL;

            return rcBuild1 > rcBuild2 ? VersionCheckResult.NEWER : VersionCheckResult.OLDER;
        } else if (newVersion.isReleaseCandidate()) {
            return VersionCheckResult.NEWER;
        }

        if (newVersion.isSnapshot() && oldVersion.isSnapshot()) {
            // Compare snapshot build number
            long snapshotBuild1 = Long.parseLong(newVersion.getPreRelease().substring(9));
            long snapshotBuild2 = Long.parseLong(oldVersion.getPreRelease().substring(9));

            if (snapshotBuild1 == snapshotBuild2)
                return VersionCheckResult.EQUAL;

            return snapshotBuild1 > snapshotBuild2 ? VersionCheckResult.NEWER : VersionCheckResult.OLDER;
        } else if (newVersion.isSnapshot()) {
            return VersionCheckResult.NEWER;
        }

        return VersionCheckResult.EQUAL;
    }

    /**
     * Gets major version.
     *
     * @return the major
     */
    public long getMajor() {
        return major;
    }

    /**
     * Gets minor minor.
     *
     * @return the minor
     */
    public long getMinor() {
        return minor;
    }

    /**
     * Gets patch version.
     *
     * @return the patch
     */
    public long getPatch() {
        return patch;
    }

    /**
     * Gets pre-release version.
     *
     * @return the pre release
     */
    public String getPreRelease() {
        return preRelease;
    }

    /**
     * Gets build-metadata.
     *
     * @return the metadata
     */
    public String getMeta() {
        return meta;
    }

    /**
     * Gets the simple Semantic version string containing only: major, minor and patch.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the full Semantic version string containing: major, minor, patch, pre-release and build-metadata.
     *
     * @return the full version
     */
    public String getVersionFull() {
        return versionFull;
    }

    /**
     * Is this version a release candidate.
     *
     * @return the boolean
     */
    public boolean isReleaseCandidate() {
        return isReleaseCandidate;
    }

    /**
     * Is this version a snapshot.
     *
     * @return the boolean
     */
    public boolean isSnapshot() {
        return isSnapshot;
    }

    /**
     * Concatenates to a Semantic versioning string
     */
    private static String concatenateVersionString(long major, long minor, long patch) {
        return "%s.%s.%s".formatted(major, minor, patch);
    }

    /**
     * Concatenates to a full Semantic versioning string
     */
    private static String concatenateVersionFullString(long major, long minor, long patch, String preRelease, String meta) {
        return "%s%s%s".formatted(concatenateVersionString(major, minor, patch), (preRelease.isEmpty() ? "" : "-" + preRelease), (meta.isEmpty() ? "" : "+" + meta));
    }
}
