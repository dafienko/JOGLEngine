package engine;

import org.joml.*;

public class Vector3fProperty extends Property {
	public Vector3f value;
	
	public Vector3fProperty(String name, Vector3f initialValue) {
		this.name = name;
		this.value = initialValue;
	}
	
	@Override
	public void updatePropertyValue(String newVal) {
		newVal = Util.removeEvery(newVal, new char[] {' ', '\n', ')', '('});
		
		if (!newVal.equals("")) {
			String[] stringComponents = newVal.split(",");
			Vector3f result = new Vector3f(value);
			
			float[] components = new float[] {value.x, value.y, value.z};
			for (int i = 0; i < 3; i++) {
				String str = stringComponents[i];
				try {
					float f = Float.valueOf(str).floatValue();
					components[i] = f;
				} catch (Exception e) {};
			}
			
			value.set(components[0], components[1], components[2]);
		}
	}
	
	@Override
	public String getPropertyText() {
		return "(" + value.x + ", " + value.y + ", " + value.z + ")";
	}
}
