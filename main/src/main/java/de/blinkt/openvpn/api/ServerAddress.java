/*
 * Copyright (c) 2026 OpenVPN for Android contributors
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.api;

import java.net.URI;
import java.net.URISyntaxException;

public final class ServerAddress {
    public final String host;
    public final String port;
    public final boolean useUdp;

    private ServerAddress(String host, String port, boolean useUdp) {
        this.host = host;
        this.port = port;
        this.useUdp = useUdp;
    }

    public static ServerAddress parse(String value) {
        if (value == null || value.isEmpty())
            throw new IllegalArgumentException("serverAddress is empty");

        final URI uri;
        try {
            uri = new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("serverAddress is not a valid URI", e);
        }

        String scheme = uri.getScheme();
        if (!"tcp".equalsIgnoreCase(scheme) && !"udp".equalsIgnoreCase(scheme))
            throw new IllegalArgumentException("serverAddress protocol must be tcp or udp");

        if (uri.getUserInfo() != null || uri.getPath() != null && !uri.getPath().isEmpty()
                || uri.getQuery() != null || uri.getFragment() != null)
            throw new IllegalArgumentException("serverAddress must only contain protocol, host and port");

        String host = uri.getHost();
        int port = uri.getPort();
        if (host == null || host.isEmpty())
            throw new IllegalArgumentException("serverAddress host is empty or invalid");
        if (host.startsWith("[") && host.endsWith("]"))
            host = host.substring(1, host.length() - 1);
        if (port < 1 || port > 65535)
            throw new IllegalArgumentException("serverAddress port must be between 1 and 65535");

        return new ServerAddress(host, Integer.toString(port), "udp".equalsIgnoreCase(scheme));
    }
}
