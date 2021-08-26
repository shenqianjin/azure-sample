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
        WebApp wa = azure.webApps().getByResourceGroup("qianjin-web", "qianjin-web-central-us-euap-02");
//        WebApp wa = azure.webApps().getByResourceGroup("qianjin-web", "qianjin-web-central-us-euap-01");
//        WebApp wa = azure.webApps().getByResourceGroup("qianjin-web", "qianjin-web-central-us-euap");
//        WebApp wa = azure.webApps().getByResourceGroup("qianjin-web", "qianjin-web-central-us");
        //WebApp wa = azure.webApps().getByResourceGroup("qianjin-web", "qianjin-web-01");
//        WebApp wa = azure.webApps().getByResourceGroup("qianjin-web", "qianjin-web-dpst-01");
        //WebApp wa = azure.webApps().getByResourceGroup("qianjin-deploy-status", "qianjin-web-central-us-euap");
        DeployOptions options = new DeployOptions().withTrackDeployment(true).withRestartSite(true).withCleanDeployment(true);
        AsyncDeploymentResult result = wa.pushDeploy(DeployType.JAR, new File("C:\\Users\\qianjinshen\\workspace\\qianjin-demo\\boot-demo\\demo-boot-01\\target\\demo-boot-01-0.0.1-SNAPSHOT.jar"), options);
        // AsyncDeploymentResult result = wa.pushDeploy(DeployType.WAR, new File("C:\\Users\\qianjinshen\\workspace\\qianjin-demo\\boot-demo\\demo-boot-02-mysql\\target\\demo-boot-02-mysql-0.0.1-SNAPSHOT.jar"), options);
        //AsyncDeploymentResult result = wa.pushDeploy(DeployType.WAR, new File("C:\\Users\\qianjinshen\\workspace\\qianjin-demo\\webapp-demo\\demo-01\\target\\demo-01.war"), options);
//        AsyncDeploymentResult result = wa.pushZipDeploy(new File("C:/github/app.zip"));
        String deploymentId = result.deploymentId(); //"03e56223-5b30-45c3-b508-e8e8669f8dad";
        getDeploymentStatus(wa, deploymentId, 600);

    }

    private static void getDeploymentStatus(WebApp wa, String deploymentId, int count) throws InterruptedException {
        DeploymentStatus status;
        long start = System.currentTimeMillis();
        int bi = 0;
        while (bi++ < count) {
            Thread.sleep(1 * 1000);
            // poll again
            status = wa.getDeploymentStatus(deploymentId);
            System.out.println("build status: " + status.buildStatus());
            System.out.println("build status: " + status.buildStatus() + ", cost: " + (System.currentTimeMillis() - start) / 1000 + " seconds");
            System.out.println("build status: " + status.buildStatus() + ", numberOfInstancesSuccessful: " + status.numberOfInstancesSuccessful()
            + ", numberOfInstancesInProgress: " + status.numberOfInstancesInProgress());
        }
    }
}
