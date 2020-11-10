package com.microsoft.azure.example.sdk.mysql;


import com.microsoft.azure.PagedList;
import com.microsoft.azure.credentials.AzureCliCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.appservice.DeployOptions;
import com.microsoft.azure.management.appservice.DeployType;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.management.mysql.v2017_12_01.Server;
import com.microsoft.azure.management.mysql.v2017_12_01.ServerUpdateParameters;
import com.microsoft.azure.management.mysql.v2017_12_01.SslEnforcementEnum;
import com.microsoft.azure.management.mysql.v2017_12_01.implementation.DatabaseInner;
import com.microsoft.azure.management.mysql.v2017_12_01.implementation.FirewallRuleInner;
import com.microsoft.azure.management.mysql.v2017_12_01.implementation.MySQLManagementClientImpl;
import com.microsoft.azure.management.mysql.v2017_12_01.implementation.MySQLManager;
import com.microsoft.azure.management.mysql.v2017_12_01.implementation.ServerInner;
import com.microsoft.azure.storage.core.JsonUtilities;
import com.microsoft.rest.LogLevel;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AzureMySQlTest {

    public static void main(String[] args) throws IOException {
        final AtomicInteger count = new AtomicInteger(0);
        /*MySQLManagementClientImpl client = new MySQLManagementClientImpl(AzureCliCredentials.create())
//                .withSubscriptionId("685ba005-af8d-4b04-8f16-a7bf38b2eb5a")
                ;
//        client.subscriptionId();
//        client.checkNameAvailabilitys();
        // list mysql server
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
        MySQLManager mySQLManager = MySQLManager.configure()
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
//        listServers(mySQLManager);
//        updateSSL(mySQLManager);
        listFirewalls(mySQLManager);
//        createOrUpdateFirewall(mySQLManager);
//        createOrUpdateFirewall2(mySQLManager);
    }


    public static void listServers(MySQLManager manager) {
        PagedList<Server> servers = manager.servers().list();
        servers.stream().forEach(e -> System.out.println(e.name() + " " + e.resourceGroupName()));
        System.out.println(servers.size());
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
        servers.stream().forEach(e -> System.out.println(e.name() + " " + e.id()));
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

    public static void createOrUpdateFirewall2(MySQLManager manager) {
        String ip = "167.220.255.104";
        FirewallRuleInner rule = new FirewallRuleInner();
        rule.withStartIpAddress(ip);
        rule.withEndIpAddress(ip);
        manager.firewallRules().inner().delete("qianjinshen", "qianjinshen-mysql-01", "AllowAllWindowsAzureIps");
        System.out.println("done ....");
    }
}
