package net.jeebiz.ftpclient.utils;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.net.ftp.FTPClient.NatServerResolverImpl;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jeebiz.ftpclient.FTPClient;
import net.jeebiz.ftpclient.FTPClientConfig;
import net.jeebiz.ftpclient.exception.FTPClientException;

/**
 * FTPClient Socket Connect 创建与释放
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPConnectUtils {

	protected static Logger LOG = LoggerFactory.getLogger(FTPConnectUtils.class);

	/**
	 * 连接到ftp服务器
	 * @param ftpClient
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static boolean connect(FTPClient ftpClient, String host, int port, String username, String password)
			throws Exception {
		if (ftpClient == null) {
			throw new IllegalArgumentException("ftpClient is null.");
		}
		if (!ftpClient.isConnected()) {
			// 使用制定用户名和密码登入 FTP 站点
			try {
				// 1.连接服务器
				// 与制定地址和端口的 FTP 站点建立 Socket 连接
				ftpClient.connect(host, port);
				// 检测连接是否成功
				int reply = ftpClient.getReplyCode();
				// 返回的code>=200&&code<300表示成功
				if (!FTPReply.isPositiveCompletion(reply)) {
					// 释放占用的链接，并重置所有连接参数为初始值
					ftpClient.disconnect();
					LOG.warn("Unable to connect to FTP server.");
				}
				// Log success msg
				LOG.trace("...connection was successful.");
				// 2.登录ftp服务器 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
				if (ftpClient.login(username, password)) {
					// 3.判断登陆是否成功
					if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))
							&& ftpClient.getAutodetectUTF8()) {
						// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
						ftpClient.setControlEncoding("UTF-8");
					}
					return true;
				} else {
					throw new FTPClientException( "ftpClient登陆失败! userName :[" + username + "] ; password:[" + password + "]");
				}
			} catch (Exception e) {
				if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect(); // 断开连接
					} catch (IOException e1) {
						// do nothing
					}
				}
				throw new FTPClientException("Could not connect to server.", e);
			}
		}
		return true;
	}

	

	public static <T extends FTPClient> T initConnectionMode(T ftpClient, FTPClientConfig clientConfig){
		try {
			if(clientConfig.isRemotePassiveMode()){
				if(StringUtils.isNotEmpty(clientConfig.getRemoteActiveHost()) && clientConfig.getRemoteActivePort() > 0){
					//设置远程被动模式下远程端IP地址和端口
					ftpClient.enterRemoteActiveMode(InetAddress.getByName(clientConfig.getRemoteActiveHost()), clientConfig.getRemoteActivePort());
				}else{
					ftpClient.enterRemotePassiveMode();
				}
			}else if (clientConfig.isLocalActiveMode()) {
				if(StringUtils.isNotEmpty(clientConfig.getActiveExternalHost())){
					//设置在主动模式下的外部IP地址
					ftpClient.setActiveExternalIPAddress(clientConfig.getActiveExternalHost());
				}
				if(clientConfig.getActiveMinPort() > 0 && clientConfig.getActiveMaxPort() > 0){
					//设置在主动模式客户端端口范围
					ftpClient.setActivePortRange(clientConfig.getActiveMinPort(), clientConfig.getActiveMaxPort());
				}
				/**
				 * 在建立数据连接之前将数据连接模式设置为主动模式:
				 * 设置当前数据连接模式ACTIVE_LOCAL_DATA_CONNECTION_MODE 。 
				 * 没有与FTP服务器进行通信，但是这会导致所有将来的数据传输要求的FTP服务器连接到客户端的数据端口。 
				 * 此外，为了适应插座之间的差异在不同平台上实现，这种方法使客户端发出一个摆在每一个数据传输端口的命令。 
				 */
				ftpClient.enterLocalActiveMode();
			}else if (clientConfig.isLocalPassiveMode()) {
				if(StringUtils.isNotEmpty(clientConfig.getPassiveLocalHost())){
					//设置在被动模式下使用的本地IP地址
					ftpClient.setPassiveLocalIPAddress(clientConfig.getPassiveLocalHost());
				}
				//启用或禁用在被动模式下使用NAT（Network Address Translation，网络地址转换）解决方案
				ftpClient.setPassiveNatWorkaroundStrategy(new NatServerResolverImpl(ftpClient));
				/**
				 * 在建立数据连接之前发送 PASV 命令至 FTP 站点，将数据连接模式设置为被动模式:
				 * 设置当前数据连接模式PASSIVE_LOCAL_DATA_CONNECTION_MODE 。
				 * 仅用于客户端和服务器之间的数据传输，此方法。 这种方法将导致使用PASV（或EPSV）命令发出到服务器之前，每一个数据连接孔， 告诉服务器来打开一个数据端口，客户端将连接进行数据传输。
				 * 该FTPClient将留在PASSIVE_LOCAL_DATA_CONNECTION_MODE直到模式是由其他的方法，例如改变调用一些enterLocalActiveMode()
				 * 注：目前可以调用任何方法将复位模式ACTIVE_LOCAL_DATA_CONNECTION_MODE。
				 */
				//设置FTPClient为被动传输模式即可解决线程挂起问题。此代码设置在登陆之后或者之前都可以。
				ftpClient.enterLocalPassiveMode();
			}
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		
		return ftpClient;
	}
	
	/**
	 * 初始化已经与FTP服务器建立连接的FTPClient,因为有些参数需要在连接建立后进行通信或者需要socket对象初始化后才能设置
	 * @param ftpClient
	 * @return
	 */
	public static <T extends FTPClient> T initConnectedSocket(T ftpClient, FTPClientConfig clientConfig){
		
		try {
			
			//设置文件的结构
			ftpClient.setFileStructure(clientConfig.getFileStructure().getType());
			//设置文件传输模式 
			ftpClient.setFileTransferMode(clientConfig.getFileTransferMode().getType());
			//设置文件传输类型和传送格式
			//注：用FTPClient部署在Linux上出现下载的文件小于FTP服务器实际文件的问题解决方法是设置以二进制形式传输:ftpClient.setFileType(FTP.BINARY_FILE_TYPE); 
			ftpClient.setFileType(clientConfig.getFileType().getType(), clientConfig.getFileFormat().getFormat());
			ftpClient.setFileType_(clientConfig.getFileType().getType());
			
			//ftpClient.setFileType(FTPConfigurationUtils.getFileTyle(configuration));
			
			//是否保持连接
			ftpClient.setKeepAlive(clientConfig.isKeepAlive());
			/** 
             * 如果我们设置了linger而不小于0，那么close会等到发送的数据已经确认了才返回。 
             * 但是如果对方宕机，超时，那么会根据linger设定的时间返回。 
             * 有了后面三句，socket关闭后, 服务端也会收到信息
             * 
             * SO_LINGER 选项用来控制 Socket 关闭时的行为. 默认情况下, 执行 Socket 的 close() 方法, 该方法会立即返回, 
             * 但底层的 Socket 实际上并不立即关闭, 它会延迟一段时间, 直到发送完所有剩余的数据, 才会真正关闭 Socket, 断开连接.
			 * 如果执行以下方法:
			 * 	socket.setSoLinger(true, 0);                                                                                               
			 * 那么执行Socket 的close() 方法, 该方法也会立即返回, 并且底层的 Socket 也会立即关闭, 所有未发送完的剩余数据被丢弃.
			 * 如果执行以下方法:
			 *	socket.setSoLinger(true, 3600);                                                                                           
			 * 那么执行Socket 的 close() 方法, 该方法不会立即返回, 而是进入阻塞状态. 同时, 底层的 Socket 会尝试发送剩余的数据. 只有满足以下两个条件之一, close() 方法才返回:
			 *	      ⑴ 底层的 Socket 已经发送完所有的剩余数据;
			 *	      ⑵ 尽管底层的 Socket 还没有发送完所有的剩余数据, 但已经阻塞了 3600 秒(注意这里是秒, 而非毫秒), close() 方法的阻塞时间超过 3600 秒, 也会返回, 剩余未发送的数据被丢弃.
			 * 值得注意的是, 在以上两种情况内, 当close() 方法返回后, 底层的 Socket 会被关闭, 断开连接. 
			 * 此外, setSoLinger(boolean on, int seconds) 方法中的 seconds 参数以秒为单位, 而不是以毫秒为单位.    
			 * 如果未设置 SO_LINGER 选项, getSoLinger()  返回的结果是 -1, 
			 * 如果设置了 socket.setSoLinger(true, 80) , getSoLinger()  返回的结果是 80.
             */   
            if(clientConfig.isSolingerEnabled()){
            	//ftpClient.setSoLinger(true, 0) 表示 断开后及时释放端口
            	//启用/禁用具有指定逗留时间（以秒为单位）的 SO_LINGER。最大超时值是特定于平台的。 该设置仅影响套接字关闭
            	ftpClient.setSoLinger(clientConfig.isSolingerEnabled(), Math.min(0, clientConfig.getSolinger_timeout()));
            }
            ftpClient.setSoTimeout(clientConfig.getSo_timeout()  );
            //启用/禁用 TCP_NODELAY（启用/禁用 Nagle 算法）。  
			ftpClient.setTcpNoDelay(clientConfig.isTcpNoDelay());
			
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
		}
		
		return ftpClient;
	}
	
	
	/**
	 * 释放FTPClient的连接
	 * @param ftpClient
	 */
	public static void releaseConnect(FTPClient ftpClient) {
		if (ftpClient != null) {
			try {
				if (ftpClient.isConnected()) {
					// 通过发送 QUIT 命令，登出 FTP 站点
					ftpClient.logout();
				}
			} catch (IOException e) {
				throw new FTPClientException("Could not disconnect from server.", e);
			} finally {
				// 注意,一定要在finally代码中断开连接，否则会导致占用ftp连接情况
				if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect(); // 断开连接
					} catch (IOException e1) {
						// do nothing
					}
				}
			}
		}
	}

	/**
	 * 
	 * @description ： 验证FTPClient连接的有效性
	 * @author ： <a href="mailto:hnxyhcwdl1003@163.com">wandalong</a>
	 * @date ：Jan 12, 2016 9:06:01 AM
	 * @param ftpClient
	 * @return
	 */
	public static boolean validateConnect(FTPClient ftpClient) {
		if (ftpClient != null) {
			try {
				// 发送一个NOOP命令到FTP服务器。 这是为防止服务器超时有用，与 noop() 类似。防止连接超时，也可以根据返回值检查连接的状态。
				return ftpClient.sendNoOp();
			} catch (IOException e) {
				LOG.warn("Failed to validate client: ", e);
				return false;
			}
		}
		return false;
	}

}
