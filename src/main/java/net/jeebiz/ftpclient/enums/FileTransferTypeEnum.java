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

import org.apache.commons.net.ftp.FTP;

/**
 * 文件传输类型：ascii,ebcdic,binary,local;默认 ascii
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
public enum FileTransferTypeEnum {

	/***
	 * A constant used to indicate the file(s) being transferred should be treated
	 * as ASCII. This is the default file type. All constants ending in
	 * <code>FILE_TYPE</code> are used to indicate file types.
	 ***/
	ASCII(FTP.ASCII_FILE_TYPE, "ASCII类型"),
	/***
	 * A constant used to indicate the file(s) being transferred should be treated
	 * as EBCDIC. Note however that there are several different EBCDIC formats. All
	 * constants ending in <code>FILE_TYPE</code> are used to indicate file types.
	 ***/
	EBCDIC(FTP.EBCDIC_FILE_TYPE, "EBCDIC类型"),
	/***
	 * A constant used to indicate the file(s) being transferred should be treated
	 * as a binary image, i.e., no translations should be performed. All constants
	 * ending in <code>FILE_TYPE</code> are used to indicate file types.
	 ***/
	BINARY(FTP.BINARY_FILE_TYPE, "二进制类型"),

	/***
	 * A constant used to indicate the file(s) being transferred should be treated
	 * as a local type. All constants ending in <code>FILE_TYPE</code> are used to
	 * indicate file types.
	 ***/
	LOCAL(FTP.LOCAL_FILE_TYPE, "本地类型");

	private int type;

	private String desc;

	private FileTransferTypeEnum(int type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public int getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}

};