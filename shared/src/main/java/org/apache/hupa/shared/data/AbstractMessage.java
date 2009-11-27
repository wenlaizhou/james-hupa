/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.hupa.shared.data;

import java.io.Serializable;
import java.util.List;

public class AbstractMessage implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 5208272852772006815L;
    private String from;
    private String subject;
    private List<String> to;
    private List<String> cc;
    private boolean hasAttachment;
    
    public String toString() {
        String toList = "";
        if (to != null)
            for (String s: to)
                toList += s + " ";

        String ccList = "";
        if (cc != null)
            for (String s: cc)
                ccList += s + " ";

        return "From='" + from 
             + "' To='" + toList
             + "' CC='" + ccList
             + "' Subject='" + subject
             + "' Attachments=" + hasAttachment;
    }

    
    public boolean hasAttachment() {
        return hasAttachment;
    }
    
    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachment = hasAttachments;
    }
    
    /**
     * Set the From: header field
     * 
     * @param from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Return the From: header field
     * 
     * @return from
     */
    public String getFrom() {
        return from;
    }


    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> getCc() {
        return cc;
    }

    /**
     * Set the Subject: header field
     * 
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Return the Subject: header field
     * 
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    public  List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }
    


}
