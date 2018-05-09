package com.github.vindell.ftpclient;

import org.junit.Before;

import com.github.vindell.ftpclient.client.FTPResourceClient;

public class FTPResourceClientTest extends FTPClientTest {
	
	@Before
	public void setUp() {
		builder = new FTPClientBuilder(null);
 		ftpClient = new FTPResourceClient(builder);
	}
 	
 	
 }
 