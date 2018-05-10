/**
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.jeebiz.ftpclient.enums;

/**
 * ftp客户端对象类型：FTPClient,FTPSClient,FTPHTTPClient;默认 FTPClient
 * 
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
public enum FTPClientTypeEnum {

	/***
	 * FTPClient encapsulates all the functionality necessary to store and retrieve
	 * files from an FTP server.
	 ***/
	FTP_CLIENT("FTPClient"),
	/***
	 * FTP over SSL processing. If desired, the JVM property -Djavax.net.debug=all
	 * can be used to see wire-level SSL details. Warning: the hostname is not
	 * verified against the certificate by default, use
	 * setHostnameVerifier(HostnameVerifier) or setEndpointCheckingEnabled(boolean)
	 * (on Java 1.7+) to enable verification. Verification is only performed on
	 * client mode connections.
	 ***/
	FTPS_CLIENT("FTPSClient"),
	/***
	 * Experimental attempt at FTP client that tunnels over an HTTP proxy
	 * connection.
	 ***/
	FTP_HTTP_CLIENT("FTPHTTPClient");

	private String type;

	private FTPClientTypeEnum(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public boolean equals(FTPClientTypeEnum type) {
		return this.compareTo(type) == 0;
	}

}
