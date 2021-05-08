package com.microsoft.sample.sqlserver;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.sql.SqlServerManager;
import com.azure.resourcemanager.sql.models.SqlServer;

public class SQLServerTest {

    public static void main(String[] args) {
        // AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        AzureProfile profile = new AzureProfile("72f988bf-86f1-41af-91ab-2d7cd011db47", "685ba005-af8d-4b04-8f16-a7bf38b2eb5a", AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        SqlServerManager manager = SqlServerManager
                .authenticate(credential, profile);
        PagedIterable<SqlServer> sql = manager.sqlServers().list();
        System.out.println(sql);
    }
}


























