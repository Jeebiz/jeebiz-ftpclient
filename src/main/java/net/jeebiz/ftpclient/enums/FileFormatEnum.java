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
 * 文件格式：telnet,carriage_control,non_print
 * 
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
public enum FileFormatEnum {

	/***
	 * A constant used for text files to indicate a non-print text format. This is
	 * the default format. All constants ending in <code>TEXT_FORMAT</code> are used
	 * to indicate text formatting for text transfers (both ASCII and EBCDIC).
	 ***/
	NON_PRINT_TEXT(FTP.NON_PRINT_TEXT_FORMAT, ""),
	/***
	 * A constant used to indicate a text file contains format vertical format
	 * control characters. All constants ending in <code>TEXT_FORMAT</code> are used
	 * to indicate text formatting for text transfers (both ASCII and EBCDIC).
	 ***/
	TELNET_TEXT(FTP.TELNET_TEXT_FORMAT, ""),

	/***
	 * A constant used to indicate a text file contains ASA vertical format control
	 * characters. All constants ending in <code>TEXT_FORMAT</code> are used to
	 * indicate text formatting for text transfers (both ASCII and EBCDIC).
	 ***/
	CARRIAGE_CONTROL_TEXT(FTP.CARRIAGE_CONTROL_TEXT_FORMAT, "");

	private int format;

	private String desc;

	private FileFormatEnum(int format, String desc) {
		this.format = format;
		this.desc = desc;
	}

	public int getFormat() {
		return format;
	}

	public String getDesc() {
		return desc;
	}
}
