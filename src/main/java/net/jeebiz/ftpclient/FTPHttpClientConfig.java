package net.jeebiz.ftpclient;

/**
 * FTPHttpClient客户端的配置
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPHttpClientConfig extends FTPClientConfig {

	//===============================================================================
	//=============FTPHTTPClient参数配置================================================
	//===============================================================================

	/** HTTP代理主机IP地址 */
	protected String httpProxyHost;
	/** HTTP代理主机端口 */
	protected int httpProxyPort;
	/** HTTP代理主机账户名 */
	protected String httpProxyUsername;
	/** HTTP代理主机密码 */
	protected String httpProxyPassword;
	 
	public String getHttpProxyHost() {
		return httpProxyHost;
	}

	public void setHttpProxyHost(String httpProxyHost) {
		this.httpProxyHost = httpProxyHost;
	}

	public int getHttpProxyPort() {
		return httpProxyPort;
	}

	public void setHttpProxyPort(int httpProxyPort) {
		this.httpProxyPort = httpProxyPort;
	}

	public String getHttpProxyUsername() {
		return httpProxyUsername;
	}

	public void setHttpProxyUsername(String httpProxyUsername) {
		this.httpProxyUsername = httpProxyUsername;
	}

	public String getHttpProxyPassword() {
		return httpProxyPassword;
	}

	public void setHttpProxyPassword(String httpProxyPassword) {
		this.httpProxyPassword = httpProxyPassword;
	}
 
}
