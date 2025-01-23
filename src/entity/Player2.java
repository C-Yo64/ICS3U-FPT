package entity;

import main.CollisionHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Player2 extends Entity {
    GamePanel gp;
    CollisionHandler colH;

    boolean jumping;
    boolean canJump;

    float momentum;
    boolean falling;
    float fallSpeed;
    float jumpSpeed;

    public Player2(GamePanel gp, CollisionHandler colH, int spawnX, int spawnY) {
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
        solidSize = size / 2;
        solidSizeX = solidSize;
        solidSizeY = solidSize;

        momentum = 0;
        falling = false;
        fallSpeed = 0.5F;

        canJump = false;
        jumpSpeed = 15F;
        jumping = false;

        loadImages();
    }

    private void loadImages() {
        int frameWidth = 16;
        int frameHeight = 32;

        try {
            spriteSheet = ImageIO.read(getClass().getResourceAsStream("/sprites/NPC_test.png"));

            down1 = spriteSheet.getSubimage(0, 0, frameWidth, frameHeight);
            down2 = spriteSheet.getSubimage(frameWidth, 0, frameWidth, frameHeight);
            down3 = spriteSheet.getSubimage(frameWidth * 2, 0, frameWidth, frameHeight);
            down4 = spriteSheet.getSubimage(frameWidth * 3, 0, frameWidth, frameHeight);

            right1 = spriteSheet.getSubimage(0, frameHeight, frameWidth, frameHeight);
            right2 = spriteSheet.getSubimage(frameWidth, frameHeight, frameWidth, frameHeight);
            right3 = spriteSheet.getSubimage(frameWidth * 2, frameHeight, frameWidth, frameHeight);
            right4 = spriteSheet.getSubimage(frameWidth * 3, frameHeight, frameWidth, frameHeight);

            up1 = spriteSheet.getSubimage(0, frameHeight * 2, frameWidth, frameHeight);
            up2 = spriteSheet.getSubimage(frameWidth, frameHeight * 2, frameWidth, frameHeight);
            up3 = spriteSheet.getSubimage(frameWidth * 2, frameHeight * 2, frameWidth, frameHeight);
            up4 = spriteSheet.getSubimage(frameWidth * 3, frameHeight * 2, frameWidth, frameHeight);

            left1 = spriteSheet.getSubimage(0, frameHeight * 3, frameWidth, frameHeight);
            left2 = spriteSheet.getSubimage(frameWidth, frameHeight * 3, frameWidth, frameHeight);
            left3 = spriteSheet.getSubimage(frameWidth * 2, frameHeight * 3, frameWidth, frameHeight);
            left4 = spriteSheet.getSubimage(frameWidth * 3, frameHeight * 3, frameWidth, frameHeight);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        int newX = worldX;
        int newY = worldY;

        // Update solid collision box
        solidX = worldX + (size / 4);
        solidY = worldY + (size / 2) - speed;

//        // Handle projectile firing if applicable
//        if (projectileDirection != null && projectileCooldown <= 0) {
//            Projectile p = new Projectile(gp, colH, worldX, worldY, projectileDirection);
//            gp.addProjectile(p);
//            projectileCooldown = projectileCooldownMax;
//        }
//        projectileCooldown--;

        // Handle jumping based on jump request
        if (jumping && canJump) {
            if (colH.isColliding(solidX, (int) (solidY + 1), solidSize)) {
                System.out.println("Player 2 jump");
                momentum = -jumpSpeed;
                falling = true;
                canJump = false;
            }
        }

        // Apply gravity and handle falling
        if (falling) {
            momentum += fallSpeed;
            if (!colH.isColliding(solidX, (int) (solidY + momentum), solidSize)) {
                newY += momentum;
                solidY += momentum;
            } else {
                momentum = 0;
                falling = false;
                canJump = true;
            }
        } else {
            momentum = 0;
            if (!colH.isColliding(solidX, (int) (solidY + 1), solidSize)) {
                falling = true;
            }
        }

        // Handle movement based on direction variable
        if (direction != null && !direction.isEmpty()) {
            switch(direction) {
                case "left":
                    if (!colH.isColliding(solidX - speed, solidY, solidSize)) {
                        newX -= speed;
                        solidX -= speed;
                    }
                    break;
                case "right":
                    if (!colH.isColliding(solidX + speed, solidY, solidSize)) {
                        newX += speed;
                        solidX += speed;
                    }
                    break;
                case "up":
                    if (!colH.isColliding(solidX, solidY - speed, solidSize)) {
                        newY -= speed;
                        solidY -= speed;
                    }
                    falling = false;
                    break;
                case "down":
                    // Depending on game mechanics, "down" could initiate falling or other actions
                    falling = true;
                    break;
            }

            // Update sprite animation
            spriteCounter++;
            if (spriteCounter > 8) {
                spriteNum = (spriteNum % 4) + 1; // Cycles spriteNum between 1 and 4
                spriteCounter = 0;
            }
        }

        // Check for collisions with NPCs and Player 1
        boolean collidesWithEntity = false;
        for (Entity npc : gp.npcs) {
            if (colH.isEntityColliding(this, npc)) {
                gp.playSE(0); // Play collision sound effect
                collidesWithEntity = true;
                break;
            }
        }
        if (!collidesWithEntity && colH.isEntityColliding(this, gp.player)) {
            gp.playSE(0);
            collidesWithEntity = true;
        }

        // Apply the new position if no collisions are detected
        if (!collidesWithEntity) {
            worldX = newX;
            worldY = newY;
        }

//        // Reset jump request after processing
//        jumpRequest = false;      
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
