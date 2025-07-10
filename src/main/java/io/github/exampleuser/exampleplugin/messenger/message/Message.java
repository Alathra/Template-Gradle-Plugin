package io.github.exampleuser.exampleplugin.messenger.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.UUID;

@SuppressWarnings("unused")
public class Message<T> implements OutgoingMessage<T>, IncomingMessage<T, Message<T>> {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Message.class, new MessageDeserializer())
        .registerTypeAdapter(Class.class, new ClassTypeAdapter())
        .create();

    @SerializedName("uuid")
    private final UUID uuid;

    @SerializedName("type")
    private final String messageType = "custom";

    @SerializedName("channel")
    private final String channelId;

    @SerializedName("payload")
    private final @NotNull T payload; // Generic payload that can be any object

    @SerializedName("payloadType")
    private final @NotNull Class<T> payloadType; // Store the class for deserialization

    public Message(
        @JsonProperty("uuid") UUID uuid,
        @JsonProperty("channel") String channelId,
        @JsonProperty("payload") @NotNull T payload,
        @JsonProperty("payloadType") @NotNull Class<T> payloadType
    ) {
        this.uuid = uuid;
        this.channelId = channelId;
        this.payload = payload;
        this.payloadType = payloadType;
    }

    @Override
    public @NotNull UUID getUUID() {
        return uuid;
    }

    @Override
    public @NotNull String getChannelID() {
        return channelId;
    }

    @Override
    public @NotNull T getPayload() {
        return payload;
    }

    @Override
    public @NotNull Class<T> getPayloadType() {
        return payloadType;
    }

    @Override
    public @NotNull String encode() {
        return GSON.toJson(this);
    }

    @Override
    public @NotNull Message<T> decode(final String json) {
        return from(json);
    }

    /**
     * Creates a new {@link Message} instance from a JSON string.
     *
     * @param json the JSON string representing the message
     * @return a new MessageImpl instance
     * @see #decode(String)
     */
    @SuppressWarnings("unchecked")
    public static <T> @NotNull Message<T> from(final String json) {
        return (Message<T>) GSON.fromJson(json, Message.class);
    }

    /**
     * Creates a new builder for constructing a {@link Message}.
     *
     * @return a new builder instance
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * Builder class for constructing a {@link Message}.
     */
    public static class Builder<T> {
        private UUID uuid;
        private String channelId;
        private T payload;

        private Builder() {
        }

        /**
         * Sets the UUID for the message.
         *
         * @param uuid the UUID to set
         * @return this builder instance
         */
        public Builder<T> uuid(@NotNull UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        /**
         * Sets the channel ID for the message.
         *
         * @param channelId the channel ID to set
         * @return this builder instance
         */
        public Builder<T> channelId(@NotNull String channelId) {
            this.channelId = channelId;
            return this;
        }

        /**
         * Sets the payload for the message.
         *
         * @param payload the payload to set, must not be null or empty
         * @return this builder instance
         */
        public Builder<T> payload(@NotNull T payload) {
            this.payload = payload;
            return this;
        }

        /**
         * Builds a new {@link Message} instance with the provided parameters.
         *
         * @return a new MessageImpl instance
         * @throws IllegalStateException if channelId or payload is null or empty
         */
        @SuppressWarnings("unchecked")
        public Message<T> build() {
            if (uuid == null)
                uuid = UUID.randomUUID();

            if (channelId == null || channelId.isEmpty())
                throw new IllegalStateException("Channel ID cannot be null or empty in a message");

            if (payload == null)
                throw new IllegalStateException("Payload cannot be null or empty in a message");

            return new Message<>(uuid, channelId, payload, (Class<T>) payload.getClass());
        }
    }

    /**
     * Custom deserializer for Gson to create instances of MessageImpl.
     */
    private static class MessageDeserializer implements JsonDeserializer<Message<?>> {
        @Override
        public Message<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final Builder<Object> builder = Message.builder();

            if (jsonObject.has("uuid") && !jsonObject.get("uuid").isJsonNull())
                builder.uuid(UUID.fromString(jsonObject.get("uuid").getAsString()));

            if (jsonObject.has("channel") && !jsonObject.get("channel").isJsonNull())
                builder.channelId(jsonObject.get("channel").getAsString());

            if (jsonObject.has("payload") && !jsonObject.get("payload").isJsonNull()) {
                final JsonElement payloadElement = jsonObject.get("payload");

                Object payload;
                if (jsonObject.has("payloadType") && !jsonObject.get("payloadType").isJsonNull()) {
                    try {
                        final String payloadTypeName = jsonObject.get("payloadType").getAsString();
                        payload = GSON.fromJson(payloadElement, Class.forName(payloadTypeName));
                    } catch (Exception e) {
                        payload = GSON.fromJson(payloadElement, Object.class);
                    }
                } else {
                    payload = GSON.fromJson(payloadElement, Object.class);
                }

                builder.payload(payload);
            }

            return builder.build();
        }
    }

    /**
     * Custom type adapter for Class objects to serialize/deserialize as class names.
     */
    private static class ClassTypeAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
        @Override
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getName());
        }

        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            try {
                return Class.forName(json.getAsString());
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Cannot find class: " + json.getAsString(), e);
            }
        }
    }
}
