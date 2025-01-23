package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;

public class Explosion {
	private static BufferedImage[] explosionFrames;
    private int x, y;
    private int size;
    private int life = 24; // Number of frames the explosion lasts
    private int frameCounter = 0; // Tracks animation progress
    private int currentFrame = 0;

    public Explosion(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void update() {
        life--;
        frameCounter++;
        if(frameCounter % 4 == 0) {
        	currentFrame++;
        }
        
    }

    public boolean isAlive() {
        return life > 0;
    }

    public void draw(Graphics2D g2, int playerX, int playerY, int screenX, int screenY) {
        int drawX = x - playerX + screenX;
        int drawY = y - playerY + screenY;
        g2.drawImage(explosionFrames[currentFrame], drawX, drawY, size, size, null);
    }
    
    public static void preloadExplosionImages(GamePanel gp) {
	    if (explosionFrames == null) {
	        explosionFrames = new BufferedImage[6]; // Adjust number based on your sprite sheet
	        try {
	            BufferedImage spriteSheet = ImageIO.read(Projectile.class.getResourceAsStream("/sprites/explosion.png"));
	            int frameWidth = 32;
	            int frameHeight = 32;
	            int tileSize = gp.tileSize;

	            for (int i = 0; i < explosionFrames.length; i++) {
	                explosionFrames[i] = spriteSheet.getSubimage(frameWidth * i, 0, frameWidth, frameHeight);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
    
   

}
