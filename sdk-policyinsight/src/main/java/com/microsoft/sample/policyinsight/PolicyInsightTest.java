package com.microsoft.sample.policyinsight;


import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.policyinsights.PolicyInsightsManager;
import com.azure.resourcemanager.policyinsights.models.CheckRestrictionsRequest;
import com.azure.resourcemanager.policyinsights.models.CheckRestrictionsResourceDetails;
import com.azure.resourcemanager.policyinsights.models.CheckRestrictionsResult;
import com.microsoft.azure.credentials.AzureCliCredentials;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PolicyInsightTest {

    public static void main(String[] args) throws IOException {
        AzureProfile profile = new AzureProfile("72f988bf-86f1-41af-91ab-2d7cd011db47", "685ba005-af8d-4b04-8f16-a7bf38b2eb5a", AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        AzureCliCredentials c = AzureCliCredentials.create();
        PolicyInsightsManager manager = PolicyInsightsManager.configure()
                .withLogOptions(new HttpLogOptions().setLogLevel(HttpLogDetailLevel.BODY_AND_HEADERS))
                .authenticate(credential, profile);
        CheckRestrictionsResourceDetails resourceDetails = new CheckRestrictionsResourceDetails();
        resourceDetails.withScope("/subscriptions/685ba005-af8d-4b04-8f16-a7bf38b2eb5a");
        Map<String, String> content = new HashMap<>();
        content.put("location", "australiaeast");
        content.put("type", "Microsoft.DBforMySQL/servers");
        resourceDetails.withResourceContent(content);
        resourceDetails.withApiVersion("2020-07-01-preview");
        CheckRestrictionsRequest request = new CheckRestrictionsRequest();
        request.withResourceDetails(resourceDetails);
        CheckRestrictionsResult result = manager.policyRestrictions().checkAtSubscriptionScope(request);
        System.out.println(result);
    }

}
