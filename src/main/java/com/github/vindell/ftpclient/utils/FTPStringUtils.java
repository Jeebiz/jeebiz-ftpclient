package com.github.vindell.ftpclient.utils;

import org.apache.commons.net.ftp.FTPFile;

import com.github.vindell.ftpclient.FTPClient;
import com.github.vindell.ftpclient.FTPClientConfig;
/**
 * <p>FTP协议里面，规定文件名编码为iso-8859-1，所以目录名或文件名需要转码</p>
 * <pre>
 *  问题描述：
 *  
 *  使用org.apache.commons.net.ftp.FTPClient创建中文目录、上传中文文件名时，目录名及文件名中的中文显示为“??”。
 *  
 *  原因：
 *  
 *  FTP协议里面，规定文件名编码为iso-8859-1，所以目录名或文件名需要转码。
 *  
 *  解决方案：
 *  
 *  将中文的目录或文件名转为iso-8859-1编码的字符。参考代码：
 *  
 *  String name = "目录名或文件名";
 *  name = new String(name.getBytes("GBK"),"iso-8859-1");// 转换后的目录名或文件名。
 *  
 *  实例：
 *  <strong>
 *  public boolean upLoadFile(File file, String path, String fileName) throws IOException {
 *      boolean result = false;
 *      FTPClient ftpClient = new FTPClient();
 *      try {
 *          ftpClient.connect(confService.getConfValue(PortalConfContants.FTP_CLIENT_HOST));
 *          ftpClient.login(confService.getConfValue(PortalConfContants.FTP_CLIENT_USERNAME), confService.getConfValue(PortalConfContants.FTP_CLIENT_PASSWORD));
 *          ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
 *  
 *          // make directory
 *          if (path != null && !"".equals(path.trim())) {
 *              String[] pathes = path.split("/");
 *             for (String onepath : pathes) {
 *                 if (onepath == null || "".equals(onepath.trim())) {
 *                      continue;
 *                }
 *                onepath = new String(onepath.getBytes("GBK"),"iso-8859-1");                    
 *                 if (!ftpClient.changeWorkingDirectory(onepath)) {
 *                     ftpClient.makeDirectory(onepath);
 *                     ftpClient.changeWorkingDirectory(onepath);
 *                 }
 *              }
 *         }
 *         result = ftpClient.storeFile(new String(fileName.getBytes("GBK"),"iso-8859-1"), new FileInputStream(file));
 *     } catch (Exception e) {
 *          e.printStackTrace();
 *      } finally {
 *          ftpClient.logout();
 *      }
 *      return result;
 *  }
 *  </strong>
 *  </pre>
 */
public class FTPStringUtils {
	
	/**
	 * FTP协议里面，规定文件名编码为iso-8859-1，所以目录名或文件名需要转码。 获取本地文件名通过本地编码格式转换成服务端编码格式的名称；默认转码[GBK ->  ISO-8859-1]
	 * 				 上传文件时，文件名称需要做编码转换 fileName = new String(fileName.getBytes(LOCAL_CHARSET),SERVER_CHARSET);
	 * @param ftpClient
	 * @param fileName
	 * @return
	 */
	public static String getRemoteName(FTPClient ftpClient,String fileName){
        try{
            if(fileName == null){
                return "";
            }else{
                return new String(fileName.getBytes(ftpClient.getLocalEncoding()),ftpClient.getControlEncoding());
            }
        }catch(Exception e){
            return "";
        }
    }
	
	/**
	 * 获取服务端文件名本地编码解码后的名称; 默认转码[ISO-8859-1 ->  GBK ]
	 * @param ftpClient
	 * @param ftpFile
	 * @return
	 */
	public static String getLocalName(FTPClient ftpClient,FTPFile ftpFile){
		try{
        	//解决中文乱码问题，两次解码
			return new String(ftpFile.getName().getBytes(ftpClient.getControlEncoding()),ftpClient.getLocalEncoding());
        }catch(Exception e){
            return "";
        }
    }
	
	/**
	 * 获取服务端文件名本地编码解码后的名称; 默认转码[ISO-8859-1 ->  GBK ]
	 * @param ftpClient
	 * @param ftpFileName
	 * @return
	 */
	public static String getLocalName(FTPClient ftpClient,String ftpFileName){
        try{
            if(ftpFileName == null){
                return "";
            }else{
                return new String(ftpFileName.getBytes(ftpClient.getControlEncoding()),ftpClient.getLocalEncoding());
            }
        }catch(Exception e){
            return "";
        }
    }
	
	/**
	 * 转码[GBK ->  ISO-8859-1]
	 * @param fileName
	 * @return
	 */
	public static String gbkToISO8859(String fileName){
        try{
            if(fileName == null){
                return "";
            }else{
            	//解决中文乱码问题，两次解码
    			return new String(fileName.getBytes(FTPClientConfig.LOCAL_CHARSET),FTPClientConfig.SERVER_CHARSET);
            }
        }catch(Exception e){
            return "";
        }
    }
	
	/**
	 * 转码[ISO-8859-1 ->  GBK ]
	 * @param ftpFileName
	 * @return
	 */
	public static String iso8859ToGBK(String ftpFileName){
        try{
            if(ftpFileName == null){
                return "";
            }else{
            	//解决中文乱码问题，两次解码
    			return new String(ftpFileName.getBytes(FTPClientConfig.SERVER_CHARSET),FTPClientConfig.LOCAL_CHARSET);
            }
        }catch(Exception e){
            return "";
        }
    }
    
}
