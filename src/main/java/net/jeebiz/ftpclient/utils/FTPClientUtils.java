package net.jeebiz.ftpclient.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.util.TrustManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jeebiz.ftpclient.FTPClient;
import net.jeebiz.ftpclient.FTPClientConfig;
import net.jeebiz.ftpclient.FTPSClient;
import net.jeebiz.ftpclient.FTPSClientConfig;
import net.jeebiz.ftpclient.filefilter.NameFileFilter;
import net.jeebiz.ftpclient.io.CopyStreamProcessListener;
import net.jeebiz.ftpclient.io.LoggerProtocolCommandListener;
import net.jeebiz.ftpclient.rename.FileRenamePolicy;

/**
 * FTPClient常用方法：
 * <pre>
 * 常用方法：
 *   void setControlEncoding(String encoding)：设置FTP控制连接的编码方式(默认读取中文文件名时为乱码)
 *   boolean changeWorkingDirectory(String pathname)：设置当前的工作目录
 *   boolean changeToParentDirectory()：返回上级目录
 *   void setRestartOffset(long offset)：设置重新启动的偏移量(用于断点续传)
 * 下载文件：
 *   boolean retrieveFile(String,remote,OutputStream local)：从服务器返回指定名称的文件并且写入到OuputStream，以便写入到文件或其它地方。
 *   InputStream retrieveFileStream(String remote)：从服务器返回指定名称的文件的InputStream以便读取。
 * 上传文件：
 *   boolean storeFile(String remote,InputStream local)：利用给定的名字(remote)和输入流(InputStream)向服务器上传一个文件。
 *   OutputStream storeFileStream(String remote)：根据给定的名字返回一个能够向服务器上传文件的OutputStream。
 *   boolean storeUniqueFile(InputStream local)：根据服务器自己指定的唯一的名字和输入流InputStream向服务器上传一个文件。
 *   boolean storeUniqueFile(String remote,InputStream local)：根据指定的名字和输入流InputStream向服务器上传一个文件。
 *   OuputStream storeUniqueFileStream()：返回一个输出流OutputStream,以便向服务器写入一个文件，该文件由服务器自己命名。
 *   OutputStream storeUniqueFileStream(String remote)：返回一个输出流OutputStream,以便向服务器写入一个文件，该文件由用户自己指定。
 * </pre>
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
@SuppressWarnings("resource")
public class FTPClientUtils {

	protected static Logger LOG = LoggerFactory.getLogger(FTPClientUtils.class);
	
	/**
	 * 初始化FTPClient
	 * @param ftpClient FTPClient对象
	 * @return FTPClient 对象
	 */
	public static <T extends FTPClient> T initFTPClient(T ftpClient, FTPClientConfig clientConfig){
		
		try {
			//初始化基础参数
			ftpClient.configure(clientConfig);
			//设置将过程中使用到的命令输出到控制台 
			if(clientConfig.isPrintDebug()){
				ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
			}else if(clientConfig.isLogDebug()){
				ftpClient.addProtocolCommandListener(new LoggerProtocolCommandListener(LOG));
			}
			//启用或禁用服务器自动编码检测（只支持UTF-8支持）
			ftpClient.setAutodetectUTF8(clientConfig.isAutodetectUTF8());
			//启用或禁用数据流方式上传/下载时是否在缓冲发送/接收完成立即将刷新缓存区
			ftpClient.setAutoFlush(clientConfig.isAutoFlush());
			//数据流方式上传/下载时缓存区达到可自动刷新的最小阀值；仅当 autoflush 为true 时才有效
			ftpClient.setAutoFlushBlockSize(clientConfig.getAutoFlushBlockSize());
			//为缓冲数据流而设置内部缓冲器区大小
			ftpClient.setBufferSize(clientConfig.getBufferSize());
			ftpClient.setBufferSize_(clientConfig.getBufferSize());
			//设置文件通道读取缓冲区大小
			ftpClient.setChannelReadBufferSize(clientConfig.getChannelReadBufferSize());
			//设置文件通道写出缓冲区大小
			ftpClient.setChannelWriteBufferSize(clientConfig.getChannelWriteBufferSize());
			//设置Socket使用的字符集
			ftpClient.setCharset(Charset.forName(clientConfig.getCharset()));
			//设置连接超时时间；参数将会传递给Socket对象的connect()方法
			ftpClient.setConnectTimeout(clientConfig.getConnectTimeout() );
			//设置FTP控制连接的编码方式(默认读取中文文件名时为乱码)
			ftpClient.setControlEncoding(clientConfig.getControlEncoding());
			//设置控制保活消息回复等待时间
			ftpClient.setControlKeepAliveReplyTimeout(clientConfig.getControlKeepAliveReplyTimeout()  );
			ftpClient.setControlKeepAliveReplyTimeout_(clientConfig.getControlKeepAliveReplyTimeout());
			//设置发送处理文件上载或下载时，控制连接保持活动消息之间的等待时间
			ftpClient.setControlKeepAliveTimeout(clientConfig.getControlKeepAliveTimeout()  );
			ftpClient.setControlKeepAliveTimeout_(clientConfig.getControlKeepAliveTimeout());
			//设置 TCP进行存储时/检索操作时keepalive连接监听对象
			ftpClient.setCopyStreamListener(clientConfig.getCopyStreamListener());
			ftpClient.setCopyStreamListener_(clientConfig.getCopyStreamListener());
			//TCP进行存储时/检索操作时数据处理进度监听对象
			if(StringUtils.isNotEmpty(clientConfig.getCopyStreamProcessListenerName())){
				@SuppressWarnings("unchecked")
				Class<CopyStreamProcessListener> listenerClazz = (Class<CopyStreamProcessListener>) Class.forName(clientConfig.getCopyStreamProcessListenerName());
				ftpClient.setCopyStreamProcessListener((CopyStreamProcessListener) ConstructorUtils.invokeConstructor(listenerClazz));
			}else{
				ftpClient.setCopyStreamProcessListener(clientConfig.getCopyStreamProcessListener());
			}
			//设置超时时间以毫秒为单位使用时，从数据连接读
			ftpClient.setDataTimeout(clientConfig.getDataTimeout());
			//设置Socket默认端口
			ftpClient.setDefaultPort(clientConfig.getPort());
			//设置打开Socket连接超时时间
			ftpClient.setDefaultTimeout(clientConfig.getConnectTimeout());
			// 文件重命名规则：默认 {@link UUIDFileRenamePolicy}
			if(StringUtils.isNotEmpty(clientConfig.getFileRenamePolicyName())){
				@SuppressWarnings("unchecked")
				Class<FileRenamePolicy> listenerClazz = (Class<FileRenamePolicy>) Class.forName(clientConfig.getFileRenamePolicyName());
				FileRenamePolicy fileRenamePolicy = (FileRenamePolicy) ConstructorUtils.invokeConstructor(listenerClazz);
				ftpClient.setFileRenamePolicy(fileRenamePolicy);
				fileRenamePolicy.setClientConfig(clientConfig);
			}else{
				FileRenamePolicy fileRenamePolicy = clientConfig.getFileRenamePolicy();
				fileRenamePolicy.setClientConfig(clientConfig);
				ftpClient.setFileRenamePolicy(fileRenamePolicy);
			}
			/**
			 * 您可以设置为true，如果你想获得隐藏的文件时listFiles(java.lang.String)了。
			 * 一个LIST -a会发出到FTP服务器。 这取决于您的FTP服务器，如果你需要调用这个方法，也不要期望得到消除隐藏文件，如果你调用“假”这个方法。
			 */
			ftpClient.setListHiddenFiles(clientConfig.isListHiddenFiles());
			//设置本地编码格式
			ftpClient.setLocalEncoding(clientConfig.getLocalEncoding());
			//问题的FTP MFMT命令（并非所有服务器都支持）中规定的最后修改文件的时间。
			//ftpClient.setModificationTime(pathname, timeval)
			if(null != clientConfig.getParserFactory()){
				//设置用于创建FTPFileEntryParser对象的工厂
				ftpClient.setParserFactory(clientConfig.getParserFactory());
			}
			if(null != clientConfig.getSocketProxy()){
				ftpClient.setProxy(clientConfig.getSocketProxy());
			}else{
				ftpClient.setSocketFactory(clientConfig.getSocketFactory());
			}
			//设置Socket底层用于接收数据的缓冲区大小,默认8KB
			ftpClient.setReceiveBufferSize(clientConfig.getReceiveBufferSize());
			//设置FTPClient接收数据的缓冲区大小,默认8KB
			ftpClient.setReceieveDataSocketBufferSize(clientConfig.getReceiveDataSocketBufferSize());
			//启用或禁用核实，利用远程主机的数据连接部分是作为控制连接到该连接的主机是相同的
			ftpClient.setRemoteVerificationEnabled(clientConfig.isRemoteVerificationEnabled());
			if(StringUtils.isNotEmpty(clientConfig.getReportActiveExternalHost())){
				//设置主动模式下在报告EPRT/PORT命令时使用的外部IP地址；在多网卡下很有用
				ftpClient.setReportActiveExternalIPAddress(clientConfig.getReportActiveExternalHost());
			}
			//重置文件传输偏移量为0
			ftpClient.setRestartOffset(0);
			//设置Socket底层用于发送数据的缓冲区大小,默认8KB
			ftpClient.setSendBufferSize(clientConfig.getSendBufferSize());
			//设置FTPClient发送数据的缓冲区大小,默认8KB
			ftpClient.setSendDataSocketBufferSize(clientConfig.getSendDataSocketBufferSize());
			//设置SocketClient打开ServerSocket的连接ServerSocketFactory工厂
			ftpClient.setServerSocketFactory(clientConfig.getServerSocketFactory());
			//设置是否严格多行解析
			ftpClient.setStrictMultilineParsing(clientConfig.isStrictMultilineParsing());
			/**
			 * 设置是否使用与IPv4 EPSV。 也许值得在某些情况下启用。 例如，当使用IPv4和NAT它可能与某些罕见的配置。
			 * 例如，如果FTP服务器有一个静态的使用PASV地址（外部网）和客户端是来自另一个内部网络。
			 * 在这种情况下，PASV命令后，数据连接会失败，而EPSV将使客户获得成功，采取公正的端口。
			 */
			ftpClient.setUseEPSVwithIPv4(clientConfig.isUseEPSVwithIPv4());
			
			//绑定FTP参数对象
			ftpClient.setClientConfig(clientConfig);
			
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		
		return ftpClient;
	}
	
	/**
	 * 初始化FTPSClient
	 * @param ftpsClient FTPSClient对象
	 * @return FTPSClient对象
	 */
	public static <T extends FTPSClient> T  initFTPSClient(T ftpsClient, FTPSClientConfig clientConfig){
		
		try {
			
			//设置AUTH命令使用的值
			ftpsClient.setAuthValue(clientConfig.getAuth());
			//设置当前连接使用的特定密码组；服务器协商之前调用
			ftpsClient.setEnabledCipherSuites(StringUtils.tokenizeToStringArray(clientConfig.getEnabledCipherSuites()));
			//设置当前连接使用的特定协议组；服务器协商之前调用
			ftpsClient.setEnabledProtocols(StringUtils.tokenizeToStringArray(clientConfig.getEnabledProtocols()));
			//设置当前Socket是否可以创建一个新的SSL会话
			ftpsClient.setEnabledSessionCreation(clientConfig.isEnabledSessionCreation());
			//设置是否使用HTTPS终端自动检查算法。默认false。仅在客户端模式的连接进行此项检查（需Java1.7+）
			ftpsClient.setEndpointCheckingEnabled(clientConfig.isTlsEndpointChecking());
			//设置在客户端模式(post-TLS)下的连接使用的域名校验对象
			ftpsClient.setHostnameVerifier(clientConfig.getHostnameVerifier());
			//设置FTPS的KeyManager实现
			ftpsClient.setKeyManager(clientConfig.getKeyManager());
			//设置是否需要客户端身份验证
			ftpsClient.setNeedClientAuth(clientConfig.isNeedClientAuth());
			//解决在通过使用FTPSClient类进行sslftp的连接 可以连接成功,但list() ,listNames()或listFiles() 为null.
			if(null != clientConfig.getSslSocketFactory()){
				ftpsClient.setSocketFactory(clientConfig.getSslSocketFactory());
			}else{
				ftpsClient.setSocketFactory(SSLSocketFactory.getDefault());
			}
			//设置FTPS的TrustManager实现；
			if(null !=  clientConfig.getTrustManager()){
				ftpsClient.setTrustManager(clientConfig.getTrustManager());
			}else{
				ftpsClient.setTrustManager(TrustManagerUtils.getValidateServerCertificateTrustManager());
			}
			//设置是否使用客户端模式
			ftpsClient.setUseClientMode(clientConfig.isUseClientMode());
			//设置是否希望客户端身份验证
			ftpsClient.setWantClientAuth(clientConfig.isWantClientAuth());
			
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		
		return ftpsClient;
	}
	
	/**
	 * 
	 * <p> 切换目录至指定目录，如果指定目录不存在则切换回原目录，并返回结果标志 </p>
	 * @param ftpClient
	 * @param ftpDir
	 * @return
	 * @throws IOException
	 */
	public static boolean changeDirectory(FTPClient ftpClient, String ftpDir) throws IOException {
		if (StringUtils.isNotEmpty(ftpDir)) {
			// 路径分割符
			String separator = FTPConfigurationUtils.getFileSeparator(ftpClient.getClientConfig());
			// FTP服务根文件目录
			String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 转换目录为目标服务器目录格式
			String tempDir = FTPPathUtils.getPath(ftpDir);
			// 转码后的目录名称
			String remoteDir = FTPStringUtils.getRemoteName(ftpClient, tempDir);
			// 验证是否有该文件夹，有就转到，没有创建后转到该目录下
			boolean hasChanged = ftpClient.changeWorkingDirectory(remoteDir);
			ftpClient.printWorkingDirectory();
			// 没有找到指定的目录，则逐级的创建目录（考虑兼容问题，逐级创建目录）
			if (!hasChanged && tempDir.indexOf(separator) != -1) {
				// 多层目录循环切换
				boolean hasNext = true;
				int index = 0;
				String dir = null;
				while (hasNext) {
					// 是否包含/
					index = tempDir.indexOf(separator);
					// 判断是否多级目录
					if (index != -1) {
						dir = FTPStringUtils.getRemoteName(ftpClient, tempDir.substring(0, index));
						// 子目录切换失败，表示不存在
						hasChanged = ftpClient.changeWorkingDirectory(dir);
						ftpClient.printWorkingDirectory();
						tempDir = tempDir.substring(index + 1, tempDir.length());
						hasNext = hasChanged;
					} else {
						// 只有一层目录
						dir = FTPStringUtils.getRemoteName(ftpClient, tempDir);
						hasChanged = ftpClient.changeWorkingDirectory(dir);
						ftpClient.printWorkingDirectory();
						hasNext = false;
					}
				}
			}
			return hasChanged;
		}
		return false;
	}
	
	/**
	 * 
	 * <p> 切换目录至指定目录，如果指定目录不存在创建目录，并返回结果标志</p>
	 * @param ftpClient
	 * @param ftpDir
	 * @return
	 * @throws IOException
	 */
	public static boolean changeExistsDirectory(FTPClient ftpClient, String ftpDir) throws IOException {
		if (ftpDir == null) {
			return false;
		}
		// 路径分割符
		String separator = FTPConfigurationUtils.getFileSeparator(ftpClient.getClientConfig());
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 转换目录为目标服务器目录格式
		ftpDir = FTPPathUtils.getPath(ftpDir);
		// 转码后的目录名称
		String remoteDir = FTPStringUtils.getRemoteName(ftpClient, ftpDir);
		// 切换目录至根目录
		ftpClient.changeWorkingDirectory(rootDir);
		// 验证是否有该文件夹，有就转到，没有创建后转到该目录下
		boolean hasChanged = ftpClient.changeWorkingDirectory(remoteDir);
		ftpClient.printWorkingDirectory();
		// 没有找到指定的目录，则逐级的创建目录（考虑兼容问题，逐级创建目录）
		if (!hasChanged) {
			// 跳转到指定的文件目录
			String tempDir = FTPPathUtils.getPath(ftpDir);
			// 多层目录循环切换
			boolean hasNext = true;
			int index = 0;
			String dir = null;
			while (hasNext) {
				// 是否包含/
				index = tempDir.indexOf(separator);
				// 判断是否多级目录
				if (index != -1) {
					dir = FTPStringUtils.getRemoteName(ftpClient, tempDir.substring(0, index));
					// 子目录切换失败，表示不存在
					hasChanged = ftpClient.changeWorkingDirectory(dir);
					ftpClient.printWorkingDirectory();
					if (!hasChanged) {
						ftpClient.makeDirectory(dir);
						hasChanged = ftpClient.changeWorkingDirectory(dir);
						ftpClient.printWorkingDirectory();
					}
					tempDir = tempDir.substring(index + 1, tempDir.length());
					hasNext = hasChanged;
				} else {
					// 只有一层目录
					dir = FTPStringUtils.getRemoteName(ftpClient, tempDir);
					hasChanged = ftpClient.changeWorkingDirectory(dir);
					ftpClient.printWorkingDirectory();
					if (!hasChanged) {
						ftpClient.makeDirectory(dir);
						hasChanged = ftpClient.changeWorkingDirectory(dir);
						ftpClient.printWorkingDirectory();
					}
					hasNext = false;
				}
			}
		}
		return hasChanged;
	}

	public static FTPFile getFTPFile(FTPClient ftpClient, String fileName) throws IOException {
		ftpClient.listFiles(null, new NameFileFilter(fileName));
		
		// 发送 LIST 命令至 FTP 站点，使用系统默认的机制列出当前工作目录的文件信息
		FTPFile[] files = ftpClient.listFiles(FTPStringUtils.getRemoteName(ftpClient, fileName));
		// 异常检查
		FTPExceptionUtils.assertFile(files, fileName);
		FTPFile fileTmp = files[0];
		// 异常检查
		FTPExceptionUtils.assertFile(fileTmp, fileName);
		return fileTmp;
	}

	public static FTPFile getFTPFile(FTPClient ftpClient, String ftpDir, String fileName) throws IOException {
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 切换目录至根目录
		ftpClient.changeWorkingDirectory(rootDir);
		try {
			
			ftpClient.listFiles(ftpDir, new NameFileFilter(fileName));
			
			
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			ftpClient.listFiles(ftpDir, new NameFileFilter(fileName));
			
			
			// 异常检查
			FTPExceptionUtils.assertDir(hasChanged, ftpDir);
			return FTPClientUtils.getFTPFile(ftpClient, fileName);
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}

	public static String[] listNames(FTPClient ftpClient, String ftpDir) throws IOException {
		// 转码后的目录名称
		String remoteDir = FTPStringUtils.getRemoteName(ftpClient, ftpDir);
		// 发送 LIST 命令至 FTP 站点，使用系统默认的机制列出当前工作目录的文件信息
		String[] fileNames = ftpClient.listNames(remoteDir);
		if (fileNames != null && fileNames.length > 0) {
			for (int i = 0; i < fileNames.length; i++) {
				if (fileNames[i] != null) {
					fileNames[i] = FTPStringUtils.getLocalName(ftpClient, fileNames[i]);
				}
			}
		}
		return fileNames;
	}

	public static List<FTPFile> listFiles(FTPClient ftpClient, String ftpDir) throws IOException {
		return listFiles(ftpClient, ftpDir, false);
	}

	public static List<FTPFile> listFiles(FTPClient ftpClient, String ftpDir, boolean recursion) throws IOException {
		// 路径分割符
		String separator = FTPConfigurationUtils.getFileSeparator(ftpClient.getClientConfig());
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 切换目录至根目录
		ftpClient.changeWorkingDirectory(rootDir);
		// 创建文件类型的文件集合
		List<FTPFile> fileList = new ArrayList<FTPFile>();
		try {
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			// 异常检查
			FTPExceptionUtils.assertDir(hasChanged, ftpDir);
			// 发送 LIST 命令至 FTP 站点，使用系统默认的机制列出当前工作目录的文件信息
			FTPFile[] files = ftpClient.listFiles();
			if (files != null && files.length > 0) {
				for (FTPFile ftpFile : files) {
					String fileName = FTPStringUtils.getLocalName(ftpClient, ftpFile);
					if (ftpFile.isDirectory()) {
						if (recursion) {
							fileList.addAll(FTPClientUtils.listFiles(ftpClient, ftpDir + separator + fileName));
						} else {
							fileList.add(ftpFile);
						}
					} else {
						fileList.add(ftpFile);
					}
				}
			}
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
		return fileList;
	}

	public static List<FTPFile> listFiles(FTPClient ftpClient, String ftpDir, String[] extensions, boolean recursion)
			throws IOException {
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 切换目录至根目录
		ftpClient.changeWorkingDirectory(rootDir);
		// 创建文件类型的文件集合
		List<FTPFile> fileList = new ArrayList<FTPFile>();
		try {
			// 切换目录
			boolean hasChanged = changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			// 异常检查
			FTPExceptionUtils.assertDir(hasChanged, ftpDir);
			// 发送 LIST 命令至 FTP 站点，使用系统默认的机制列出当前工作目录的文件信息
			Collection<FTPFile> files = FTPFileUtils.listFiles(ftpClient.listFiles(), extensions);
			if (files != null && files.size() > 0) {
				// 循环文件
				for (FTPFile ftpFile : files) {
					if (ftpFile.isDirectory()) {
						fileList.addAll(FTPFileUtils.listFiles(ftpClient, ftpFile, extensions, recursion));
					} else {
						fileList.add(ftpFile);
					}
				}
			}
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
		return fileList;
	}

	public static List<FTPFile> listFiles(FTPClient ftpClient, String ftpDir, FTPFileFilter filter, boolean recursion)
			throws IOException {
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 切换目录至根目录
		ftpClient.changeWorkingDirectory(rootDir);
		// 创建文件类型的文件集合
		List<FTPFile> fileList = new ArrayList<FTPFile>();
		try {
			// 切换目录
			boolean hasChanged = changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			// 异常检查
			FTPExceptionUtils.assertDir(hasChanged, ftpDir);
			// 发送 LIST 命令至 FTP 站点，使用系统默认的机制列出当前工作目录的文件信息
			Collection<FTPFile> files = FTPFileUtils.listFiles(ftpClient.listFiles(), filter);
			if (files != null && files.size() > 0) {
				// 循环文件
				for (FTPFile ftpFile : files) {
					if (ftpFile.isDirectory()) {
						fileList.addAll(FTPFileUtils.listFiles(ftpClient, ftpFile, filter, recursion));
					} else {
						fileList.add(ftpFile);
					}
				}
			}
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
		return fileList;
	}

	/**
	 * 
	 * <p> 创建目录，并且创建完目录后，设置工作目录为当前创建的目录下</p>
	 * @param ftpClient
	 * @param ftpDir
	 * @return
	 * @throws IOException
	 */
	public static boolean makeRootDir(FTPClient ftpClient, String ftpDir) throws IOException {
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 切换目录至根目录
		ftpClient.changeWorkingDirectory(rootDir);
		// 转换目录为目标服务器目录格式
		ftpDir = FTPPathUtils.getPath(ftpDir);
		// 转码后的目录名称
		String remoteDir = FTPStringUtils.getRemoteName(ftpClient, ftpDir);
		// 验证是否有该文件夹，有就转到，没有创建后转到该目录下
		if (ftpClient.changeWorkingDirectory(remoteDir)) {
			ftpClient.printWorkingDirectory();
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			return true;
		}
		// 在当前工作目录下新建子目录
		return ftpClient.makeDirectory(remoteDir);
	}
	
	/**
	 * 
	 * <p> 循环创建目录，并且创建完目录后，设置工作目录为当前创建的目录下</p>
	 * @param ftpClient
	 * @param ftpDir
	 * @return
	 * @throws IOException
	 */
	public static boolean makeDirectory(FTPClient ftpClient, String ftpDir) throws IOException {
		// 路径分割符
		String separator = FTPConfigurationUtils.getFileSeparator(ftpClient.getClientConfig());
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 切换目录至根目录
		ftpClient.changeWorkingDirectory(rootDir);
		// 转换目录为目标服务器目录格式
		ftpDir = FTPPathUtils.getPath(ftpDir);
		// 验证是否有该文件夹，有就转到，没有创建后转到该目录下
		String remoteDir = FTPStringUtils.getRemoteName(ftpClient, ftpDir);
		if (ftpClient.changeWorkingDirectory(remoteDir)) {
			ftpClient.printWorkingDirectory();
			return true;
		}
		try {
			// 跳转到指定的文件目录
			boolean hasMaked = false;
			if (ftpDir != null && !ftpDir.equals("")) {
				String parentDir = null;
				// 多层目录循环切换
				if (ftpDir.indexOf(separator) != -1) {
					int index = 0;
					boolean hasNext = true;
					while ((index = ftpDir.indexOf(separator)) != -1 && hasNext) {
						// 转码后的目录名称
						parentDir = FTPStringUtils.getRemoteName(ftpClient, ftpDir.substring(0, index));
						// 创建目录
						hasMaked = ftpClient.makeDirectory(parentDir);
						// 切换至新建目录
						ftpClient.changeWorkingDirectory(parentDir);
						ftpClient.printWorkingDirectory();
						// 下一级目录
						ftpDir = ftpDir.substring(index + 1, ftpDir.length());
						hasNext = hasMaked;
					}
					if (!ftpDir.equals("")) {
						// 转码后的目录名称
						parentDir = FTPStringUtils.getRemoteName(ftpClient, ftpDir);
						// 创建目录
						hasMaked = ftpClient.makeDirectory(parentDir);
						// 切换至新建目录
						ftpClient.changeWorkingDirectory(parentDir);
						ftpClient.printWorkingDirectory();
					}
				} else {// 只有一层目录

					// 转码后的目录名称
					parentDir = FTPStringUtils.getRemoteName(ftpClient, ftpDir);
					// 创建目录
					hasMaked = ftpClient.makeDirectory(parentDir);
					// 切换至新建目录
					ftpClient.changeWorkingDirectory(parentDir);
					ftpClient.printWorkingDirectory();
				}
			}
			return hasMaked;
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
	/**
	 * 
	 * <p>在指定目录创建目录，并且创建完目录后，设置工作目录为当前创建的目录下</p>
	 * @param ftpClient
	 * @param parentDir
	 * @param ftpDir
	 * @return
	 * @throws IOException
	 */
	public static boolean makeDirectory(FTPClient ftpClient, String parentDir, String ftpDir) throws IOException {
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 切换目录至根目录
		ftpClient.changeWorkingDirectory(rootDir);
		try {
			// 转换目录为目标服务器目录格式
			parentDir = FTPPathUtils.getPath(parentDir);
			// 转码后的目录名称
			String remoteParentDir = FTPStringUtils.getRemoteName(ftpClient, parentDir);
			// 验证是否有该文件夹，有就转到，没有创建后转到该目录下
			if (!ftpClient.changeWorkingDirectory(remoteParentDir)) {
				ftpClient.makeDirectory(remoteParentDir);
				ftpClient.changeWorkingDirectory(remoteParentDir);
				ftpClient.printWorkingDirectory();
			}
			// 转换目录为目标服务器目录格式
			String tempDir = FTPPathUtils.getPath(ftpDir);
			// 转码后的目录名称
			return ftpClient.makeDirectory(FTPStringUtils.getRemoteName(ftpClient, tempDir));
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
	/**
	 * 
	 * 获取当前目录的父级目录
	 * @param ftpClient		： FTPClient对象
	 * @return
	 * @throws IOException
	 */
	public static String getParentDirectory(FTPClient ftpClient) throws IOException {
		// 当前目录
		String currentDir = ftpClient.printWorkingDirectory();
		ftpClient.changeToParentDirectory();
		currentDir = ftpClient.printWorkingDirectory();
		return currentDir;
	}
	
	/**
	 * 
	 * 检查指定目录是否存在
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @return
	 * @throws IOException
	 */
	public static boolean hasDirectory(FTPClient ftpClient, String ftpDir) throws IOException {
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 切换目录至根目录
		ftpClient.changeWorkingDirectory(rootDir);
		try {
			// 切换目录
			return FTPClientUtils.changeDirectory(ftpClient, ftpDir);
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
	/**
	 * 删除FTP服务器上的文件
	 * <p> 1、采用方法： ftpClient.deleteFile(pathname) </p>
	 * @param ftpClient		： FTPClient对象
	 * @param fileNames		：文件名称
	 * @return
	 * @throws IOException
	 */
	public static boolean deleteFile(FTPClient ftpClient, String[] fileNames) throws IOException {
		for (String fileName : fileNames) {
			// 删除 FTP 站点上的一个指定文件
			boolean hasDel = ftpClient.deleteFile(fileName);
			if (!hasDel) {
				LOG.error("Can't Del file [" + fileName + "] from FTP server.");
			}
		}
		return true;
	}
	
	/**
	 * 删除FTP服务器上的文件
	 * <p> 1、采用方法： ftpClient.deleteFile(pathname) </p>
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param fileNames		：文件名称
	 * @return
	 * @throws IOException
	 */
	public static boolean deleteFile(FTPClient ftpClient, String ftpDir, String... fileNames) throws IOException {
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		try {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			if( hasChanged ) {
				for (String fileName : fileNames) {
					// 删除 FTP 站点上的一个指定文件
					boolean hasDel = ftpClient.deleteFile(fileName);
					if (!hasDel) {
						LOG.error("Can't Del file '" + fileName + "' from FTP server.");
					}
				}
			}
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
		return true;
	}

	public static boolean removeDirectory(FTPClient ftpClient, String ftpDir) throws IOException {
		// 切换目录
		boolean hasDir = FTPClientUtils.hasDirectory(ftpClient, ftpDir);
		// 异常检查
		FTPExceptionUtils.assertDir(hasDir, ftpDir);
		// 切换到父级目录
		ftpClient.changeToParentDirectory();
		return ftpClient.removeDirectory(FTPStringUtils.getRemoteName(ftpClient, ftpDir));
	}

	// delete all subDirectory and files.
	public static boolean removeDirectory(FTPClient ftpClient, String ftpDir, boolean isAll) throws IOException {

		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 切换目录至根目录
		ftpClient.changeWorkingDirectory(rootDir);
		try {
			// 切换目录
			boolean hasDir = FTPClientUtils.hasDirectory(ftpClient, ftpDir);
			// 异常检查
			FTPExceptionUtils.assertDir(hasDir, ftpDir);
			// 转换目录为目标服务器目录格式
			ftpDir = FTPPathUtils.getPath(ftpDir);
			// 转码后的目录名称
			String remoteDir = FTPStringUtils.getRemoteName(ftpClient, ftpDir);
			if (!isAll) {
				ftpClient.changeToParentDirectory();
				// 异常检查
				FTPExceptionUtils.assertRomve(ftpClient.removeDirectory(remoteDir), ftpDir);
			}
			FTPFile[] ftpFileArr = ftpClient.listFiles(remoteDir);
			if (ftpFileArr == null || ftpFileArr.length == 0) {
				ftpClient.changeToParentDirectory();
				return ftpClient.removeDirectory(remoteDir);
			}
			// 路径分割符
			String separator = FTPConfigurationUtils.getFileSeparator(ftpClient.getClientConfig());
			// 切换到指定目录
			ftpClient.changeWorkingDirectory(remoteDir);
			ftpClient.printWorkingDirectory();
			for (FTPFile ftpFile : ftpFileArr) {
				String fileName = FTPStringUtils.getLocalName(ftpClient, ftpFile);
				if (ftpFile.isDirectory()) {
					LOG.info("Delete subDir [" + ftpDir + separator + fileName + "]");
					FTPClientUtils.removeDirectory(ftpClient, ftpDir + separator + fileName, true);
				} else if (ftpFile.isFile()) {
					if (ftpClient.deleteFile(FTPStringUtils.getRemoteName(ftpClient, fileName))) {
						LOG.info("Delete file [" + ftpDir + separator + fileName + "]");
					}
				} else if (ftpFile.isSymbolicLink()) {

				} else if (ftpFile.isUnknown()) {

				}
			}
			return ftpClient.removeDirectory(remoteDir);
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
	/**
	 * 
	 * 续传输入流至FTP服务器
	 * <p> 1、采用方法： ftpClient.appendFile(remote, local) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param localFile		：文件
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult resumeFile(FTPClient ftpClient, File localFile) throws IOException {
		
		// 文件检查
		Assert.notNull(localFile, "The localFile must not be null.");
		Assert.isTrue(localFile.isFile(), "Local file [" + localFile.getPath() + "] not a file.");
		Assert.isTrue(localFile.exists(), "Local file [" + localFile.getPath() + "] not exist.");
	
		return resumeFile(ftpClient, localFile.getName(), new FileInputStream(localFile));
	}
	
	
	/**
	 * 
	 * 续传输入流至FTP服务器
	 * <p> 1、采用方法： ftpClient.appendFile(remote, local) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：文件名称 
	 * @param localFile		：文件
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult resumeFile(FTPClient ftpClient, String ftpDir, File localFile) throws IOException {
		
		// 文件检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(localFile, "The localFile must not be null.");
		Assert.isTrue(localFile.isFile(), "Local file [" + localFile.getPath() + "] not a file.");
		Assert.isTrue(localFile.exists(), "Local file [" + localFile.getPath() + "] not exist.");
	
		return resumeFile(ftpClient, ftpDir, new FileInputStream(localFile));
	}
	
	/**
	 * 
	 * 续传输入流至FTP服务器
	 * <p> 1、采用方法： ftpClient.appendFile(remote, local) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：文件名称 
	 * @param input			： 输入流
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult resumeFile(FTPClient ftpClient, String fileName, InputStream input) throws IOException {
		
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		Assert.notNull(input, "The input must not be null.");
		
		try {
			
			FTPStoreResult result = new FTPStoreResult();
			
			result.setLength(input.available());
			result.setOriginalName(fileName);

			// 包装文件输入流
			input = IOUtils.toBufferedInputStream(input, ftpClient.getBufferSize());
			
			String[] fileNames = ftpClient.listNames(fileName);
			// 如果文件存在
			if(null != fileNames) {
				// 获取文件信息
				FTPFile ftpFile = ftpClient.listFiles(fileName)[0];
				// 跳过指定的长度,实现断点续传
				IOUtils.skip(input, ftpFile.getSize());
			}
			
			// 根据指定的名字和输入流InputStream向服务器续传一个文件
			result.setResult(ftpClient.appendFile(fileName, input));
			
			return result;
			
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(input);
		}
	}
	
	
	/**
	 * 上传输入流至FTP服务器（支持断点续传）
	 * <p> ftpClient.appendFile(remote, local)</p>
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @param input			：输入流
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult resumeFile(FTPClient ftpClient, String ftpDir, String fileName, InputStream input) throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		Assert.notNull(input, "The input must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		
		try {
			
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeExistsDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			FTPStoreResult result = new FTPStoreResult();
			
			result.setLength(input.available());
			result.setOriginalName(fileName);
			
			// 将指定的输入流写入 FTP 站点上的一个指定文件
			input = IOUtils.toBufferedInputStream(input, ftpClient.getBufferSize());
						
			String[] fileNames = ftpClient.listNames(fileName);
			// 如果文件存在
			if(null != fileNames) {
				// 获取文件信息
				FTPFile ftpFile = ftpClient.listFiles(fileName)[0];
				// 跳过指定的长度,实现断点续传
				IOUtils.skip(input, ftpFile.getSize());
			}
			
			// 根据指定的名字和输入流InputStream向服务器上传一个文件
			result.setResult(ftpClient.appendFile(fileName, input));
			
			return result;
			
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(input);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
		
	}	
	
	/**
	 * 采用NO续传文件至FTP服务器
	 * <p> ftpClient.appendFileStream(remote)</p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：上次存储的文件名称
	 * @param localFile		：文件对象
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult resumeFileChannel(FTPClient ftpClient, String fileName, File localFile) throws IOException {
		
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		Assert.notNull(localFile, "The localFile must not be null.");
		Assert.isTrue(localFile.isFile(), "Local file [" + localFile.getPath() + "] not a file.");
		Assert.isTrue(localFile.exists(), "Local file [" + localFile.getPath() + "] not exist.");
		
		FileChannel inChannel = null;
		try {
			// 先按照“rw”访问模式打开localFile文件，如果这个文件还不存在，RandomAccessFile的构造方法会创建该文件
			// 其中的“rws”参数中，rw代表读写方式，s代表同步方式，也就是锁。这种方式打开的文件，就是独占方式。
			// RandomAccessFile不支持只写模式，因为把参数设为“w”是非法的
			inChannel = new RandomAccessFile(localFile, "rws").getChannel();
			// 从FileChannel中读取数据写出到OutputStream
			return resumeFileChannel(ftpClient, fileName, inChannel);
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(inChannel);
		}
	}
	
	/**
	 * 采用NOI续传文件至FTP服务器
	 * <p> ftpClient.appendFileStream(remote)</p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：上次存储的文件名称
	 * @param inChannel		：文件通道
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult resumeFileChannel(FTPClient ftpClient, String fileName, FileChannel inChannel) throws IOException {

		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		
		OutputStream output = null;
		try {
			
			FTPStoreResult result = new FTPStoreResult();
			
			result.setLength(inChannel.size());
			result.setOriginalName(fileName);
			
			String[] fileNames = ftpClient.listNames(fileName);
			// 如果文件存在
			if(null != fileNames) {
				// 获取文件信息
				FTPFile ftpFile = ftpClient.listFiles(fileName)[0];
				// 跳过指定的长度,实现断点续传
				IOUtils.skip(inChannel, ftpFile.getSize());
			}
			// 初始化进度监听
			FTPCopyListenerUtils.initCopyListener(ftpClient, fileName);
			// 获得OutputStream
			output = retrieveFileStream(ftpClient, fileName);
			// 从FileChannel中读取数据写出到OutputStream
			result.setResult(FTPChannelUtils.copyLarge(inChannel, output, ftpClient));
			
			return result;
			
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(inChannel);
			// 关闭输出流
			IOUtils.closeQuietly(output);
		}
	}
	
	/**
	 * 
	 * <p> 返回一个输出流OutputStream,以便向服务器写入一个文件（支持断点续传）：</p>
	 * <p> ftpClient.appendFileStream(String remote) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：文件名称 
	 * @return
	 * @throws IOException
	 */
	public static OutputStream retrieveFileStream(FTPClient ftpClient, String fileName)
			throws IOException {
		
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// 返回一个输出流OutputStream,以便向服务器写入一个文件，该文件由用户自己指定
		OutputStream buffOutput = IOUtils.toBufferedOutputStream(ftpClient.appendFileStream(fileName), ftpClient.getBufferSize());
		// 获得OutputStream
		return FTPStreamUtils.toWrapedOutputStream(buffOutput, ftpClient);
	}
	
	
	/**
	 * 
	 * <p> 返回一个输入流InputStream,以便从服务器读取一个文件（支持断点续传）：</p>
	 * <p> ftpClient.retrieveFileStream(String remote) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：文件名称
	 * @param skipOffset	：跳过的内容长度
	 * @return
	 * @throws IOException
	 */
	public static InputStream retrieveFileStream(FTPClient ftpClient, String fileName, long skipOffset) throws IOException {
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		// 设置接收数据流的起始位置
		ftpClient.setRestartOffset(Math.max(0, skipOffset));
		/**
		 * 从服务器返回指定名称的文件的InputStream以便读取;可能Socket每次接收8KB
		 * 如果当前文件类型是ASCII，返回的InputStream将转换文件中的行分隔符到本地的代表性。 您必须关闭InputStream的当你完成从它读。
		 * 本身的InputStream将被关闭，关闭后父数据连接插座的照顾。
		 * 为了完成文件传输你必须调用completePendingCommand并检查它的返回值来验证成功。
		 */
		InputStream buffInput = IOUtils.toBufferedInputStream(ftpClient.retrieveFileStream(fileName),
				ftpClient.getBufferSize());
		// 获得InputStream
		return FTPStreamUtils.toWrapedInputStream(buffInput, ftpClient);
	}

	
	/**
	 * 
	 * <p> 返回一个输入流InputStream,以便从服务器读取一个文件（支持断点续传）：</p>
	 * <p> ftpClient.retrieveFileStream(String remote) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @param skipOffset	：跳过的内容长度
	 * @return
	 * @throws IOException
	 */
	public static InputStream retrieveFileStream(FTPClient ftpClient, String ftpDir, String fileName, long skipOffset)
			throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		try {
			
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			// 检查文件是否存在
			String remote = FTPStringUtils.getRemoteName(ftpClient, fileName);
			String[] fileNames = ftpClient.listNames(remote);
			Assert.isTrue(null != fileNames, "File " + fileName + " was not found on FTP server.");
			
			// 设置接收数据流的起始位置
			ftpClient.setRestartOffset(Math.max(0, skipOffset));
			/**
			 * 从服务器返回指定名称的文件的InputStream以便读取。
			 * 如果当前文件类型是ASCII，返回的InputStream将转换文件中的行分隔符到本地的代表性。 您必须关闭InputStream的当你完成从它读。
			 * 本身的InputStream将被关闭，关闭后父数据连接插座的照顾。
			 * 为了完成文件传输你必须调用completePendingCommand并检查它的返回值来验证成功。
			 */
			InputStream buffInput = IOUtils.toBufferedInputStream(
					ftpClient.retrieveFileStream(remote),
					ftpClient.getBufferSize());
			
			// 获得InputStream
			return FTPStreamUtils.toWrapedInputStream(buffInput, ftpClient);
			
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
	/**
	 * 从服务器返回指定名称的文件并且写入到ByteArrayOutputStream并返回该OuputStream，以便写入到文件或其它地方（基于buffer）
	 * <p> 1、调用方法： ftpClient.retrieveFile(remote, local) </p>
     * @param ftpClient		： FTPClient对象
	 * @param fileName		：文件名称
	 * @param localFile		：输出文件
	 * @return 
	 * @throws IOException
	 */
	public static boolean retrieveToFile(FTPClient ftpClient, String fileName, File localFile) throws IOException {
		
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		try {
			
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
						
			// 从服务器返回指定名称的文件并且写入到OuputStream，以便写入到文件或其它地方（基于buffer）
			OutputStream buffOutput = IOUtils.toBufferedOutputStream(new FileOutputStream(localFile));
			
			return ftpClient.retrieveFile(fileName, buffOutput);
				
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
		
	}

	/**
	 * 从服务器返回指定名称的文件并且写入到ByteArrayOutputStream并返回该OuputStream，以便写入到文件或其它地方（基于buffer）
	 * <p> 1、调用方法： ftpClient.retrieveFile(remote, local) </p>
     * @param ftpClient		： FTPClient对象
     * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @param localFile		：输出文件
	 * @return 
	 * @throws IOException
	 */
	public static boolean retrieveToFile(FTPClient ftpClient, String ftpDir, String fileName, File localFile)
			throws IOException {
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		try {
			
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
						
			// 从服务器返回指定名称的文件并且写入到OuputStream，以便写入到文件或其它地方（基于buffer）
			OutputStream buffOutput = IOUtils.toBufferedOutputStream(new FileOutputStream(localFile));
			
			return ftpClient.retrieveFile(fileName, buffOutput);
				
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
	/**
	 * 从服务器返回指定名称的文件输入流并且写入到FileChannel
	 * <p> 1、调用方法： ftpClient.retrieveFile(remote, local) </p>
     * @param ftpClient		： FTPClient对象
	 * @param fileName		：文件名称
	 * @param localFile		：输出文件
	 * @return 
	 * @throws IOException
	 */
	public static boolean retrieveToFileChannel(FTPClient ftpClient, String fileName, File localFile) throws IOException {
		
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		InputStream input = null;
		FileChannel outChannel = null;
		try {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			
			if (!localFile.exists()) {
				File dir = localFile.getParentFile();
				if (!dir.exists()) {
					dir.mkdirs();
				}
				localFile.setReadable(true);
				localFile.setWritable(true);
				localFile.createNewFile();
			}
			// 先按照“rw”访问模式打开localFile文件，如果这个文件还不存在，RandomAccessFile的构造方法会创建该文件
			// 其中的“rws”参数中，rw代表读写方式，s代表同步方式，也就是锁。这种方式打开的文件，就是独占方式。
			// RandomAccessFile不支持只写模式，因为把参数设为“w”是非法的
			outChannel = new RandomAccessFile(localFile, "rws").getChannel();
			// 初始化进度监听
			FTPCopyListenerUtils.initCopyListener(ftpClient, fileName);
			// 获得InputStream
			input = retrieveFileStream(ftpClient, fileName, outChannel.size());
			// 将InputStream写到FileChannel
			return FTPChannelUtils.copyLarge(input, outChannel, ftpClient);
		} finally {
			// 关闭输入流
			IOUtils.closeQuietly(input);
			// 关闭输出通道
			IOUtils.closeQuietly(outChannel);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
		
	}
	
	/**
	 * 从服务器返回指定名称的文件输入流并且写入到FileChannel
	 * <p> 1、调用方法： ftpClient.retrieveFile(remote, local) </p>
     * @param ftpClient		： FTPClient对象
	 * @param fileName		：文件名称
	 * @param outChannel	：文件输出通道
	 * @return 
	 * @throws IOException
	 */
	public static boolean retrieveToFileChannel(FTPClient ftpClient, String fileName, FileChannel outChannel) throws IOException {
		
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		Assert.notNull(outChannel, "The outChannel must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		InputStream input = null;
		try {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 初始化进度监听
			FTPCopyListenerUtils.initCopyListener(ftpClient, fileName);
			// 获得InputStream
			input = retrieveFileStream(ftpClient, fileName, outChannel.size());
			// 将InputStream写到FileChannel
			return FTPChannelUtils.copyLarge(input, outChannel, ftpClient);
		} finally {
			// 关闭输入流
			IOUtils.closeQuietly(input);
			// 关闭输出通道
			IOUtils.closeQuietly(outChannel);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
		
	}
	
	/**
	 * 从服务器返回指定名称的文件输入流并且写入到FileChannel
	 * <p> 1、调用方法： ftpClient.retrieveFile(remote, local) </p>
     * @param ftpClient		： FTPClient对象
     * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @param localFile		：输出文件
	 * @return 
	 * @throws IOException
	 */
	public static boolean retrieveToFileChannel(FTPClient ftpClient, String ftpDir, String fileName, File localFile) throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		InputStream input = null;
		FileChannel outChannel = null;
		try {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			if (!localFile.exists()) {
				File dir = localFile.getParentFile();
				if (!dir.exists()) {
					dir.mkdirs();
				}
				localFile.setReadable(true);
				localFile.setWritable(true);
				localFile.createNewFile();
			}
			// 先按照“rw”访问模式打开localFile文件，如果这个文件还不存在，RandomAccessFile的构造方法会创建该文件
			// 其中的“rws”参数中，rw代表读写方式，s代表同步方式，也就是锁。这种方式打开的文件，就是独占方式。
			// RandomAccessFile不支持只写模式，因为把参数设为“w”是非法的
			outChannel = new RandomAccessFile(localFile, "rws").getChannel();
			// 初始化进度监听
			FTPCopyListenerUtils.initCopyListener(ftpClient, fileName);
			// 获得InputStream
			input = retrieveFileStream(ftpClient, fileName, outChannel.size());
			// 将InputStream写到FileChannel
			return FTPChannelUtils.copyLarge(input, outChannel, ftpClient);
		} finally {
			// 关闭输入流
			IOUtils.closeQuietly(input);
			// 关闭输出通道
			IOUtils.closeQuietly(outChannel);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
		
	}
	
	/**
	 * 从服务器返回指定名称的文件输入流并且写入到FileChannel
	 * <p> 1、调用方法： ftpClient.retrieveFile(remote, local) </p>
     * @param ftpClient		： FTPClient对象
     * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @param outChannel	：文件输出通道
	 * @return 
	 * @throws IOException
	 */
	public static boolean retrieveToFileChannel(FTPClient ftpClient, String ftpDir, String fileName, FileChannel outChannel) throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		InputStream input = null;
		try {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			// 初始化进度监听
			FTPCopyListenerUtils.initCopyListener(ftpClient, fileName);
			// 获得InputStream
			input = retrieveFileStream(ftpClient, fileName, outChannel.size());
			// 将InputStream写到FileChannel
			return FTPChannelUtils.copyLarge(input, outChannel, ftpClient);
		} finally {
			// 关闭输入流
			IOUtils.closeQuietly(input);
			// 关闭输出通道
			IOUtils.closeQuietly(outChannel);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
		
	}
	
	/**
	 * 从服务器返回指定名称的文件并且写入到ByteArrayOutputStream并返回该OuputStream，以便写入到文件或其它地方（基于buffer）
	 * <p> 1、调用方法： ftpClient.retrieveFile(remote, local) </p>
	 * <p> 2、特别注意： 本方法不适用大文件，易造成内存溢出 </p>
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @return OutputStream {@link BufferedOutputStream} 对象
	 * @throws IOException
	 */
	public static OutputStream retrieveToMem(FTPClient ftpClient, String ftpDir, String fileName) throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		try {
			
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			// 带缓冲的输出流
			OutputStream buffOutput = IOUtils.toBufferedOutputStream(new ByteArrayOutputStream(), ftpClient.getBufferSize());
			// 从服务器返回指定名称的文件并且写入到OuputStream，以便写入到文件或其它地方（基于buffer）
			ftpClient.retrieveFile(fileName, buffOutput);
			
			return buffOutput;
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}

	/**
	 * 从服务器返回指定名称的文件并且写入到ByteArrayOutputStream并返回该OuputStream，以便写入到文件或其它地方（基于buffer）
	 * <p> 1、调用方法： ftpClient.retrieveFile(remote, local) </p>
	 * <p> 2、特别注意： 本方法不适用大文件，易造成内存溢出 </p>
     * @param ftpClient		： FTPClient对象
	 * @param fileName		：文件名称
	 * @return OutputStream {@link BufferedOutputStream} 对象
	 * @throws IOException
	 */
	public static OutputStream retrieveToMem(FTPClient ftpClient, String fileName)
			throws IOException {
		
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// 带缓冲的输出流
		OutputStream buffOutput = IOUtils.toBufferedOutputStream(new ByteArrayOutputStream(), ftpClient.getBufferSize());
		// 从服务器返回指定名称的文件并且写入到OuputStream，以便写入到文件或其它地方（基于buffer）
		ftpClient.retrieveFile(fileName, buffOutput);
		
		return buffOutput;
	}

	/**
	 * 从服务器返回指定名称的文件并且写入到OuputStream，以便写入到文件或其它地方（基于buffer）
	 * <p> 1、调用方法： ftpClient.retrieveFile(remote, local) </p>
     * @param ftpClient		： FTPClient对象
	 * @param fileName		：文件名称
	 * @param output		：输出流
	 * @throws IOException
	 */
	public static void retrieveToStream(FTPClient ftpClient, String fileName, OutputStream output)
			throws IOException {
		
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		Assert.notNull(output, "The output must not be null.");
		
		// 带缓冲的输出流
		OutputStream buffOutput = null;
		try {
			// 返回带缓冲的输出流
			buffOutput = IOUtils.toBufferedOutputStream(output, ftpClient.getBufferSize());
			// 从服务器返回指定名称的文件并且写入到OuputStream，以便写入到文件或其它地方（基于buffer）
			ftpClient.retrieveFile(fileName, buffOutput);
			// 刷新输出
			buffOutput.flush();
		} finally {
			// 关闭输出流
			IOUtils.closeQuietly(buffOutput);
		}
	}
	
	/**
	 * 从服务器返回指定名称的文件并且写入到OuputStream，以便写入到文件或其它地方（基于buffer）
	 * <p> 1、调用方法： ftpClient.retrieveFile(remote, local) </p>
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @param output		：输出流
	 * @throws IOException
	 */
	public static void retrieveToStream(FTPClient ftpClient, String ftpDir, String fileName, OutputStream output)
			throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		Assert.notNull(output, "The output must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		// 带缓冲的输出流
		OutputStream buffOutput = null;
		try {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			// 返回带缓冲的输出流
			buffOutput = IOUtils.toBufferedOutputStream(output, ftpClient.getBufferSize());
			// 从服务器返回指定名称的文件并且写入到OuputStream，以便写入到文件或其它地方（基于buffer）
			ftpClient.retrieveFile(fileName, buffOutput);
			// 刷新输出
			buffOutput.flush();
			
		} finally {
			// 关闭输出流
			IOUtils.closeQuietly(buffOutput);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
	/**
	 * 上传文件至FTP服务器，根据unique参数采用不同的方法：
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、根据unique参数采用不同的方法： ftpClient.storeUniqueFile(remote, local) 或  ftpClient.storeFile(remote, local) </p>
	 * @param ftpClient		： FTPClient对象
	 * @param localFile		：本地文件
	 * @param unique		：文件名是否唯一
	 * @return {@link FTPStoreResult} 上传结果
	 * @throws IOException
	 */
	public static FTPStoreResult storeFile(FTPClient ftpClient, File localFile, boolean unique) throws IOException {
		
		// 文件检查
		Assert.isTrue(localFile.isFile(), "Local file [" + localFile.getPath() + "] not a file.");
		Assert.isTrue(localFile.exists(), "Local file [" + localFile.getPath() + "] not exist.");
		
		InputStream input = null;
		try {
			
			FTPStoreResult result = new FTPStoreResult();
			
			// 调用文件重命名策略构造新的文件: 重新命名后的文件
			LOG.debug("localFile : fileName = {0}, path = {1}", localFile.getName(), localFile.getAbsolutePath());
			File renameFile = ftpClient.getFileRenamePolicy().rename(localFile);
			LOG.debug("renameFile : fileName = {0}, path = {1}", renameFile.getName(), renameFile.getAbsolutePath());
			
			result.setLength(renameFile.length());
			result.setOriginalName(localFile.getName());
			result.setRenamedName(renameFile.getName());
			
			// 包装文件输入流
			input = IOUtils.toBufferedInputStream(renameFile, ftpClient.getBufferSize());
			
			if(unique) {
				result.setResult(ftpClient.storeUniqueFile(renameFile.getName(), input));
			} else {
				result.setResult(ftpClient.storeFile(renameFile.getName(), input));
			}
			
			return result;
			
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(input);
		}
		 
	}
	
	/**
	 * 上传文件至FTP服务器，根据unique参数采用不同的方法：
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、根据unique参数采用不同的方法： ftpClient.storeUniqueFile(remote, local) 或  ftpClient.storeFile(remote, local) </p>
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param localFile		：本地文件
	 * @param unique		：文件名是否唯一
	 * @return {@link FTPStoreResult} 上传结果
	 * @throws IOException
	 */
	public static FTPStoreResult storeFile(FTPClient ftpClient, String ftpDir, File localFile, boolean unique) throws IOException {
		
		// 文件检查
		Assert.isTrue(localFile.isFile(), "Local file [" + localFile.getPath() + "] not a file.");
		Assert.isTrue(localFile.exists(), "Local file [" + localFile.getPath() + "] not exist.");
	
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		InputStream input = null;
		try {
			
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			
			FTPStoreResult result = new FTPStoreResult();
			
			// 调用文件重命名策略构造新的文件: 重新命名后的文件
			LOG.debug("localFile : fileName = {0}, path = {1}", localFile.getName(), localFile.getAbsolutePath());
			File renameFile = ftpClient.getFileRenamePolicy().rename(localFile);
			LOG.debug("renameFile : fileName = {0}, path = {1}", renameFile.getName(), renameFile.getAbsolutePath());
			
			result.setLength(renameFile.length());
			result.setOriginalName(localFile.getName());
			result.setRenamedName(renameFile.getName());
				
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeExistsDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			// 包装文件输入流
			input = IOUtils.toBufferedInputStream(renameFile, ftpClient.getBufferSize());
			
			if(unique) {
				result.setResult(ftpClient.storeUniqueFile(renameFile.getName(), input));
			} else {
				result.setResult(ftpClient.storeFile(renameFile.getName(), input));
			}
			
			return result;
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(input);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
	/**
	 * 采用NOI上传文件至FTP服务器
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、采用方法： ftpClient.storeUniqueFileStream(remote) 获取输出流 </p>
	 * <p> 3、NIO 读取文件并写入输出流 </p>
	 * @param ftpClient		： FTPClient对象
	 * @param localFile		：文件
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult storeFileChannel(FTPClient ftpClient, File localFile) throws IOException {
		
		// 文件检查
		Assert.isTrue(localFile.isFile(), "Local file [" + localFile.getPath() + "] not a file.");
		Assert.isTrue(localFile.exists(), "Local file [" + localFile.getPath() + "] not exist.");
		
		OutputStream output = null;
		FileChannel inChannel = null;
		try {
			
			FTPStoreResult result = new FTPStoreResult();
			
			// 调用文件重命名策略构造新的文件: 重新命名后的文件
			LOG.debug("localFile : fileName = {0}, path = {1}", localFile.getName(), localFile.getAbsolutePath());
			File renameFile = ftpClient.getFileRenamePolicy().rename(localFile);
			LOG.debug("renameFile : fileName = {0}, path = {1}", renameFile.getName(), renameFile.getAbsolutePath());
			
			result.setLength(renameFile.length());
			result.setOriginalName(localFile.getName());
			result.setRenamedName(renameFile.getName());
			
			// 先按照“rw”访问模式打开localFile文件，如果这个文件还不存在，RandomAccessFile的构造方法会创建该文件
			// 其中的“rws”参数中，rw代表读写方式，s代表同步方式，也就是锁。这种方式打开的文件，就是独占方式。
			// RandomAccessFile不支持只写模式，因为把参数设为“w”是非法的
			inChannel = new RandomAccessFile(renameFile, "rws").getChannel();
			// 初始化进度监听
			FTPCopyListenerUtils.initCopyListener(ftpClient, renameFile.getName());
			// 获得OutputStream
			output = storeUniqueFileStream(ftpClient, renameFile.getName());
			// 从FileChannel中读取数据写出到OutputStream
			result.setResult(FTPChannelUtils.copyLarge(inChannel, output, ftpClient));
			
			return result;
			
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(inChannel);
			// 关闭输出流
			IOUtils.closeQuietly(output);
		}
	}
	
	/**
	 * 采用NOI上传文件至FTP服务器
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、采用方法： ftpClient.storeUniqueFileStream(remote) 获取输出流 </p>
	 * <p> 3、NIO 读取文件并写入输出流 </p>
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param localFile		：文件
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult storeFileChannel(FTPClient ftpClient, String ftpDir, File localFile) throws IOException {
		
		// 文件检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(localFile, "The localFile must not be null.");
		Assert.isTrue(localFile.isFile(), "Local file [" + localFile.getPath() + "] not a file.");
		Assert.isTrue(localFile.exists(), "Local file [" + localFile.getPath() + "] not exist.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		OutputStream output = null;
		FileChannel inChannel = null;
		try {
			
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			
			FTPStoreResult result = new FTPStoreResult();
			
			// 调用文件重命名策略构造新的文件: 重新命名后的文件
			LOG.debug("localFile : fileName = {0}, path = {1}", localFile.getName(), localFile.getAbsolutePath());
			File renameFile = ftpClient.getFileRenamePolicy().rename(localFile);
			LOG.debug("renameFile : fileName = {0}, path = {1}", renameFile.getName(), renameFile.getAbsolutePath());
			
			result.setLength(renameFile.length());
			result.setOriginalName(localFile.getName());
			result.setRenamedName(renameFile.getName());
			
			// 先按照“rw”访问模式打开localFile文件，如果这个文件还不存在，RandomAccessFile的构造方法会创建该文件
			// 其中的“rws”参数中，rw代表读写方式，s代表同步方式，也就是锁。这种方式打开的文件，就是独占方式。
			// RandomAccessFile不支持只写模式，因为把参数设为“w”是非法的
			inChannel = new RandomAccessFile(renameFile, "rws").getChannel();
			
			// 初始化进度监听
			FTPCopyListenerUtils.initCopyListener(ftpClient, renameFile.getName());
			// 获得OutputStream
			output = storeUniqueFileStream(ftpClient, ftpDir, renameFile.getName());
			// 从FileChannel中读取数据写出到OutputStream
			result.setResult(FTPChannelUtils.copyLarge(inChannel, output, ftpClient));
			
			return result;
			
		} finally {
			// 关闭输出通道
			IOUtils.closeQuietly(output);
			// 关闭输入通道
			IOUtils.closeQuietly(inChannel);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
	
	/**
	 * 采用NOI上传文件至FTP服务器
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、采用方法： ftpClient.storeUniqueFileStream(remote) 获取输出流 </p>
	 * <p> 3、NIO 读取文件并写入输出流 </p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：文件名称
	 * @param inChannel		：文件通道
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult storeFileChannel(FTPClient ftpClient, String fileName, FileChannel inChannel) throws IOException {
		
		// 文件检查
		Assert.notNull(fileName, "The fileName must not be null.");
		Assert.notNull(inChannel, "The inChannel must not be null.");
		
		OutputStream output = null; 
		try {
			
			FTPStoreResult result = new FTPStoreResult();
			
			// 调用文件重命名策略构造新的文件: 重新命名后的文件
			LOG.debug("local : fileName = {0} ", fileName);
			String renamedName = ftpClient.getFileRenamePolicy().rename(fileName);
			LOG.debug("rename : fileName = {0}, path = {1}", renamedName);
			
			result.setLength(inChannel.size());
			result.setOriginalName(fileName);
			result.setRenamedName(renamedName);
		 
			// 初始化进度监听
			FTPCopyListenerUtils.initCopyListener(ftpClient, renamedName);
			// 获得OutputStream
			output = storeUniqueFileStream(ftpClient, renamedName);
			// 从FileChannel中读取数据写出到OutputStream
			result.setResult(FTPChannelUtils.copyLarge(inChannel, output, ftpClient));
			
			return result;
			
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(inChannel);
			// 关闭输出流
			IOUtils.closeQuietly(output);
		}
	}
	

	/**
	 * 采用NOI上传文件至FTP服务器
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、采用方法： ftpClient.storeUniqueFileStream(remote) 获取输出流 </p>
	 * <p> 3、NIO 读取文件并写入输出流 </p>
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @param inChannel		：文件通道
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult storeFileChannel(FTPClient ftpClient, String ftpDir, String fileName, FileChannel inChannel) throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		Assert.notNull(inChannel, "The inChannel must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		OutputStream output = null; 
		try {
			
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			
			FTPStoreResult result = new FTPStoreResult();
			
			// 调用文件重命名策略构造新的文件: 重新命名后的文件
			LOG.debug("local : fileName = {0} ", fileName);
			String renamedName = ftpClient.getFileRenamePolicy().rename(fileName);
			LOG.debug("rename : fileName = {0}, path = {1}", renamedName);
			
			result.setLength(inChannel.size());
			result.setOriginalName(fileName);
			result.setRenamedName(renamedName);
			
			// 初始化进度监听
			FTPCopyListenerUtils.initCopyListener(ftpClient, renamedName);
			// 获得OutputStream
			output = storeUniqueFileStream(ftpClient, ftpDir, renamedName);
			// 从FileChannel中读取数据写出到OutputStream
			result.setResult(FTPChannelUtils.copyLarge(inChannel, output, ftpClient));
			
			return result;
			
		} finally {
			// 关闭输出通道
			IOUtils.closeQuietly(output);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}

	/**
	 * 
	 * <p> 根据给定的名字返回一个能够向服务器上传文件的OutputStream</p>
	 * <p> ftpClient.storeFileStream(String remote) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：文件名称
	 * @param unique		：文件是否唯一  
	 * @return
	 * @throws IOException
	 */
	public static OutputStream storeFileStream(FTPClient ftpClient, String fileName) throws IOException {
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
		// 返回一个输出流OutputStream,以便向服务器写入一个文件，该文件由用户自己指定
		OutputStream buffOutput = IOUtils.toBufferedOutputStream(ftpClient.storeFileStream(fileName),
					ftpClient.getBufferSize());
		// 获得OutputStream
		return FTPStreamUtils.toWrapedOutputStream(buffOutput, ftpClient);
	}

	/**
	 * 
	 * <p> 根据给定的名字返回一个能够向服务器上传文件的OutputStream</p>
	 * <p> ftpClient.storeFileStream(String remote) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @return
	 * @throws IOException
	 */
	public static OutputStream storeFileStream(FTPClient ftpClient, String ftpDir, String fileName)
			throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		
		try {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeExistsDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			// 带缓冲的输出流
			OutputStream buffOutput = null;
			// 返回一个输出流OutputStream,以便向服务器写入一个文件，该文件由用户自己指定
			buffOutput = IOUtils.toBufferedOutputStream(ftpClient.storeFileStream(fileName),
					ftpClient.getBufferSize());
			// 获得OutputStream
			return FTPStreamUtils.toWrapedOutputStream(buffOutput, ftpClient);
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}

	
	/**
	 * 
	 * 上传输入流至FTP服务器
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、采用方法： ftpClient.storeFile(remote, local) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：文件名称 
	 * @param input			： 输入流
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult storeFile(FTPClient ftpClient, String fileName, InputStream input) throws IOException {
		try {
			
			// 参数检查
			Assert.notNull(fileName, "The fileName must not be null.");
			Assert.notNull(input, "The input must not be null.");
			
			FTPStoreResult result = new FTPStoreResult();
			
			// 调用文件重命名策略构造新的文件: 重新命名后的文件
			LOG.debug("local : fileName = {0} ", fileName);
			String renamedName = ftpClient.getFileRenamePolicy().rename(fileName);
			LOG.debug("rename : fileName = {0}, path = {1}", renamedName);
			
			result.setLength(input.available());
			result.setOriginalName(fileName);
			result.setRenamedName(renamedName);
			
			// 包装文件输入流
			input = IOUtils.toBufferedInputStream(input, ftpClient.getBufferSize());
			// 根据指定的名字和输入流InputStream向服务器上传一个文件
			result.setResult(ftpClient.storeFile(renamedName, input));
			
			return result;
			
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(input);
		}
	}
	
	/**
	 * 上传输入流至FTP服务器
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、采用方法： ftpClient.storeFile(remote, local) </p>
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @param input			：输入流
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult storeFile(FTPClient ftpClient, String ftpDir, String fileName, InputStream input) throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		Assert.notNull(input, "The input must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		
		try {
			
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			
			FTPStoreResult result = new FTPStoreResult();
			
			// 调用文件重命名策略构造新的文件: 重新命名后的文件
			LOG.debug("local : fileName = {0} ", fileName);
			String renamedName = ftpClient.getFileRenamePolicy().rename(fileName);
			LOG.debug("rename : fileName = {0}, path = {1}", renamedName);
			
			result.setLength(input.available());
			result.setOriginalName(fileName);
			result.setRenamedName(renamedName);
			
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeExistsDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			// 将指定的输入流写入 FTP 站点上的一个指定文件
			// 包装文件输入流
			input = IOUtils.toBufferedInputStream(input, ftpClient.getBufferSize());
			// 根据指定的名字和输入流InputStream向服务器上传一个文件
			result.setResult(ftpClient.storeFile(renamedName, input));
			
			return result;
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(input);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
 
	
	/**
	 * 
	 * 上传文件至FTP服务器
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、调用方法： ftpClient.storeUniqueFile(remote, local)</p>
	 * @param ftpClient		： FTPClient对象
	 * @param localFile		：本地文件
	 * @return {@link FTPStoreResult} 上传结果
	 * @throws IOException
	 */
	public static FTPStoreResult storeUniqueFile(FTPClient ftpClient, File localFile) throws IOException {
		return storeFile(ftpClient, localFile, true);
	}
	
	/**
	 * 
	 * 上传输入流至FTP服务器
	 * <p> 1、采用方法： ftpClient.storeUniqueFile(local) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param input			： 输入流
	 * @return
	 * @throws IOException
	 */
	public static boolean storeUniqueFile(FTPClient ftpClient, InputStream input) throws IOException {
		
		// 参数检查
		Assert.notNull(input, "The input must not be null.");
		
		try {
			
			// 包装文件输入流
			input = IOUtils.toBufferedInputStream(input, ftpClient.getBufferSize());
			// 根据指定的名字和输入流InputStream向服务器上传一个文件
			return ftpClient.storeUniqueFile(input);
			
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(input);
		}
	}
	
	/**
	 * 
	 * 上传文件至FTP服务器
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、调用方法： ftpClient.storeUniqueFile(remote, local)</p>
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param localFile		：本地文件
	 * @return {@link FTPStoreResult} 上传结果
	 * @throws IOException
	 */
	public static FTPStoreResult storeUniqueFile(FTPClient ftpClient, String ftpDir, File localFile) throws IOException {
		return storeFile(ftpClient, ftpDir, localFile, true);
	}
	
	/**
	 * 
	 * 上传输入流至FTP服务器
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、采用方法： ftpClient.storeUniqueFile(remote, local) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：文件名称 
	 * @param input			： 输入流
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult storeUniqueFile(FTPClient ftpClient, String fileName, InputStream input) throws IOException {
		try {
			
			// 参数检查
			Assert.notNull(fileName, "The fileName must not be null.");
			Assert.notNull(input, "The input must not be null.");
			
			FTPStoreResult result = new FTPStoreResult();
			
			// 调用文件重命名策略构造新的文件: 重新命名后的文件
			LOG.debug("local : fileName = {0} ", fileName);
			String renamedName = ftpClient.getFileRenamePolicy().rename(fileName);
			LOG.debug("rename : fileName = {0}, path = {1}", renamedName);
			
			result.setLength(input.available());
			result.setOriginalName(fileName);
			result.setRenamedName(renamedName);
			
			// 包装文件输入流
			input = IOUtils.toBufferedInputStream(input, ftpClient.getBufferSize());
			// 根据指定的名字和输入流InputStream向服务器上传一个文件
			result.setResult(ftpClient.storeUniqueFile(renamedName, input));
			
			return result;
			
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(input);
		}
	}
	
	/**
	 * 上传输入流至FTP服务器
	 * <p> 1、使用重命名策略进行文件名重命名</p>
	 * <p> 2、采用方法： ftpClient.storeUniqueFile(remote, local) </p>
	 * @param ftpClient		： FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param fileName		：文件名称
	 * @param input			：输入流
	 * @return
	 * @throws IOException
	 */
	public static FTPStoreResult storeUniqueFile(FTPClient ftpClient, String ftpDir, String fileName, InputStream input) throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		Assert.notNull(input, "The input must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		
		try {
			
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			
			FTPStoreResult result = new FTPStoreResult();
			
			// 调用文件重命名策略构造新的文件: 重新命名后的文件
			LOG.debug("local : fileName = {0} ", fileName);
			String renamedName = ftpClient.getFileRenamePolicy().rename(fileName);
			LOG.debug("rename : fileName = {0}, path = {1}", renamedName);
			
			result.setLength(input.available());
			result.setOriginalName(fileName);
			result.setRenamedName(renamedName);
			
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeExistsDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			
			// 将指定的输入流写入 FTP 站点上的一个指定文件 ; 包装文件输入流
			input = IOUtils.toBufferedInputStream(input, ftpClient.getBufferSize());
			// 根据指定的名字和输入流InputStream向服务器上传一个文件
			result.setResult(ftpClient.storeUniqueFile(renamedName, input));
			
			return result;
		} finally {
			// 关闭输入通道
			IOUtils.closeQuietly(input);
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
	/**
	 * 
	 * 根据给定的名字返回一个能够向服务器上传文件的OutputStream
	 * <p> 调用方法：ftpClient.storeUniqueFileStream(String remote) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param fileName		：文件名称 
	 * @return
	 * @throws IOException
	 */
	public static OutputStream storeUniqueFileStream(FTPClient ftpClient, String fileName) throws IOException {
		
		// 参数检查
		Assert.notNull(fileName, "The fileName must not be null.");
				
		// 带缓冲的输出流
		OutputStream buffOutput = IOUtils.toBufferedOutputStream(ftpClient.storeUniqueFileStream(fileName),
					ftpClient.getBufferSize());
		// 获得OutputStream
		return FTPStreamUtils.toWrapedOutputStream(buffOutput, ftpClient);
	}
	
	/**
	 * 
	 * 根据给定的名字返回一个能够向服务器上传文件的OutputStream
	 * <p> 调用方法：ftpClient.storeUniqueFileStream(String remote) </p>
	 * @param ftpClient		：FTPClient对象
	 * @param ftpDir		：文件目录
	 * @param fileName		：文件名称 
	 * @return
	 * @throws IOException
	 */
	public static OutputStream storeUniqueFileStream(FTPClient ftpClient, String ftpDir, String fileName)
			throws IOException {
		
		// 参数检查
		Assert.notNull(ftpDir, "The ftpDir must not be null.");
		Assert.notNull(fileName, "The fileName must not be null.");
		
		// FTP服务根文件目录
		String rootDir = FTPPathUtils.getRootDir(ftpClient.getClientConfig().getRootdir());
		
		try {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
			// 切换目录
			boolean hasChanged = FTPClientUtils.changeExistsDirectory(ftpClient, ftpDir);
			Assert.isTrue(hasChanged, "Directory [ " + ftpDir + " ] was not found on FTP server.");
			// 带缓冲的输出流
			OutputStream buffOutput = null;
			// 返回一个输出流OutputStream,以便向服务器写入一个文件，该文件由用户自己指定
			buffOutput = IOUtils.toBufferedOutputStream(ftpClient.storeUniqueFileStream(fileName),
						ftpClient.getBufferSize());
			// 获得OutputStream
			return FTPStreamUtils.toWrapedOutputStream(buffOutput, ftpClient);
		} finally {
			// 切换目录至根目录
			ftpClient.changeWorkingDirectory(rootDir);
		}
	}
	
}