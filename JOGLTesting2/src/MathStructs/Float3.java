package MathStructs;

public class Float3 {
	public int x, y, z;
	
	public Float3() {
		x = 0; 
		y = 0; 
		z = 0;
	}
	
	public Float3(int X, int Y, int Z) {
		x = X;
		y = Y;
		z = Z;
	}
	
	public Float3 add(Float3 r) {
		x += r.x;
		y += r.y;
		z += r.z;
		return this;
	}
	
	public Float3 sub(Float3 r) {
		x -= r.x;
		y -= r.y;
		z -= r.z;
		return this;
	}
	
	public Float3 mul(float r) {
		x *= r;
		y *= r;
		z *= r;
		return this;
	}
	
	public Float3 div(float r) {
		x /= r;
		y /= r;
		z /= r;
		return this;
	}
}
