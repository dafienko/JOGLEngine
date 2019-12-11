package engine;

import org.joml.*;

public class Camera {
	public Vector3f position;
	public Vector3f rotation;
	
	public Matrix4f returnMatrix; //initialized before hand to speed up runtime
	
	public float FOV;
	
	public Camera(Vector3f pos) {
		position = pos;
		rotation = new Vector3f();
		returnMatrix = new Matrix4f();
		FOV = 105;
	}
	
	public Camera() {
		this(new Vector3f(0, 0, 0));
	}
	
	public Matrix4f getMatrix() {
		returnMatrix.identity();
		returnMatrix.rotateXYZ(-rotation.x, -rotation.y, -rotation.z);
		returnMatrix.translate(-position.x, -position.y, -position.z);
		//returnMatrix.setRotationXYZ(rotation.x, -rotation.y, rotation.z);
		return returnMatrix;
	}
}
