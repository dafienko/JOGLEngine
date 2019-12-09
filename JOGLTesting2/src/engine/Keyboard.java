package engine;

import java.awt.event.*;
import java.util.ArrayList;

public class Keyboard implements KeyListener {
	private ArrayList<String> keysDown;
	private ArrayList<String> keysPressedThisFrame;
	private ArrayList<String> keysReleasedThisFrame;
	
	public Keyboard() {
		keysDown = new ArrayList<String>();
		keysPressedThisFrame = new ArrayList<String>();
		keysReleasedThisFrame = new ArrayList<String>();
	}
	
	public boolean isKeyDown(String keyName) {
		for (String k : keysDown) {
			if (k.equals(keyName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean keyPressedThisFrame(String keyName) {
		for (String k : keysPressedThisFrame) {
			if (k.equals(keyName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void updateKeyboard() {
		for (String k : keysPressedThisFrame) { // add keys pressed this frame to keysDown
			boolean keyFound = false;
			for (String s : keysDown) {
				if (s.equals(k)) {
					keyFound = true;
					break;
				}
			}
			
			if (!keyFound) {
				keysDown.add(k);
			}
		}
		
		for (String k : keysReleasedThisFrame) { // remove keys released this frame from keysDown
			for (int i = 0; i < keysDown.size(); i++) {
				String s = keysDown.get(i);
				if (s.equals(k)) {
					keysDown.remove(i);
					break;
				}
			}
		}
		
		keysPressedThisFrame.clear();
		keysReleasedThisFrame.clear();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		//System.out.println("KeyPressed: " + KeyEvent.getKeyText(e.getKeyCode()));
		keysPressedThisFrame.add(KeyEvent.getKeyText(e.getKeyCode()));
	}
	
	@Override 
	public void keyReleased(KeyEvent e) {
		//System.out.println("KeyReleased: " + KeyEvent.getKeyText(e.getKeyCode()));
		keysReleasedThisFrame.add(KeyEvent.getKeyText(e.getKeyCode()));
	}
	
	@Override 
	public void keyTyped(KeyEvent e) {
		//System.out.println("KeyTyped: " + KeyEvent.getKeyText(e.getKeyCode()));
	}
}
