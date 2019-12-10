package engine;

import java.util.ArrayList;

public interface Container {
	public ArrayList<Instance> children = new ArrayList<Instance>();
	
	public void setChild(Instance instance);
	
	public void removeChild(Instance instance);
}
