package engine;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.jogamp.opengl.GL4;

public class Game {

	private static VertexDataHolder brian;
	
	public static void setupVerts(VertexDataHolder mesh, int width, int length, float scale) {
		Vector3f[] points = new Vector3f[width * length];
		Vector3f[] normals = new Vector3f[width * length];
		Vector2f[] texCoords = new Vector2f[width * length];
		
		for (int x = 0; x < width; x++) { //calculate point positions
			for (int z = 0; z < length; z++) {
				points[x * length + z] = 
						new Vector3f((x * scale) - (width * scale / 2.0f), 
								(float)((Math.sin(x/5.0f) * Math.cos(z/5.0f) + 1) * 40), 
								(z * scale) - (length * scale / 2.0f));
				
				texCoords[x * length + z] = new Vector2f((float)x / (float)width, (float)z / (float)length);
			}
		}
		
		// calculate vertex normals for each point
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < length; z++) {
				Vector3f origin, top, right, diag;
				Vector3f normal = new Vector3f();
				if (x < width - 1 && z < length - 1) 
				{ // if this point isn't on the top or right side of the grid
					origin = points[x * length + z];
					top = points[x * length + z + 1];
					right = points[(x + 1) * length + z];
					diag = points[(x + 1) * length + z + 1];
				} 
				else if (x == width - 1 && z == length - 1) 
				{ // upper right corner
					origin = points[x * length + z];
					top = points[(x) * length + z - 1];
					right = points[(x - 1) * length + z];
					diag = points[(x - 1) * length + z - 1];
				} 
				else if (z == length - 1) 
				{ // touching top edge but not upper right corner
					origin = points[x * length + z];
					top = points[(x + 1) * length + z];
					right = points[(x + 1) * length + (z - 1)];
					diag = points[(x) * length + z - 1];
				}
				else 
				{ // x = width - 1 touching right side but not upper right corner
					origin = points[x * length + z];
					top = points[(x - 1) * length + z];
					right = points[(x) * length + z + 1];
					diag = points[(x - 1) * length + z + 1];
				}
				
				Vector3f rightVector = new Vector3f();
				right.sub(origin, rightVector);

				Vector3f upVector = new Vector3f();
				top.sub(origin, upVector);
				
				Vector3f diagVector = new Vector3f();
				diag.sub(origin, diagVector);
				
				Vector3f leftNormal = new Vector3f();
				Vector3f rightNormal = new Vector3f();
				
				rightVector.cross(diagVector, rightNormal);
				diagVector.cross(upVector, leftNormal);
				
				rightNormal.normalize();
				leftNormal.normalize();
				
				rightNormal.mul(-1);
				leftNormal.mul(-1);
				
				normal = new Vector3f(); // the average of the right and left normals
				rightNormal.add(leftNormal, normal);
				normal.div(2.0f); 
				
				normals[x * length + z] = normal;
			}
		}
		
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		for (int x = 0; x < width - 1; x++) { // generate indices
			for (int z = 0; z < length - 1; z++) {
				int originIndex = x * length + z;
				int rightIndex = (x + 1) * length + z;
				int topIndex = x * length + z + 1;
				int diagIndex = (x + 1) * length + z + 1;
				
				indices.add(originIndex);
				indices.add(diagIndex);
				indices.add(rightIndex);
				
				indices.add(originIndex);
				indices.add(topIndex);
				indices.add(diagIndex);
			}
		}
		
		ArrayList<Vector3f> colors = new ArrayList<Vector3f>();
		for (int i = 0; i < width * length; i++) {
			//colors.add(new Vector3f((float)Math.random(), (float)Math.random(), (float)Math.random()));
			colors.add(new Vector3f(1, 1, 1));
		}
		
		ArrayList<Vector2f> texCoordArrayList = new ArrayList<Vector2f>();
		for (Vector2f v : texCoords) {
			texCoordArrayList.add(v);
		}
		
		mesh.indices = indices;
		mesh.vertexColors = colors;
		mesh.vertexPositions = VertexDataHolder.arrayToArrayList(points);
		mesh.vertexNormals = VertexDataHolder.arrayToArrayList(normals);
		
		mesh.textureCoordinates = texCoordArrayList;
	}
	
	public static void init(GL4 gl, Window wnd) {
		int width = 100; 
		int length = 100;
		float scale = 10f;
		
		brian = wnd.createInstance();
		VertexDataHolder lary = wnd.createInstance();
		VertexDataHolder dave = wnd.createInstance();


		setupVerts(brian, width, length, scale);
		
		brian.imageName = "behaan.jpg";
		brian.textured = true;
		brian.createVertexArrayObject(gl);
		brian.createFlatVertexArrayObject(gl);
		
		lary.indices = brian.indices;
		lary.vertexColors = brian.vertexColors;
		lary.vertexPositions = brian.vertexPositions;
		lary.vertexNormals = brian.vertexNormals;
		
		lary.textureCoordinates = brian.textureCoordinates;
		
		lary.imageName = "lary.jpg";
		
		lary.modelPosition = new Vector3f(width * scale, 0, 0);
		
		lary.updateMatrix();
		lary.textured = true;
		
		lary.createVertexArrayObject(gl);
		lary.createFlatVertexArrayObject(gl);
		
		
		
		dave.indices = brian.indices;
		dave.vertexColors = brian.vertexColors;
		dave.vertexPositions = brian.vertexPositions;
		dave.vertexNormals = brian.vertexNormals;
		
		dave.textureCoordinates = brian.textureCoordinates;
		
		dave.imageName = "david.jpg";
		
		dave.modelPosition = new Vector3f(width * -scale, 0, 0);
		dave.updateMatrix();
		dave.textured = false;
		dave.material = Materials.getGoldMaterial();
		
		dave.createVertexArrayObject(gl);
		dave.createFlatVertexArrayObject(gl);
		
		wnd.removeChild(brian);
	}

	private static float elapsedTime = 0.0f;
	public static void update(float dt) {
		elapsedTime += dt;
		brian.modelPosition.set(new Vector3f(0, (float)Math.sin(elapsedTime) * 200, 0));
	}
}
