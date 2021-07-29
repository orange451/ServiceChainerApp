package dev.anarchy.ui.codemirror;

public enum CodeSyntax {
	JAVA("text/x-java"),
	VELOCITY("text/velocity"),
	;
	
	private String type;
	
	private CodeSyntax(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
}
