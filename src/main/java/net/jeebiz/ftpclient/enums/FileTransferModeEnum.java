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
 * 文件传输模式 ：stream,block,compressed
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 */
public enum FileTransferModeEnum {

	/***
	 * A constant used to indicate a file is to be transferred as a stream of bytes.
	 * This is the default transfer mode. All constants ending in
	 * <code>TRANSFER_MODE</code> are used to indicate file transfer modes.
	 ***/
	STREAM(FTP.STREAM_TRANSFER_MODE, "字节流"),
	/***
	 * A constant used to indicate a file is to be transferred as a series of
	 * blocks. All constants ending in <code>TRANSFER_MODE</code> are used to
	 * indicate file transfer modes.
	 ***/
	BLOCK(FTP.BLOCK_TRANSFER_MODE, "序列块"),

	/***
	 * A constant used to indicate a file is to be transferred as FTP compressed
	 * data. All constants ending in <code>TRANSFER_MODE</code> are used to indicate
	 * file transfer modes.
	 ***/
	COMPRESSED(FTP.COMPRESSED_TRANSFER_MODE, "压缩数据");

	private int type;

	private String desc;

	private FileTransferModeEnum(int type, String desc) {
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
