package net.jeebiz.ftpclient.client;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletResponse;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import net.jeebiz.ftpclient.FTPClient;
import net.jeebiz.ftpclient.utils.FTPStoreResult;

/**
 * FTPClient 客户端接口
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public interface IFTPClient {

public boolean makeRootDir(String targetDir) throws Exception;
	
	public boolean makeDir(String parentDir,String targetDir) throws Exception;
	
	public void downloadToFile(String ftpFileName, String localFile) throws Exception;
	
	public void downloadToFile(String ftpFileName, File localFile) throws Exception;
	
	public void downloadToFile(String ftpDir,String ftpFileName, String localFile) throws Exception;
	
	public void downloadToFile(String ftpDir,String ftpFileName, File localFile) throws Exception;
	
	public void downloadToFileByChannel(String ftpFileName, String localFile) throws Exception;
	
	public void downloadToFileByChannel(String ftpFileName, File localFile) throws Exception;
	
	public void downloadToFileByChannel(String ftpDir,String ftpFileName, String localFile) throws Exception;
	
	public void downloadToFileByChannel(String ftpDir,String ftpFileName, File localFile) throws Exception;
	
	public void downloadToStream(String ftpFileName,OutputStream out) throws Exception;
	
	public void downloadToStream(String ftpDir,String ftpFileName,OutputStream out) throws Exception;
	
	public void downloadToResponse(String ftpFileName,ServletResponse response) throws Exception;
	
	public void downloadToResponse(String ftpDir,String ftpFileName,ServletResponse response) throws Exception;
	
	public boolean removeDir(String path) throws Exception;
	
	public boolean remove(String ftpFileName) throws Exception;
	
	public boolean remove(String[] ftpFiles) throws Exception;
	
	public boolean remove(String ftpDir,String ftpFileName) throws Exception;
	
	public boolean remove(String ftpDir,String[] ftpFiles) throws Exception;
	
	public String[] listNames(String ftpDir) throws Exception;
	
	public List<FTPFile> listFiles(String ftpDir) throws Exception;

	public List<FTPFile> listFiles(String ftpDir, String[] extensions) throws Exception;
		
	public List<FTPFile> listFiles(String ftpDir, String[] extensions,boolean recursion) throws Exception;
	
	public List<FTPFile> listFiles(String ftpDir, FTPFileFilter filter) throws Exception;
		
	public List<FTPFile> listFiles(String ftpDir, FTPFileFilter filter,boolean recursion) throws Exception;
	
	public FTPFile getFile(String ftpFilePath) throws Exception;
	
	public FTPFile getFile(String ftpDir,String ftpFileName) throws Exception;
	
	public InputStream getFileStream(String ftpFilePath) throws Exception;
	
	public InputStream getFileStream(String ftpDir,String ftpFileName) throws Exception;
	
	public FTPStoreResult upload(byte[] bytes,String ftpFileName) throws Exception;
	
	public FTPStoreResult upload(byte[] bytes,String ftpDir,String ftpFileName) throws Exception;
	
	public FTPStoreResult upload(File localFile) throws Exception;
	
	public FTPStoreResult upload(File localFile,String ftpDir) throws Exception;
	
	public FTPStoreResult upload(InputStream input,String ftpFileName) throws Exception;
	
	public FTPStoreResult upload(InputStream input,String ftpDir,String ftpFileName) throws Exception;
	
	public FTPStoreResult upload(String localFile) throws Exception;
	
	public FTPStoreResult upload(String localFile,String ftpDir) throws Exception;
	
	public FTPStoreResult upload(StringBuilder fileContent,String ftpFileName) throws Exception;
	
	public FTPStoreResult upload(StringBuilder fileContent,String ftpDir,String ftpFileName) throws Exception;
	
	public FTPStoreResult uploadByChannel(File localFile) throws Exception;
	
	public FTPStoreResult uploadByChannel(File localFile,String ftpDir) throws Exception;
	
	public void sendCommand(String args) throws Exception;
	
	public FTPClient getFTPClient() throws Exception;
	
	public void releaseClient(FTPClient ftpClient) throws Exception;
	
}
