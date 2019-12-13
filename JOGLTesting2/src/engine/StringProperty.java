package engine;

public class StringProperty extends Property {
	public String value;
	
	public StringProperty(String name, String initialValue) {
		this.name = name;
		this.value = initialValue;
	}
	
	@Override
	public void updatePropertyValue(String newVal) {
		value = newVal;
	}
	
	@Override
	public String getPropertyText() {
		return value;
	}
}
