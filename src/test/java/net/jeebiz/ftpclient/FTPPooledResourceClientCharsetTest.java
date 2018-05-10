package net.jeebiz.ftpclient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.JVM) 
public class FTPPooledResourceClientCharsetTest extends FTPPooledResourceClientTest {
	 
 	/**
 	 * ftp列举文件
 	 */
 	@Test
 	public void listFile() {
 		try {
 			
 			for(FTPFile ftpFile : ftpClient.listFiles("20063586/20160118")){
 				LOG.info("File:" + ftpFile.getName());
 			}
 			
 			LOG.info("Files:" +StringUtils.join(ftpClient.listNames("20063586/20160118"),","));
 			
 			FTPFile ftpFile1 = ftpClient.getFile("20063586/20160118", "tst1.txt");
 			LOG.info("File1:" + ftpFile1.getName());
 			FTPFile ftpFile2 = ftpClient.getFile("20063586/20160118/tst1.txt");
 			LOG.info("File2:" + ftpFile2.getName());
 			
 		} catch (Exception e) {
 			e.printStackTrace();
 		} 
 	}
 	 
 }
 