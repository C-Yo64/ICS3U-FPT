package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class KeyHandler implements KeyListener {

    GamePanel gp;
    public boolean leftP, rightP, upP, downP, eP, enterP, spaceP;


    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_SPACE) {
            if (gp.gameState == gp.playState) {
                spaceP = true;
            }

//			if(gp.gameState == gp.startState) {
//				gp.gameState = gp.playState;
//			}
        }

        switch (gp.gameState) {
            case "start":
                switch (code) {
                    // Navigate through the menu
                    case KeyEvent.VK_W, KeyEvent.VK_UP:
                        gp.menu.changeOption("up");
                        break;
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN:
                        gp.menu.changeOption("down");
                        break;

                    case KeyEvent.VK_ENTER:
                        gp.menu.selectOption();
                        break;
                }
                break;

            case "play":
                if (code == KeyEvent.VK_A) {
                    leftP = true;
                    gp.network.sendMessage(gp.network.out, "left");
                }
                if (code == KeyEvent.VK_D) {
                    rightP = true;
                    gp.network.sendMessage(gp.network.out, "right");
                }
                if (code == KeyEvent.VK_W) {
                    upP = true;
                    gp.network.sendMessage(gp.network.out, "up");
                }
                if (code == KeyEvent.VK_S) {
                    downP = true;
                    gp.network.sendMessage(gp.network.out, "down");
                }
                //This was added with the HorseBuggy to interact with it
                //I removed some of the code in the gamepanel and player classes
                //but this is find to keep, basically we can unpause the game
                //using the e button if it is paused for some reason
                if (code == KeyEvent.VK_E) {
                    eP = true;
                    if (gp.gameState == gp.pauseState) {
                        gp.gameState = gp.playState;
                    }
                }


                //Here is the key event we are adding, it is for starting the game
                //it will only change the gameState if we are currently at the
                //title screen - then it will turn the game on
                if (code == KeyEvent.VK_ENTER) {

                    //I also saved the enterP button logic the same as the rest
                    //just in case - but it is not needed
                    if (gp.gameState == gp.playState) {
                        enterP = true;
                    }

//			if(gp.gameState == gp.startState) {
//				gp.gameState = gp.playState;
//			}


                }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_A) {
            leftP = false;
            gp.network.sendMessage(gp.network.out, "!left");
        }
        if (code == KeyEvent.VK_D) {
            rightP = false;
            gp.network.sendMessage(gp.network.out, "!right");
        }
        if (code == KeyEvent.VK_W) {
            upP = false;
            gp.network.sendMessage(gp.network.out, "!up");
        }
        if (code == KeyEvent.VK_S) {
            downP = false;
            gp.network.sendMessage(gp.network.out, "!down");
        }
        if (code == KeyEvent.VK_E) {
            eP = false;
        }

        //we will also turn it off just in case
        if (code == KeyEvent.VK_ENTER) {
            enterP = false;
        }

        if (code == KeyEvent.VK_SPACE) {
            spaceP = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}
