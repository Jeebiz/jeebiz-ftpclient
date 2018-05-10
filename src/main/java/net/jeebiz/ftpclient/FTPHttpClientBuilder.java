package net.jeebiz.ftpclient;

import net.jeebiz.ftpclient.utils.FTPClientUtils;
import net.jeebiz.ftpclient.utils.StringUtils;

/**
 * FTPHTTPClient对象构建器
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPHttpClientBuilder extends FTPClientBuilder {
	
	private FTPHttpClientConfig clientConfig;
	
	public FTPHttpClientBuilder(FTPHttpClientConfig clientConfig) {
		super(clientConfig);
		this.clientConfig = clientConfig;
	}

	@Override
	public FTPHTTPClient build() {
		//HTTP代理的FTPHTTPClient
		FTPHTTPClient ftpHttpClient = null;
		if(StringUtils.isEmpty(clientConfig.getHttpProxyUsername()) || StringUtils.isEmpty(clientConfig.getHttpProxyPassword())){
			ftpHttpClient = new FTPHTTPClient(clientConfig.getHttpProxyHost(),clientConfig.getHttpProxyPort());
		}else{
			ftpHttpClient = new FTPHTTPClient(clientConfig.getHttpProxyHost(),clientConfig.getHttpProxyPort(),clientConfig.getHttpProxyUsername(),clientConfig.getHttpProxyPassword());
		}
		//初始化FTPHTTPClient
		return FTPClientUtils.initFTPClient(ftpHttpClient, clientConfig);
	}

	@Override
	public FTPHttpClientConfig getClientConfig() {
		return clientConfig;
	}
	
}
