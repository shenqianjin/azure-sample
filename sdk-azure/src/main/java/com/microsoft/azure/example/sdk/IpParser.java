package com.microsoft.azure.example.sdk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpParser {

    private static final Pattern IPADDRESS_PATTERN = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

    public static void main(String[] args) {

        String message = "Cannot open server 'sqlserver-20210513161507' requested by the login. Client with IP address '167.220.255.40' is not allowed to access the server.  " +
                "To enable access, use the Windows Azure Management Portal or run sp_set_firewall_rule on the master database to create a firewall rule for this IP address or address range. " +
                " It may take up to five minutes for this change to take effect. ClientConnectionId:9658751c-a023-4251-b2db-da28f94cd102\n";
        Matcher matcher = IPADDRESS_PATTERN.matcher(message);
        if (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
