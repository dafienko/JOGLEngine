package engine;

public abstract class Instance {
	public Container parent;
	
	public void setParent(Container parent) {
		this.parent.removeChild(this);
		parent.setChild(this);
		this.parent = parent;
	}
}
