package dev.anarchy;

public interface DFolderElement {
	public void setParent(DFolder parent);
	public DFolder getParent();
	public String getName();
}
