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

public class AzureTest extends BaseDeploy {

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
//        warTest(azure);
//        warNonRootTest(azure);
//        zipFromWarTest(azure);
//        bootJarTest(azure);
//        jarLibTest(azure);
//        staticTest(azure);
//        zipTest(azure);
//        staticStartupLinuxTest(azure);
//        staticStartupWindowsTest(azure);
        warTest2(azure);
//        warTest3(azure);
    }

    /**
     * type = war:
     * path is optional, it is only allowed path is 'webapps/<directory-name>' when type = war, example: path = webapps/ROOT.
     * clean: if path is not black, default to clean up '/home/site/wwwroot/webapps/directory-name' directory.
     * @param azure
     */
    private static void warTest2(Azure azure) {
        final WebApp webApp1 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-01");
//        webApp1.deploy(DeployType.WAR, new File("C:\\Users\\qianjinshen\\workspace\\qianjinwebapp-demo\\qianjinwebapp-4maven-demo-01\\target\\qianjinwebapp-4maven-demo-01.war"),
//                new DeployOptions().withRestartSite(false).withPath("webapps/ROOT_test_war"));
        webApp1.deploy(DeployType.WAR, new File(SOURCE_WAR),
                new DeployOptions().withRestartSite(true).withPath("webapps/qianjinwebapp-4maven-demo-01+test"));
        System.out.println("[war]: one deploy completed.");
    }
    private static void warTest3(Azure azure) {
        final WebApp webApp1 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-01");
        webApp1.warDeploy(new File(SOURCE_WAR), "ROOT_test explored");
        System.out.println("1 [war]: one deploy completed.");
    }

    private static void warTest(Azure azure) {
        final WebApp webApp1 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-02");
        webApp1.deploy(DeployType.WAR, new File(SOURCE_WAR));
        System.out.println("1 [war]: one deploy completed.");
    }

    private static void warNonRootTest(Azure azure) {
        final WebApp webApp1 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-02");
        webApp1.deploy(DeployType.WAR, new File(SOURCE_WAR),
                new DeployOptions().withPath("webapps/qianjinwebapp-demo-02"));
        System.out.println("1 [war]: one deploy completed.");
    }

    private static void zipFromWarTest(Azure azure) {
        final WebApp webApp1 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-01");
        webApp1.deploy(DeployType.ZIP, new File(SOURCE_ZIP_1));
        System.out.println("1 [zip]: one deploy completed.");
    }

    /**
     * type = jar:
     * path will be ignored.
     * clean: by default, deploy incrementally.
     * @param azure
     */
    private static void bootJarTest(Azure azure) {
        final WebApp webApp2 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-javese-01");
        webApp2.deploy(DeployType.JAR, new File(SOURCE_JAR),
                new DeployOptions().withRestartSite(true).withPath("webapps/ROOT"));
        System.out.println("[jar]: one deploy completed.");
    }

    /**
     * type = lib:
     * will deploy the script to /home/site/libs
     * path is required and must end with simple file name, it can contains relative directories before file name, but it cannot be start with "/" because '/home/site/libs/' is the base directory for any lib file.
     * clean: by default, deploy incrementally.
     * @param azure
     */
    private static void jarLibTest(Azure azure) {
        final WebApp webApp2 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-01");
//        webApp2.deploy(DeployType.JAR_LIB, new File("C:\\Users\\qianjinshen\\workspace\\azure-core-1.7.0.jar"), new DeployOptions().withPath("lib/azure-core.jar"));
        webApp2.deploy(DeployType.JAR_LIB, new File(SOURCE_LIB_1), new DeployOptions().withPath("lib3/lib31/azure-core.jar"));
        System.out.println("2 [lib]: one deploy completed.");
    }

    /**
     * type = static:
     * will deploy the script to /home/site/wwwroot
     * path is required and must end with simple file name, it can contains relative directories before file name, but it cannot be start with "/" because '/home/site/wwwroot/' is the base directory for any static file.
     * clean: by default, deploy incrementally.
     * @param azure
     */
    private static void staticTest(Azure azure) {
        final WebApp webApp2 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-01");
        webApp2.deploy(DeployType.STATIC, new File(SOURCE_STATIC_1), new DeployOptions().withPath("shell2"));
        System.out.println("2 [static]: one deploy completed.");
    }

    /**
     * type = zip:
     *
     * clean: default to clean up /home/site/wwwroot.
     *
     * will deploy the script to /home/site/wwwroot
     * path: is optional. if path is specified it means directories (not file name) which is relative to '/home/site/wwwroot/' and cannot be started with "/". because '/home/site/wwwroot/' is the base directory for any zip file
     *
     * @param azure
     */
    private static void zipTest(Azure azure) {
        final WebApp webApp1 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-wl-03");
        webApp1.deploy(DeployType.ZIP, new File(SOURCE_ZIP_1), new DeployOptions().withPath("zip1"));
        System.out.println("1 [zip]: one deploy completed.");
    }

    private static void staticStartupLinuxTest(Azure azure) {
        final WebApp webApp2 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-02");
        webApp2.deploy(DeployType.SCRIPT_STARTUP, new File(SOURCE_STARTUP_LINUX));
        System.out.println("2 [jar]: one deploy completed.");
    }

    /**
     * type = startup:
     *
     * path will be ignored. and script will always be renamed to startup.cmd (windows) or startup.sh (linux).
     *
     * @param azure
     */
    private static void staticStartupWindowsTest(Azure azure) {
        final WebApp webApp2 = azure.webApps().getByResourceGroup("qianjinshen", "qianjinshen-ww-01");
        webApp2.deploy(DeployType.SCRIPT_STARTUP, new File(SOURCE_STARTUP_WINDOWS), new DeployOptions().withPath("/home/start1"));
        System.out.println("2 [jar]: one deploy completed.");
    }
}
