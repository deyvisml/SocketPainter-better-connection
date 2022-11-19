/**
 * @author Brian Weir (https://github.com/bweir27)
 */
package socketPainter;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class PaintingPanel extends JPanel {

	private ArrayList<PaintingPrimitive> primitives = new ArrayList<PaintingPrimitive>();

	public PaintingPanel() {
		this.setBackground(Color.WHITE);
	}

	public void addPrimitive(PaintingPrimitive obj) {
		this.primitives.add(obj);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(PaintingPrimitive obj : primitives) {  
			obj.draw(g);
		}
	}
}
