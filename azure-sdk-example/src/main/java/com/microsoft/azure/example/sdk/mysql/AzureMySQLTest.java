package com.microsoft.azure.example.sdk.mysql;


import com.microsoft.azure.PagedList;
import com.microsoft.azure.credentials.AzureCliCredentials;
import com.microsoft.azure.management.mysql.v2017_12_01.Server;
import com.microsoft.azure.management.mysql.v2017_12_01.ServerForCreate;
import com.microsoft.azure.management.mysql.v2017_12_01.ServerPropertiesForCreate;
import com.microsoft.azure.management.mysql.v2017_12_01.ServerUpdateParameters;
import com.microsoft.azure.management.mysql.v2017_12_01.ServerVersion;
import com.microsoft.azure.management.mysql.v2017_12_01.Sku;
import com.microsoft.azure.management.mysql.v2017_12_01.SslEnforcementEnum;
import com.microsoft.azure.management.mysql.v2017_12_01.StorageProfile;
import com.microsoft.azure.management.mysql.v2017_12_01.implementation.DatabaseInner;
import com.microsoft.azure.management.mysql.v2017_12_01.implementation.FirewallRuleInner;
import com.microsoft.azure.management.mysql.v2017_12_01.implementation.MySQLManager;
import com.microsoft.azure.management.mysql.v2017_12_01.implementation.ServerInner;
import com.microsoft.rest.LogLevel;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class AzureMySQLTest {

    public static void main(String[] args) throws IOException {
        final AtomicInteger count = new AtomicInteger(0);
        /*MySQLManagementClientImpl client = new MySQLManagementClientImpl(AzureCliCredentials.create())
//                .withSubscriptionId("685ba005-af8d-4b04-8f16-a7bf38b2eb5a")
                ;
//        client.subscriptionId();
//        client.checkNameAvailabilitys();
        // list mysql
        PagedList<ServerInner> serverInners = client.servers().list();
        // client.servers()
        System.out.println(serverInners.size());
        //
//        client.servers().c
//        client.virtualNetworkRules().
        List<DatabaseInner> databaseInners = client.databases().listByServer("qianjinshen", "petstore4qianjin");
        System.out.println(databaseInners.size());
        List<FirewallRuleInner> firewallRuleInners = client.firewallRules().listByServer("qianjinshen", "petstore4qianjin");
        System.out.println(firewallRuleInners.size());*/
        AzureCliCredentials credentials = AzureCliCredentials.create();
        MySQLManager manager = MySQLManager.configure()
                .withLogLevel(LogLevel.BODY_AND_HEADERS)
                .withInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        count.addAndGet(1);
                        final Request request = chain.request();
                        final Response response = chain.proceed(request);
                        return chain.proceed(chain.request());
                    }
                })
                .authenticate(credentials, "685ba005-af8d-4b04-8f16-a7bf38b2eb5a")
//                .withDefaultSubscription()
        ;
//        listServers(manager);
//        listServerInners(manager);
//        updateSSL(manager);
//        listFirewalls(manager);
//        createOrUpdateFirewall(manager);
//        findAndDeleteFirewall(manager);
//        listDatabases(manager);

    }




    public static void listServers(MySQLManager manager) {
        PagedList<Server> servers = manager.servers().list();
        servers.stream().forEach(e -> System.out.println(e.name() + " " + e.fullyQualifiedDomainName()));
        System.out.println(servers.size());
    }


    public static void listServerInners(MySQLManager manager) {
        PagedList<ServerInner> servers = manager.servers().inner().list();
        servers.stream().forEach(e -> System.out.println(e.name() + " " + e.fullyQualifiedDomainName()));
        System.out.println(servers.size());
    }

    public static void createServer(MySQLManager manager) {
        ServerForCreate parameters = new ServerForCreate();
        parameters.withLocation("US");

        Sku sku = new Sku();
//        sku.withSize();
//        sku.withFamily();
//        sku.withTier();
//        sku.withName();
        parameters.withSku(sku);

        ServerPropertiesForCreate properties = new ServerPropertiesForCreate();
        properties.withSslEnforcement(SslEnforcementEnum.DISABLED);
        StorageProfile storageProfile = new StorageProfile();
//        storageProfile.withBackupRetentionDays();
//        storageProfile.withGeoRedundantBackup();
//        storageProfile.withStorageAutogrow();
//        storageProfile.withStorageMB();
        properties.withStorageProfile(storageProfile);

        ServerVersion version = new ServerVersion();
        properties.withVersion(ServerVersion.FIVE_FULL_STOP_SEVEN);
        parameters.withProperties(properties);
        manager.servers().inner().create("qianjinshen", "qianjin-" + UUID.randomUUID().toString().replaceAll("\\.", ""), parameters);

    }

    public static void updateSSL(MySQLManager manager) {
//        PagedList<Server> servers = manager.servers().list();
//        servers.stream().forEach(e -> System.out.println(e.name() + " " + e.resourceGroupName()));
//        System.out.println(servers.size());
//        Server server = managerager.servers().getByResourceGroup("qianjinshen", "qianjinshen-mysql-01");
        ServerUpdateParameters parameters = new ServerUpdateParameters();
        parameters.withSslEnforcement(SslEnforcementEnum.ENABLED);
        manager.servers().inner().update("qianjinshen", "qianjinshen-mysql-01", parameters);
        System.out.println(String.format("updated: %s", parameters));
    }

    public static void listFirewalls(MySQLManager manager) {
        List<FirewallRuleInner> servers = manager.firewallRules().inner().listByServer("qianjinshen", "qianjinshen-mysql-01");
        servers.stream().forEach(e -> System.out.println(e.name() + " " + e.id() + "\n"));
        System.out.println(servers.size());
    }

    public static void createOrUpdateFirewall(MySQLManager manager) {
        String ip = "167.220.255.104";
        FirewallRuleInner rule = new FirewallRuleInner();
        rule.withStartIpAddress(ip);
        rule.withEndIpAddress(ip);
        FirewallRuleInner result = manager.firewallRules().inner().createOrUpdate("qianjinshen", "qianjinshen-mysql-01", "allow-" + ip.replaceAll("\\.", "-"), rule);
        System.out.println(result.id() + "  " + result.name());
    }

    public static void findAndDeleteFirewall(MySQLManager manager) {
        // AllowAllWindowsAzureIps
        String ruleName = "allow----------------";
        FirewallRuleInner rule = manager.firewallRules().inner().get("qianjinshen", "qianjinshen-mysql-01", ruleName);
        System.out.println(rule.name() + " " + rule.id() + "\n");
        manager.firewallRules().inner().delete("qianjinshen", "qianjinshen-mysql-01", ruleName);
        System.out.println("done ....");
    }

    public static void listDatabases(MySQLManager manager) {
        List<DatabaseInner> databases = manager.databases().inner().listByServer("qianjinshen", "qianjinshen-mysql-01");
        databases.stream().forEach(e -> System.out.println(e.name() + " " + e.charset()));
        System.out.println(databases.size());
        databases.stream().forEach(e -> System.out.println(e));

    }
}
