package engine;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

@SuppressWarnings("serial")
class ContainerFrame extends JPanel {
	public static int getChildrenFrameHeight(Container parent, ArrayList<Container> expandedContainers) {
		int height = 0;
		final int ELEMENT_SIZE = 16;
		for (Container c : parent.children) {
			height += ELEMENT_SIZE;
			
			if (expandedContainers.indexOf(c) >= 0) { // if this container is also expanded...
				height += getChildrenFrameHeight(c, expandedContainers);
			}
		}
		return height;
	}
	
	public ContainerFrame(final Heirarchy h, JPanel parentFrame, final Container container) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		System.out.println(parentFrame.getWidth());
		this.setAlignmentX(20/Math.max(parentFrame.getWidth(), 1));
		this.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel headerFrame = new JPanel();
		headerFrame.setLayout(new BoxLayout(headerFrame, BoxLayout.X_AXIS));
		headerFrame.setAlignmentX(.2f);
		headerFrame.setMinimumSize(new Dimension(0, 16));
		headerFrame.setPreferredSize(new Dimension(200, 16));
		
		JPanel childrenFrame = new JPanel();
		childrenFrame.setLayout(new BoxLayout(childrenFrame, BoxLayout.Y_AXIS));
		childrenFrame.setAlignmentX(LEFT_ALIGNMENT); ///20/Math.max(parentFrame.getWidth(), 1)
		if (h.expandedContainers.indexOf(container) >= 0 ) { // if this container is expanded in the heirarchy
			childrenFrame.setMinimumSize(new Dimension(0, getChildrenFrameHeight(container, h.expandedContainers)));
			childrenFrame.setPreferredSize(new Dimension(200, getChildrenFrameHeight(container, h.expandedContainers)));
			
			for (Container c : container.children) {
				ContainerFrame cf = new ContainerFrame(h, childrenFrame, c);
				childrenFrame.add(cf);
			}
		}
		this.setBackground(new Color(255, 0, 0));
		
		
		JButton dropDown = new JButton();
		dropDown.setLayout(new FlowLayout());
		dropDown.setPreferredSize(new Dimension(parentFrame.getWidth(), 30));
		dropDown.setAlignmentY(TOP_ALIGNMENT);
		dropDown.setPreferredSize(new Dimension(16, 16));
		dropDown.setMinimumSize(new Dimension(16, 16));
		dropDown.setMaximumSize(new Dimension(16, 16));
		dropDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("pressed");
				int index = h.expandedContainers.indexOf(container);
				if (index < 0) {
					h.expandedContainers.add(container);
				} else {
					h.expandedContainers.remove(index);
				}
				
				h.updateHeirarchy(h.mainPanel, h.mainContainer);
			}
		});
	
		JLabel name = new JLabel();
		name.setMinimumSize(new Dimension(16, 16));
		name.setAlignmentY(TOP_ALIGNMENT);
		if (h.expandedContainers.indexOf(container) >= 0) {
			name.setText(" v " + container.name);
		} else {
			name.setText(" > " + container.name);
		}
		
		headerFrame.add(dropDown);
		headerFrame.add(name);
		
		this.add(headerFrame);
		this.add(childrenFrame);
		
		
	}
}

@SuppressWarnings("serial")
public class Heirarchy extends JFrame {
	protected ArrayList<Container> expandedContainers;
	protected Container mainContainer;
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
		
		mainContainer = mainComponent;
		
		updateHeirarchy(mainPanel, mainComponent);

	}
	
	public void updateHeirarchy(JPanel mainPanel, Container container) { 
		clearHeirarchy();
		
		System.out.println("Updating heirarchy");
		
		for (Instance i : container.children) {
			ContainerFrame cf = new ContainerFrame(this, mainPanel, i);
			addedPanels.add(cf);
			mainPanel.add(cf);
		}
		
		this.setVisible(false);
		this.setVisible(true);
	}
}
