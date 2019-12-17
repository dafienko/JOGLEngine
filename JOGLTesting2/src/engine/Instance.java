package engine;

public abstract class Instance extends Container {
	public Container parent;
	
	public void setParent(Container parent) {
		if( this.parent != null) {
			this.parent.removeChild(this);
		}
		if (parent != null) {
			parent.setChild(this);
		}
		this.parent = parent;
	}
}
