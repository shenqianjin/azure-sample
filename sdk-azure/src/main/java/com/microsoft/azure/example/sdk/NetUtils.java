/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.azure.example.sdk;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Networking-related convenience methods.
 */
public final class NetUtils {

    private static final String UNKNOWN_LOCALHOST = "UNKNOWN_LOCALHOST";

    private NetUtils() {
        // empty
    }

    /**
     * This method gets the network name of the machine we are running on. Returns "UNKNOWN_LOCALHOST" in the unlikely
     * case where the host name cannot be found.
     *
     * @return String the name of the local host
     */
    public static String getLocalHostname() {
        try {
            final InetAddress addr = InetAddress.getLocalHost();
            return addr == null ? UNKNOWN_LOCALHOST : addr.getHostName();
        } catch (final UnknownHostException uhe) {
            try {
                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                if (interfaces != null) {
                    while (interfaces.hasMoreElements()) {
                        final NetworkInterface nic = interfaces.nextElement();
                        final Enumeration<InetAddress> addresses = nic.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            final InetAddress address = addresses.nextElement();
                            if (!address.isLoopbackAddress()) {
                                final String hostname = address.getHostName();
                                if (hostname != null) {
                                    return hostname;
                                }
                            }
                        }
                    }
                }
            } catch (final SocketException se) {
                // ignore and log below.
            }
            // log.error("Could not determine local host name", uhe);
            return UNKNOWN_LOCALHOST;
        }
    }

    /**
     *  Returns the local network interface's MAC address if possible. The local network interface is defined here as
     *  the {@link NetworkInterface} that is both up and not a loopback interface.
     *
     * @return the MAC address of the local network interface or {@code null} if no MAC address could be determined.
     */
    public static byte[] getMacAddress() {
        byte[] mac = null;
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            try {
                final NetworkInterface localInterface = NetworkInterface.getByInetAddress(localHost);
                if (isUpAndNotLoopback(localInterface)) {
                    mac = localInterface.getHardwareAddress();
                }
                if (mac == null) {
                    final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                    if (networkInterfaces != null) {
                        while (networkInterfaces.hasMoreElements() && mac == null) {
                            final NetworkInterface nic = networkInterfaces.nextElement();
                            if (isUpAndNotLoopback(nic)) {
                                mac = nic.getHardwareAddress();
                            }
                        }
                    }
                }
            } catch (final SocketException e) {
                // log.error(e);
            }
            if (ArrayUtils.isEmpty(mac) && localHost != null) {
                // Emulate a MAC address with an IP v4 or v6
                final byte[] address = localHost.getAddress();
                // Take only 6 bytes if the address is an IPv6 otherwise will pad with two zero bytes
                mac = Arrays.copyOf(address, 6);
            }
        } catch (final UnknownHostException ignored) {
            // ignored
        }
        return mac;
    }

    /**
     * Returns the mac address, if it is available, as a string with each byte separated by a ":" character.
     * @return the mac address String or null.
     */
    public static String getMacAddressString() {
        final byte[] macAddr = getMacAddress();
        if (!ArrayUtils.isEmpty(macAddr)) {
            StringBuilder sb = new StringBuilder(String.format("%02X", macAddr[0]));
            for (int i = 1; i < macAddr.length; ++i) {
                sb.append("-").append(String.format("%02X", macAddr[i]));
            }
            return sb.toString();

        }
        return null;
    }

    private static boolean isUpAndNotLoopback(final NetworkInterface ni) throws SocketException {
        return ni != null && !ni.isLoopback() && ni.isUp();
    }

    /**
     * Converts a URI string or file path to a URI object.
     *
     * @param path the URI string or path
     * @return the URI object
     */
    public static URI toURI(final String path) {
        try {
            // Resolves absolute URI
            return new URI(path);
        } catch (final URISyntaxException e) {
            // A file path or a Apache Commons VFS URL might contain blanks.
            // A file path may start with a driver letter
            try {
                final URL url = new URL(path);
                return new URI(url.getProtocol(), url.getHost(), url.getPath(), null);
            } catch (MalformedURLException | URISyntaxException nestedEx) {
                return new File(path).toURI();
            }
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        System.out.println(addr.getHostName());
        System.out.println(NetUtils.getLocalHostname());
        System.out.println(NetUtils.getMacAddress());
        System.out.println(NetUtils.getMacAddressString());
        Pattern IPADDRESS_PATTERN = Pattern.compile("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}");
        String a = "asdfasdfasdfasdf123.123.123.123asdfasdf12.12.12.12sdfgfg";
        Matcher matcher = IPADDRESS_PATTERN.matcher(a);
        if (matcher.find()) {
            System.out.println(matcher.group());
        }

    }

}
