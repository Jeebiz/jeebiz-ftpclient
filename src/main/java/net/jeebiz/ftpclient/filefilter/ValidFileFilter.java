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
public class ValidFileFilter implements FTPFileFilter , Serializable {

    /** Singleton instance of file filter */
    public static final FTPFileFilter VALID = new ValidFileFilter();

    /**
     * Restrictive consructor.
     */
    protected ValidFileFilter() {
    }

    /**
     * Checks to see if the file is a file.
     * @param file  the File to check
     * @return true if the file is a file
     */
    @Override
    public boolean accept(final FTPFile file) {
        return file.isValid();
    }
    
}