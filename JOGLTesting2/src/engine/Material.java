package engine;

import java.lang.Math;

public class Material {
	public float[] ambient, diffuse, specular;
	public float shininess;
	
	public Material() {
		ambient = new float[4];
		diffuse = new float[4];
		specular = new float[4];
		shininess = 0.0f;
	}
	
	private void fillArrWithRandomElements(float[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = (float)Math.random();
		}
	}
	
	public void randomizeMaterial() {
		fillArrWithRandomElements(ambient);
		ambient[3] = 1.0f;
		fillArrWithRandomElements(diffuse);
		diffuse[3] = 1.0f;
		fillArrWithRandomElements(specular);
		specular[3] = 1.0f;
		shininess = .5f;
	}
}
