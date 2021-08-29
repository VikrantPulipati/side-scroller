import java.awt.geom.Rectangle2D;

public class Hero {
	
	int heroX, heroY, aladdinCount = 0, jumpCount = 0, count = 0, originalY = 0;
	int[][] locsAndDims;
	int[][] jumpLocsAndDims;
	int currPos = 0;
	boolean jumping = false, falling = false, onBox = false;
	
	public Hero (int heroX, int heroY, int[][] locsAndDims, int[][] jumpLocsAndDims) {
		this.heroX = heroX;
		this.heroY = heroY;
		this.originalY = this.heroY;
		this.locsAndDims = locsAndDims;
		this.jumpLocsAndDims = jumpLocsAndDims;
	}
	
	public int getX () {
		return this.heroX;
	}
	
	public int getY () {
		return this.heroY;
	}
	
	public int getAladdinCount () {
		return this.aladdinCount;
	}
	
	public boolean isJumping () {
		return this.jumping;
	}
	
	public void setJumping (boolean b) {
		this.jumping = b;
	}
	
	public boolean isFalling () {
		return this.falling;
	}
	
	public void setFalling (boolean b) {
		this.falling = b;
	}
	
	public int getJumpCount () {
		return this.jumpCount;
	}
	
	public void updateJumping () {
		count++;
		heroY--;
		
		if (count%25 == 0) {
			jumpCount++;
			if (jumpCount == 6) {
				this.setJumping(false);
				this.setFalling(true);
			}
		}
	}
	
	public void updateFalling () {
		heroY++;
		count++;
		if (count % 25 == 0) {
			jumpCount++;
			if (jumpCount == 12) {
				jumpCount = 6;
			}
		}
	}
	
	public int getOriginalY () {
		return this.originalY;
	}
	
	public void setJumpCount (int x) {
		this.jumpCount = x;
	}
	
	public void updateAladdinCount (int count) {
		this.aladdinCount = count;
		
		if (aladdinCount  == 13) {
			aladdinCount = 1;
		}
	}
	
	public Rectangle2D getCollisionBox () {
		/*int w = locsAndDims[aladdinCount][2]*2;
		int h = locsAndDims[0][3]*2;
		
		int wJump = jumpLocsAndDims[jumpCount][2]*2;
		int hJump = jumpLocsAndDims[0][3]*2;
		
		if (isJumping() || isFalling()) {
			return new Rectangle2D.Double(this.getX(), this.getY(), wJump, hJump);
		} else {
			return new Rectangle2D.Double(this.getX(), this.getY(), w, h);
		}*/
		return new Rectangle2D.Double(this.heroX + locsAndDims[aladdinCount][2]*2, this.heroY+1, 1, locsAndDims[aladdinCount][3]*2-2);
	}
	
	public Rectangle2D getCollisionBoxBelow () {
		
		/*int w = locsAndDims[aladdinCount][2]*2;
		int h = locsAndDims[aladdinCount][0]*2;
		
		int wJump = jumpLocsAndDims[jumpCount][2]*2;
		int hJump = jumpLocsAndDims[0][3]*2;
		
		if (isJumping() || isFalling()) {
			return new Rectangle2D.Double(this.getX(), this.getY()+1, wJump, hJump);
		} else {
			return new Rectangle2D.Double(this.getX(), this.getY()+1, w, h);
		}*/
		return new Rectangle2D.Double(this.heroX+1, this.heroY + locsAndDims[0][3]*2+1, locsAndDims[0][2]*2-2, 1);
	}
	
	public Rectangle2D getCollisionBoxAbove () {
		int w = locsAndDims[aladdinCount][2]*2+2;
		int h = locsAndDims[aladdinCount][0]*2;
		
		int wJump = jumpLocsAndDims[jumpCount][2]*2-4;
		int hJump = jumpLocsAndDims[0][3]*2;
		
		/*if (isJumping() || isFalling()) {
			return new Rectangle2D.Double(this.getX(), this.getY()-1, wJump, 1);
		} else {
			return new Rectangle2D.Double(this.getX(), this.getY()-1, w, 1);
		}*/
		return new Rectangle2D.Double(this.heroX+1, this.heroY, locsAndDims[0][2]*2-2, 1);
	}
	
	public int getHeight () {
		return locsAndDims[0][3]*2;
	}
	
	public boolean isOnBox () {
		return this.onBox;
	}
	
	public void setOnBox (boolean onBox) {
		this.onBox = onBox;
	}
	
	public boolean isAbove (Block b) {
		//System.out.println(this.getHeight());
		int y = this.getY() + this.getHeight() -1;
		int blockY = b.getY();
		
		//System.out.println(y + " " + blockY);
		
		return this.getY()+this.getHeight()-1 < b.getY();
	}
	
	public boolean sameLevel (Block b) {
		return this.getY()+this.getHeight() == b.getY()+50;
	}
}
