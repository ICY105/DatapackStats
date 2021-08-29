package datapack_stats;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JExandableLabel extends JPanel {

	private static final long serialVersionUID = -2409536316688250342L;

	private final String label;
	private final int prefWidth;
	private final int prefHeight;
	
	private final JLabel mainLabel;
	private final List<JLabel> labelList;
	private boolean expanded = false;
	
	public JExandableLabel(String label, int prefWidth, int prefHeight) {
		this.label = label;
		this.prefWidth = prefWidth;
		this.prefHeight = prefHeight;
		
		BoxLayout layoutBasic = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(layoutBasic);
		
		mainLabel = new JLabel("▶ " + label);
		this.add(mainLabel);
		labelList = new LinkedList<JLabel>();
		
		this.setPreferredSize(new Dimension(prefWidth,prefHeight) );
		
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				toggleList();
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
	}
	
	private void toggleList() {
		if(expanded) {
			expanded = false;
			this.setPreferredSize(new Dimension(prefWidth,prefHeight) );
			mainLabel.setText("▶ " + label);
			
			this.removeAll();
			this.add(mainLabel);
		} else {
			expanded = true;
			this.setPreferredSize(new Dimension(prefWidth, prefHeight * (labelList.size()+1) + 2 ) );
			mainLabel.setText("▼ " + label);
			
			for(JLabel label: labelList)
				this.add(label);
		}
	}
	
	public void addLabel(JLabel label) {
		labelList.add(label);
	}
	
}
