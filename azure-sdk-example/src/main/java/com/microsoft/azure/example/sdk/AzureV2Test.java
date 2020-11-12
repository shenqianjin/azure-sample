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

public class AzureV2Test extends BaseDeploy {

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
                        return response;
                    }
                })
                .authenticate(AzureCliCredentials.create())
                .withDefaultSubscription();
//        warTest(azure);
//        warNonRootTest(azure);
//        bootJarTest(azure);
//        earTest(azure);
//        jarLibTest(azure);
//        staticTest(azure);
//        zipTest(azure);
//        staticStartupLinuxTest(azure);
        azure.resourceGroups().list();
        System.out.println("done....");
    }

    private static void warTest(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-wl-04");
        webApp.deploy(DeployType.WAR, new File(SOURCE_WAR), new DeployOptions().withRestartSite(true).withPath("webapps/ROOT"));
        System.out.println("[war]: one deploy completed.");
    }

    private static void warNonRootTest(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-wl-04");
        webApp.deploy(DeployType.WAR, new File(SOURCE_WAR), new DeployOptions().withRestartSite(false).withPath("webapps/helloworld"));
        System.out.println("[war]: one deploy completed.");
    }

    private static void bootJarTest(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-wlse-04");
        webApp.deploy(DeployType.JAR, new File(SOURCE_JAR),
                new DeployOptions().withRestartSite(true));
        System.out.println("[jar]: one deploy completed.");
    }

    private static void earTest(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-wlear-04");
        webApp.deploy(DeployType.EAR, new File(SOURCE_EAR),
                new DeployOptions().withRestartSite(true));
        System.out.println("[ear]: one deploy completed.");
    }

    private static void jarLibTest(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-wl-04");
        webApp.deploy(DeployType.JAR_LIB, new File(SOURCE_LIB_1), new DeployOptions().withPath("mongo-java-driver-3.12.7.jar").withRestartSite(false));
        webApp.deploy(DeployType.JAR_LIB, new File(SOURCE_LIB_2), new DeployOptions().withPath("lib3/mysql-connector-java-8.0.22.jar").withRestartSite(false));
        System.out.println("[lib]: one deploy completed.");
    }

    private static void staticTest(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-wl-04");
        webApp.deploy(DeployType.STATIC, new File(SOURCE_STATIC_1), new DeployOptions().withPath("static_file1.html").withRestartSite(false));
        webApp.deploy(DeployType.STATIC, new File(SOURCE_STATIC_2), new DeployOptions().withPath("static_file2.html").withRestartSite(false));
        webApp.deploy(DeployType.STATIC, new File(SOURCE_STATIC_3), new DeployOptions().withPath("folder1/static_file2.txt").withRestartSite(false));
        System.out.println("[static]: one deploy completed.");
    }

    private static void zipTest(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-wl-04");
        webApp.deploy(DeployType.ZIP, new File(SOURCE_ZIP_1), new DeployOptions().withRestartSite(false).withPath(null));
//        webApp.deploy(DeployType.ZIP, new File(SOURCE_ZIP_2), new DeployOptions().withRestartSite(false).withPath("zipFolder1").withCleanDeployment(false));
        System.out.println("[zip]: one deploy completed.");
    }

    private static void staticStartupLinuxTest(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-02");
        webApp.deploy(DeployType.SCRIPT_STARTUP, new File(SOURCE_STARTUP_LINUX));
        System.out.println("[startup]: one deploy completed.");
    }

    private static void staticStartupWindowsTest(Azure azure) {
        final WebApp webApp = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-01");
        webApp.deploy(DeployType.SCRIPT_STARTUP, new File(SOURCE_STARTUP_WINDOWS), new DeployOptions().withPath("/home/start1"));
        System.out.println("[startup]: one deploy completed.");
    }
}
