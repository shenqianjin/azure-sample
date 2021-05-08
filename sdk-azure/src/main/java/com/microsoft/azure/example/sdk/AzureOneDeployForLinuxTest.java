package com.microsoft.azure.example.sdk;


import com.microsoft.azure.credentials.AzureCliCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.appservice.*;
import com.microsoft.rest.LogLevel;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class AzureOneDeployForLinuxTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        final AtomicInteger count = new AtomicInteger(0);
        final Azure azure = Azure.configure()
                .withLogLevel(LogLevel.BODY_AND_HEADERS)
                .withInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        count.addAndGet(1);
                        final Request request = chain.request();
                        // final Response response = chain.proceed(request);
                        return chain.proceed(chain.request());
                    }
                })
                .authenticate(AzureCliCredentials.create())
                .withDefaultSubscription();
        //oneDeployForLinux(azure);
        canGetDeploymentStatus(azure);
    }

    private static void oneDeployForLinux(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-wl-04");
        webApp.deploy(DeployType.SCRIPT_STARTUP, new File("C:\\Users\\qianjinshen\\workspace\\qianjinwebapp-demo\\qianjinwebapp-4maven-demo-01\\src\\main\\resources\\deployment\\startup\\startup.sh"),
        new DeployOptions().withRestartSite(false));
        System.out.println(" - [startup]: one deploy completed.");
        webApp.deploy(DeployType.WAR, new File("C:\\Users\\qianjinshen\\workspace\\qianjinwebapp-demo\\qianjinwebapp-4maven-demo-01\\target\\qianjinwebapp-4maven-demo-01.war"),
                new DeployOptions().withRestartSite(true).withPath("webapps/ROOT"));
        System.out.println(" - [war]: one deploy completed.");
    }

    public static void canGetDeploymentStatus(Azure azure) throws InterruptedException {
        WebApp wa = azure.webApps().getByResourceGroup("qianjin-web", "qianjin-web-test-04");
        //WebApp wa = azure.webApps().getByResourceGroup("qianjin-deploy-status", "qianjin-web-central-us-euap");
        AsyncDeploymentResult result = wa.pushDeploy(DeployType.WAR, new File("C:\\Users\\qianjinshen\\workspace\\qianjin-demo\\demo-01\\target\\demo-01.war"), null);
//        AsyncDeploymentResult result = wa.pushZipDeploy(new File("C:/github/app.zip"));
        DeploymentStatus status = wa.getDeploymentStatus(result.getDeploymentId());
        while (status == null) {
            System.out.println("status not ready");
            Thread.sleep(10 * 1000);
            // try again
            status = wa.getDeploymentStatus(result.getDeploymentId());
        }
        BuildStatus buildStatus = status.buildStatus();
        System.out.println("build status: " + buildStatus);
        while (buildStatus != BuildStatus.BUILD_SUCCESSFUL) {
            Thread.sleep(10 * 1000);
            // poll again
            status = wa.getDeploymentStatus(result.getDeploymentId());
            buildStatus = status.buildStatus();
            System.out.println("build status: " + buildStatus);
        }
    }
}
