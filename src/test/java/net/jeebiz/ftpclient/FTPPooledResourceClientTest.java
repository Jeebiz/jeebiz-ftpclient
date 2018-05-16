package net.jeebiz.ftpclient;

import java.net.Proxy;
import java.util.Locale;

import javax.net.ServerSocketFactory;

import org.apache.commons.net.DefaultSocketFactory;
import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;
import org.junit.Before;

import net.jeebiz.ftpclient.client.FTPPooledResourceClient;
import net.jeebiz.ftpclient.enums.FTPServerTypeEnum;
import net.jeebiz.ftpclient.enums.FileFormatEnum;
import net.jeebiz.ftpclient.enums.FileStructureEnum;
import net.jeebiz.ftpclient.enums.FileTransferModeEnum;
import net.jeebiz.ftpclient.enums.FileTransferTypeEnum;
import net.jeebiz.ftpclient.io.PrintCopyStreamProcessListener;
import net.jeebiz.ftpclient.pool.FTPClientPool;
import net.jeebiz.ftpclient.pool.FTPClientPoolConfig;
import net.jeebiz.ftpclient.pool.FTPPooledClientFactory;
import net.jeebiz.ftpclient.rename.UUIDFileRenamePolicy;
// https://www.cnblogs.com/lighten/p/7375611.html
public class FTPPooledResourceClientTest extends FTPClientTest {
	
	@Before
	public void setUp() {
		
		
		FTPClientConfig clientConfig = new FTPClientConfig();
		
		// ftp服务器地址
		clientConfig.setHost("10.71.19.230");
		// ftp服务器端口
		clientConfig.setPort(21);
		// ftp服务器用户名
		clientConfig.setUsername("zfsoft");
		// ftp服务器密码
		clientConfig.setPassword("123456");
		// ftp服务器根路径
		clientConfig.setRootdir("/");
		
		// 在主动模式下的外部IP地址
		clientConfig.setActiveExternalHost("192.168.1.100");
		// 在主动模式客户端最大端口
		clientConfig.setActiveMaxPort(25100);
		// 在主动模式客户端起始端口
		clientConfig.setActiveMinPort(25000);
		// 启用或禁用服务器自动编码检测（只支持UTF-8支持）;默认false 
		clientConfig.setAutodetectUTF8(false);
		// 启用或禁用数据流方式上传/下载时是否在缓冲发送/接收完成自动刷新缓存区；大文件上传下载时比较有用;默认false
		clientConfig.setAutoFlush(false);
		// 数据流方式上传/下载时缓存区达到可自动刷新的最小阀值；仅当 autoflush 为true 时才有效；默认与默认缓存区大小相同即 8M
		clientConfig.setAutoFlushBlockSize(8 * 1024);
		// 为缓冲数据流而设置内部缓冲器区大小;默认 8M
		clientConfig.setBufferSize(8 * 1024 * 1024);
		// 文件通道读取缓冲区大小;默认 2M
		clientConfig.setChannelReadBufferSize(2 * 1024 * 1024);
		// 文件通道写出缓冲区大小;默认 2M
		clientConfig.setChannelWriteBufferSize(2 * 1024 * 1024);
		// Socket使用的字符集;默认UTF-8
		clientConfig.setCharset("GBK");
		// 连接超时时间，单位为毫秒，默认10000毫秒
		clientConfig.setConnectTimeout(10 * 1000);
		// 服务端编码格式;默认ISO-8859-1
		clientConfig.setControlEncoding("ISO-8859-1");
		// 控制保活消息回复等待时间,必须设置，防止长时间连接没响应，单位毫秒;默认1000毫秒；大多数FTP服务器不支持并发控制和数据连接使用
		clientConfig.setControlKeepAliveReplyTimeout(5 * 1000);
		// 发送处理文件上载或下载时，控制连接保持活动消息之间的等待时间，单位毫秒;默认1000毫秒
		clientConfig.setControlKeepAliveTimeout(10 * 1000);
		// TCP进行存储时/检索操作时数据处理进度监听对象类路径
		clientConfig.setCopyStreamListener(new PrintCopyStreamProcessListener());
		clientConfig.setCopyStreamProcessListenerName("net.jeebiz.ftpclient.io.PrintCopyStreamProcessListener");
		// 从数据连接读取数据的 超时时间，单位（毫秒）；默认 30000 毫秒
		clientConfig.setDataTimeout(60 * 1000);
		// 配置解析器用于解析文件时间戳的日期格式。 如果未指定，则此类解析器将用作默认值，这是en_US语言环境中使用的最常用格式。
		clientConfig.setDefaultDateFormatStr("yyyy-MM-dd HH:mm:SSS");
		// 文件格式：telnet,carriage_control,non_print
		clientConfig.setFileFormat(FileFormatEnum.TELNET_TEXT);
		// 文件重命名规则：默认 {@link UUIDFileRenamePolicy}
		clientConfig.setFileRenamePolicy(new UUIDFileRenamePolicy());
		clientConfig.setFileRenamePolicyName("net.jeebiz.ftpclient.rename.UUIDFileRenamePolicy");
		// 文件结构：file,record,page
		clientConfig.setFileStructure(FileStructureEnum.FILE);
		// 文件传输模式 ：stream,block,compressed
		clientConfig.setFileTransferMode(FileTransferModeEnum.STREAM);
		// 文件传输类型：ascii,ebcdic,binary,local;默认 binary;注：用FTPClient部署在Linux上出现下载的文件小于FTP服务器实际文件的问题解决方法是设置以二进制形式传输
		clientConfig.setFileType(FileTransferTypeEnum.BINARY);
		// 表示TCP是否监视连接是否有效,值为 false时不活动的客户端可能会永远存在下去, 而不会注意到服务器已经崩溃.默认值为 false
		clientConfig.setKeepAlive(false);
		clientConfig.setLenientFutureDates(false);
		// 是否获取隐藏文件，如果想获得隐藏的文件则需要设置为true,默认false
		clientConfig.setListHiddenFiles(false);
		// 是否本地主动模式 ;默认 true
		clientConfig.setLocalActiveMode(false);
		// 是否本地备份上传的文件：该方式有助于提高文件服务可用性，为用户下载文件省去网络开销
		clientConfig.setLocalBackupAble(false);
		// 本地备份路径 ;默认userdir,如果开启了本地备份功能，建议指定该目录地址
		clientConfig.setLocalBackupDir("D:\\");
		// 本地编码格式 ;默认GBK
		clientConfig.setLocalEncoding("GBK");
		// 是否本地被动模式 ,常用于服务器有防火墙的情况;默认 true
		clientConfig.setLocalPassiveMode(true);
		// 是否使用Log4j记录命令信息,默认打印出命令，如果开启日志则关闭打印;默认 false
		clientConfig.setLogDebug(true);
		// 网络超时的时限，单位为毫秒，默认60000毫秒 
		clientConfig.setNetworkTimeout(60 * 1000);
		// 用于创建FTPFileEntryParser对象的工厂
		clientConfig.setParserFactory(new DefaultFTPFileEntryParserFactory());
		// 被动模式下使用的本地IP地址
		clientConfig.setPassiveLocalHost("192.168.31.1");
		// 启用或禁用在被动模式下使用NAT（Network Address Translation，网络地址转换）解决方案。默认true
		clientConfig.setPassiveNatWorkaround(true);
		// 是否打印出FTP命令，默认 true
		clientConfig.setPrintDebug(true);
		/*
		 * Socket用于接收数据的缓冲区大小,默认是8KB.此值必须大于 0； 增大接收缓存大小可以增大大量连接的网络 I/O 的性能，而减小它有助于减少传入数据的 backlog。
		 * 计算TCP缓冲区大小:
		 * 	假设没有网络拥塞和丢包，则网络吞吐量直接和TCP缓冲区大小和网络延迟有关。网络延迟是一个包在网络中传输所用的时间。
		 * 计算出吞吐量为：
		 * 	吞吐量 = 缓冲区大小 / 网络延迟
		 * 	举例来说，从Sunnyvale 到 Reston 的网络延迟为40ms，windowsXP的默认TCP缓冲区为17520bytes，那么 17520 bytes / 0.04 seconds = 3.5 Mbits / second
		 * 	Mac OS X 是64K，所以其能达到 65936 B / 0.04 s = 13Mb / s
		 *  大多数网络专家认为较优的TCP缓冲区大小是网络的两倍延迟(delay times)乘以带宽 buffer size = 2 * delay * bandwidth
		 *  使用ping命令可以得到一次RTT时间，也就是2倍延迟，那么 buffer size = RTT * bandwidth
		 *  继续上面例子，ping返回的值为80ms，所以TCP缓冲区大小应该为 0.08s * 100Mbps / 8 = 1MByte
		 *  32kb
		 */
		clientConfig.setReceiveBufferSize(32 * 1024);
		// FTPClient接收数据的缓冲区大小,默认是8KB.
		clientConfig.setReceiveDataSocketBufferSize(8 * 1024);
		// 配置解析器用于解析文件最近修改时间戳的日期格式。 如果未指定，则此类解析器将用作默认值，这是en_US语言环境中使用的最常用格式。
		clientConfig.setRecentDateFormatStr("yyyy-MM-dd HH:mm:SSS");
		// 远程被动模式远程端IP地址 
		clientConfig.setRemoteActiveHost(null);
		// 远程被动模式远程端端口
		clientConfig.setRemoteActivePort(21);
		/* 是否远程被动模式 ;默认 false; 仅用于服务器到服务器的数据传输此方法。
		 * 这种方法发出PASV命令到服务器，告诉它打开一个数据端口的活动服务器将连接进行数据传输。 您必须调用这个摆在每一个服务器到服务器传输尝试的方法。
		 * 该FTPClient不会自动继续发行PASV命令。 你还必须记住调用enterLocalActiveMode()如果你想返回到正常的数据连接模式。
		 */
		clientConfig.setRemotePassiveMode(false);
		// 启用或禁用核实，利用远程主机的数据连接部分是作为控制连接到该连接的主机是相同的；默认true
		clientConfig.setRemoteVerificationEnabled(false);
		// 主动模式下在报告EPRT/PORT命令时使用的外部IP地址；在多网卡下很有用
		clientConfig.setReportActiveExternalHost("127.0.0.1");
		// Socket发送数据的缓冲区大小,默认是8KB. 如果底层 Socket 不支持 SO_SNDBUF 选项,setSendBufferSize() 方法会抛出 SocketException.
		clientConfig.setSendBufferSize(8 * 1024);
		// FTPClient发送数据的缓冲区大小,默认是8KB.
		clientConfig.setSendDataSocketBufferSize(8 * 1024);
		clientConfig.setServerLanguageCode(Locale.CHINESE.toString());
		// SocketClient打开ServerSocket的连接ServerSocketFactory工厂
		// FTPSServerSocketFactory.class
		clientConfig.setServerSocketFactory(ServerSocketFactory.getDefault());
		// 指定FTP服务器所在时区, 例如 America / Chicago 或 Asia / Rangoon
		//clientConfig.setServerTimeZoneId(serverTimeZoneId);
		// ftp服务器显示风格 一般为unix 或者nt
		clientConfig.setServerType(FTPServerTypeEnum.UNIX);
		// 设置月份短名称 ： Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec
		clientConfig.setShortMonthNames("Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec");
		/*
		 * Socket调用InputStream 读数据的等待超时时间，以毫秒为单位, 默认5秒，值为 0表示会无限等待, 永远不会超时.
		 * 如果超过这个时候，会抛出java.net.SocketTimeoutException。
		 * 当输入流的read方法被阻塞时，如果设置timeout（timeout的单位是毫秒），那么系统在等待了timeout毫秒后会抛出一个InterruptedIOException例外。
		 * 在抛出例外后，输入流并未关闭，你可以继续通过read方法读取数据。
		 * 当底层的Socket实现不支持SO_TIMEOUT选项时，这两个方法将抛出SocketException例外。
		 * 不能将timeout设为负数，否则setSoTimeout方法将抛出IllegalArgumentException例外
		*/
		clientConfig.setSo_timeout(10 * 1000);
		// Socket创建工厂
		clientConfig.setSocketFactory(DefaultSocketFactory.getDefault());
		// Socket代理对象
		clientConfig.setSocketProxy(Proxy.NO_PROXY);
		// Socket关闭后，SO_LINGER 延迟关闭时间;单位毫秒，默认0
		clientConfig.setSolinger_timeout(0);
		// 启用/禁用SO_LINGER延迟关闭
		clientConfig.setSolingerEnabled(true);
		// 是否严格多行解析；默认false
		clientConfig.setStrictMultilineParsing(false);
		// FTPClient是否使用NoDelay策略;默认 true;nagle算法默认是打开的，会引起delay的问题；所以要手工关掉
		clientConfig.setTcpNoDelay(false);
		// 是否运行通过list方式查询文件对象解析失败时，创建基本FTPFile条目。
		clientConfig.setUnparseableEntries(false);
		/*
		 * 设置是否使用与IPv4 EPSV。 也许值得在某些情况下启用。 例如，当使用IPv4和NAT它可能与某些罕见的配置。 
		 * 例如，如果FTP服务器有一个静态的使用PASV地址（外部网）和客户端是来自另一个内部网络。 在这种情况下，PASV命令后，数据连接会失败，而EPSV将使客户获得成功，采取公正的端口。 
		 */
		clientConfig.setUseEPSVwithIPv4(false);
		
		/*
		#===============================================================================
		#=============FTPClient参数配置====================================================
		#===============================================================================
	  

		#===============================================================================
		#=============FTPSClient参数配置===================================================
		#===============================================================================

		#安全模式. （True - 隐式模式/False - 显性模式）
		ftpsclient.isImplicit = false
		#安全的Socket使用的协议，使用SSL或TLS;默认 TLS
		ftpsclient.protocol = TLS
		#AUTH命令使用的值;默认TLS
		ftpsclient.auth = TLS
		#当前Socket是否可以创建一个新的SSL会话；默认true
		ftpsclient.enabledSessionCreation = true
		#是否使用客户端模式；默认true
		ftpsclient.useClientMode = true
		#是否需要客户端身份验证；默认false
		ftpsclient.needClientAuth = false
		#是否希望客户端身份验证；默认false
		ftpsclient.wantClientAuth = false
		#当前连接使用的特定密码组，多个使用 ",; \t\n"分割；服务器协商之前调用 ;如 123,124
		ftpsclient.enabledCipherSuites = 
		#当前连接使用的特定协议组，多个使用 ",; \t\n"分割；服务器协商之前调用;如TLS,SSL
		ftpsclient.enabledProtocols = 
		#是否使用HTTPS终端自动检查算法。默认false。仅在客户端模式的连接进行此项检查（需Java1.7+）
		ftpsclient.tlsEndpointChecking = false

		#===============================================================================
		#=============FTPHTTPClient参数配置================================================
		#===============================================================================

		#HTTP代理主机IP地址
		ftpclient.httpProxyHost =
		#HTTP代理主机端口  
		ftpclient.httpProxyPort = 
		#HTTP代理主机账户名
		ftpclient.httpProxyUsername =
		#HTTP代理主机密码  
		ftpclient.httpProxyPassword = */
		
		
		
		builder = new FTPClientBuilder(clientConfig);
		
		FTPPooledClientFactory factory = new FTPPooledClientFactory(builder);
		
		
		/*
		 * #===============================================================================
		 * #=============FTPClient对象池配置==================================================
		 * #===============================================================================
		 * maxActive" -> "maxTotal" and "maxWait" -> "maxWaitMillis
		 */
		
		FTPClientPoolConfig poolConfig = new FTPClientPoolConfig();
				 
		// 在对象池耗尽时是否阻塞，默认true。false的话超时就没有作用了
		poolConfig.setBlockWhenExhausted(true);
		//设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
		poolConfig.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
		// 驱逐线程关闭的超时时间，默认10秒。
		poolConfig.setEvictorShutdownTimeoutMillis(10000);
		// 是否使用公平锁，默认false(公平锁是线程安全中的概念，true的含义是谁先等待获取锁，谁先在锁释放的时候获取锁，如非必要，一般不使用公平锁，会影响性能)
		poolConfig.setFairness(false);
		// 是否启用pool的jmx管理功能, 默认true
		poolConfig.setJmxEnabled(true);
		// jmx默认的base name，默认为null，意味着池提供一个名称
		poolConfig.setJmxNameBase(null);
		// jmx默认的前缀名，默认为pool
		// MBean ObjectName = new ObjectName("org.apache.commons.pool2:type=GenericObjectPool,name=" + "pool" + i)
		poolConfig.setJmxNamePrefix("ftpclient.pool");
		//borrowObject返回对象时，是采用DEFAULT_LIFO（last in first out，即类似cache的最频繁使用队列），如果为False，则表示FIFO队列；是否启用后进先出, 默认true
		poolConfig.setLifo(true);
		//最大能够保持idel状态的对象数；控制一个pool最多有多少个状态为idle的ftpclient实例；
		poolConfig.setMaxIdle(8);
		//控制一个pool可分配多少个ftpclient实例，通过pool.getResource()来获取；  
		//如果赋值为-1，则表示不限制；如果pool已经分配了maxTotal个ftpclient实例，则此时pool的状态为exhausted(耗尽)。
		poolConfig.setMaxTotal(500);
		//当池内没有返回对象时，最大等待时间获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
		//表示当borrow一个ftpclient实例时，最大的等待时间，如果超过等待时间，则直接抛出ftpclientConnectionException；
		poolConfig.setMaxWaitMillis(-1);
		//逐出连接的最小空闲时间, 默认1800000毫秒(30分钟)，达到此值后空闲连接将可能会被移除。负值(-1)表示不移除。
		//表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义；
		poolConfig.setMinEvictableIdleTimeMillis(-1);
		//连接池中最少空闲的连接数,默认为0.
		poolConfig.setMinIdle(0);
		//每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3 . 表示idle object evitor每次扫描的最多的对象数；
		poolConfig.setNumTestsPerEvictionRun(3);
		//连接空闲的最小时间，达到此值后空闲链接将会被移除，且保留“minIdle”个空闲连接数。默认为-1.
		//对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
		//在minEvictableIdleTimeMillis基础上，加入了至少 minIdle个对象已经在pool里面了。
		//如果为-1，evicted不会根据idle time驱逐任何对象。
		//如果minEvictableIdleTimeMillis>0，则此项设置无意义，且只有在 timeBetweenEvictionRunsMillis大于0时才有意义；
		poolConfig.setSoftMinEvictableIdleTimeMillis(-1);
		//向调用者输出“链接”资源时，是否检测是有有效，如果无效则从连接池中移除，并尝试获取继续获取。默认为false。建议保持默认值.
		//在borrow一个ftpclient实例时，是否提前进行alidate操作；如果为true，则得到的ftpclient实例均是可用的；
		poolConfig.setTestOnBorrow(true);
		//向连接池“获取”链接时，是否检测“链接”对象的有效性。默认为false。建议保持默认值.
		poolConfig.setTestOnCreate(true);
		//向连接池“归还”链接时，是否检测“链接”对象的有效性。默认为false。建议保持默认值.
		poolConfig.setTestOnReturn(true);
		//向调用者输出“链接”对象时，是否检测它的空闲超时；默认为false。如果“链接”空闲超时，将会被移除。建议保持默认值.
		//如果为true，表示有一个idle object evitor线程对idle object进行扫描，如果validate失败，此object会被从pool中drop掉；这一项只有在 timeBetweenEvictionRunsMillis大于0时才有意义；
		poolConfig.setTestWhileIdle(true);
		//“空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。默认为-1. 表示idle object evitor两次扫描之间要sleep的毫秒数；
		poolConfig.setTimeBetweenEvictionRunsMillis(-1);
		
		FTPClientPool clientPool = new FTPClientPool(factory, poolConfig);
		
 		ftpClient = new FTPPooledResourceClient(clientPool, clientConfig);
	}
	 
 }
 