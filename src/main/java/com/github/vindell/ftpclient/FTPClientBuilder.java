package com.github.vindell.ftpclient;

import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.lang3.builder.Builder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.util.TrustManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.vindell.ftpclient.enums.FTPClientTypeEnum;
import com.github.vindell.ftpclient.io.CopyStreamProcessListener;
import com.github.vindell.ftpclient.io.LoggerProtocolCommandListener;
import com.github.vindell.ftpclient.utils.StringUtils;

/**
 * FTPClient对象构建器
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPClientBuilder implements Builder<FTPClient> {
	
	protected static Logger LOG = LoggerFactory.getLogger(FTPClientBuilder.class);
	protected FTPClientConfig clientConfig;
	
	public FTPClientBuilder(FTPClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	@Override
	public FTPClient build() {
		//ftp客户端对象类型：FTPClient,FTPSClient,FTPHTTPClient
		if(FTPClientTypeEnum.FTPS_CLIENT.equals(clientConfig.getClientType())){
			//加密的FTPSClient
			FTPSClient ftpsClient = new FTPSClient(false);
			
			ftpsClient = new FTPSClient();
			//初始化FTPSClient
			return this.initFTPSClient(this.initFTPClient(ftpsClient));
		}else if(FTPClientTypeEnum.FTP_HTTP_CLIENT.equals(clientConfig.getClientType())){
			//HTTP代理的FTPHTTPClient
			FTPHTTPClient ftpHttpClient = null;
			if(StringUtils.isEmpty(clientConfig.getHttpProxyUsername()) || StringUtils.isEmpty(clientConfig.getHttpProxyPassword())){
				ftpHttpClient = new FTPHTTPClient(clientConfig.getHttpProxyHost(),clientConfig.getHttpProxyPort());
			}else{
				ftpHttpClient = new FTPHTTPClient(clientConfig.getHttpProxyHost(),clientConfig.getHttpProxyPort(),clientConfig.getHttpProxyUsername(),clientConfig.getHttpProxyPassword());
			}
			//初始化FTPHTTPClient
			return this.initFTPClient(ftpHttpClient);
		}else{
			//普通的FTPClient
			FTPClient ftpClient = new FTPClient();
			//初始化FTPClient
			return this.initFTPClient(ftpClient);
		}
	}
	
	/**
	 * 初始化FTPClient
	 * @param ftpClient FTPClient对象
	 * @return FTPClient 对象
	 */
	public <T extends FTPClient> T initFTPClient(T ftpClient){
		
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
	public <T extends FTPSClient> T  initFTPSClient(T ftpsClient){
		
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

	public FTPClientConfig getClientConfig() {
		return clientConfig;
	}
	
}
