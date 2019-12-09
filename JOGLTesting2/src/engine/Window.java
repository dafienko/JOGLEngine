package engine;

import static com.jogamp.opengl.GL.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

import org.joml.*;

import java.lang.Math;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

@SuppressWarnings("serial")
public class Window extends JFrame implements GLEventListener {
	private GLCanvas mainCanvas; //test1
	
	private Animator animator;
	
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
	
	ArrayList<VertexDataHolder> instances;
	
	JPanel mainPanel;
	JPanel info;
	
	JLabel frameRateLabel;
	
	public Window() {
		this.setBounds(0, 0, 1920, 1080);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("yeet");
		this.setLayout(new BorderLayout());
		
		mainPanel = new JPanel(new BorderLayout());
		info = new JPanel();
		
		mainCanvas = new GLCanvas();
		mainCanvas.addGLEventListener(this);
		
		mainPanel.add(mainCanvas);
		
		frameRateLabel = new JLabel("FrameRate");
		
		frameRateLabel.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		
		info.add(frameRateLabel);
		
		this.add(info, BorderLayout.NORTH);
		this.add(mainPanel, BorderLayout.CENTER);
	
		
		this.setVisible(true);
		
		initVariables();
		
		animator = new Animator(mainCanvas);
		animator.start();
	}
	
	public VertexDataHolder createInstance() {
		VertexDataHolder v = new VertexDataHolder();
		instances.add(v);
		return v;
	}
	
	public void initVariables() {
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
		
		instances = new ArrayList<VertexDataHolder>();
		
		kbd = new Keyboard();
		this.addKeyListener(kbd);
	}
	
	public GL4 getGLContext() {
		return (GL4) GLContext.getCurrentGL();
 	}

	
	@Override 
	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		//vfMainProgram = Util.createShaderProgram(drawable); // gouraud shading
		vfMainProgram = Util.createShaderProgram(drawable, "vertexPhong.glsl", "fragmentPhong.glsl");
		vfFlatColorProgram = Util.createShaderProgram(drawable, "vertexFlat.glsl", "fragmentFlat.glsl");
		
		Game.init(gl, this);
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
	
	public void updateUniforms() {
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
		
		
		nLoc = gl.glGetUniformLocation(vfMainProgram, "normMatrix");
		gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(matrixVals));
		
		globalAmbLoc = gl.glGetUniformLocation(vfMainProgram, "globalAmbient");
		ambLoc = gl.glGetUniformLocation(vfMainProgram, "light.ambient");
		diffLoc = gl.glGetUniformLocation(vfMainProgram, "light.diffuse");
		specLoc = gl.glGetUniformLocation(vfMainProgram, "light.specular");
		posLoc = gl.glGetUniformLocation(vfMainProgram, "light.position");
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
	
	public int texturedLoc;
	public void updateMeshUniforms(VertexDataHolder mesh) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		modelMatLoc = gl.glGetUniformLocation(vfMainProgram, "modelMatrix");	
		gl.glUniformMatrix4fv(modelMatLoc, 1, false, mesh.modelMatrix.get(matrixVals));
		
		modelMatLoc = gl.glGetUniformLocation(vfFlatColorProgram, "modelMatrix");	
		gl.glUniformMatrix4fv(modelMatLoc, 1, false, mesh.modelMatrix.get(matrixVals));
		
		
		texturedLoc = gl.glGetUniformLocation(vfMainProgram, "textured");
		gl.glProgramUniform1i(vfMainProgram, texturedLoc, mesh.textured ? 1 : 0);
		
		mAmbLoc = gl.glGetUniformLocation(vfMainProgram, "material.ambient");
		mDiffLoc = gl.glGetUniformLocation(vfMainProgram, "material.diffuse");
		mSpecLoc = gl.glGetUniformLocation(vfMainProgram, "material.specular");
		mShiLoc = gl.glGetUniformLocation(vfMainProgram, "material.shininess");
		
		gl.glProgramUniform4fv(vfMainProgram, mAmbLoc, 1, mesh.material.ambient, 0);
		gl.glProgramUniform4fv(vfMainProgram, mDiffLoc, 1, mesh.material.diffuse, 0);
		gl.glProgramUniform4fv(vfMainProgram, mSpecLoc, 1, mesh.material.specular, 0);
		gl.glProgramUniform1f(vfMainProgram, mShiLoc, mesh.material.shininess);
	}
	
	private void installLights(Matrix4f vMatrix, VertexDataHolder mesh) {
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
	}
	
	public void glDrawFaces(VertexDataHolder mesh) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glUseProgram(vfMainProgram);
		
		mesh.updateMatrix();
		updateMeshUniforms(mesh);
		
		gl.glBindVertexArray(mesh.vao[0]);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, mesh.texId);
		
		gl.glDrawElements(GL_TRIANGLES, mesh.indices.size(), GL_UNSIGNED_INT, 0);	
	}
	
	public void glDrawLinesAndPoints(VertexDataHolder mesh) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glDisable(GL_DEPTH_TEST);
		
		gl.glUseProgram(vfFlatColorProgram);
		
		mesh.updateMatrix();
		updateMeshUniforms(mesh);
		
		gl.glBindVertexArray(mesh.flatvao[0]);
		
		gl.glLineWidth(1.0f);
<<<<<<< HEAD
		
		gl.glDisable(GL_DEPTH_TEST);	
=======
>>>>>>> refs/remotes/origin/master
		
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
		//this.setTitle(fpsCounter.getFPS() + " fps");
		frameRateLabel.setText("framerate = " + fpsCounter.getFPS() + " FPS");
		
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
		
		Game.update(deltaTime);
		
<<<<<<< HEAD
		glClearCrap(); // clear depth buffer and stuff
		
=======
>>>>>>> refs/remotes/origin/master
		updateCamera(deltaTime);
		
		updateMatrices(); // update the camera matrix
		
		updateUniforms();
		glEnableCrap(); 
		
		switch(wireFrameMode) {
		case 0:
<<<<<<< HEAD
			for (VertexDataHolder v : instances) {
				installLights(camMatrix, v);
				glDrawFaces(v);
			}
=======
			glEnableCrap();
			glDrawFaces(brian);
			glDrawFaces(dave);
			glDrawFaces(lary);
>>>>>>> refs/remotes/origin/master
			break;
		case 1:
<<<<<<< HEAD
			for (VertexDataHolder v : instances) {
				installLights(camMatrix, v);
				glDrawFaces(v);
			}
			
			updateUniforms();
			for (VertexDataHolder v : instances) {
				glDrawLinesAndPoints(v);
			}
=======
			glEnableCrap();
			glDrawFaces(brian);
			glDrawLinesAndPoints(brian);
			
			glEnableCrap();
			glDrawFaces(dave);
			glDrawLinesAndPoints(dave);
			
			glEnableCrap();
			glDrawFaces(lary);
			glDrawLinesAndPoints(lary);
>>>>>>> refs/remotes/origin/master
			break;
		case 2:
			for (VertexDataHolder v : instances) {
				glDrawLinesAndPoints(v);
			}
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
	