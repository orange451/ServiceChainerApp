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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "ManhXML")
public class Document implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String entity = "entity";

    @JsonProperty(value = "Messages")
    protected Messages messages;

    private Map<String, Object> map = new HashMap<>();

    public Document() {
    	//
    }

    public Document(Map<String, Object> map) {
        this.map = map;
    }

    public Document(String entity, Map<String, Object> map) {
        this.entity = entity;
        this.map = map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    @JsonAnySetter
    public void put(String key, Object value) {
        map.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getMap() {
        return map;
    }

    public void putAll(Map<String, Object> map) {
        this.map.putAll(map);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public Object remove(String key) {
        return map.remove(key);
    }

    public void clear() {
        this.map.clear();
    }

    @JsonIgnore
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(17, 31, this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
