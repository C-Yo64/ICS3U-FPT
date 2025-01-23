package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.CollisionHandler;
import main.GamePanel;

public class Projectile extends Entity {
	
	GamePanel gp;
	CollisionHandler colH;
	final int maxLife = 120;
	public int life = maxLife;
	//I went with static variables here so we don't need to load
	//the images every time we create a new projectile.
	//this is because the image file I am using is really large
	//which was causing significant lag in the game.
	private static BufferedImage sheet, im1, im2, im3, im4;

	public Projectile(GamePanel gp, CollisionHandler colH, int spawnX, int spawnY, String direction) {
		this.gp = gp;
		this.colH = colH;
		setDefaultValues(spawnX, spawnY, direction);
	}
	
	private void setDefaultValues(int spawnX, int spawnY, String direction) {
		worldX = spawnX;
		worldY = spawnY;
		this.direction = direction;
		size = gp.tileSize;
		speed = 5;
		solidSize = size;
		solidSizeX = solidSize;
		solidSizeY = solidSize;
		
		//loadImages();
	}

	//originally I had a regular method to load the images everytime
	//that a projectile was made, but this caused lag, so we don't need this
//	private void loadImages() {
//		int frameWidth = 512;
//    	int frameHeight = 384;
//    	
//    	if(im1 == null) {
//	    	try {
//				spriteSheet = ImageIO.read(getClass().getResourceAsStream("/sprites/fireball.png"));
//	
//				im1 = spriteSheet.getSubimage(frameWidth * 0, frameHeight * 5, frameWidth, frameHeight);
//				im2 = spriteSheet.getSubimage(frameWidth * 1, frameHeight * 5, frameWidth, frameHeight);
//				im3 = spriteSheet.getSubimage(frameWidth * 2, frameHeight * 5, frameWidth, frameHeight);
//				im4 = spriteSheet.getSubimage(frameWidth * 3, frameHeight * 5, frameWidth, frameHeight);
//				
//			}catch(IOException e) {
//				e.printStackTrace();
//			}
//    	}
//	}
	
	//instead I create images as static variables and using a static method
	//we can preload the images from the GamePanel and they will be saved to
	//those static variables and thus will only be loaded when the game starts
	//This is a method done for very large games, and why you get a loading screen
	//when big games startup, but notably, these games will have little if any lag
	//when we are playing. That is same goal with these static methods
	//The following methods allow up to pre-load the projectiles so it does not cause lag
	public static void preloadImages(GamePanel gp) {
	    if (im1 == null) {
	        int frameWidth = 512;
	        int frameHeight = 384;
	        int tileSize = gp.tileSize;

	        try {
	            BufferedImage spriteSheet = ImageIO.read(Projectile.class.getResourceAsStream("/sprites/fireball.png"));
	            im1 = scaleImage(spriteSheet.getSubimage(frameWidth * 0, frameHeight * 5, frameWidth, frameHeight), tileSize, tileSize);
	            im2 = scaleImage(spriteSheet.getSubimage(frameWidth * 1, frameHeight * 5, frameWidth, frameHeight), tileSize, tileSize);
	            im3 = scaleImage(spriteSheet.getSubimage(frameWidth * 2, frameHeight * 5, frameWidth, frameHeight), tileSize, tileSize);
	            im4 = scaleImage(spriteSheet.getSubimage(frameWidth * 3, frameHeight * 5, frameWidth, frameHeight), tileSize, tileSize);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	private static BufferedImage scaleImage(BufferedImage original, int width, int height) {
	    BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = scaledImage.createGraphics();
	    g2d.drawImage(original, 0, 0, width, height, null);
	    g2d.dispose();
	    return scaledImage;
	}

	
	public void update() {
		life--;
		switch(direction) {
		case "up":
			worldY -= speed;
			break;
		case "down":
			worldY += speed;
			break;
		case "left":
			worldX -= speed;
			break;
		case "right":
			worldX += speed;
			break;
		}
		solidX = worldX;
		solidY = worldY;
		
		// Check for collisions with NPCs
		for (int i =0; i < gp.npcs.size(); i++) {
		    if (colH.isEntityColliding(this, gp.npcs.get(i))) {
		    	int destroyX = gp.npcs.get(i).worldX;
		    	int destroyY = gp.npcs.get(i).worldY;
		        gp.npcs.remove(gp.npcs.get(i));
		        destroySequence(destroyX, destroyY);
		    }
		}
		
		spriteCounter++;
	    if (spriteCounter > 8) {
	        if (spriteNum == 1) {
	            spriteNum = 2;
	        } else if (spriteNum == 2) {
	            spriteNum = 3;
	        } else if (spriteNum == 3) {
	            spriteNum = 4;
	        } else if (spriteNum == 4) {
	            spriteNum = 1;
	        }
	        spriteCounter = 0;
	    }
		
	}
	
	private void destroySequence(int destroyX, int destroyY) {
		 Explosion explosion = new Explosion(destroyX, destroyY, gp.tileSize);
		   gp.addExplosion(explosion);
	}

	public void draw(Graphics2D g2) {
		
		if(spriteNum == 1) {
			image = im1;
		}
		if(spriteNum == 2) {
			image = im2;
		}
		if(spriteNum == 3) {
			image = im3;
		}
		if(spriteNum == 4) {
			image = im4;
		}

		int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        
        if(worldX > gp.player.worldX - gp.player.screenX - gp.tileSize &&
        		worldX < gp.player.worldX + gp.player.screenX + gp.tileSize &&
        		worldY > gp.player.worldY - gp.player.screenX - gp.tileSize &&
        		worldY < gp.player.worldY + gp.player.screenX + gp.tileSize) {
        	g2.drawImage(image, screenX, screenY, size, size, null);
        	//g2.setColor(Color.red);
        	//g2.fillRect(screenX, screenY, size, size);
        }
	}
}
