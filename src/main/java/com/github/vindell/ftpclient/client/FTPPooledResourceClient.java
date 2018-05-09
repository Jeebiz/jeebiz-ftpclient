package com.github.vindell.ftpclient.client;


import com.github.vindell.ftpclient.FTPClient;
import com.github.vindell.ftpclient.FTPClientConfig;
import com.github.vindell.ftpclient.pool.FTPClientPool;
 
/**
 * 基于 Apache Pool2的FTPClient资源服务客户端实现
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public class FTPPooledResourceClient extends FTPResourceClient{
	
	private FTPClientPool clientPool = null;
	private FTPClientConfig clientConfig = null;
	
	public FTPPooledResourceClient(FTPClientPool clientPool, FTPClientConfig clientConfig){
		 this.clientPool = clientPool;
		 this.clientConfig = clientConfig;
	} 

	@Override
	public FTPClient getFTPClient() throws Exception {
		//从对象池获取FTPClient对象
		return clientPool.borrowObject();
	}
 
	@Override
	public void releaseClient(FTPClient ftpClient) throws Exception{
		try {
			//释放FTPClient到对象池
			if(ftpClient !=null){
				clientPool.returnObject(ftpClient);
			}
		} catch (Throwable e) {
			 
		}
	}
	
	public FTPClientConfig getClientConfig() {
		return clientConfig;
	}
	
}
