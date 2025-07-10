package io.github.exampleuser.exampleplugin.messenger.config;

import io.github.exampleuser.exampleplugin.messenger.broker.BrokerType;
import io.github.milkdrinkers.crate.Config;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Config object for messaging services
 */
@SuppressWarnings("unused")
public record MessengerConfig(
    boolean enabled,
    long pollingInterval,
    long cleanupInterval,
    BrokerType brokerType,
    Addresses addresses,
    String username,
    String password,
    Boolean ssl,
    String virtualHost
) {
    /**
     * Gets messaging config from file.
     *
     * @param cfg the cfg
     * @return the messaging config from file
     */
    public static MessengerConfig fromConfig(Config cfg) {
        return MessengerConfig.builder()
            .withEnabled(cfg.getOrDefault("messenger.enabled", true))
            .withPollingInterval(cfg.getLong("messenger.polling-interval"))
            .withCleanupInterval(cfg.getLong("messenger.cleanup-interval"))
            .withBroker(cfg.getString("messenger.type"))
            .withAddresses(cfg.getString("messenger.address"))
            .withUsername(cfg.getString("messenger.username"))
            .withPassword(cfg.getString("messenger.password"))
            .withSSL(cfg.getOrDefault("messenger.ssl", false))
            .withVirtualHost(cfg.getString("messenger.virtual-host"))
            .build();
    }

    /**
     * Get a config builder instance.
     *
     * @return builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The type Messenger config builder.
     */
    public static final class Builder {
        private static final Logger LOGGER = LoggerFactory.getLogger(MessengerConfig.class);

        private Builder() {
        }

        private @Nullable Boolean enabled;
        private @Nullable Long pollingInterval;
        private @Nullable Long cleanupInterval;
        private @Nullable String broker;
        private @Nullable Addresses addresses;
        private @Nullable String username;
        private @Nullable String password;
        private @Nullable Boolean ssl;
        private @Nullable String virtualHost;

        public Builder withEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder withPollingInterval(long pollingInterval) {
            this.pollingInterval = pollingInterval;
            return this;
        }

        public Builder withCleanupInterval(long cleanupInterval) {
            this.cleanupInterval = cleanupInterval;
            return this;
        }

        public Builder withBroker(String broker) {
            this.broker = broker;
            return this;
        }

        public Builder withAddresses(Object address) {
            this.addresses = Addresses.of(address);
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withSSL(boolean ssl) {
            this.ssl = ssl;
            return this;
        }

        public Builder withVirtualHost(String virtualHost) {
            this.virtualHost = virtualHost;
            return this;
        }

        public MessengerConfig build() {
            if (enabled == null)
                enabled = false;

            if (pollingInterval == null)
                pollingInterval = 1000L; // Default to 1 second

            if (cleanupInterval == null)
                cleanupInterval = 30000L; // Default to 30 seconds

            if (cleanupInterval < 10000L) {
                LOGGER.warn("Messenger \"cleanup-interval\" was set to less than the minimum 10000 ({}), using default.", cleanupInterval);
                cleanupInterval = 10000L; // Minimum cleanup interval of 10 seconds
            }

            if (pollingInterval > cleanupInterval / 3) {
                LOGGER.warn("Messenger \"polling-interval\" was set to more than the maximum \"cleanup-interval\" divided by three ({}), using default.", pollingInterval);
                pollingInterval = cleanupInterval / 3;
            }

            BrokerType brokerType = BrokerType.fromName(broker);
            if (brokerType == null) {
                LOGGER.warn("Messenger \"type\" is invalid, using default \"{}\".", BrokerType.DATABASE.getName());
                brokerType = BrokerType.DATABASE;
            }

            if (addresses == null)
                addresses = Addresses.of(null);

            if (username == null)
                username = "";

            if (password == null)
                password = "";

            if (ssl == null)
                ssl = false;

            if (virtualHost == null)
                virtualHost = "/";

            return new MessengerConfig(enabled, pollingInterval, cleanupInterval, brokerType, addresses, username, password, ssl, virtualHost);
        }
    }
}
