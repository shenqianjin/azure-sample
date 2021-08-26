/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package com.microsoft.sample.sqlserver;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.util.Objects;

public class JdbcUrl {

    private URIBuilder uri;

    private JdbcUrl(String url) {
        Preconditions.checkArgument(StringUtils.startsWith(url, "jdbc:"), "invalid jdbc url.");
        if (StringUtils.startsWith(url, "jdbc:sqlserver:")) {
            url = StringUtils.replaceOnce(url, ";", "?").replaceAll(";", "&");
        }
        try {
            this.uri = new URIBuilder(url.substring(5));
        } catch (final URISyntaxException e) {
            //throw new AzureToolkitRuntimeException("invalid jdbc url: %s", url);
        }
    }

    public static JdbcUrl from(String connectionString) {
        Preconditions.checkArgument(StringUtils.startsWith(connectionString, "jdbc:"), "invalid jdbc url.");
        return new JdbcUrl(connectionString);
    }

    public static JdbcUrl mysql(String serverHost, String database) {
        return new JdbcUrl(String.format("jdbc:mysql://%s:3306/%s?serverTimezone=UTC&useSSL=true&requireSSL=false", serverHost, database));
    }

    public static JdbcUrl mysql(String serverHost) {
        return new JdbcUrl(String.format("jdbc:mysql://%s:3306?serverTimezone=UTC&useSSL=true&requireSSL=false", serverHost));
    }

    public static JdbcUrl sqlserver(String serverHost, String database) {
        return new JdbcUrl(String.format("jdbc:sqlserver://%s.database.windows.net:1433;encrypt=true;trustServerCertificate=false;loginTimeout=30;database=%s;", serverHost, database));
    }

    public static JdbcUrl sqlserver(String serverHost) {
        return new JdbcUrl(String.format("jdbc:sqlserver://%s.database.windows.net:1433;encrypt=true;trustServerCertificate=false;loginTimeout=30;", serverHost));
    }

    public int getPort() {
        if (this.uri.getPort() >= 0) {
            return this.uri.getPort();
        }
        // default port
        if (StringUtils.equals(this.uri.getScheme(), "mysql")) {
            return 3306;
        } else if (StringUtils.equals(this.uri.getScheme(), "sqlserver")) {
            return 1433;
        }
        return -1;
        //throw new AzureToolkitRuntimeException("unknown jdbc url scheme: %s", this.uri.getScheme());
    }

    public String getHost() {
        return this.uri.getHost();
    }

    public String getServer() {
        return this.getHost();
    }

    public String getDatabase() {
        final String path = this.uri.getPath();
        return StringUtils.startsWith(path, "/") ? path.substring(1) : path;
    }

    @Override
    public String toString() {
        if (StringUtils.equals(uri.getScheme(), "sqlserver")) {
            return "jdbc:" + StringUtils.replaceOnce(uri.toString(), "?", ";").replaceAll("&", ";");
        }
        return "jdbc:" + this.uri.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JdbcUrl jdbcUrl = (JdbcUrl) o;
        return Objects.equals(uri.toString(), jdbcUrl.uri.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri.toString());
    }

    public static void main(String[] args) {
        JdbcUrl url = JdbcUrl.sqlserver("myserver", "test");
        System.out.println(url.getDatabase());
        System.out.println(url.getHost());
        System.out.println(url.getServer());
        System.out.println(url.getPort());
        JdbcUrl url2 = JdbcUrl.from(String.format("jdbc:mysql://%s:3306/%s?serverTimezone=UTC&useSSL=true&requireSSL=false", "server", "user", "server"));
        System.out.println(url2.getDatabase());
        System.out.println(url2.getHost());
        System.out.println(url2.getServer());
        System.out.println(url2.getPort());
    }
}
