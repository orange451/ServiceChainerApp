package dev.anarchy.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use=Id.DEDUCTION)
@JsonSubTypes({@Type(DFolder.class), @Type(DServiceChain.class)})
public interface DFolderElement {
	public String getName();
}
