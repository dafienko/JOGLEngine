package engine;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

@SuppressWarnings("serial")
class ContainerFrame extends JPanel {
	public ContainerFrame(JPanel parentFrame, Container container, boolean showContents) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel headerFrame = new JPanel();
		headerFrame.setLayout(new BoxLayout(headerFrame, BoxLayout.X_AXIS));
		headerFrame.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel childrenFrame = new JPanel();
		childrenFrame.setLayout(new BoxLayout(childrenFrame, BoxLayout.X_AXIS));
		childrenFrame.setAlignmentX(20/Math.max(parentFrame.getWidth(), 1));
		
		JButton dropDown = new JButton();
		dropDown.setLayout(new FlowLayout());
		dropDown.setPreferredSize(new Dimension(parentFrame.getWidth(), 30));
		dropDown.setAlignmentY(TOP_ALIGNMENT);
		dropDown.setPreferredSize(new Dimension(16, 16));
		dropDown.setMinimumSize(new Dimension(16, 16));
		dropDown.setMaximumSize(new Dimension(16, 16));
		
		JLabel name = new JLabel(" > " + container.name);
		System.out.println(container.name);
		name.setMinimumSize(new Dimension(16, 16));
		name.setAlignmentY(TOP_ALIGNMENT);
		
		headerFrame.add(dropDown);
		headerFrame.add(name);
		
		this.add(headerFrame);
		this.add(childrenFrame);
	}
}

@SuppressWarnings("serial")
public class Heirarchy extends JFrame {
	private ArrayList<Container> expandedContainers;
	
	public JPanel mainPanel;
	private ArrayList<JPanel> addedPanels;
	
	private void clearHeirarchy() {
		for (JPanel jp : addedPanels) {
			mainPanel.remove(jp);
		}
		
		addedPanels.clear();
	}
	
	public Heirarchy(Container mainComponent) {
		this.setBounds(1200, 100, 200, 600);
		
		this.setTitle("Heirarchy");
		
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		addedPanels = new ArrayList<JPanel>();
		expandedContainers = new ArrayList<Container>();
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		this.add(mainPanel);
		
		this.setVisible(true);
		
		updateHeirarchy(mainPanel, mainComponent);

	}
	
	public void updateHeirarchy(JPanel mainPanel, Container container) { 
		clearHeirarchy();
		
		for (Instance i : container.children) {
			ContainerFrame cf = new ContainerFrame(mainPanel, i, false);
			addedPanels.add(cf);
			mainPanel.add(cf);
		}
		
		this.setVisible(false);
		this.setVisible(true);
	}
}
