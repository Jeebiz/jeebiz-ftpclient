package net.jeebiz.ftpclient;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class RouteBuilderExample {

    public static void main(String args[]) throws Exception {
        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                 from("ftp://ftpTest@10.71.19.130:21/FtpDownLoad?password=123456").
                 to("file:E:/FtpTestFile");
            }
        });
        context.start();
        Thread.sleep(10000);
        context.stop();
        
    }
}