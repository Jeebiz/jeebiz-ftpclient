package com.github.vindell.ftpclient;

import org.apache.commons.lang3.builder.Builder;

import com.github.vindell.ftpclient.utils.FTPClientUtils;

/**
 * FTPClient对象构建器
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPClientBuilder implements Builder<FTPClient> {
	
	private FTPClientConfig clientConfig;
	
	public FTPClientBuilder(FTPClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	@Override
	public FTPClient build() {
		//普通的FTPClient
		FTPClient ftpClient = new FTPClient();
		//初始化FTPClient
		return FTPClientUtils.initFTPClient(ftpClient, clientConfig);
	}

	public FTPClientConfig getClientConfig() {
		return clientConfig;
	}
	
}
