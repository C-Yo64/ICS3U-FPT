package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.CollisionHandler;
import main.GamePanel;

import static java.lang.Math.abs;

public class Player extends Entity {

    GamePanel gp;
    CollisionHandler collisionHandler;

    //We are going to create variables for where the player should be located on the screen
    public final int screenX;
    public final int screenY;
    private final int projectileCooldownMax = 40;
    private int projectileCooldown = 0;

    float momentum;
    boolean falling;
    float fallSpeed;
    float jumpSpeed;

    boolean canJump;

    public Player(GamePanel gamepanel, CollisionHandler collisionHandler) {
        this.gp = gamepanel;
        this.collisionHandler = collisionHandler;

        screenX = (gp.screenWidth - gp.tileSize) / 2;
        screenY = (gp.screenHeight - gp.tileSize) / 2;

        initialize();
    }

    private void initialize() {

        //set the world position
        worldX = 14 * gp.tileSize - gp.tileSize / 2;
        worldY = 10 * gp.tileSize - gp.tileSize / 2;

        size = gp.tileSize;
        sizeX = size;
        sizeY = size;
        solidSize = size / 2;
        solidSizeX = solidSize;
        solidSizeY = solidSize;
        //We do not need posX/Y anymore
        // posX = 14 * gp.tileSize;
        // posY = 10 * gp.tileSize;
        direction = "down";

        getPlayerImage();

        momentum = 0;
        falling = false;
        fallSpeed = 0.5F;

        canJump = false;
        jumpSpeed = 15F;

//		gp.playMusic(0);
//		gp.stopMusic();
    }

    private void getPlayerImage() {

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
        solidX = worldX + (size / 4);
        solidY = worldY + (size / 2) - speed;

        if (gp.keyH.enterP && projectileCooldown <= 0) {
            Projectile p = new Projectile(gp, collisionHandler, worldX, worldY, direction);
            gp.addProjectile(p);
            projectileCooldown = projectileCooldownMax;
        }
        projectileCooldown--;

        if (gp.keyH.spaceP && canJump) {
            if (collisionHandler.isColliding(solidX, (int) (solidY + 1), solidSize)) {
                System.out.println("jump");
                momentum = -jumpSpeed;
                falling = true;
            }
        }

        if (falling) {
            canJump = false;
            momentum += fallSpeed;
            if (!collisionHandler.isColliding(solidX, (int) (solidY + momentum), solidSize)) {
                newY += momentum;
                solidY += momentum;
            } else {
                momentum = 0;
                falling = false;
                canJump = true;
            }
        } else {
            momentum = 0;
            canJump = true;
            if (!collisionHandler.isColliding(solidX, (int) (solidY + 1), solidSize)) {
                falling = true;
            }
        }

        if (gp.keyH.leftP || gp.keyH.rightP || gp.keyH.upP || gp.keyH.downP) {
            // Check left movement
            if (gp.keyH.leftP) {
                direction = "left";
                if (!collisionHandler.isColliding(solidX - speed, solidY, solidSize)) {
                    newX -= speed;
                    solidX -= speed;
                }
            }

            // Check right movement
            if (gp.keyH.rightP) {
                direction = "right";
                if (!collisionHandler.isColliding(solidX + speed, solidY, solidSize)) {
                    newX += speed;
                    solidX += speed;
                }
            }

//		    // Check up movement
            if (gp.keyH.upP) {
                direction = "up";
                if (!collisionHandler.isColliding(solidX, solidY - speed, solidSize)) {
                    newY -= speed;
                    solidY -= speed;
                }
                falling = false;
            }

            // Check down movement
            if (gp.keyH.downP) {
//		        direction = "down";
//		        if (!collisionHandler.isColliding(solidX, solidY + speed, solidSize)) {
//		            newY += speed;
//		            solidY += speed;
//		        }
                falling = true;
            }

            // Update sprite animation
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

        // Check for collisions with NPCs
        boolean collidesWithNPC = false;
        for (Entity npc : gp.npcs) {
            if (collisionHandler.isEntityColliding(this, npc)) {
                gp.playSE(0); //When we collide with an NPC, lets make a sound play.
                collidesWithNPC = true;
            }
        }

        // Apply new position if no collisions are detected
        if (!collidesWithNPC) {
            worldX = newX;
            worldY = newY;
        }
    }

    public void draw(Graphics2D g2) {

        switch (direction) {
            case "up":
                if (spriteNum == 1) {
                    image = up1;
                }
                if (spriteNum == 2) {
                    image = up2;
                }
                if (spriteNum == 3) {
                    image = up3;
                }
                if (spriteNum == 4) {
                    image = up4;
                }
                break;

            case "down":
                if (spriteNum == 1) {
                    image = down1;
                }
                if (spriteNum == 2) {
                    image = down2;
                }
                if (spriteNum == 3) {
                    image = down3;
                }
                if (spriteNum == 4) {
                    image = down4;
                }
                break;

            case "left":
                if (spriteNum == 1) {
                    image = left1;
                }
                if (spriteNum == 2) {
                    image = left2;
                }
                if (spriteNum == 3) {
                    image = left3;
                }
                if (spriteNum == 4) {
                    image = left4;
                }
                break;

            case "right":
                if (spriteNum == 1) {
                    image = right1;
                }
                if (spriteNum == 2) {
                    image = right2;
                }
                if (spriteNum == 3) {
                    image = right3;
                }
                if (spriteNum == 4) {
                    image = right4;
                }
                break;
        }

        //we are going to draw our player at screenX / screenY now
        g2.drawImage(image, screenX, screenY, size, size, null);

        //bounding boxes so you can see the your tile and your collision area
        //g2.drawRect(posX,  posY,  size, size);
        //g2.drawRect(solidX,  solidY,  solidSize, solidSize);
    }
}
