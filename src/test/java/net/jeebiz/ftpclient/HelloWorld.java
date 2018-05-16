package net.jeebiz.ftpclient;
/**
* Apache Camel FTP Demo
* @author 小卖铺的老爷爷
*/

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

public class HelloWorld extends RouteBuilder {
	//启动FTP路由，实际项目中初始化应该是单独的一个类
   public static void main(String[] args) throws Exception {
       // 这是camel上下文对象，整个路由的驱动全靠它了。
       ModelCamelContext camelContext = new DefaultCamelContext();
       // 启动route
       camelContext.start();
       // 将我们的路由处理加入到上下文中
       camelContext.addRoutes(new HelloWorld());
   }

   @Override
   public void configure() throws Exception {
       //从FTP上下载文件到本地目录，相关参数的意义，参考我上文贴出的API，实际项目中这些地址一般写在配置文件中
       from("ftp://10.71.19.130:2121/MHE/?username=zfsoft&password=123456&binary=true&passiveMode=true&delete=true&delay=60000")
       //自定义的处理器，可以做各种逻辑处理，如文件名匹配下载等
       //.process(new HttpProcessor())
       .to("file:d:/wms-fe/inFile");
   }
}