package engine;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import static com.jogamp.opengl.GL.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexDataHolder {
	public ArrayList<Vector3f> vertexPositions;
	public ArrayList<Vector3f> vertexNormals;
	public ArrayList<Vector3f> vertexColors;
	public ArrayList<Integer> indices;
	public ArrayList<Integer> lineIndices;
	public ArrayList<Vector2f> textureCoordinates;
	
	public boolean textured;
	public Material material;
	
	public Vector3f modelPosition;
	public Vector3f modelRotation;
	
	public Matrix4f modelMatrix;
	
	public String imageName;
	
	public int[] flatvao;
	public int[] flatvbo;
	
	public int[] vao;
	public int[] vbo;
	
	public int[] textures;
	public int texId;
	
	public static ArrayList<Vector3f> arrayToArrayList(Vector3f[] arr) {
		ArrayList<Vector3f> result = new ArrayList<Vector3f>();
		
		for (Vector3f v : arr) {
			result.add(v);
		}
		
		return result;
	}
	
	public void updateMatrix() {
		modelMatrix.identity();
		modelMatrix.setRotationXYZ(modelRotation.x, modelRotation.y, modelRotation.z);
		modelMatrix.setTranslation(modelPosition);
	}
	
	public VertexDataHolder() {
		vertexPositions = new ArrayList<Vector3f>();
		vertexNormals = new ArrayList<Vector3f>();
		vertexColors = new ArrayList<Vector3f>();
		indices = new ArrayList<Integer>();
		lineIndices = new ArrayList<Integer>();
		textureCoordinates  = new ArrayList<Vector2f>();
		material = Materials.getDefMaterial();
		textures = new int[1];
		textured = false;
		vao = new int[1];
		vbo = new int[6];
		
		flatvao = new int[1];
		flatvbo = new int[2];
		
		modelPosition = new Vector3f(0, 0, 0);
		modelRotation = new Vector3f(0, 0, 0);
		
		modelMatrix = new Matrix4f();
		modelMatrix.identity();
		modelMatrix.setRotationXYZ(modelRotation.x, modelRotation.y, modelRotation.z);
		modelMatrix.setTranslation(modelPosition);
		
		texId = 0;
		
		imageName = "";
	}
	
	private float[] vector3ArrayListfToFloatArray(ArrayList<Vector3f> arr) {
		float[] result = new float[arr.size() * 3]; // 3 floats per Vector3f
		
		for (int i = 0; i < arr.size(); i++) {
			result[i * 3 + 0] = arr.get(i).x;
			result[i * 3 + 1] = arr.get(i).y;
			result[i * 3 + 2] = arr.get(i).z;
		}
		
		return result;
	}
	
	private float[] vector2ArrayListfToFloatArray(ArrayList<Vector2f> arr) {
		float[] result = new float[arr.size() * 2]; // 3 floats per Vector3f
		
		for (int i = 0; i < arr.size(); i++) {
			result[i * 2 + 0] = arr.get(i).x;
			result[i * 2 + 1] = arr.get(i).y;
		}
		
		return result;
	}
	
	
	public void createVertexArrayObject(GL4 gl) {
		gl.glGenVertexArrays(1, vao, 0);
		gl.glBindVertexArray(vao[0]);
		
		gl.glGenBuffers(6, vbo, 0);
		
		int[] indArray = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) {
			indArray[i] = (int)indices.get(i);
		}
		
		// indices vbo (index 0)
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[0]);
		IntBuffer indBuffer = Buffers.newDirectIntBuffer(indArray);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indBuffer.limit() * 4, indBuffer, GL_STATIC_DRAW);
		gl.glVertexAttribPointer(0, 1, GL_UNSIGNED_INT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		
		float[] vPosArray = vector3ArrayListfToFloatArray(vertexPositions);
		
		// vertex positions vbo (index 1)
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);		
		FloatBuffer vPosBuffer = Buffers.newDirectFloatBuffer(vPosArray);
		gl.glBufferData(GL_ARRAY_BUFFER, vPosBuffer.limit() * 4, vPosBuffer, GL_STATIC_DRAW);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		
		float[] vNormArray = vector3ArrayListfToFloatArray(vertexNormals);
		// vertex normals vbo (index 2)
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
		FloatBuffer vNormBuffer = Buffers.newDirectFloatBuffer(vNormArray);
		gl.glBufferData(GL_ARRAY_BUFFER, vNormBuffer.limit() * 4, vNormBuffer, GL_STATIC_DRAW);
		gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(2);
		
		
		float[] vColorArray = vector3ArrayListfToFloatArray(vertexColors);
		// vertex colors vho (index 3)
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer vColorBuffer = Buffers.newDirectFloatBuffer(vColorArray);
		gl.glBufferData(GL_ARRAY_BUFFER, vColorBuffer.limit() * 4, vColorBuffer, GL_STATIC_DRAW);
		gl.glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(3);
		
		float[] vTexCoordArray = vector2ArrayListfToFloatArray(textureCoordinates);
		// vertex texture coordinates vho (index 4)
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer vTexCoordBuffer = Buffers.newDirectFloatBuffer(vTexCoordArray);
		gl.glBufferData(GL_ARRAY_BUFFER, vTexCoordBuffer.limit() * 4, vTexCoordBuffer, GL_STATIC_DRAW);
		gl.glVertexAttribPointer(4, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(4);
		
		if (textured) {
			texId = Util.loadTextureAWT(imageName, gl);
		}
		
	}
	
	public void createFlatVertexArrayObject(GL4 gl) {
		gl.glGenVertexArrays(1, flatvao, 0);
		gl.glBindVertexArray(flatvao[0]);
		
		gl.glGenBuffers(2, flatvbo, 0);
		
		lineIndices = new ArrayList<Integer>(); // get the indices for a line-mesh render
		for (int i = 0; i < indices.size(); i+=3) {
			lineIndices.add(indices.get(i));
			lineIndices.add(indices.get(i+1));
			
			lineIndices.add(indices.get(i+1));
			lineIndices.add(indices.get(i+2));
			
			lineIndices.add(indices.get(i+2));
			lineIndices.add(indices.get(i));	
		}
		
		int[] indArray = new int[lineIndices.size()]; // put those indices in indArray
		for (int i = 0; i < lineIndices.size(); i++) {
			indArray[i] = (int)lineIndices.get(i);
		}
		
		IntBuffer indBuffer = Buffers.newDirectIntBuffer(indArray);
		
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, flatvbo[0]);
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indBuffer.limit() * 4, indBuffer, GL_STATIC_DRAW);
		gl.glVertexAttribPointer(0, 1, GL_UNSIGNED_INT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		float[] vPosArray = vector3ArrayListfToFloatArray(vertexPositions);
	
		FloatBuffer vPosBuffer = Buffers.newDirectFloatBuffer(vPosArray);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, flatvbo[1]);	
		gl.glBufferData(GL_ARRAY_BUFFER, vPosBuffer.limit() * 4, vPosBuffer, GL_STATIC_DRAW);
		gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
	}

}
