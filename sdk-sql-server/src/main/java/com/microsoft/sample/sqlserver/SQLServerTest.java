package com.microsoft.sample.sqlserver;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.AzureCliCredential;
import com.azure.identity.AzureCliCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.resources.fluentcore.model.Creatable;
import com.azure.resourcemanager.sql.SqlServerManager;
import com.azure.resourcemanager.sql.implementation.SqlServerImpl;
import com.azure.resourcemanager.sql.models.SqlFirewallRule;
import com.azure.resourcemanager.sql.models.SqlServer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SQLServerTest {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static void main(String[] args) {
        //AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        AzureProfile profile = new AzureProfile("72f988bf-86f1-41af-91ab-2d7cd011db47", "685ba005-af8d-4b04-8f16-a7bf38b2eb5a", AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        AzureCliCredential cliCredential = new AzureCliCredentialBuilder().build();

        SqlServerManager manager = SqlServerManager.authenticate(cliCredential, profile);
//        create(manager);
        list(manager);
//        listFirewalls(manager);
    }

    public static void create(SqlServerManager manager) {
        String date = DATE_FORMAT.format(new Date());
        SqlServer server = manager.sqlServers().define("qianjin-sqlserver-test-" + date).withRegion("eastus").withNewResourceGroup("qianjin-sqlserver-rg-" + date).withAdministratorLogin("qianjin")
                .withAdministratorPassword("a222222@").create();
        System.out.println(server.fullyQualifiedDomainName());
    }

    public static void list(SqlServerManager manager) {
        PagedIterable<SqlServer> pagedServers = manager.sqlServers().list();
        List<SqlServer> servers = pagedServers.stream().collect(Collectors.toList());
        System.out.println("listed sqlserver size:" + servers.size());
        for (SqlServer server : servers) {
            System.out.println("name: " + server.name() + ", administratorLogin: " + server.administratorLogin()
                    + ", fullyQualifiedDomainName: " + server.fullyQualifiedDomainName() + ", kind: " + server.kind()
                    + ", state: " + server.state() + ", version: " + server.version());
        }
    }

    public static void listFirewalls(SqlServerManager manager) {
        SqlServer server = manager.sqlServers().getByResourceGroup("qianjin-sqlserver-rg-20210510143459", "qianjin-sqlserver-test-20210510143459");
        List<SqlFirewallRule> rules = server.firewallRules().list();
        System.out.println("listed sqlserver size:" + rules.size());
        for (SqlFirewallRule rule : rules) {
            System.out.println("id: " + rule.id() + "name: " + rule.name() + ", startIpAddress: " + rule.startIpAddress()
                    + ", endIpAddress: " + rule.endIpAddress() + ", kind: " + rule.kind()
                    + ", parentId: " + rule.parentId() + ", sqlServerName: " + rule.sqlServerName());
        }
    }
}


























