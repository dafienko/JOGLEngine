package engine;

import static com.jogamp.opengl.GL.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

@SuppressWarnings("serial")
public class Window extends JFrame implements GLEventListener {
	private GLCanvas mainCanvas;
	
	private Animator animator;
	
	private int[] vao;
	private int[] vbo;
	private int vfMainProgram;
	private int vfFlatColorProgram;
	private FPSCounter fpsCounter;
	
	float[] verts;
	float[] vertNormals;
	
	char wireFrameMode = 0;
	Vector3f wireframeColor;
	float elapsedTime;
	
	Vector3f camPos;
	Vector3f camRot;
	Matrix4f camMatrix;
	
	Matrix4f invTrMat;
	
	Matrix4f projectionMatrix;
	
	Matrix4f modelMatrix;
	Matrix4f viewModelMatrix; // camMatrix * modelMatrix
	Matrix4f modelViewMatrix;
	
	FloatBuffer matrixVals;
	
	Keyboard kbd;
	
	float aspect;
	
	private int projMatLoc, modelMatLoc, cameraMatLoc, cLoc, globalAmbLoc, ambLoc, diffLoc, specLoc, posLoc, mAmbLoc, mDiffLoc, mSpecLoc, mShiLoc, nLoc;
	
	private Vector3f currentLightPos = new Vector3f(0);
	private float[] lightPos = new float[3];
	
	private Vector3f initialLightLoc = new Vector3f(2.0f, 4.0f, -13.0f);
	private float colorVal, deltaTime;
	
	float[] globalAmbient = new float[] {.6f, .6f, .6f, 1.0f};
	float[] lightAmbient = new float[] {.1f, .1f, .1f, 1.0f};
	float[] lightDiffuse = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
	float[] lightSpecular = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
	
	Material currentMaterial;
	
	VertexDataHolder brian, lary, dave;
	
	
	/*
	float[] matAmb = Materials.JADE_AMBIENT;
	float[] matDif = Materials.JADE_DIFFUSE;
	float[] matSpe = Materials.JADE_SPECULAR;
	float matShi = Materials.JADE_SHININESS;
	*/
	public Window() {
		this.setBounds(0, 0, 1920, 1080);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		mainCanvas = new GLCanvas();
		mainCanvas.addGLEventListener(this);
		
		this.add(mainCanvas);
		
		this.setVisible(true);
		
		initVariables();
		
		animator = new Animator(mainCanvas);
		animator.start();
	}
	
	public void initVariables() {
		vao = new int[1];
		vbo = new int[2];
		
		camPos = new Vector3f(0, 200, 0.4f);
		camRot = new Vector3f((float)(Math.PI/-5.0f), (float)(-Math.PI/1.4f), 0);
		
		camMatrix = new Matrix4f();
		
		projectionMatrix = new Matrix4f();
		
		modelMatrix = new Matrix4f();
		viewModelMatrix = new Matrix4f();
		modelViewMatrix = new Matrix4f();
		modelViewMatrix.identity();
		invTrMat = new Matrix4f();
		invTrMat.identity();
		
		wireframeColor = new Vector3f();
		
		matrixVals = Buffers.newDirectFloatBuffer(16);
		
		fpsCounter = new FPSCounter();
		
		//currentMaterial = new Material(); currentMaterial.randomizeMaterial();
		//currentMaterial = Materials.getGoldMaterial();
		currentMaterial = Materials.getDefMaterial();
		brian = new VertexDataHolder();
		lary = new VertexDataHolder();
		dave = new VertexDataHolder();
		
		kbd = new Keyboard();
		this.addKeyListener(kbd);
	}
	
	public GL4 getGLContext() {
		return (GL4) GLContext.getCurrentGL();
 	}
	
	private float scale = 14f;
	private int width = 70;
	private int length = 70;
	
	public void setupVerts(VertexDataHolder mesh) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glGenVertexArrays(1, vao, 0);
		gl.glBindVertexArray(vao[0]);
		
		gl.glGenBuffers(2, vbo, 0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		
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
		
		mesh.imageName = "behaan.jpg";
		
		mesh.createVertexArrayObject(gl);
		mesh.createFlatVertexArrayObject(gl);
	}
	
	@Override 
	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		//vfMainProgram = Util.createShaderProgram(drawable); // gouraud shading
		vfMainProgram = Util.createShaderProgram(drawable, "vertexPhong.glsl", "fragmentPhong.glsl");
		vfFlatColorProgram = Util.createShaderProgram(drawable, "vertexFlat.glsl", "fragmentFlat.glsl");
		setupVerts(brian);
		
		lary.indices = brian.indices;
		lary.vertexColors = brian.vertexColors;
		lary.vertexPositions = brian.vertexPositions;
		lary.vertexNormals = brian.vertexNormals;
		
		lary.textureCoordinates = brian.textureCoordinates;
		
		lary.imageName = "lary.jpg";
		
		lary.modelPosition = new Vector3f(width * scale, 0, 0);
		
		lary.updateMatrix();
		
		lary.createVertexArrayObject(gl);
		lary.createFlatVertexArrayObject(gl);
		
		
		
		dave.indices = brian.indices;
		dave.vertexColors = brian.vertexColors;
		dave.vertexPositions = brian.vertexPositions;
		dave.vertexNormals = brian.vertexNormals;
		
		dave.textureCoordinates = brian.textureCoordinates;
		
		dave.imageName = "david.jpg";
		
		dave.modelPosition = new Vector3f(width * scale * 2, 0, 0);
		dave.updateMatrix();
		
		dave.createVertexArrayObject(gl);
		dave.createFlatVertexArrayObject(gl);
	};
	
	public void glClearCrap() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		//gl.glClearColor(0.65f, 0.92f, .97f, 1.0f); // sky blue
		gl.glClearColor(0f, 0f, 0f, 1.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
	}
	
	public void glEnableCrap() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.setSwapInterval(0);
		
		gl.glEnable(GL_CULL_FACE);
		gl.glCullFace(GL_BACK);
		
		gl.glUseProgram(vfMainProgram);
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LESS);
	}
	
	public void setColor(GL4 gl, Vector3f c) {
		gl.glUniform4f(cLoc, c.x, c.y, c.z, 1.0f);
	}
	
	public void updateUniforms(VertexDataHolder mesh) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		cLoc = gl.glGetUniformLocation(vfFlatColorProgram, "vColor");
		
		projMatLoc = gl.glGetUniformLocation(vfMainProgram, "projectionMatrix");
		gl.glUniformMatrix4fv(projMatLoc, 1, false, projectionMatrix.get(matrixVals));
		
		projMatLoc = gl.glGetUniformLocation(vfFlatColorProgram, "projectionMatrix");
		gl.glUniformMatrix4fv(projMatLoc, 1, false, projectionMatrix.get(matrixVals));
		
		
		cameraMatLoc = gl.glGetUniformLocation(vfFlatColorProgram, "cameraMatrix");
		gl.glUniformMatrix4fv(cameraMatLoc, 1, false, camMatrix.get(matrixVals));
		
		cameraMatLoc = gl.glGetUniformLocation(vfMainProgram, "cameraMatrix");
		gl.glUniformMatrix4fv(cameraMatLoc, 1, false, camMatrix.get(matrixVals));
		
		
		modelMatLoc = gl.glGetUniformLocation(vfMainProgram, "modelMatrix");	
		gl.glUniformMatrix4fv(modelMatLoc, 1, false, mesh.modelMatrix.get(matrixVals));
		
		modelMatLoc = gl.glGetUniformLocation(vfFlatColorProgram, "modelMatrix");	
		gl.glUniformMatrix4fv(modelMatLoc, 1, false, mesh.modelMatrix.get(matrixVals));
		
		
		nLoc = gl.glGetUniformLocation(vfMainProgram, "normMatrix");
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(matrixVals));
		
		globalAmbLoc = gl.glGetUniformLocation(vfMainProgram, "globalAmbient");
		ambLoc = gl.glGetUniformLocation(vfMainProgram, "light.ambient");
		diffLoc = gl.glGetUniformLocation(vfMainProgram, "light.diffuse");
		specLoc = gl.glGetUniformLocation(vfMainProgram, "light.specular");
		posLoc = gl.glGetUniformLocation(vfMainProgram, "light.position");
		mAmbLoc = gl.glGetUniformLocation(vfMainProgram, "material.ambient");
		mDiffLoc = gl.glGetUniformLocation(vfMainProgram, "material.diffuse");
		mSpecLoc = gl.glGetUniformLocation(vfMainProgram, "material.specular");
		mShiLoc = gl.glGetUniformLocation(vfMainProgram, "material.shininess");
	}
	
	public void updateMatrices() {
		modelMatrix.identity();
		
		aspect = (float) mainCanvas.getWidth() / (float) mainCanvas.getHeight();
		projectionMatrix.setPerspective((float)Math.toRadians(105.0f), aspect, 0.1f, 4000.0f);
		
		modelViewMatrix.identity();
		modelViewMatrix.set(camMatrix);
		modelViewMatrix.mul(new Matrix4f().identity());
		
		viewModelMatrix.identity();
		viewModelMatrix.mul(camMatrix);
		viewModelMatrix.mul(modelMatrix);
		
		currentLightPos.set(initialLightLoc);
		
		invTrMat.identity();
		modelViewMatrix.invert(invTrMat);
		invTrMat.transpose(invTrMat);
	}
	
	private void installLights(Matrix4f vMatrix) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		currentLightPos.mulPosition(vMatrix);
		
		lightPos[0] = currentLightPos.x();
		lightPos[1] = currentLightPos.y();
		lightPos[2] = currentLightPos.z();
		
		gl.glProgramUniform4fv(vfMainProgram, globalAmbLoc, 1, globalAmbient, 0);
		gl.glProgramUniform4fv(vfMainProgram, ambLoc, 1, lightAmbient, 0);
		gl.glProgramUniform4fv(vfMainProgram, diffLoc, 1, lightDiffuse, 0);
		gl.glProgramUniform4fv(vfMainProgram, specLoc, 1, lightSpecular, 0);
		gl.glProgramUniform4fv(vfMainProgram, posLoc, 1, lightPos, 0);
		gl.glProgramUniform4fv(vfMainProgram, mAmbLoc, 1, currentMaterial.ambient, 0);
		gl.glProgramUniform4fv(vfMainProgram, mDiffLoc, 1, currentMaterial.diffuse, 0);
		gl.glProgramUniform4fv(vfMainProgram, mSpecLoc, 1, currentMaterial.specular, 0);
		gl.glProgramUniform1f(vfMainProgram, mShiLoc, currentMaterial.shininess);
	}
	
	public void glDrawFaces(VertexDataHolder mesh) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glUseProgram(vfMainProgram);
		
		mesh.updateMatrix();
		updateUniforms(mesh);
		
		gl.glBindVertexArray(mesh.vao[0]);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, mesh.texId);
		
		gl.glDrawElements(GL_TRIANGLES, mesh.indices.size(), GL_UNSIGNED_INT, 0);	
	}
	
	public void glDrawLinesAndPoints(VertexDataHolder mesh) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glUseProgram(vfFlatColorProgram);
		
		updateUniforms(mesh);
		
		gl.glBindVertexArray(mesh.flatvao[0]);
		
		if (wireFrameMode == 1) {
			gl.glLineWidth(3.0f);
		} else {
			gl.glLineWidth(1.0f);
		}
			
		
		
		setColor(gl, wireframeColor);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.flatvbo[0]);
		gl.glDrawElements(GL_LINES, mesh.lineIndices.size(), GL_UNSIGNED_INT, 0);
	}
	
	private float sensitivity, xRot, yRot, speed, zDir, xDir;
	public void updateCamera(float deltaTime) {
		sensitivity = 1.8f * deltaTime;
		yRot = 0;
		xRot = 0;
		if (kbd.isKeyDown("Down")) {
			xRot = 1;
		} else if (kbd.isKeyDown("Up")) {
			xRot = -1;
		}
		
		if (kbd.isKeyDown("Right")) {
			yRot = 1;
		} else if (kbd.isKeyDown("Left")) {
			yRot = -1;
		}
		
		camRot.add(new Vector3f(-xRot * sensitivity, -yRot * sensitivity, 0));
		
		speed = 200f * deltaTime;
		zDir = 0;
		xDir = 0;
		if (kbd.isKeyDown("S")) {
			zDir = 1;
		} else if (kbd.isKeyDown("W")) {
			zDir = -1;
		}
		
		if (kbd.isKeyDown("D")) {
			xDir = 1;
		} else if (kbd.isKeyDown("A")) {
			xDir = -1;
		}
		
		float yDir = 0;
		if (kbd.isKeyDown("E")) {
			yDir = 1;
		} else if (kbd.isKeyDown("Q")) {
			yDir = -1;
		}
		
		Vector3f rightVector = new Vector3f();
		rightVector.y = 0;
		rightVector.x = (float)Math.cos(camRot.y);
		rightVector.z = (float)-Math.sin(camRot.y);
		
		Vector3f lookVector = new Vector3f();
		lookVector.y = (float)Math.sin(camRot.x);
		lookVector.x = (float)Math.cos(camRot.y + (Math.PI/2));
		lookVector.z = (float)-Math.sin(camRot.y + (Math.PI/2));
		
		Vector3f movementVector = new Vector3f();
		movementVector.add(rightVector.mul(xDir * speed).add(lookVector.mul(-zDir * speed)));
		movementVector.add(new Vector3f(0, yDir * speed, 0));
		
		camPos.add(movementVector);
		
		camMatrix.identity();
		camMatrix.rotateXYZ(-camRot.x, -camRot.y, -camRot.z);
		camMatrix.translate(-camPos.x, -camPos.y, -camPos.z);
	}
	
	@Override 
	public void display(GLAutoDrawable drawable) {
		deltaTime = fpsCounter.tick();
		elapsedTime += deltaTime;
		this.setTitle(fpsCounter.getFPS() + " fps");
		
		colorVal = (float)((Math.sin(elapsedTime*3) + 1) / 2);
		wireframeColor.set(colorVal, colorVal, 1);
		
		this.requestFocus();
		
		if (kbd.keyPressedThisFrame("R")) {
			wireFrameMode++;
			if (wireFrameMode > 2) {
				wireFrameMode = 0;
			}
		}
		
		kbd.updateKeyboard();
		
		glClearCrap(); // clear depth buffer and stuff
		
		glEnableCrap(); // enable face culling and other crap
		
		updateCamera(deltaTime);
		
		updateMatrices(); // update the camera matrix
		
		installLights(camMatrix);
		
		switch(wireFrameMode) {
		case 0:
			glDrawFaces(brian);
			glDrawFaces(dave);
			glDrawFaces(lary);
			break;
		case 1:
			glDrawFaces(brian);
			glDrawFaces(dave);
			glDrawFaces(lary);
			
			glDrawLinesAndPoints(brian);
			glDrawLinesAndPoints(dave);
			glDrawLinesAndPoints(lary);
			break;
		case 2:
			glDrawLinesAndPoints(brian);
			glDrawLinesAndPoints(dave);
			glDrawLinesAndPoints(lary);
			break;
		}
			
	};
	
	@Override
	public void dispose(GLAutoDrawable drawable) {};
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		this.requestFocus();
	};
	
}
	