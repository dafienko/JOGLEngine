package engine;

import java.util.ArrayList;

public class Container {
	public ArrayList<Instance> children;
	public String containerName;
	
	public Container() {
		children = new ArrayList<Instance>();
		containerName = "Container";
	}
	
	public void setChild(Instance child) {
		children.add(child);
	}
	
	public void removeChild(Instance child) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i) == child) {
				children.remove(i);
				break;
			}
		}
	}
}
