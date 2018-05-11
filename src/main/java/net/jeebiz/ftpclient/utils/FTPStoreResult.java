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
package net.jeebiz.ftpclient.utils;

import java.io.Serializable;

/**
 * FTP 上传调用结果
 * 
 * @author ： <a href="https://github.com/vindell">vindell</a>
 */
@SuppressWarnings("serial")
public class FTPStoreResult implements Serializable {

	private String originalName;
	private String renamedName;
	private long length;
	private boolean result;

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getRenamedName() {
		return renamedName;
	}

	public void setRenamedName(String renamedName) {
		this.renamedName = renamedName;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

}
