package com.github.vindell.ftpclient;

import com.github.vindell.ftpclient.utils.FTPClientUtils;
import com.github.vindell.ftpclient.utils.StringUtils;

/**
 * FTPHTTPClient对象构建器
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPHTTPClientBuilder extends FTPClientBuilder {
	
	private FTPHTTPClientConfig clientConfig;
	
	public FTPHTTPClientBuilder(FTPHTTPClientConfig clientConfig) {
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
	public FTPHTTPClientConfig getClientConfig() {
		return clientConfig;
	}
	
}
