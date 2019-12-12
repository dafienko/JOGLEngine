package engine;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

class ContainerFrame extends JPanel {
	public ContainerFrame(JPanel parentFrame, Container container, boolean showContents) {
		JPanel headerFrame = new JPanel();
		headerFrame.setLayout(new BoxLayout(headerFrame, BoxLayout.Y_AXIS));
		
		JPanel childrenFrame = new JPanel();
		childrenFrame.setLayout(new BoxLayout(childrenFrame, 20/parentFrame.getWidth()));
		
		JButton dropDown = new JButton();
		dropDown.setLayout(new FlowLayout());
		dropDown.setPreferredSize(new Dimension(parentFrame.getWidth(), 30));
		dropDown.setAlignmentX(LEFT_ALIGNMENT);
		dropDown.setPreferredSize(new Dimension(16, 16));
		dropDown.setMinimumSize(new Dimension(16, 16));
		dropDown.setMaximumSize(new Dimension(16, 16));
		
		JLabel name = new JLabel(container.containerName);
		name.setMinimumSize(new Dimension(16, 16));
		name.setPreferredSize(new Dimension(16, name.getWidth()));
		name.setMaximumSize(new Dimension(16, parentFrame.getWidth()));
	}
}

@SuppressWarnings("serial")
public class Heirarchy extends JFrame {
	private ArrayList<Container> expandedContainers;
	
	public Heirarchy(Container mainComponent) {
		this.setBounds(1200, 100, 200, 600);
		
		this.setTitle("Heirarchy");
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.setVisible(true);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		expandedContainers = new ArrayList<Container>();
	}
	
	public void updateHeirarchy() { 
		
	}
}
