import java.awt.geom.Rectangle2D;

public class Block {
	
	int blockX, blockY, width, height;
	String type;
	boolean highlight = false;
	
	public Block (int x, int y, int w, int h, String type) {
		this.blockX = x;
		this.blockY = y;
		this.width = w;
		this.height = h;
		this.type = type;
	}
	
	public void toggleHighlight () {
		highlight = !highlight;
	}
	
	public boolean highlight () {
		return this.highlight;
	}
	
	public boolean isTopBlock () {
		return type.equals("B") ? true : false;
	}
	
	public int getX () {
		return this.blockX;
	}
	
	public void updateX (int x) {
		this.blockX += x;
		if (blockX <= -50) {
			blockX += 7050;
		}
	}
	
	public int getY () {
		return this.blockY;
	}
	
	public int getWidth () {
		return this.width;
	}
	
	public int getHeight () {
		return this.height;
	}
	
	public Rectangle2D getCollisionBox () {
		return new Rectangle2D.Double(blockX, blockY, width, height);
	}
	
}
