package engine;

import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class Properties extends JFrame {
	private Heirarchy heirachy;
	
	public Properties(Heirarchy h) {
		heirachy = h;
		this.setTitle("Yeet");
		this.setBounds(1400, 100, 200, 600);
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		this.setVisible(true);
	}
	
	public void updateProperties() {
		
	}
}
