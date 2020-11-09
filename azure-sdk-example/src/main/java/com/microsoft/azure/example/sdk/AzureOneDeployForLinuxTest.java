package com.microsoft.azure.example.sdk;


import com.microsoft.azure.credentials.AzureCliCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.appservice.DeployOptions;
import com.microsoft.azure.management.appservice.DeployType;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.rest.LogLevel;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class AzureOneDeployForLinuxTest {

    public static void main(String[] args) throws IOException {
        final AtomicInteger count = new AtomicInteger(0);
        final Azure azure = Azure.configure()
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
                .authenticate(AzureCliCredentials.create())
                .withDefaultSubscription();
        oneDeployForLinux(azure);
    }

    private static void oneDeployForLinux(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-wl-05");
        webApp.deploy(DeployType.SCRIPT_STARTUP, new File("C:\\Users\\qianjinshen\\workspace\\qianjinwebapp-demo\\qianjinwebapp-4maven-demo-01\\src\\main\\resources\\deployment\\startup\\startup.sh"),
        new DeployOptions().withRestartSite(false));
        System.out.println(" - [startup]: one deploy completed.");
        webApp.deploy(DeployType.WAR, new File("C:\\Users\\qianjinshen\\workspace\\qianjinwebapp-demo\\qianjinwebapp-4maven-demo-01\\target\\qianjinwebapp-4maven-demo-01.war"),
                new DeployOptions().withRestartSite(true).withPath("webapps/ROOT"));
        System.out.println(" - [war]: one deploy completed.");
    }
}
