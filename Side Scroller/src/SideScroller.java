import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SideScroller extends JPanel implements Runnable, KeyListener {
	
	JFrame frame;
	BufferedImage aladdinImage, smallCity, bigCity, clouds, sunset, boxImage;
	
	HashMap<Integer, ArrayList<Block>> map = new HashMap<Integer, ArrayList<Block>>();
	
	Hero hero;
	
	boolean gameOn = true, right = false;
	int aladdinCount = 0, count = 0, smallCityCount = 0, bigCityCount = 0, cloudCount = 0, sunsetCount = 0, currCol = 0, heroStartCol = 0;
	Thread timer;
	
	BufferedImage[] aladdin = new BufferedImage[13];
	BufferedImage[] aladdinJumping = new BufferedImage[12];
	
	public SideScroller () {
		frame = new JFrame("Side Scroller");
		frame.add(this);
		
		int[][] locsAndDims = new int[][] {
			//x, y, width, height
			{337,  3, 23, 55},
			{  4, 64, 31, 53},
			{ 34, 64, 31, 53},
			{ 62, 64, 31, 51},
			{ 92, 64, 31, 51},
			{127, 64, 37, 51},
			{166, 64, 31, 51},
			{205, 64, 31, 51},
			{233, 64, 30, 51},
			{263, 61, 30, 56},
			{292, 61, 34, 56},
			{325, 60, 41, 56},
			{367, 60, 36, 56}
		};
		
		int[][] jumpLocsAndDims = new int[][] {
			//x, y, width, height
			{  4, 294, 31, 59},
			{ 35, 300, 29, 58},
			{ 62, 301, 38, 56},
			{100, 301, 36, 56},
			{140, 303, 41, 50},
			{183, 304, 49, 47},
			{230, 303, 42, 50},
			{278, 302, 37, 54},
			{321, 303, 33, 56},
			{  4, 363, 35, 64},
			{ 42, 365, 36, 63},
			{168, 361, 25, 53}
		};
		
		try {
			aladdinImage = ImageIO.read(new File("src\\Aladdin.png"));
			smallCity = ImageIO.read(new File("src\\smallCity.png"));
			bigCity = ImageIO.read(new File("src\\bigCity.png"));
			clouds = ImageIO.read(new File("src\\clouds.png"));
			sunset = ImageIO.read(new File("src\\sunset.png"));
			boxImage = ImageIO.read(new File("src\\box.png"));
			
			File file = new File("src\\map");
			BufferedReader input = new BufferedReader(new FileReader(file));
			String text;
			int row = 0;
			while ((text = input.readLine()) != null) {
				String[] pieces = text.split("");
				for (int i = 0; i < pieces.length; i++) {
					if (!pieces[i].equals("-")) {
						if (pieces[i].equals("H")) {
							hero = new Hero(50*i, 450, locsAndDims, jumpLocsAndDims);
							heroStartCol = i;
							currCol = heroStartCol;
						} else {
							ArrayList<Block> list = map.containsKey(i) ? map.get(i) : new ArrayList<Block>();
							list.add(new Block(50*i, 50*row+10, 50, 50, pieces[i]));
							map.put(i, list);
						}
					}
				}
				row++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		for (int i = 0; i < aladdin.length; i++) {
			aladdin[i] = aladdinImage.getSubimage(locsAndDims[i][0], locsAndDims[i][1], locsAndDims[i][2], locsAndDims[i][3]);
			aladdin[i] = resize(aladdin[i], aladdin[i].getWidth()*2, aladdin[i].getHeight()*2);
		}
		
		for (int i = 0; i < aladdinJumping.length; i++) {
			aladdinJumping[i] = aladdinImage.getSubimage(jumpLocsAndDims[i][0], jumpLocsAndDims[i][1], jumpLocsAndDims[i][2], jumpLocsAndDims[i][3]);
			aladdinJumping[i] = resize(aladdinJumping[i], aladdinJumping[i].getWidth()*2, aladdinJumping[i].getHeight()*2);
		}
		
		boxImage = resize(boxImage, 50, 50);
		
		frame.addKeyListener(this);
		
		
		frame.setSize(900, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		timer = new Thread(this);
		timer.start();
	}
	
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(sunset, sunsetCount+960, 0, this);
		g.drawImage(sunset, sunsetCount-960, 0, this);
		g.drawImage(clouds, cloudCount+960, 0, this);
		g.drawImage(clouds, cloudCount-960, 0, this);
		g.drawImage(bigCity, bigCityCount+960, 0, this);
		g.drawImage(bigCity, bigCityCount-960, 0, this);
		g.drawImage(smallCity, smallCityCount+960, 0, this);
		g.drawImage(smallCity, smallCityCount-960, 0, this);
		
		for (int i = currCol-4; i < currCol+17; i++) {
			try {
				ArrayList<Block> blocks = map.get(i);
				for (Block b : blocks) {
					g.drawImage(boxImage, b.getX(), b.getY(), this);
					if (b.highlight()) {
						g.setColor(Color.MAGENTA);
						((Graphics2D) g).draw(b.getCollisionBox());
					}
				}
			} catch (NullPointerException e) {}
		}
		
		if (hero.isJumping() || hero.isFalling()) {
			g.drawImage(aladdinJumping[hero.getJumpCount()], hero.getX(), hero.getY(), this);
		} else {
			g.drawImage(aladdin[hero.getAladdinCount()], hero.getX(), hero.getY(), this);
		}
		
		g.setColor(Color.BLACK);
		((Graphics2D) g).draw(hero.getCollisionBox());
		g.setColor(Color.GREEN);
		((Graphics2D) g).draw(hero.getCollisionBoxAbove());
		g.setColor(Color.BLUE);
		((Graphics2D) g).draw(hero.getCollisionBoxBelow());
	}
	
	public BufferedImage resize (BufferedImage image, int newWidth, int newHeight) {
		Image temp = image.getScaledInstance(newWidth,  newHeight,  Image.SCALE_SMOOTH);
		BufferedImage scaledVersion = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = scaledVersion.createGraphics();
		g2.drawImage(temp, 0,  0,  null);
		g2.dispose();
		return scaledVersion;
	}
	
	public void run () {
		while (true) {
			if (gameOn) {
				if (hero.isJumping()) {
					hero.updateJumping();
					boolean hitBlockAbove = collidingAbove();
					
					if (hitBlockAbove) {
						hero.setJumpCount(6);
						hero.setJumping(false);
						hero.setFalling(true);
						hero.updateFalling();
						
					}
					
				}
				else if (hero.isFalling()) {
					boolean hitBlock = collidingBelow();
					
					if (!hitBlock && hero.getY() < hero.getOriginalY()) {
						hero.updateFalling();
					} else {
						hero.setFalling(false);
						if (hitBlock) {
							hero.setOnBox(true);
						}
						hero.setJumpCount(6);
					}
				}
				
				else if (hero.isOnBox()) {
					boolean hitBlock = collidingBelow();
					
					if (!hitBlock) {
						hero.setOnBox(false);
						hero.setFalling(true);
					}
				}
				if (right) {
					boolean hitBlock = colliding();
					
					if (!hitBlock) {
						count++;
						if (count % 20 == 0) {
							hero.updateAladdinCount(hero.getAladdinCount()+1);
						}
						
						for (Integer i : map.keySet()) {
							for (Block b : map.get(i)) {
								b.updateX(-1);		
							}
						}
						
						currCol = heroStartCol + (int)(count/50.0);
						
						if (count % 5 == 0) {
							cloudCount --;
	
							if (cloudCount < -1920) {
								cloudCount += 1920;
							}
						}
						
						if (count % 3 == 0) {
							bigCityCount --;
	
							if (bigCityCount < -1920) {
								bigCityCount += 1920;
							}
						}
						
						smallCityCount -= 1;
						if (smallCityCount < -1920) {
							smallCityCount += 1920;
						}
					}
				}
				repaint();
			}
			try {
				timer.sleep(3);
			} catch (InterruptedException e) {}
		}
	}
	
	public boolean collidingBelow () {
		for (int c = currCol; c <= currCol+1; c++) {
			try {
				ArrayList<Block> blocks = map.get(c);
				for (Block b : blocks) {
					if (hero.getCollisionBoxBelow().intersects(b.getCollisionBox()) && hero.isAbove(b) && b.isTopBlock()) {
						return true;
					}
				}
			} catch (NullPointerException e) {}
		}
		return false;
	}
	
	public boolean collidingAbove () {
		for (int c = currCol; c <= currCol+1; c++) {
			try {
				ArrayList<Block> blocks = map.get(c);
				for (Block b : blocks) {
					if (hero.getCollisionBoxAbove().intersects(b.getCollisionBox())) {
						return true;
					}
				}
			} catch (NullPointerException e) {}
		}
		return false;
	}
	
	public boolean colliding () {
		for (int c = currCol; c <= currCol+1; c++) {
			try {
				ArrayList<Block> blocks = map.get(c);
				for (Block b : blocks) {
					if (hero.getCollisionBox().intersects(b.getCollisionBox())) {
						return true;
					}
				}
			} catch (NullPointerException e) {}
		}
		return false;
	}
	
	public static void main (String[] args) {
		SideScroller app = new SideScroller();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == 39) {
			right = true;
		}
		if (e.getKeyCode() == 32 || e.getKeyCode() == 38) {// up = 38, spacebar = 32
			if (!(hero.isJumping() || hero.isFalling())) {
				hero.setJumping(true);
				hero.setJumpCount(0);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 39) {
			right = false;
			hero.updateAladdinCount(0);
			aladdinCount++;
		}
	}
}
