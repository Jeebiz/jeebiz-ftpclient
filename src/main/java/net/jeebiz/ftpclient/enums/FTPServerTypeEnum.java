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

import net.jeebiz.ftpclient.FTPClientConfig;

/**
 * 服务端系统类型：unix,unix-trim,vms,nt,os2,os400,as400,mvs,l8,netware,macos;默认 unix
 * 
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
public enum FTPServerTypeEnum {

	/**
	 * Identifier by which a unix-based ftp server is known throughout the
	 * commons-net ftp system.
	 */
	UNIX(FTPClientConfig.SYST_UNIX, "unix"),
	/**
	 * Identifier for alternate UNIX parser; same as {@link #SYST_UNIX} but leading
	 * spaces are trimmed from file names. This is to maintain backwards
	 * compatibility with the original behaviour of the parser which ignored
	 * multiple spaces between the date and the start of the file name.
	 */
	UNIX_TRIM_LEADING(FTPClientConfig.SYST_UNIX_TRIM_LEADING, "unix-trim"),
	/**
	 * Identifier by which a vms-based ftp server is known throughout the
	 * commons-net ftp system.
	 */
	VMS(FTPClientConfig.SYST_VMS, "vms"),
	/**
	 * Identifier by which a WindowsNT-based ftp server is known throughout the
	 * commons-net ftp system.
	 */
	NT(FTPClientConfig.SYST_NT, "WindowsNT"),
	/**
	 * Identifier by which an OS/2-based ftp server is known throughout the
	 * commons-net ftp system.
	 */
	OS2(FTPClientConfig.SYST_OS2, "OS/2"),
	/**
	 * Identifier by which an OS/400-based ftp server is known throughout the
	 * commons-net ftp system.
	 */
	OS400(FTPClientConfig.SYST_OS400, "OS/400"),
	/**
	 * Identifier by which an AS/400-based ftp server is known throughout the
	 * commons-net ftp system.
	 */
	AS400(FTPClientConfig.SYST_AS400, "AS/400"),
	/**
	 * Identifier by which an MVS-based ftp server is known throughout the
	 * commons-net ftp system.
	 */
	MVS(FTPClientConfig.SYST_MVS, "MVS"),
	/**
	 * Some servers return an "UNKNOWN Type: L8" message in response to the SYST
	 * command. We set these to be a Unix-type system. This may happen if the ftpd
	 * in question was compiled without system information.
	 *
	 * NET-230 - Updated to be UPPERCASE so that the check done in
	 * createFileEntryParser will succeed.
	 */
	L8(FTPClientConfig.SYST_L8, "L8"),
	/**
	 * Identifier by which an Netware-based ftp server is known throughout the
	 * commons-net ftp system.
	 */
	NETWARE(FTPClientConfig.SYST_NETWARE, "Netware"),
	/**
	 * Identifier by which a Mac pre OS-X -based ftp server is known throughout the
	 * commons-net ftp system.
	 */
	MACOS_PETER(FTPClientConfig.SYST_MACOS_PETER, "OS-X");

	private String type;

	private String desc;

	private FTPServerTypeEnum(String type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}
	
	public boolean equals(FTPServerTypeEnum type){
		return this.compareTo(type) == 0;
	}

};