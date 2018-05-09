/*
 * Copyright (c) 2010-2020, wandalong (hnxyhcwdl1003@163.com).
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
package com.github.vindell.ftpclient.filefilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

public class AndFileFilter implements FTPFileFilter , ConditionalFileFilter, Serializable {
	
	private static final long serialVersionUID = 7215974688563965257L;
	
	/** The list of file filters. */
	private final List<FTPFileFilter> fileFilters;
	
	/**
	* Constructs a new instance of <code>AndFileFilter</code>.
	*
	* @since 1.1
	*/
	public AndFileFilter() {
		this.fileFilters = new ArrayList<FTPFileFilter>();
	}
	
	/**
	* Constructs a new instance of <code>AndFileFilter</code>
	* with the specified list of filters.
	*
	* @param fileFilters  a List of FTPFileFilter instances, copied, null ignored
	* @since 1.1
	*/
	public AndFileFilter(final List<FTPFileFilter> fileFilters) {
		if (fileFilters == null) {
		    this.fileFilters = new ArrayList<FTPFileFilter>();
		} else {
		    this.fileFilters = new ArrayList<FTPFileFilter>(fileFilters);
		}
	}
	
	/**
	* Constructs a new file filter that ANDs the result of two other filters.
	*
	* @param filter1  the first filter, must not be null
	* @param filter2  the second filter, must not be null
	* @throws IllegalArgumentException if either filter is null
	*/
	public AndFileFilter(final FTPFileFilter filter1, final FTPFileFilter filter2) {
		if (filter1 == null || filter2 == null) {
		    throw new IllegalArgumentException("The filters must not be null");
		}
		this.fileFilters = new ArrayList<FTPFileFilter>(2);
		addFileFilter(filter1);
		addFileFilter(filter2);
	}
	
	/**
	* {@inheritDoc}
	*/
	public void addFileFilter(final FTPFileFilter FTPFileFilter) {
		this.fileFilters.add(FTPFileFilter);
	}
	
	/**
	* {@inheritDoc}
	*/
	public List<FTPFileFilter> getFileFilters() {
		return Collections.unmodifiableList(this.fileFilters);
	}
	
	/**
	* {@inheritDoc}
	*/
	public boolean removeFileFilter(final FTPFileFilter FTPFileFilter) {
		return this.fileFilters.remove(FTPFileFilter);
	}
	
	/**
	* {@inheritDoc}
	*/
	public void setFileFilters(final List<FTPFileFilter> fileFilters) {
		this.fileFilters.clear();
		this.fileFilters.addAll(fileFilters);
	}
	
   /**
	* {@inheritDoc}
	*/
	@Override
	public boolean accept(final FTPFile file) {
		if (this.fileFilters.isEmpty()) {
		    return false;
		}
		for (final FTPFileFilter fileFilter : fileFilters) {
		    if (!fileFilter.accept(file)) {
		        return false;
		    }
		}
		return true;
	}
	
	/**
	* Provide a String representaion of this file filter.
	* @return a String representaion
	*/
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(super.toString());
		buffer.append("(");
		if (fileFilters != null) {
		    for (int i = 0; i < fileFilters.size(); i++) {
		        if (i > 0) {
		            buffer.append(",");
		        }
		        final Object filter = fileFilters.get(i);
		        buffer.append(filter == null ? "null" : filter.toString());
		    }
		}
		buffer.append(")");
		return buffer.toString();
	}

}
