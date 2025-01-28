package entity;

import main.CollisionHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player2 extends Entity {
    GamePanel gp;
    CollisionHandler colH;

    BufferedImage attackImage;
    BufferedImage jumpRight, jumpLeft;

    public boolean jumping;
    boolean canJump;

    public boolean attacking;

    public float yMomentum;
    public float xMomentum;
    boolean falling;
    float fallSpeed;
    float horizontalDrag;
    float jumpSpeed;
    public float launchSpeed = 12F;

    public Player2(GamePanel gp, CollisionHandler colH, int spawnX, int spawnY) {
        this.gp = gp;
        this.colH = colH;
        setDefaultValues(spawnX * gp.tileSize, spawnY * gp.tileSize);
    }

    private void setDefaultValues(int spawnX, int spawnY) {
        worldX = spawnX;
        worldY = spawnY;
        direction = "!right";
        size = gp.tileSize;
        speed = gp.player.speed;
        solidSize = size / 2;
        solidSizeX = solidSize;
        solidSizeY = solidSize;

        yMomentum = 0;
        xMomentum = 0;
        falling = false;
        fallSpeed = 0.5F;
        horizontalDrag = 0.5F;

        canJump = false;
        jumpSpeed = 15F;
        jumping = false;

        attacking = false;

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

            jumpRight = spriteSheet.getSubimage(0, frameHeight * 4, frameWidth, frameHeight);
            jumpLeft = spriteSheet.getSubimage(frameWidth, frameHeight * 4, frameWidth, frameHeight);

            attackImage = ImageIO.read(getClass().getResourceAsStream("/sprites/swoosh_black.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // The normal player movement code is simulated here (altered a bit to account for the other player's position on the screen)
    // The other player sends their keyboard inputs and the direction they're facing, so this uses that
    // Every 80ms, the other player will send their coordinates to correct any desyncs
    public void update() {

        int newX = worldX;
        int newY = worldY;

//        npcDirectionChange();


        switch (direction) {
            case "up":
                newY -= speed;
                if (colH.isColliding(newX + (size / 4), newY + (size / 2) - speed, solidSize, false)) {
                    newY += speed;
                }
                falling = false;
                break;
            case "down":
                newY += speed;
                if (colH.isColliding(newX + (size / 4), newY + (size / 2) - speed, solidSize, false)) {
                    newY -= speed;
                }
                break;
            case "left":
                newX -= speed;
                if (colH.isColliding(newX + (size / 4), newY + (size / 2) - speed, solidSize, false)) {
                    newX += speed;
                }
                break;
            case "right":
                newX += speed;
                if (colH.isColliding(newX + (size / 4), newY + (size / 2) - speed, solidSize, false)) {
                    newX -= speed;
                }
                break;
        }

        if (jumping && canJump) {
            if (colH.isColliding(newX + (size / 4), (int) ((newY + (size / 2) - speed) + 1), solidSize, false)) {
                yMomentum = -jumpSpeed;
                falling = true;
                canJump = false;
                gp.playSE(1);
            }
        }

        // Apply gravity if Player 2 is falling
        if (falling) {
//            canJump = false;
            yMomentum += fallSpeed;
            if (xMomentum > 0) {
                xMomentum -= horizontalDrag;
            }
            if (xMomentum < 0) {
                xMomentum += horizontalDrag;
            }
            if (!colH.isColliding((newX + (size / 4)) + (int) xMomentum, (newY + (size / 2) - speed) + (int) yMomentum, solidSize, false)) {
                newY += (int) yMomentum;
                newX += (int) xMomentum;
//                solidY += (int) yMomentum;
            } else {
                // Collision detected below, stop falling
                yMomentum = 0;
                xMomentum = 0;
                falling = false;
                canJump = true;
            }
        } else {
            // Check if Player 2 should start falling
            if (!colH.isColliding(newX + (size / 4), (newY + (size / 2) - speed) + 1, solidSize, false)) {
                falling = true;
            }
        }

        solidX = newX + (size / 4);
        solidY = newY + (size / 2) - speed;

        boolean colidesWithNPC = false;
        for (Entity npc : gp.npcs) {
            if (colH.isEntityColliding(this, npc) && npc != this) {
                colidesWithNPC = true;
            }
        }

        if (!colH.isColliding(solidX, solidY, solidSize, false) && !colH.isEntityColliding(this, gp.player) && !colidesWithNPC) {
            worldX = newX;
            worldY = newY;
        }

        incrementSpriteNumber();
    }


    public void draw(Graphics2D g2) {
//        loadCurrentSprite();

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Jumping/falling sprites
        if (yMomentum < 0) {
            switch (direction) {
                case "left":
                case "!left":
                    image = jumpLeft;
                    break;
                case "right":
                case "!right":
                    image = jumpRight;
                    break;
            }
        }
        if (yMomentum > 0) {
            // Since the "!" directions don't have sprites associated with them, convert the "!" directions into the positive ones, load the sprite, then change them back
            String oldDirection = direction;
            switch (direction) {
                case "!left":
                    direction = "left";
                    break;
                case "!right":
                    direction = "right";
                    break;
            }
            loadCurrentSprite();
            direction = oldDirection;
        }

        if (yMomentum == 0) {
            loadCurrentSprite();
        }

        if (worldX > gp.player.worldX - gp.player.screenX - gp.tileSize &&
                worldX < gp.player.worldX + gp.player.screenX + gp.tileSize &&
                worldY > gp.player.worldY - gp.player.screenX - gp.tileSize &&
                worldY < gp.player.worldY + gp.player.screenX + gp.tileSize) {
            g2.drawImage(image, screenX, screenY, size, size, null);
        }

        if (attacking) {
            switch (direction) {
                case "right":
                case "!right":
                    g2.drawImage(attackImage, screenX + size * 2, screenY, -size, size, null);
                    break;
                case "left":
                case "!left":
                    g2.drawImage(attackImage, screenX - size, screenY, size, size, null);
                    break;
            }
        }
    }
}
