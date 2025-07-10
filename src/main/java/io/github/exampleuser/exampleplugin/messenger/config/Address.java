package io.github.exampleuser.exampleplugin.messenger.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Holds an addresses for connecting to things.
 *
 * @param host the host addresses
 * @param port the port (optional)
 */
@SuppressWarnings("unused")
public record Address(@NotNull String host, @Nullable Integer port) {
    /**
     * Parse any string into an addresses.
     *
     * @param address the addresses to parse
     * @return addresses record
     */
    public static @NotNull Address of(@Nullable String address) {
        if (address == null || address.isEmpty())
            return new Address(Addresses.DEFAULT_ADDRESS, null);

        final int lastDelimiterIndex = address.lastIndexOf(":");
        if (lastDelimiterIndex == -1) // No delimiter found
            return new Address(address, null);

        final String host = address.substring(0, lastDelimiterIndex);
        if (lastDelimiterIndex == address.length() - 1) // Delimiter is last character in string
            return new Address(host, null);

        final String portStr = address.substring(lastDelimiterIndex + 1);
        try {
            final int port = Integer.parseInt(portStr);
            if (port < 1 || port > 65535)
                return new Address(host, null);

            return new Address(host, port);
        } catch (NumberFormatException e) {
            return new Address(address, null);
        }
    }

    /**
     * Get the host of this address or a empty optional.
     *
     * @return the host wrapped in a optional
     */
    public @NotNull Optional<String> hostOptional() {
        return Optional.of(host);
    }

    /**
     * Get the port of this address or a empty optional.
     *
     * @return the port wrapped in a optional
     */
    public @NotNull Optional<Integer> portOptional() {
        return Optional.ofNullable(port);
    }

    /**
     * Get the full addresses of this addresses. Includes port if port is set.
     *
     * @return the addresses
     */
    public @NotNull String getAddress() {
        if (port() == null)
            return host();

        return host() + ":" + port();
    }

    @Override
    public @NotNull String toString() {
        return getAddress();
    }
}
