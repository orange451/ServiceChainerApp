/*
 * Copyright &#169; 2015 Manhattan Associates, Inc.  All Rights Reserved.
 *
 * Confidential, Proprietary and Trade Secrets Notice
 *
 * Use of this software is governed by a license agreement. This software
 * contains confidential, proprietary and trade secret information of
 * Manhattan Associates, Inc. and is protected under United States and
 * international copyright and other intellectual property laws. Use, disclosure,
 * reproduction, modification, distribution, or storage in a retrieval system in
 * any form or by any means is prohibited without the prior express written
 * permission of Manhattan Associates, Inc.
 *
 * Manhattan Associates, Inc.
 * 2300 Windy Ridge Parkway, 10th Floor
 * Atlanta, GA 30339 USA
 */

package dev.anarchy.common.dto;

import java.util.ArrayList;
import java.util.List;

public enum MessageType {

    ERROR("error"), WARNING("warning"), INFO("info");

    private final String code;

    private MessageType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static List<String> getCodes() {
        List<String> result = new ArrayList<String>();
        for (MessageType messageType : values()) {
            result.add(messageType.getCode());
        }
        
        return result;
    }

}
