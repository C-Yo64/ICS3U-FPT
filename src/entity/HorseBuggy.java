package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import main.CollisionHandler;
import main.GamePanel;

/*********************************************************************************************************
 * 
 * I made this class before implementing the camera to follow the character
 * Currently this entity will not work properly, for now just leave this alone and do not add
 * a horse buggy to your game
 *
 *********************************************************************************************************/

public class HorseBuggy extends Entity {
	
	GamePanel gp;
	CollisionHandler colH;
	BufferedImage leftBuggy1, leftBuggy2, upBuggy1, upBuggy2, rightBuggy1, rightBuggy2, downBuggy1, downBuggy2;
	BufferedImage buggy;
	
	final int dirChangeBase = 200;
	final int stuckBase = 5;
	int stuck = stuckBase;
	int dirChange = dirChangeBase;
	int dir = 1;
	int buggyPosX, buggyPosY;
	
	public HorseBuggy(GamePanel gp, CollisionHandler colH) {
		this.gp = gp;
		this.colH = colH;
		initialize();
	}
	
	private void initialize() {
		worldX = gp.tileSize * 999;
		worldY = gp.tileSize * 999;
		size = gp.tileSize;
		buggyPosX = worldX + size;
		buggyPosY = worldY;		
		sizeX = size * 2;
		sizeY = size;
		speed = 1;
		solidX = worldX + (size / 4);
        solidY = worldY + (size / 4) - speed;
        solidSizeX = sizeX - (gp.tileSize / 3);
        solidSizeY = sizeY - (gp.tileSize / 3);
		direction = "up";
		
		
		getPlayerImage();

		
	}

	private void getPlayerImage() {
    	
    	int frameWidth = 16;
    	int frameHeight = 16;
    	
    	try {
			spriteSheet = ImageIO.read(getClass().getResourceAsStream("/sprites/horseboatsheet.png"));
			
			leftBuggy1 = spriteSheet.getSubimage(frameWidth * 12, frameHeight * 3, frameWidth, frameHeight);
			leftBuggy2 = spriteSheet.getSubimage(frameWidth * 13, frameHeight * 3, frameWidth, frameHeight);
			left1 = spriteSheet.getSubimage(frameWidth * 4, frameHeight * 3, frameWidth, frameHeight);
			left2 = spriteSheet.getSubimage(frameWidth * 5, frameHeight * 3, frameWidth, frameHeight);

			upBuggy1 = spriteSheet.getSubimage(frameWidth * 12, frameHeight * 0, frameWidth, frameHeight);
			upBuggy2 = spriteSheet.getSubimage(frameWidth * 13, frameHeight * 0, frameWidth, frameHeight);
			up1 = spriteSheet.getSubimage(frameWidth * 4, frameHeight * 0, frameWidth, frameHeight);
			up2 = spriteSheet.getSubimage(frameWidth * 5, frameHeight * 0, frameWidth, frameHeight);
			
			rightBuggy1 = spriteSheet.getSubimage(frameWidth * 12, frameHeight * 1, frameWidth, frameHeight);
			rightBuggy2 = spriteSheet.getSubimage(frameWidth * 13, frameHeight * 1, frameWidth, frameHeight);
			right1 = spriteSheet.getSubimage(frameWidth * 4, frameHeight * 1, frameWidth, frameHeight);
			right2 = spriteSheet.getSubimage(frameWidth * 5, frameHeight * 1, frameWidth, frameHeight);

			downBuggy1 = spriteSheet.getSubimage(frameWidth * 12, frameHeight * 2, frameWidth, frameHeight);
			downBuggy2 = spriteSheet.getSubimage(frameWidth * 13, frameHeight * 2, frameWidth, frameHeight);
			down1 = spriteSheet.getSubimage(frameWidth * 4, frameHeight * 2, frameWidth, frameHeight);
			down2 = spriteSheet.getSubimage(frameWidth * 5, frameHeight * 2, frameWidth, frameHeight);
			
    	}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		
		int newX = worldX;
        int newY = worldY;
        int newBX = buggyPosX;
        int newBY = buggyPosY;

        
        if(dirChange <= 0 || stuck <=0) {
        	Random random = new Random();
            dir = random.nextInt(1, 5);
        	dirChange = dirChangeBase + 1;
        	stuck = stuckBase;
        }
        dirChange--;
        
		if (dir == 1) {
        	direction = "left";
            newX -= speed;
            if(dirChange == dirChangeBase) {
            	newBX = newX + gp.tileSize;
            	newBY = newY;
            	buggyPosX = newBX;
            	buggyPosY = newBY;
            }
            newBX -= speed;
            solidX = newX + (size / 6);
            solidY = newY + (size / 6) - speed;
            solidSizeX = (2 * gp.tileSize) - (gp.tileSize / 3);
            solidSizeY = gp.tileSize - (gp.tileSize / 3);
            
        }
        if (dir == 2) {
        	direction = "right";
            newX += speed;
            if(dirChange == dirChangeBase) {
            	newBX = worldX - gp.tileSize;
            	newBY = worldY;
            	buggyPosX = newBX;
            	buggyPosY = newBY;
            }
            newBX += speed;
            solidX = newX + (size / 6) - gp.tileSize;
            solidY = newY + (size / 6) - speed;
            solidSizeX = (2 * gp.tileSize) - (gp.tileSize / 3);
            solidSizeY = gp.tileSize - (gp.tileSize / 3);

        }
        if (dir == 3) {
        	direction = "up";
            newY -= speed;
            if(dirChange == dirChangeBase) {
            	newBX = worldX;
            	newBY = worldY + gp.tileSize;
            	buggyPosX = newBX;
            	buggyPosY = newBY;
            }
            newBY -= speed;
            solidX = newX + (size / 6);
            solidY = newY + (size / 6) - speed;
            solidSizeX = gp.tileSize - (gp.tileSize / 3);
            solidSizeY = (2 * gp.tileSize) - (gp.tileSize / 3);

        }
        if (dir == 4) {
        	direction = "down";
            newY += speed;
            if(dirChange == dirChangeBase) {
            	newBX = worldX;
            	newBY = worldY - gp.tileSize;
            	buggyPosX = newBX;
            	buggyPosY = newBY;
            }
            newBY += speed;
            solidX = newX + (size / 6);
            solidY = newY - gp.tileSize + (size / 6) + speed;
            solidSizeX = gp.tileSize - (gp.tileSize / 3);
            solidSizeY = (2 * gp.tileSize) - (gp.tileSize / 3);

        }
        
        spriteCounter++;
		if(spriteCounter > 8) {
			if(spriteNum == 1) {
				spriteNum = 2;
			}
			else if(spriteNum == 2) {
				spriteNum = 1;
			}
			
			spriteCounter = 0;
		}
		
		if (!colH.isColliding(newX, newY, size, false) && !colH.isEntityColliding(gp.player, this)) {
            worldX = newX;
            worldY = newY;
            buggyPosX = newBX;
            buggyPosY = newBY;
            
            
        }
		else if(colH.isColliding(newX, newY, size, false)) {
			stuck--;
		}
		else if(colH.isEntityColliding(gp.player, this)){
			
		}
		
	}
	
	public void draw(Graphics2D g2) {
		
		switch(direction) {
		case "up":
			if(spriteNum == 1) {
				image = up1;
				buggy = upBuggy1;
			}
			if(spriteNum == 2) {
				image = up2;
				buggy = upBuggy2;
			}
			break;
			
		case "down":
			if(spriteNum == 1) {
				image = down1;
				buggy = downBuggy1;

			}
			if(spriteNum == 2) {
				image = down2;
				buggy = downBuggy2;
			}
			break;
			
		case "left":
			if(spriteNum == 1) {
				image = left1;
				buggy = leftBuggy1;
			}
			if(spriteNum == 2) {
				image = left2;
				buggy = leftBuggy2;
			}
			break;
			
		case "right":
			if(spriteNum == 1) {
				image = right1;
				buggy = rightBuggy1;
			}
			if(spriteNum == 2) {
				image = right2;
				buggy = rightBuggy2;
			}
			break;
    	}
		
		
		// screen position for Hobnob will work with the following changes to drawing
		// though there are still some issues with Hobnob's code (mainly collision)
		
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        int buggyScreenX = buggyPosX - gp.player.worldX + gp.player.screenX;
        int buggyScreenY = buggyPosY - gp.player.worldY + gp.player.screenY;

		g2.drawImage(image, screenX, screenY, size, size, null);
		g2.drawImage(buggy, buggyScreenX, buggyScreenY, size, size, null);

	}
}
