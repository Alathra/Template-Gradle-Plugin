package io.github.exampleuser.exampleplugin.messenger.config;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds a single or multiple addresses.
 */
public final class Addresses {
    static final String DEFAULT_ADDRESS = "localhost";
    private final List<Address> addresses;

    Addresses(List<Address> addresses) {
        this.addresses = new ArrayList<>(addresses);
    }

    /**
     * Construct one or more addresses from a {@link String} or {@link Collections<String>} object.
     *
     * @param o An object that can be {@link String} or {@link Collections<String>}
     * @return an addresses object
     */
    public static Addresses of(@Nullable Object o) {
        final List<String> newAddresses = new ArrayList<>();
        if (o == null)
            newAddresses.add(DEFAULT_ADDRESS); // Add default addresses if invalid config entry

        if (o instanceof String stringAddress) // Add single addresses
            newAddresses.add(stringAddress);

        if (o instanceof List<?> addressList) { // Parse list of addresses
            final List<String> parsedAddresses = new ArrayList<>(addressList.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList());

            if (parsedAddresses.isEmpty())
                parsedAddresses.add(DEFAULT_ADDRESS);

            newAddresses.addAll(parsedAddresses);
        }

        return new Addresses(newAddresses.stream()
            .map(Address::of)
            .toList());
    }

    /**
     * Describes whether this addresses object holds a single or multiple addresses.
     */
    public enum AddressesType {
        SINGLE,
        MULTIPLE
    }

    /**
     * Gets the addresses type.
     *
     * @return the type
     */
    public AddressesType getType() {
        if (addresses.size() == 1)
            return AddressesType.SINGLE;

        return AddressesType.MULTIPLE;
    }

    /**
     * Get the address of this addresses object.
     *
     * @return address
     * @implNote Use {@link #getType()} before this to determine the expected return type
     */
    public Address getAddress() {
        return addresses.stream().findFirst().orElse(Address.of(null));
    }

    /**
     * Get the addresses of this addresses object.
     *
     * @return list of address
     * @implNote Use {@link #getType()} before this to determine the expected return type
     */
    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }
}
