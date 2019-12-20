import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Timer;

/**
 * 
 * @author Tabassum Fabiha | tf2478 --> Stolen from JRK
 * 
 * needs an html file (real or simulated) that looks like:
 * 
 * <applet code="Playground.class" width="xxx" height="yyy">
 * <param name="parameter1" value="value1"/>
 * <param name="parameter2" value="value2"/>
 * < . . etc . . . />
 * </applet>
 *
 * This class creates an applet that shows off the plays in RPSKL interacting with each
 * other if they were aimlessly moving in one direction in a box that wraps around on all 
 * four sides. 
 * 
 * Corner cases:
 * 		- If a rock, paper, and scissor simultaneously intersect then they will all die
 * 		- If two rocks intersect a paper then the paper dies
 * 
 * Questions:
 * 		- The window can handle at least 31 Strings.
 * 		- Because the assignment says that the Strings move in increments of 1, no the
 * 		  Strings do not hop over each other.
 * 		- All Strings can be stationary, but in that case they do nothing. There doesn't
 * 		  need to be a way to alert the user if they've made all their Strings stationary.
 * 		- If two Strings have the exact same content, location, and movement then they do
 * 		  exactly the same thing, so of course you will only "see" one. 
 * 		- The display keeps running even if there hasn't been any collisions in a while.
 * 
 * Black holes have also been implemented. They appear every time the user clicks down on
 * the applet screen and the intersection of any of the throws with a black hole results in
 * the termination of the throw. There can be multiple black holes  
 * 
 * There is a video named "RPSKL.mov" that is in the same directory as this class that 
 * shows a demo of this.
 */
public class Playground extends Applet {

	/**
	 * Setup
	 * 
	 * Pulls all the parameters from the html file and creates the number of throws given.
	 * Also sets up the timer actionListener to update after a certain amount of time. 
	 * This actionListener updates all the Throws first then sees if there has been any
	 * interaction between any of the throws or with any of the throws and a black hole
	 * and removes the throws if necessary. It also paints everything to the applet screen
	 * once finished with the updates.
	 */
	public void init() {
		blackHoles = new ArrayList<Rectangle>();
		thrower = new ArrayList<Throw>();
		int numThrows = Integer.parseInt(getParameter("numthrows"));
		
		// idioms to establish Graphics environment
		Graphics2D g2D = (Graphics2D) getGraphics();
		// idioms to get information about the applet "paintbrushes"
		FontRenderContext throwContext = g2D.getFontRenderContext();
		
		for (int i = 0; i < numThrows; i++) {
			// note that htmlThrow has to be available for paint()
			RPSKL throwType;
			String htmlThrowName = getParameter("throw" + i);
			if (htmlThrowName.equals("rock"))
				throwType = RPSKL.ROCK;
			else if (htmlThrowName.equals("paper"))
				throwType = RPSKL.PAPER;
			else if (htmlThrowName.equals("scissors"))
				throwType = RPSKL.SCISSORS;
			else if (htmlThrowName.equals("spock"))
				throwType = RPSKL.SPOCK;
			else
				throwType = RPSKL.LIZARD;
	
			// info necessary for making strings look nice
			// note that throwFont has to be available for paint()
			// but htmlFontName and htmlFontSize are only used locally
			String htmlFontName = getParameter("fontname" + i);
			int htmlFontSize = Integer.parseInt(getParameter("fontsize" + i));
			Font throwFont = new Font(htmlFontName, Font.BOLD, htmlFontSize);
			
			// note throwY has to be available for paint()
			// note that getY() returns a *double*, so it has to be cast to an int
			// but getSTringBounds measures the height from the *lower* left,
			// so its height is negative, since Y going *up* is negative
			// yes, *three* violations of Principle of Least Surprise in one line!
			int throwY = Integer.parseInt(getParameter("y-coor" + i));
			
			// throwX starts on the right side of the component
			int throwX = Integer.parseInt(getParameter("x-coor" + i));
			
			int velX = Integer.parseInt(getParameter("x-vel" + i));
			int velY = Integer.parseInt(getParameter("y-vel" + i));
	
			Throw temp = new Throw
						.ThrowBuilder(throwContext, getWidth(), getHeight())
						.setThrowType(throwType)
						.setThrowFont(throwFont)
						.setPosition(throwX, throwY)
						.setVelocity(velX, velY)
						.done();
			thrower.add(temp);
		}
		
		// info for the timer
		// note that htmlDelay has to be available for the listener
		htmlDelay = Integer.parseInt(getParameter("delay"));
		// usual Timer idiom
		appletTimer = new Timer(htmlDelay, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean[] survived = new boolean[thrower.size()];
				
				for (int i = 0; i < thrower.size(); i++) {
					thrower.get(i).update();
					survived[i] = true;
				}
				
				for (int i = 0; i < thrower.size(); i++) {
					for (int j = 0; j < thrower.size(); j++) {
						if ( i != j && thrower.get(i).intersects( thrower.get(j)) ) {
							survived[i] = survived[i] && thrower.get(i).survives( thrower.get(j) );
						}
					}
					
					for (int j = 0; j < blackHoles.size(); j++) {
						if ( thrower.get(i).intersects( blackHoles.get(j) ) ) {
							survived[i] = false;
						}
					}
				}
				
				for (int i = survived.length - 1; i >= 0; i--) {
					if (!survived[i]) {
						thrower.remove(i);
					}
				}
				
				repaint();
			}
		});
		
		addMouseListener( new Playground.MouseHandler() );
	}

	/**
	 * Starts the timer
	 */
	public void start() {
		appletTimer.start();
	}

	/**
	 * Paints each Throw and black hole to the applet screen.
	 */
	public void paint(Graphics g) {
		for (int i = 0; i < thrower.size(); i++) {
			thrower.get(i).paintThrow(g);
		}
		
		for (int i = 0; i < blackHoles.size(); i++) {
			Rectangle rect = blackHoles.get(i);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
		}
	}

	/**
	 * Stops the timer
	 */
	public void stop() {
		appletTimer.stop();
	}

	/**
	 * Destroys the applet
	 */
	public void destroy() {
	}
	
	/**
	 * @author Tabassum Fabiha | tf2478
	 * This class creates a new black hole every time the user clicks down on the applet.
	 */
	private class MouseHandler extends MouseAdapter {
		/**
		 * Upon mouseclick creates a new black hole
		 */
		public void mousePressed(MouseEvent e) {
				blackHoles.add( new Rectangle(e.getX() - 5, e.getY() - 5, 10, 10) );
			}
	}

	private int htmlDelay;
	// variables associated with the throw String
	// timer stuff
	private Timer appletTimer;
	
	private ArrayList<Throw> thrower;
	private ArrayList<Rectangle> blackHoles;
}
