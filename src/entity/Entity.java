package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Entity {

    public int worldX, worldY;

    final int directionChangeMaxInterval = 100;
    int directionChangeInterval = directionChangeMaxInterval;

    public int size;
    public int solidSize;
    public int sizeX;
    public int sizeY;
    public int solidX;
    public int solidY;
    public int solidSizeX;
    public int solidSizeY;
    public BufferedImage spriteSheet;
    public BufferedImage up1, up2, up3, up4, down1, down2, down3, down4, left1, left2, left3, left4, right1, right2, right3, right4;
    public BufferedImage image;
    public String direction;
    int spriteNum = 1;
    int spriteCounter = 0;
    int speed = 4;

    public void incrementSpriteNumber() {
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

    public void loadCurrentSprite() {
        switch (direction) {
            case "up":
            case "!up":
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
            case "!down":
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
            case "!left":
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
            case "!right":
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
    }

    public void npcDirectionChange() {
        if (directionChangeInterval <= 0) {
            Random random = new Random();
            int directionChange = random.nextInt(0, 4);
            switch (directionChange) {
                case 0:
                    direction = "up";
                    break;
                case 1:
                    direction = "down";
                    break;
                case 2:
                    direction = "left";
                    break;
                case 3:
                    direction = "right";
                    break;
            }
            directionChangeInterval = directionChangeMaxInterval;
        }
        directionChangeInterval--;
    }

    public void update() {
    }

    public void draw(Graphics2D g2) {
    }
}
