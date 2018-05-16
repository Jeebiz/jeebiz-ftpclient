package net.jeebiz.ftpclient;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.jeebiz.ftpclient.utils.FTPStringUtils;
@FixMethodOrder(MethodSorters.JVM) 
public class FTPResourceClientListTest extends FTPResourceClientTest {
	
	/**
 	 * ftp列举文件
 	 */
 	@Test
 	public void listFile() {
 		try {
 			

 			for(FTPFile ftpFile : ftpClient.listFiles("20063586")){
 				System.out.println("File:" + ftpFile.getName() + " >> " +  FTPStringUtils.getLocalName(ftpClient.getFTPClient(), ftpFile.getName()));
 				System.err.println("---------------------------");
 			}
 			
 			/*for(FTPFile ftpFile : ftpClient.listFiles("20063586", new String[] {"mp4"})){
 				System.out.println("File (*.mp4):" + ftpFile.getName() + " >> " +  FTPStringUtils.getLocalName(ftpClient.getFTPClient(), ftpFile.getName()));
 				System.err.println("---------------------------");
 			}
 			
 			System.out.println("File Names:" +StringUtils.join(ftpClient.listNames("20063586"),","));
 			System.err.println("---------------------------");
 			
 			FTPFile ftpFile1 = ftpClient.getFile("20063586", "34df8494-52af-4e89-9424-b1e395233186.mp4");
 			System.out.println("File1:" + ftpFile1.getName());
 			System.err.println("---------------------------");
 			FTPFile ftpFile2 = ftpClient.getFile("20063586/34df8494-52af-4e89-9424-b1e395233186.mp4");
 			System.out.println("File2:" + ftpFile2.getName());*/
 			
 		} catch (Exception e) {
 			e.printStackTrace();
 		} 
 	}
	
 }
 