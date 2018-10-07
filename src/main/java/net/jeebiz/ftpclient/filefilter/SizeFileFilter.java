/*
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
package net.jeebiz.ftpclient.filefilter;

import java.io.Serializable;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

@SuppressWarnings("serial")
public class SizeFileFilter  implements FTPFileFilter,Serializable {
	
	 /** The size threshold. */
    private final long size;
    /** Whether the files accepted will be larger or smaller. */
    private final boolean acceptLarger;

    /**
     * Constructs a new size file filter for files equal to or 
     * larger than a certain size.
     *
     * @param size  the threshold size of the files
     * @throws IllegalArgumentException if the size is negative
     */
    public SizeFileFilter(final long size) {
        this(size, true);
    }

    /**
     * Constructs a new size file filter for files based on a certain size
     * threshold.
     *
     * @param size  the threshold size of the files
     * @param acceptLarger  if true, files equal to or larger are accepted,
     * otherwise smaller ones (but not equal to)
     * @throws IllegalArgumentException if the size is negative
     */
    public SizeFileFilter(final long size, final boolean acceptLarger) {
        if (size < 0) {
            throw new IllegalArgumentException("The size must be non-negative");
        }
        this.size = size;
        this.acceptLarger = acceptLarger;
    }

    @Override
    public boolean accept(final FTPFile file) {
        final boolean smaller = file.getSize() < size;
        return acceptLarger ? !smaller : smaller;
    }

    /**
     * Provide a String representaion of this file filter.
     *
     * @return a String representaion
     */
    @Override
    public String toString() {
        final String condition = acceptLarger ? ">=" : "<";
        return super.toString() + "(" + condition + size + ")";
    }
}
