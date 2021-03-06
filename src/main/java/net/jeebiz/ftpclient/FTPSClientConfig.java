package net.jeebiz.ftpclient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.net.util.TrustManagerUtils;

/**
 * FTPS客户端的配置
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPSClientConfig extends FTPClientConfig {

	//===============================================================================
	//=============FTPSClient对象池配置==================================================
	//===============================================================================

	/** 安全模式. （True - 隐式模式/False - 显性模式） */
	protected boolean isImplicit = false;
	/** 安全的Socket使用的协议，使用SSL或TLS;默认 TLS. */
	protected String protocol = "TLS";
	/** AUTH命令使用的值;默认TLS */
	protected String auth = "TLS";
	/** SSLContext对象. */
	protected SSLContext sslContext;
	/** 当前Socket是否可以创建一个新的SSL会话；默认true */
	protected boolean enabledSessionCreation = true;
	/** 是否使用客户端模式；默认true. */
	protected boolean useClientMode = true;
	/** 是否需要客户端身份验证；默认false. */
	protected boolean needClientAuth = false;
	/** 是否希望客户端身份验证；默认false. */
	protected boolean wantClientAuth = false;
	/** 当前连接使用的特定密码组，多个使用 ",; \t\n"分割；服务器协商之前调用 */
	protected String enabledCipherSuites = null;
	/** 当前连接使用的特定协议组，多个使用 ",; \t\n"分割；服务器协商之前调用 */
	protected String enabledProtocols = null;
	/** FTPS的TrustManager实现；默认使用 {@link TrustManagerUtils#getValidateServerCertificateTrustManager()}. */
	protected TrustManager trustManager = TrustManagerUtils.getValidateServerCertificateTrustManager();
	/** FTPS的KeyManager实现，默认null使用系统默认. */
	protected KeyManager keyManager = null;
	/** 在客户端模式(post-TLS)下的连接使用的域名校验对象，默认为null表示不校验  */
	protected HostnameVerifier hostnameVerifier = null;
	/** 是否使用HTTPS终端自动检查算法。默认false。仅在客户端模式的连接进行此项检查（需Java1.7+） */
	protected boolean tlsEndpointChecking = false;
	/** 加密Socket创建工厂 */
	protected SSLSocketFactory sslSocketFactory = null;

	public boolean isImplicit() {
		return isImplicit;
	}

	public void setImplicit(boolean isImplicit) {
		this.isImplicit = isImplicit;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public SSLContext getSslContext() {
		return sslContext;
	}

	public void setSslContext(SSLContext sslContext) {
		this.sslContext = sslContext;
	}

	public boolean isEnabledSessionCreation() {
		return enabledSessionCreation;
	}

	public void setEnabledSessionCreation(boolean enabledSessionCreation) {
		this.enabledSessionCreation = enabledSessionCreation;
	}

	public boolean isUseClientMode() {
		return useClientMode;
	}

	public void setUseClientMode(boolean useClientMode) {
		this.useClientMode = useClientMode;
	}

	public boolean isNeedClientAuth() {
		return needClientAuth;
	}

	public void setNeedClientAuth(boolean needClientAuth) {
		this.needClientAuth = needClientAuth;
	}

	public boolean isWantClientAuth() {
		return wantClientAuth;
	}

	public void setWantClientAuth(boolean wantClientAuth) {
		this.wantClientAuth = wantClientAuth;
	}

	public String getEnabledCipherSuites() {
		return enabledCipherSuites;
	}

	public void setEnabledCipherSuites(String enabledCipherSuites) {
		this.enabledCipherSuites = enabledCipherSuites;
	}

	public String getEnabledProtocols() {
		return enabledProtocols;
	}

	public void setEnabledProtocols(String enabledProtocols) {
		this.enabledProtocols = enabledProtocols;
	}

	public TrustManager getTrustManager() {
		return trustManager;
	}

	public void setTrustManager(TrustManager trustManager) {
		this.trustManager = trustManager;
	}

	public KeyManager getKeyManager() {
		return keyManager;
	}

	public void setKeyManager(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	public HostnameVerifier getHostnameVerifier() {
		return hostnameVerifier;
	}

	public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}

	public boolean isTlsEndpointChecking() {
		return tlsEndpointChecking;
	}

	public void setTlsEndpointChecking(boolean tlsEndpointChecking) {
		this.tlsEndpointChecking = tlsEndpointChecking;
	}

	public SSLSocketFactory getSslSocketFactory() {
		return sslSocketFactory;
	}

	public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
		this.sslSocketFactory = sslSocketFactory;
	}
 
}
