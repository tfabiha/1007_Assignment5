import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * @author Tabassum Fabiha | tf2478
 * 
 * This class holds all the information needed to make each individual throw. It holds the
 * type of throw it is, the font and size to use, the x, y positions, and the velocities.
 * It is able to draw to the applet when asked. It is also be able to compare itself to 
 * other throws to see if there is an intersection with another throw/black hole or to see 
 * if it would be able to survive in the case of an intersection with another Throw.
 */
public class Throw {

	/**
	 * @param appletWidth width of the applet
	 * @param appletHeight height of the applet
	 * @param throwType the type of throw being done
	 * @param throwFont the font to use for this throw
	 * @param throwRectangle the rectangle that contains this throw
	 * @param throwX the x coordinate of this throw
	 * @param throwY the y coordinate of this throw
	 * @param velX the x velocity of this throw
	 * @param velY the y velocity of this throw
	 */
	private Throw(int appletWidth, int appletHeight, RPSKL throwType, 
				  Font throwFont, Rectangle2D throwRectangle,
				  int throwX, int throwY, int velX, int velY) {
		APPLETWIDTH = appletWidth;
		APPLETHEIGHT = appletHeight;
		THROWTYPE = throwType;
		
		this.throwFont = throwFont;
		this.throwRectangle = throwRectangle;
		
		this.throwX = throwX;
		this.throwY = throwY;
		this.velX = velX;
		this.velY = velY;
	}
	
	/**
	 * @param g Graphics
	 * 
	 * Paints the throw to the applet screen
	 */
	public void paintThrow(Graphics g) {
		g.setFont(throwFont);
		g.drawString(THROWTYPE.toString(), throwX, throwY);
	}
	
	/**
	 * Updates the x and y coordinates and the rectangle that the throw is contained in
	 * according to the way its just moved.
	 */
	public void update() {
		// move to the left one pixel
		throwX += velX;
		throwY += velY;
		// check for wrap-around
		// note that the second getWidth() is from Applet!
		if (throwX + throwRectangle.getWidth() < 0)
			throwX = APPLETWIDTH;
		else if (throwX > APPLETWIDTH)
			throwX = 0 - (int) throwRectangle.getWidth();
		
		if (throwY + throwRectangle.getHeight() < 0)
			throwY = APPLETHEIGHT;
		else if (throwY > APPLETHEIGHT)
			throwY = 0 - (int) throwRectangle.getHeight();
		
		throwRectangle.setFrame(throwX, throwY, throwRectangle.getWidth(), throwRectangle.getHeight());
	}
	
	/**
	 * @param other the Throw to compare to
	 * @return if there has been an intersection between this object and other
	 */
	public boolean intersects(Throw other) {
		return throwRectangle.intersects( other.throwRectangle );
	}
	
	/**
	 * @param hole a black hole
	 * @return if there has been an intersection between this object and the black hole
	 */
	public boolean intersects(Rectangle hole) {
		return throwRectangle.intersects( hole );
	}
	
	/**
	 * @param other the Throw to compare to
	 * @return if this object survives its interaction with the other Throw
	 * 
	 * This method uses the algorithm provided by JRK on 9/26.
	 */
	public boolean survives(Throw other) {
		int diff = THROWTYPE.compareTo(other.THROWTYPE);
		int evenDiag = (diff % 2 == 0) ? 1 : -1; //ternary: Zipf! 
		int upperTri = (diff < 0) ? 1 : -1;
		if (diff == 0 || evenDiag * upperTri > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * @author Tabassum Fabiha
	 * 
	 * Inner class used to build a Throw.
	 */
	public static class ThrowBuilder {
		public ThrowBuilder(FontRenderContext context, int width, int height) {
			this.context = context;
			this.APPLETWIDTH = width;
			this.APPLETHEIGHT = height;
		}
		
		public ThrowBuilder setThrowType(RPSKL throwType) {
			this.throwType = throwType;
			return this;
		}
		
		public ThrowBuilder setThrowFont(Font throwFont) {
			this.throwFont = throwFont;
			return this;
		}
		
		public ThrowBuilder setPosition(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}
		
		public ThrowBuilder setVelocity(int velX, int velY) {
			this.velX = velX;
			this.velY = velY;
			return this;
		}
		
		/**
		 * @return a new Throw with the values provided
		 * 
		 * Creates a new throwRectangle with the dimensions of the Throw and sets the 
		 * throwX and throwY appropriately.
		 */
		public Throw done() {
			String htmlThrowName = throwType.toString();	
			throwRectangle = throwFont.getStringBounds(htmlThrowName, context);
			
			throwY = y;
			if (throwY < (int) -throwRectangle.getY())
				throwY = (int) -throwRectangle.getY();
			else if (throwY > APPLETHEIGHT)
				throwY = APPLETHEIGHT;
			
			throwX = x;
			if (throwX < 0)
				throwX = 0;
			else if (throwX > APPLETWIDTH)
				throwX = APPLETWIDTH;
			
			return new Throw(APPLETWIDTH, APPLETHEIGHT, throwType, 
					  		 throwFont, throwRectangle,
					  		 throwX, throwY, velX, velY);
		}
		
		private FontRenderContext context;
		private int x;
		private int y;
		
		private final int APPLETWIDTH;
		private final int APPLETHEIGHT;
		private RPSKL throwType;
		private Font throwFont;
		private Rectangle2D throwRectangle;
		private int throwX;
		private int throwY;
		private int velX;
		private int velY;
		
	}
	
	private final int APPLETWIDTH;
	private final int APPLETHEIGHT;
	private final RPSKL THROWTYPE;
	private Font throwFont;
	private Rectangle2D throwRectangle;
	private int throwX;
	private int throwY;
	private int velX;
	private int velY;
}
