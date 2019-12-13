package engine;

public class FloatProperty extends Property {
	public float value;
	
	public FloatProperty(String name, float initialValue) {
		this.name = name;
		this.value = initialValue;
	}

	@Override
	public void updatePropertyValue(String newValString) {
		newValString = Util.removeEvery(newValString, new char[] {' ', '\n', ')', '('});
		
		if (!newValString.equals("")) {
			float newVal = value;

			try {
				float f = Float.valueOf(newValString).floatValue();
				newVal = f;
			} catch (Exception e) {};

			
			value = newVal;
		}
	}
	
	@Override
	public String getPropertyText() {
		return "" + value;
	}
}
