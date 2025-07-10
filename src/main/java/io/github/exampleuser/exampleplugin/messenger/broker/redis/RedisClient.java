package io.github.exampleuser.exampleplugin.messenger.broker.redis;

import io.github.exampleuser.exampleplugin.messenger.config.Addresses;
import io.github.exampleuser.exampleplugin.messenger.config.MessengerConfig;
import redis.clients.jedis.*;

import java.util.stream.Collectors;

/**
 * A wrapping client implementation of Jedis
 */
final class RedisClient {
    private final UnifiedJedis jedis;

    RedisClient(MessengerConfig config) {
        this.jedis = createJedisClient(config);
    }

    private UnifiedJedis createJedisClient(MessengerConfig config) {
        final DefaultJedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder()
            .user(config.username())
            .password(config.password())
            .ssl(config.ssl())
            .timeoutMillis(Protocol.DEFAULT_TIMEOUT)
            .build();

        if (config.addresses().getType().equals(Addresses.AddressesType.SINGLE)) {
            return new JedisPooled(
                new HostAndPort(
                    config.addresses().getAddress().host(),
                    config.addresses().getAddress().portOptional().orElse(6379)
                ),
                jedisClientConfig
            );
        } else {
            return new JedisCluster(
                config.addresses().getAddresses().stream()
                    .map(a -> new HostAndPort(a.host(), a.portOptional().orElse(6379)))
                    .collect(Collectors.toSet()),
                jedisClientConfig
            );
        }
    }

    public void publish(String channel, String message) {
        jedis.publish(channel, message);
    }

    public void subscribe(JedisPubSub subscriber, String channel) {
        jedis.subscribe(subscriber, channel);
    }

    public boolean isAlive() {
        if (jedis instanceof JedisPooled jedisPooled) {
            return !jedisPooled.getPool().isClosed();
        } else if (jedis instanceof JedisCluster jedisCluster) {
            return !jedisCluster.getClusterNodes().isEmpty();
        } else {
            throw new RuntimeException("Unknown jedis type: " + jedis.getClass().getName());
        }
    }

    public void close() {
        jedis.close();
    }
}
