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
package com.github.vindell.ftpclient.enums;

import org.apache.commons.net.ftp.FTP;

/**
 * 文件结构：file,record,page
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
public enum FileStructureEnum {

	/***
	 * A constant used to indicate a file is to be treated as a continuous sequence
	 * of bytes. This is the default structure. All constants ending in
	 * <code>_STRUCTURE</code> are used to indicate file structure for file
	 * transfers.
	 ***/
	FILE(FTP.FILE_STRUCTURE, "文件"),
	/***
	 * A constant used to indicate a file is to be treated as a sequence of records.
	 * All constants ending in <code>_STRUCTURE</code> are used to indicate file
	 * structure for file transfers.
	 ***/
	RECORD(FTP.RECORD_STRUCTURE, "记录"),
	/***
	 * A constant used to indicate a file is to be treated as a set of independent
	 * indexed pages. All constants ending in <code>_STRUCTURE</code> are used to
	 * indicate file structure for file transfers.
	 ***/
	PAGE(FTP.PAGE_STRUCTURE, "文件索引页");

	private int type;

	private String desc;

	private FileStructureEnum(int type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public int getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}

}
