package net.jeebiz.ftpclient.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.ServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import net.jeebiz.ftpclient.FTPClient;
import net.jeebiz.ftpclient.FTPClientBuilder;
import net.jeebiz.ftpclient.FTPClientConfig;
import net.jeebiz.ftpclient.utils.Assert;
import net.jeebiz.ftpclient.utils.FTPClientUtils;
import net.jeebiz.ftpclient.utils.FTPConnectUtils;
import net.jeebiz.ftpclient.utils.FTPStoreResult;

/**
 * 基于ThreadLocal多线程对象复用的FTPClient资源服务客户端实现
 * 
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPResourceClient implements IFTPClient {

	private FTPClientBuilder clientBuilder = null;

	public FTPResourceClient() {
	}

	public FTPResourceClient(FTPClientBuilder builder) {
		this.clientBuilder = builder;
	}

	@Override
	public boolean makeRootDir(String targetDir) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			// 在当前工作目录下新建子目录
			return FTPClientUtils.makeRootDir(ftpClient, targetDir);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public boolean makeDir(String parentDir, String targetDir) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			// 在当前工作目录下新建子目录
			return FTPClientUtils.makeDirectory(ftpClient, parentDir, targetDir);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public void downloadToFile(String ftpFileName, String localFile) throws Exception {
		this.downloadToFile(ftpFileName, new File(localFile));
	}

	@Override
	public void downloadToFile(String ftpFileName, File localFile) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			if (!localFile.exists()) {
				File dir = localFile.getParentFile();
				if (!dir.exists()) {
					dir.mkdirs();
				}
				localFile.setReadable(true);
				localFile.setWritable(true);
				localFile.createNewFile();
			} else {
				localFile.setReadable(true);
				localFile.setWritable(true);
			}
			// 下载文件到指定的输出流
			FTPClientUtils.retrieveToFile(ftpClient, ftpFileName, localFile);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public void downloadToFile(String ftpDir, String ftpFileName, String localFile) throws Exception {
		this.downloadToFile(ftpDir, ftpFileName, new File(localFile));
	}

	@Override
	public void downloadToFile(String ftpDir, String ftpFileName, File localFile) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			if (!localFile.exists()) {
				File dir = localFile.getParentFile();
				if (!dir.exists()) {
					dir.mkdirs();
				}
				localFile.setReadable(true);
				localFile.setWritable(true);
				localFile.createNewFile();
			} else {
				localFile.setReadable(true);
				localFile.setWritable(true);
			}
			// 下载文件到指定的输出流
			FTPClientUtils.retrieveToFile(ftpClient, ftpDir, ftpFileName, localFile);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public void downloadToFileByChannel(String ftpFileName, String localFile) throws Exception {
		this.downloadToFileByChannel(ftpFileName, new File(localFile));
	}

	@Override
	public void downloadToFileByChannel(String ftpFileName, File localFile) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			FTPClientUtils.retrieveToFileChannel(ftpClient, ftpFileName, localFile);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public void downloadToFileByChannel(String ftpDir, String ftpFileName, String localFile) throws Exception {
		this.downloadToFileByChannel(ftpDir, ftpFileName, new File(localFile));
	}

	@Override
	public void downloadToFileByChannel(String ftpDir, String ftpFileName, File localFile) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			FTPClientUtils.retrieveToFileChannel(ftpClient, ftpDir, ftpFileName, localFile);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public void downloadToStream(String ftpDir, String ftpFileName, OutputStream output) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			FTPClientUtils.retrieveToStream(ftpClient, ftpDir, ftpFileName, output);
		} finally {
			IOUtils.closeQuietly(output);
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public void downloadToStream(String ftpFileName, OutputStream output) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			FTPClientUtils.retrieveToStream(ftpClient, ftpFileName, output);
		} finally {
			IOUtils.closeQuietly(output);
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public void downloadToResponse(String ftpDir, String ftpFileName, ServletResponse response) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			FTPClientUtils.retrieveToStream(ftpClient, ftpDir, ftpFileName, response.getOutputStream());
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public void downloadToResponse(String ftpFileName, ServletResponse response) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			FTPClientUtils.retrieveToStream(ftpClient, ftpFileName, response.getOutputStream());
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPFile getFile(String ftpFileName) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.getFTPFile(ftpClient, ftpFileName);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPFile getFile(String ftpDir, String ftpFileName) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.getFTPFile(ftpClient, ftpDir, ftpFileName);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	public InputStream getFileStream(String ftpFilePath) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return ftpClient.retrieveFileStream(ftpFilePath);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public InputStream getFileStream(String ftpDir, String ftpFileName) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			//
			return FTPClientUtils.retrieveFileStream(ftpClient, ftpDir, ftpFileName, 0);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public String[] listNames(String ftpDir) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.listNames(ftpClient, ftpDir);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public List<FTPFile> listFiles(String ftpDir) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.listFiles(ftpClient, ftpDir);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public List<FTPFile> listFiles(String ftpDir, String[] extensions) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.listFiles(ftpClient, ftpDir, extensions, false);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public List<FTPFile> listFiles(String ftpDir, String[] extensions, boolean recursion) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.listFiles(ftpClient, ftpDir, extensions, recursion);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public List<FTPFile> listFiles(String ftpDir, FTPFileFilter filter) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.listFiles(ftpClient, ftpDir, filter, false);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public List<FTPFile> listFiles(String ftpDir, FTPFileFilter filter, boolean recursion) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.listFiles(ftpClient, ftpDir, filter, recursion);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public boolean remove(String ftpFileName) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.deleteFile(ftpClient, new String[] { ftpFileName });
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public boolean remove(String[] ftpFiles) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.deleteFile(ftpClient, ftpFiles);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public boolean remove(String ftpDir, String ftpFileName) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.deleteFile(ftpClient, ftpDir, new String[] { ftpFileName });
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public boolean remove(String ftpDir, String[] ftpFiles) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.deleteFile(ftpClient, ftpDir, ftpFiles);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public boolean removeDir(String ftpDir) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.removeDirectory(ftpClient, ftpDir);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPStoreResult upload(byte[] bytes, String ftpFileName) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		InputStream input = null;
		try {
			// 包装字节输入流
			input = ByteArrayOutputStream.toBufferedInputStream(new ByteArrayInputStream(bytes));
			return FTPClientUtils.storeFile(ftpClient, ftpFileName, input);
		} finally {
			// 关闭输入流
			IOUtils.closeQuietly(input);
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPStoreResult upload(byte[] bytes, String ftpDir, String ftpFileName) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		InputStream input = null;
		try {
			// 包装字节输入流
			input = ByteArrayOutputStream.toBufferedInputStream(new ByteArrayInputStream(bytes));
			return FTPClientUtils.storeFile(ftpClient, ftpDir, ftpFileName, input);
		} finally {
			// 关闭输入流
			IOUtils.closeQuietly(input);
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPStoreResult upload(File localFile) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.storeFile(ftpClient, localFile);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPStoreResult upload(File localFile, String ftpDir) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.storeFile(ftpClient, ftpDir, localFile);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPStoreResult upload(InputStream input, String ftpFileName) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.storeFile(ftpClient, ftpFileName, input);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPStoreResult upload(InputStream input, String ftpDir, String ftpFileName) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.storeFile(ftpClient, ftpDir, ftpFileName, input);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPStoreResult upload(String localFile) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.storeFile(ftpClient, new File(localFile));
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPStoreResult upload(String localFile, String ftpDir) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.storeFile(ftpClient, ftpDir, new File(localFile));
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPStoreResult upload(StringBuilder fileContent, String ftpFileName) throws Exception {
		StringReader reader = null;
		try {
			reader = new StringReader(fileContent.toString());
			return this.upload(IOUtils.toByteArray(reader, Charset.defaultCharset()), ftpFileName);
		} finally {
			// 关闭输入流
			IOUtils.closeQuietly(reader);
		}
	}

	@Override
	public FTPStoreResult upload(StringBuilder fileContent, String ftpDir, String ftpFileName) throws Exception {
		StringReader reader = null;
		try {
			reader = new StringReader(fileContent.toString());
			return this.upload(IOUtils.toByteArray(reader, Charset.defaultCharset()), ftpDir, ftpFileName);
		} finally {
			// 关闭输入流
			IOUtils.closeQuietly(reader);
		}
	}

	@Override
	public FTPStoreResult uploadByChannel(File localFile) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.storeFileChannel(ftpClient, localFile);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPStoreResult uploadByChannel(File localFile, String ftpDir) throws Exception {
		// 获得一个活动连接的FTPClient
		FTPClient ftpClient = getFTPClient();
		try {
			return FTPClientUtils.storeFileChannel(ftpClient, ftpDir, localFile);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public void sendCommand(String args) throws Exception {
		FTPClient ftpClient = getFTPClient();
		try {
			ftpClient.sendSiteCommand(args);
		} finally {
			// 释放连接
			releaseClient(ftpClient);
		}
	}

	@Override
	public FTPClient getFTPClient() throws Exception {

		Assert.notNull(clientBuilder, "The clientBuilder must not be null");

		// 构造一个FtpClient实例
		FTPClient ftpClient = getClientBuilder().build();
		// FTP未连接
		if (!ftpClient.isConnected()) {
			FTPClientConfig clientConfig = clientBuilder.getClientConfig();
			// 连接FTP服务器
			boolean isConnected = FTPConnectUtils.connect(ftpClient, clientConfig.getHost(), clientConfig.getPort(),
					clientConfig.getUsername(), clientConfig.getPassword());
			if (isConnected) {
				// 初始化已经与FTP服务器建立连接的FTPClient
				FTPConnectUtils.initConnectedSocket(ftpClient, clientConfig);
				FTPConnectUtils.initConnectionMode(ftpClient, clientConfig);
			}
		}
		return ftpClient;
	}

	@Override
	public void releaseClient(FTPClient ftpClient) throws Exception {
		// 断开连接
		FTPConnectUtils.releaseConnect(ftpClient);
	}

	public FTPClientBuilder getClientBuilder() {
		return clientBuilder;
	}

	public void setClientBuilder(FTPClientBuilder clientBuilder) {
		this.clientBuilder = clientBuilder;
	}

}