package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import main.CollisionHandler;
import main.GamePanel;

import static java.lang.Math.abs;

public class Player extends Entity {

    GamePanel gp;
    CollisionHandler collisionHandler;

    BufferedImage attackImage;
    BufferedImage jumpRight, jumpLeft;

    //We are going to create variables for where the player should be located on the screen
    public final int screenX;
    public final int screenY;

    public boolean attacking = false;

    public float yMomentum;
    public float xMomentum;
    public boolean falling;
    float fallSpeed;
    float horizontalDrag;
    public float jumpSpeed;
    public float launchSpeed = 12F;

    boolean canJump;

    public Timer posUpdateTimer;

    public Player(GamePanel gamepanel, CollisionHandler collisionHandler) {
        this.gp = gamepanel;
        this.collisionHandler = collisionHandler;

        screenX = (gp.screenWidth - gp.tileSize) / 2;
        screenY = (gp.screenHeight - gp.tileSize) / 2;

        // start and stop a sound effect so that it doesn't lag the first time one is played
        // no one will notice lag on the first frame of the menu compared to it lagging when doing an action that would play a sound
        gp.playSE(1);
        gp.se.stop();

        initialize();
    }

    public void initialize() {

        //set the world position
        if (gp.menu.status.equals("host player")) {
            worldX = 14 * gp.tileSize - gp.tileSize / 2;
            worldY = 10 * gp.tileSize - gp.tileSize / 2;
        }
        if (gp.menu.status.equals("joined player")) {
            worldX = 15 * gp.tileSize - gp.tileSize / 2;
            worldY = 10 * gp.tileSize - gp.tileSize / 2;
        }

        size = gp.tileSize;
        sizeX = size;
        sizeY = size;
        solidSize = size / 2;
        solidSizeX = solidSize;
        solidSizeY = solidSize;
        //We do not need posX/Y anymore
        // posX = 14 * gp.tileSize;
        // posY = 10 * gp.tileSize;
        direction = "left";

        getPlayerImage();

        yMomentum = 0;
        xMomentum = 0;
        falling = false;
        fallSpeed = 0.5F;
        horizontalDrag = 0.5F;

        canJump = false;
        jumpSpeed = 15F;

        posUpdateTimer = new Timer();
        posUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendPosition();
            }
        }, 80, 80);

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

            jumpRight = spriteSheet.getSubimage(0, frameHeight * 4, frameWidth, frameHeight);
            jumpLeft = spriteSheet.getSubimage(frameWidth, frameHeight * 4, frameWidth, frameHeight);

            attackImage = ImageIO.read(getClass().getResourceAsStream("/sprites/swoosh_black.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPosition() {
        int posX = (worldX + gp.tileSize / 2) / gp.tileSize;
        int posY = (worldY + gp.tileSize / 2) / gp.tileSize;
        if (gp.network.connected) {
            gp.network.sendMessage(gp.network.out, "PosX: " + worldX + ", PosY: " + worldY + ", Direction: " + direction + ",");
        }
    }

    public void update() {

        int newX = worldX;
        int newY = worldY;
        solidX = worldX + (size / 4);
        solidY = worldY + (size / 2) - speed;

        // When the attack button is pressed, check for collision with the player and the tile in front of them to see if player2 is there
        // If so, then launch player2 in that direction and send a message to tell the other game client to do the same
        // Reset the solidX and solidY values back after to make sure that later collision detections still work
        if (gp.keyH.enterP && !attacking) {
            attacking = true;
            gp.network.sendMessage(gp.network.out, "attacking");

            int oldSolidX = solidX;
            if (direction.equals("left")) {
                solidX -= size;
            }
            if (direction.equals("right")) {
                solidX += size;
            }

            if (collisionHandler.isEntityColliding(this, gp.npcs.get(2))) {
                gp.network.sendMessage(gp.network.out, "hit");
                ((Player2) gp.npcs.get(2)).yMomentum = -jumpSpeed;
                switch (direction) {
                    case "left":
                        ((Player2) gp.npcs.get(2)).xMomentum -= launchSpeed;
                        break;
                    case "right":
                        ((Player2) gp.npcs.get(2)).xMomentum += launchSpeed;
                        break;
                }
                ((Player2) gp.npcs.get(2)).falling = true;
                gp.playSE(2);
            }
            solidX = oldSolidX;

            // set a timer for the attack cooldown
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    attacking = false;
                    gp.network.sendMessage(gp.network.out, "!attacking");
                }
            }, 100);
        }

        // If the jump button is pressed and the player can jump, double check that they're oin the ground by checking for collision 1 pixel below the player
        // If there is collision, that means the player is on the ground and can jump
        // Set the momentum to a negative value and set falling to true to launch the player into the air
        if (gp.keyH.spaceP && canJump) {
            if (collisionHandler.isColliding(solidX, (int) (solidY + 1), solidSize, false)) {
                System.out.println("jump");
                yMomentum = -jumpSpeed;
                falling = true;
                gp.playSE(1);
            }
        }

        // If the player is falling, add to their y momentum and take away from their x momentum
        // If no collisions would occure from the change, add the y momentum and any possible x momentum make the player fall
        // Since the player is moving, the collision check can trigger a game over if the player touches the lava border
        if (falling) {
            canJump = false;
            yMomentum += fallSpeed;
            if (xMomentum > 0) {
                xMomentum -= horizontalDrag;
            }
            if (xMomentum < 0) {
                xMomentum += horizontalDrag;
            }
            if (!collisionHandler.isColliding((int) (solidX + xMomentum), (int) (solidY + yMomentum), solidSize, true)) {
                newY += yMomentum;
                solidY += yMomentum;
                newX += xMomentum;
                solidX += xMomentum;
            } else {
                // If there is collision, the player is no longer in the air
                yMomentum = 0;
                falling = false;
                canJump = true;
            }
        } else {
            // If the player is not falling, check if they are still on the ground. If not, they are falling
            yMomentum = 0;
            xMomentum = 0;
            canJump = true;
            if (!collisionHandler.isColliding(solidX, (int) (solidY + 1), solidSize, false)) {
                falling = true;
            }
        }

        if (gp.keyH.leftP || gp.keyH.rightP || gp.keyH.upP || gp.keyH.downP) {
            // Check left movement
            if (gp.keyH.leftP) {
                direction = "left";
                // Since the player is moving, the collision check can trigger a game over if the player touches the lava border
                if (!collisionHandler.isColliding(solidX - speed, solidY, solidSize, true)) {
                    newX -= speed;
                    solidX -= speed;
                }
            }

            // Check right movement
            if (gp.keyH.rightP) {
                direction = "right";
                if (!collisionHandler.isColliding(solidX + speed, solidY, solidSize, true)) {
                    newX += speed;
                    solidX += speed;
                }
            }

//		    // Check up movement
            if (gp.keyH.upP) {
//                direction = "up";
//                if (!collisionHandler.isColliding(solidX, solidY - speed, solidSize, true)) {
//                    newY -= speed;
//                    solidY -= speed;
//                }
//                falling = false;
            }

            // Check down movement
            if (gp.keyH.downP) {
//		        direction = "down";
//		        if (!collisionHandler.isColliding(solidX, solidY + speed, solidSize)) {
//		            newY += speed;
//		            solidY += speed;
//		        }
//                falling = true;
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
//                gp.playSE(0); //When we collide with an NPC, lets make a sound play.
                collidesWithNPC = true;
            }
        }

        // Apply new position if no collisions are detected
//        if (!collidesWithNPC) {
            worldX = newX;
            worldY = newY;
//        }
    }

    public void gameOver() {
        gp.network.sendMessage(gp.network.out, "game over");
        // Go to the game over menu
        gp.menu.gameOver(true);
    }

    public void draw(Graphics2D g2) {

        // If the player is jumping, show the jump sprite corresponding to their direction. Otherwise, show the normal walking sprites
        if (yMomentum >= 0) {
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
        } else {
            switch (direction) {
                case "left":
                    image = jumpLeft;
                    break;
                case "right":
                    image = jumpRight;
                    break;
            }
        }

        //we are going to draw our player at screenX / screenY now
        g2.drawImage(image, screenX, screenY, size, size, null);

        //bounding boxes so you can see the your tile and your collision area
        //g2.drawRect(posX,  posY,  size, size);
        //g2.drawRect(solidX,  solidY,  solidSize, solidSize);

        // Draw the attack 1 tile to the side of the player
        if (attacking) {
            switch (direction) {
                case "right":
                    g2.drawImage(attackImage, screenX + size * 2, screenY, -size, size, null);
                    break;
                case "left":
                    g2.drawImage(attackImage, screenX - size, screenY, size, size, null);
                    break;
            }
        }
    }
}
