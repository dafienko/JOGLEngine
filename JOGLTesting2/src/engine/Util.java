package engine;

import static com.jogamp.opengl.GL2ES2.GL_COMPILE_STATUS;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
import static com.jogamp.opengl.GL2ES2.GL_LINK_STATUS;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES2.*;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.nio.*;

import javax.imageio.ImageIO;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Util {
	public static boolean debugMode = true;
	
	public static String removeEvery(String s, char c) {
		String result = "";
		for (char x : s.toCharArray()) {
			if (x != c) {
				result += x;
			}
		}
		
		return result;
	}
	
	public static String removeEvery(String s, char[] cArr) {
		String result = new String(s);
		
		for (char c : cArr) {
			result = removeEvery(result, c);
		}
		
		return result;
	}
	
	public static String[] toStringArray(File file) {
		ArrayList<String> temp = new ArrayList<String>();
		String[] result;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				if (!line.equals("\n")) {
					temp.add(line + '\n');
				}
			}
			temp.add("\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		result = new String[temp.size()];
		
		for (int i = 0; i < temp.size(); i++) {
			result[i] = temp.get(i);
		}
		
		return result;
	}
	
	public static void printShaderLog(GL4 gl, int shader) {
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;
		
		gl.glGetShaderiv(shader,  GL_INFO_LOG_LENGTH,  len, 0);
		if(len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader,  len[0], chWrittn, 0, log, 0);
			System.out.println("Shader info log: ");
			for (int i = 0; i < log.length; i++) 
				System.out.print((char) log[i]);
		
		}
	}
	
	public static void printProgramLog(GL4 gl, int prog) {
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;
		
		gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], chWrittn,  0, log, 0);
			System.out.println("Program info log: ");
			for (int i = 0; i < log.length; i++) {
				System.out.print(log[i]);
			}
		}
	}
	
	public static boolean checkOpenGLError(GL4 gl) {
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while (glErr != GL_NO_ERROR) {
			System.err.println("glError: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}
	
	public static int createShaderProgram(GLAutoDrawable drawable, String vShaderFileName, String fShaderFileName) {
		GL4 gl = (GL4) drawable.getGL();
		
		int[] vertCompiled = new int[1];
		int[] fragCompiled = new int[1];
		int[] linked = new int[1];
		
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		File vShaderFile = new File(".\\shaders\\" + vShaderFileName);
		String[] vShaderText = toStringArray(vShaderFile);
		
		
		gl.glShaderSource(vShader, vShaderText.length, vShaderText, null, 0);
		gl.glCompileShader(vShader);
		if (debugMode) {
			checkOpenGLError(gl);
			gl.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
			if(vertCompiled[0] != 1) {
				System.out.println(vShaderFileName + " compilation failed");
				printShaderLog(gl, vShader);
			}
		}
		
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		File fShaderFile = new File(".\\shaders\\" + fShaderFileName);
		String[] fShaderText = toStringArray(fShaderFile);
	
		gl.glShaderSource(fShader, fShaderText.length, fShaderText, null, 0);
		gl.glCompileShader(fShader);
		if (debugMode) {
			checkOpenGLError(gl);
			gl.glGetShaderiv(fShader,  GL_COMPILE_STATUS, fragCompiled, 0);
			if (fragCompiled[0] != 1) {
				System.out.println(fShaderFileName + " compilation failed");
				printShaderLog(gl, fShader);
			}
		}
		
		int program = gl.glCreateProgram();
		gl.glAttachShader(program, vShader);
		gl.glAttachShader(program, fShader);
		gl.glLinkProgram(program);
		if (debugMode) {
			checkOpenGLError(gl);
			gl.glGetProgramiv(program, GL_LINK_STATUS, linked, 0);
			if (linked[0] != 1) {
				System.out.println("linking failed");
				printProgramLog(gl, program);
			}
		}
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		
		return program;
	}
	
	public static int createShaderProgram(GLAutoDrawable drawable) {
		return createShaderProgram(drawable, "vertex.glsl", "fragment.glsl");
	}
	
	public static int loadTexture(String textureFileName) 
	{
		Texture tex = null;
		try {tex = TextureIO.newTexture(new File(".\\images\\" + textureFileName), false); }
		catch (Exception e) {
			e.printStackTrace();
		}
		
		int textureID = tex.getTextureObject();
		return textureID;
	}
	
	public static int loadTextureAWT(String textureFileName, GL4 gl) {
		BufferedImage textureImage = getBufferedImage(textureFileName);
		byte[] imgRGBA = getRGBAPixelData(textureImage, true);
		ByteBuffer rgbaBuffer = Buffers.newDirectByteBuffer(imgRGBA);
		
		int[] textureIDs = new int[1];
		gl.glGenTextures(1, textureIDs, 0);
		int textureID = textureIDs[0];

		gl.glBindTexture(GL_TEXTURE_2D, textureID);
		
		gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 
				textureImage.getWidth(), textureImage.getHeight(), 0, 
				GL_RGBA, GL_UNSIGNED_BYTE, 
				rgbaBuffer);
		
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		return textureID;
	}
	
	private static BufferedImage getBufferedImage(String fileName) {
		BufferedImage img;
		
		try { img = ImageIO.read(new File(".\\images\\" + fileName)); } 
		catch (Exception e) {
			System.err.println("Error reading " + fileName + "");
			throw new RuntimeException(e);
		}
		
		return img;
		
	}
	
	private static byte[] getRGBAPixelData(BufferedImage img, boolean flip) {
		byte[] imgRGBA;
		
		int height = img.getHeight(null);
		int width = img.getWidth(null);
		
		WritableRaster raster = 
				Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 4, null);
		
		ComponentColorModel colorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), 
				new int[] {8, 8, 8, 8}, true, false, 
				ComponentColorModel.TRANSLUCENT, 
				DataBuffer.TYPE_BYTE);
		
		BufferedImage newImage = new BufferedImage(colorModel, raster, false, null);
		Graphics2D g = newImage.createGraphics();
		
		if(flip) {
			AffineTransform gt = new AffineTransform();
			gt.translate(0, height);
			gt.scale(1, -1d);
			g.transform(gt);
		}
		
		g.drawImage(img,  null,  null);
		g.dispose();
		
		DataBufferByte dataBuf = (DataBufferByte) raster.getDataBuffer();
		
		imgRGBA = dataBuf.getData();
		
		return imgRGBA;
	}
}
