package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import simulator.control.Controller;
import simulator.misc.Vector;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class Viewer extends JComponent implements SimulatorObserver {

	private double _scale; 
	private List<Body> _bodies; 
	private boolean _showHelp;
	private int _centerY;
	private int _centerX;

	public Viewer(Controller ctrl) { 
		_bodies = ctrl.getBodies();
		initGUI(); 
		ctrl.addObserver(this); 
	}

	private void initGUI() {
		setLayout(new BorderLayout()); 
		setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.black, 2), "Viewer", TitledBorder.LEFT, TitledBorder.TOP));
		_scale = 1.0; 
		_showHelp = true;
		addKeyListener(new KeyListener() { 
			@Override 
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) { 
				case '-': _scale = _scale * 1.1; 
				break; 
				case '+': _scale = Math.max(1000.0, _scale / 1.1); 
				break; 
				case '=': autoScale(); 
				break; 
				case 'h': _showHelp = !_showHelp; 
				break; 
				default: 
				} repaint(); 
			}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyTyped(KeyEvent e) {}
		});
		
		addMouseListener(new MouseListener() { 
			@Override 
			public void mouseEntered(MouseEvent e) { 
				requestFocus(); 
			}

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}
		});
	}

	@Override 
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); 
		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// use ’gr’ to draw not ’g’
		// calculate the center 
		_centerX = 5 / 2;
		_centerY = 5 / 2;
		// TODO draw a cross at center ????????????????????
		gr.setColor(Color.RED);
		gr.drawLine(_centerX+502, _centerY+152, _centerX+500, _centerY+152);
		gr.drawLine(_centerX+502, _centerY+152, _centerX+502, _centerY+150);
		gr.drawLine(_centerX+502, _centerY+152, _centerX+504, _centerY+152);
		gr.drawLine(_centerX+502, _centerY+152, _centerX+502, _centerY+154);
		// TODO draw help if _showHelp is true
		if(_showHelp == true) {
			String h = "h: toggle help , +: zoom-in , -: zoom-out , =: fit";
			String sc = "Scalling ratio: 2.3333333333333334E8";
			gr.setColor(Color.RED);
			gr.drawString(h, 10, 25);
			gr.drawString(sc, 10, 40);	
		}
		// TODO draw bodies 
		for(int i = 0; i < _bodies.size(); i++) {
			int x = (_centerX + (int) (_bodies.get(i).getPosition().coordinate(0)/_scale));
			int y = (_centerY - (int) (_bodies.get(i).getPosition().coordinate(1)/_scale));
			String id = _bodies.get(i).getId();
			gr.setColor(Color.BLUE);
			gr.fillOval(x+500, y+150, 5, 5);
			gr.setColor(Color.BLACK);
			gr.drawString(id, x+498, y+148);
		
		}
	}
	// other private/protected methods 
	// ...
	private void autoScale() { 
		double max = 1.0;
		for (Body b : _bodies) { 
			Vector p = b.getPosition(); 
			for (int i = 0; i < p.dim(); i++) {
				max = Math.max(max, Math.abs(b.getPosition().coordinate(i))); 
			}
		}
		double a = Math.min(_centerX, _centerY);
		
		double size = Math.max(1.0, Math.min((double) getWidth(), (double) getHeight()));
		_scale = max > size ? 4.0 * max / size : 1.0;
	}

	// SimulatorObserver methods // ...
	
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		this._bodies = bodies;
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				autoScale();
				repaint();
			} });
	}
	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		this._bodies = bodies;
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				autoScale();
				repaint();
			} });
	}
	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		this._bodies = bodies;
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				autoScale();
				repaint();
			} });
	}
	@Override
	public void onAdvance(List<Body> bodies, double time) {
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				repaint();
			} });
	}
	@Override
	public void onDeltaTimeChanged(double dt) {

	}
	@Override
	public void onGravityLawChanged(String gLawsDesc) {
	
	}
	
}

