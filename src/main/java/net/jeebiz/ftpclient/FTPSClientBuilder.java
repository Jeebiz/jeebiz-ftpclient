package net.jeebiz.ftpclient;

import net.jeebiz.ftpclient.utils.FTPClientUtils;

/**
 * FTPClient对象构建器
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPSClientBuilder extends FTPClientBuilder {
	
	private FTPSClientConfig clientConfig;
	
	public FTPSClientBuilder(FTPSClientConfig clientConfig) {
		super(clientConfig);
		this.clientConfig = clientConfig;
	}

	@Override
	public FTPSClient build() {
		//加密的FTPSClient
		FTPSClient ftpsClient = new FTPSClient(clientConfig.isImplicit());
		//初始化FTPSClient
		return FTPClientUtils.initFTPSClient(FTPClientUtils.initFTPClient(ftpsClient, clientConfig), clientConfig);
	}

	@Override
	public FTPSClientConfig getClientConfig() {
		return clientConfig;
	}
	
}
