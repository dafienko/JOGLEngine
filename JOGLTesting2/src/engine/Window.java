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
	
	private Camera currentCamera;
	
	private int vfMainProgram;
	private int vfFlatColorProgram;
	private FPSCounter fpsCounter;
	
	private char wireFrameMode = 0;
	private Vector3f wireframeColor;
	private float elapsedTime;
	
	private Matrix4f invTrMat;
	
	private Matrix4f projectionMatrix;
	
	private Matrix4f modelMatrix;
	private Matrix4f viewModelMatrix; // camMatrix * modelMatrix
	private Matrix4f modelViewMatrix;
	
	private FloatBuffer matrixVals;
	
	private Keyboard kbd;
	
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
	
	private JPanel mainPanel;
	private JPanel info;
	
	private JLabel frameRateLabel;
	
	public Heirarchy heirarchy;
	public Properties properties;
	
	public Container container;
	
	public Window() {
		this.setBounds(200, 100, 1000, 600);
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
	
		currentCamera = new Camera(new Vector3f(20, 0, -40));
		
		this.setVisible(true);
		
		initVariables();
		
		animator = new Animator(mainCanvas);
		animator.start();
		
		container = new Container();
		
		heirarchy = new Heirarchy(container);
		properties = new Properties(heirarchy);
	}
	
	public VertexDataHolder createInstance() {
		VertexDataHolder v = new VertexDataHolder();
		v.setParent(container);
		heirarchy.updateHeirarchy(heirarchy.mainPanel, container);
		return v;
	}
	
	public void initVariables() {
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

		kbd = new Keyboard();
		this.addKeyListener(kbd);
		mainCanvas.addKeyListener(kbd);
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
		cLoc = gl.glGetUniformLocation(vfFlatColorProgram, "vColor");
		gl.glUniform4f(cLoc, c.x, c.y, c.z, 1.0f);
	}
	
	public void updateUniforms() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		projMatLoc = gl.glGetUniformLocation(vfMainProgram, "projectionMatrix");
		gl.glUniformMatrix4fv(projMatLoc, 1, false, projectionMatrix.get(matrixVals));
		
		projMatLoc = gl.glGetUniformLocation(vfFlatColorProgram, "projectionMatrix");
		gl.glUniformMatrix4fv(projMatLoc, 1, false, projectionMatrix.get(matrixVals));
		
		
		cameraMatLoc = gl.glGetUniformLocation(vfFlatColorProgram, "cameraMatrix");
		gl.glUniformMatrix4fv(cameraMatLoc, 1, false, currentCamera.returnMatrix.get(matrixVals));
		
		cameraMatLoc = gl.glGetUniformLocation(vfMainProgram, "cameraMatrix");
		gl.glUniformMatrix4fv(cameraMatLoc, 1, false, currentCamera.returnMatrix.get(matrixVals));
		
		
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
		
		projectionMatrix.setPerspective((float)Math.toRadians(currentCamera.FOV), aspect, 0.1f, 4000.0f);
		
		modelViewMatrix.identity();
		modelViewMatrix.set(currentCamera.getMatrix());
		modelViewMatrix.mul(new Matrix4f().identity());
		
		viewModelMatrix.identity();
		viewModelMatrix.mul(currentCamera.getMatrix());
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
	}
	
	public void glDrawFaces(VertexDataHolder mesh) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glUseProgram(vfMainProgram);
		
		updateUniforms();
		
		mesh.updateMatrix();
		updateMeshUniforms(mesh);
		
		gl.glBindVertexArray(mesh.vao[0]);
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LESS);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, mesh.texId);
		
		gl.glDrawElements(GL_TRIANGLES, mesh.indices.size(), GL_UNSIGNED_INT, 0);	
	}
	
	public void glDrawWireframe(VertexDataHolder mesh) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glUseProgram(vfFlatColorProgram);
		
		setColor(gl, wireframeColor);
		
		updateUniforms();
		
		mesh.updateMatrix();
		updateMeshUniforms(mesh);
		
		
		
		gl.glBindVertexArray(mesh.flatvao[0]);
		
		gl.glLineWidth(1.0f);
		
		gl.glDepthFunc(GL_ALWAYS);

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
		
		currentCamera.rotation.add(new Vector3f(-xRot * sensitivity, -yRot * sensitivity, 0));
		
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
		rightVector.x = (float)Math.cos(currentCamera.rotation.y);
		rightVector.z = (float)-Math.sin(currentCamera.rotation.y);
		
		Vector3f lookVector = new Vector3f();
		lookVector.y = (float)Math.sin(currentCamera.rotation.x);
		lookVector.x = (float)Math.cos(currentCamera.rotation.y + (Math.PI/2));
		lookVector.z = (float)-Math.sin(currentCamera.rotation.y + (Math.PI/2));
		
		Vector3f movementVector = new Vector3f();
		movementVector.add(rightVector.mul(xDir * speed).add(lookVector.mul(-zDir * speed)));
		movementVector.add(new Vector3f(0, yDir * speed, 0));
		
		currentCamera.position.add(movementVector);
		
		currentCamera.getMatrix();
	}
	
	public void drawChildrenFaces(Container c) {
		for (Instance i : c.children) {
			if (i instanceof VertexDataHolder) {
				VertexDataHolder v = (VertexDataHolder) i;
				
				installLights(currentCamera.returnMatrix);
				
				glDrawFaces(v);
				
				if (v.selected) {
					
					
					glDrawWireframe(v);
				}
				
			}
			
			if (i.children.size() > 0) {
				drawChildrenFaces(i);
			}
		}
	}
	
	public void drawChildrenWireframe(Container c) {
		for (Instance i : c.children) {
			if (i instanceof VertexDataHolder) {
				VertexDataHolder v = (VertexDataHolder) i;
				
				glDrawWireframe(v);
				
			}
			
			if (i.children.size() > 0) {
				drawChildrenWireframe(i);
			}
		}
	}
	
	@Override 
	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		deltaTime = fpsCounter.tick();
		elapsedTime += deltaTime;
		//this.setTitle(fpsCounter.getFPS() + " fps");
		frameRateLabel.setText("framerate = " + fpsCounter.getFPS() + " FPS");
		
		colorVal = (float)((Math.sin(elapsedTime*3) + 1) / 2);
		wireframeColor.set(colorVal, colorVal, 1);
		
		if (kbd.keyPressedThisFrame("R")) {
			wireFrameMode++;
			if (wireFrameMode > 2) {
				wireFrameMode = 0;
			}
		}
		
		kbd.updateKeyboard();
		
		Game.update(deltaTime);
		
		glClearCrap(); // clear depth buffer and stuff
		
		updateCamera(deltaTime);
		
		updateMatrices(); // update the camera matrix
		
		updateUniforms();
		glEnableCrap(); 
		
		switch(wireFrameMode) {
		case 0:
			drawChildrenFaces(container);
			break;
		case 1:
			drawChildrenFaces(container);
			
			updateUniforms();
			drawChildrenWireframe(container);
			break;
		case 2:
			drawChildrenWireframe(container);
			break;
		}
			
	};
	
	@Override
	public void dispose(GLAutoDrawable drawable) {};
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		aspect = (float) mainCanvas.getWidth() / (float) mainCanvas.getHeight();
		this.requestFocus();
	};
	
}
	