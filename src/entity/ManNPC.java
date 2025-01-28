package entity;

import java.awt.Graphics2D;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.CollisionHandler;
import main.GamePanel;

public class ManNPC extends Entity {
	
	GamePanel gp;
	CollisionHandler colH;

	public ManNPC(GamePanel gp, CollisionHandler colH, int spawnX, int spawnY) {
		this.gp = gp;
		this.colH = colH;
		setDefaultValues(spawnX * gp.tileSize, spawnY * gp.tileSize);
	}
	
	private void setDefaultValues(int spawnX, int spawnY) {
		worldX = spawnX;
		worldY = spawnY;
		direction = "down";
		size = gp.tileSize;
		speed = 1;
		solidSize = size /2;
		solidSizeX = solidSize;
		solidSizeY = solidSize;
		loadImages();
	}
	
	private void loadImages() {
		int frameWidth = 16;
    	int frameHeight = 16;
    	
    	try {
			spriteSheet = ImageIO.read(getClass().getResourceAsStream("/sprites/characters.png"));

			down1 = spriteSheet.getSubimage(frameWidth * 3, 0, frameWidth, frameHeight);
			down2 = spriteSheet.getSubimage(frameWidth * 4, 0, frameWidth, frameHeight);
			down3 = spriteSheet.getSubimage(frameWidth * 5, 0, frameWidth, frameHeight);
			down4 = spriteSheet.getSubimage(frameWidth * 4, 0, frameWidth, frameHeight);
			
			left1 = spriteSheet.getSubimage(frameWidth * 3, frameHeight, frameWidth, frameHeight);
			left2 = spriteSheet.getSubimage(frameWidth * 4, frameHeight, frameWidth, frameHeight);
			left3 = spriteSheet.getSubimage(frameWidth * 5, frameHeight, frameWidth, frameHeight);
			left4 = spriteSheet.getSubimage(frameWidth * 4, frameHeight, frameWidth, frameHeight);
			
			right1 = spriteSheet.getSubimage(frameWidth * 3, frameHeight * 2, frameWidth, frameHeight);
			right2 = spriteSheet.getSubimage(frameWidth * 4, frameHeight * 2, frameWidth, frameHeight);
			right3 = spriteSheet.getSubimage(frameWidth * 5, frameHeight * 2, frameWidth, frameHeight);
			right4 = spriteSheet.getSubimage(frameWidth * 4, frameHeight * 2, frameWidth, frameHeight);
			
			up1 = spriteSheet.getSubimage(frameWidth * 3, frameHeight * 3, frameWidth, frameHeight);
			up2 = spriteSheet.getSubimage(frameWidth * 4, frameHeight * 3, frameWidth, frameHeight);
			up3 = spriteSheet.getSubimage(frameWidth * 5, frameHeight * 3, frameWidth, frameHeight);
			up4 = spriteSheet.getSubimage(frameWidth * 4, frameHeight * 3, frameWidth, frameHeight);

		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		
		int newX = worldX;
	    int newY = worldY;
	    
	    npcDirectionChange();
		
		
		switch(direction) {
		case "up":
			newY -= speed;
			break;
		case "down":
			newY += speed;
			break;
		case "left":
			newX -= speed;
			break;
		case "right":
			newX += speed;
			break;
		}
		solidX = newX + (size / 4);
	    solidY = newY + (size / 2) - speed;
	    
	    boolean colidesWithNPC = false;
        for(Entity npc : gp.npcs) {
        	if(colH.isEntityColliding(this, npc) && npc != this) {
        		colidesWithNPC = true;
        	}
        }
        
		if(!colH.isColliding(solidX, solidY, solidSize, false) && !colH.isEntityColliding(this, gp.player) && !colidesWithNPC) {
			worldX = newX;
			worldY = newY;
		}
		
		incrementSpriteNumber();
	}
	
	public void draw(Graphics2D g2) {
		loadCurrentSprite();
		
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        
        if(worldX > gp.player.worldX - gp.player.screenX - gp.tileSize &&
        		worldX < gp.player.worldX + gp.player.screenX + gp.tileSize &&
        		worldY > gp.player.worldY - gp.player.screenX - gp.tileSize &&
        		worldY < gp.player.worldY + gp.player.screenX + gp.tileSize) {
        	g2.drawImage(image, screenX, screenY, size, size, null);
        }
	}
}
