package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import entity.*;
import tile.Tile;
import tile.TileManager;


public class GamePanel extends JPanel implements Runnable {

	private Thread thread;
	private final int FPS = 60;
	
	private final int originalTileSize = 16;
	private final int scale = 3;
	public final int tileSize = originalTileSize * scale;
	
	public final int screenWidth = tileSize * 24;
	public final int screenHeight = tileSize * 14;
	
	public final int maxWorldCol = 48;
	public final int maxWorldRow = 28;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;

	public Network network = new Network(this);
	public Menu menu = new Menu(this, network);

	public Sound se = new Sound();

	public Sound music = new Sound();

	public KeyHandler keyH = new KeyHandler(this);
	TileManager tileM = new TileManager(this);
	CollisionHandler collisionHandler = new CollisionHandler(this, tileM);
	public Player player = new Player(this, collisionHandler);
	
	public ArrayList<Entity> npcs = new ArrayList<>();
	public ArrayList<Projectile> projectiles = new ArrayList<>();
	private final ArrayList<Explosion> explosions = new ArrayList<>();

	
	public final String playState = "play";
	public final String pauseState = "pause";
	public final String startState = "start";
	public final String endState = "end";
	public String gameState = startState;

	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.LIGHT_GRAY);
		this.addKeyListener(keyH);
		this.setFocusable(true);
		startup();
	}

	private void startup() {
		npcs.add(new HorseBuggy(this, collisionHandler));
		npcs.add(new ManNPC(this, collisionHandler, 999, 999));
		npcs.add(new Player2(this, collisionHandler, 14, 10));
		
		//preload all our images for our projectiles to avoid lag
		Projectile.preloadImages(this);
		Explosion.preloadExplosionImages(this);

		
		
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		
		double drawInterval = 1000000000 / FPS;
		
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(thread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			
			if(delta >= 1) {
				
				//One option is to only allow updating if you are
				//in the playState, but that is not very dynamic
				//if(gameState == playState) {
					update();
				//}
				repaint();
				delta--;
			}
		}
	}

	private void update() {
		
		//A better option would be to allow different kinds of updates
		//depending on which state we are in
		//This would also be required for the draw() method
		
		if(gameState == playState) {
			player.update();
			for(Entity npc : npcs) {
				npc.update();
			}
			for(int i = 0; i < projectiles.size(); i++) {
				projectiles.get(i).update();
				if(projectiles.get(i).life <= 0) {
					projectiles.remove(projectiles.get(i));
				}
			}
			for (int i = 0; i < explosions.size(); i++) {
			    Explosion explosion = explosions.get(i);
			    explosion.update();
			    if (!explosion.isAlive()) {
			        explosions.remove(i);
			        i--; // Adjust index after removal
			    }
			}
		}
	
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		super.paintComponent(g2);
		draw(g2);
		g2.dispose();
	}

	private void draw(Graphics2D g2) {
		if(gameState == startState) {
//			drawTitleScreen(g2);
			menu.draw(g2);
		}
		else if(gameState == playState) {
			tileM.draw(g2);
			player.draw(g2);
			for(Entity npc : npcs) {
				npc.draw(g2);
			}
			for(Projectile p : projectiles) {
				p.draw(g2);
			}
			
			for (Explosion explosion : explosions) {
			    explosion.draw(g2, player.worldX, player.worldY, player.screenX, player.screenY);
			}
		}
		
		
	}

	private void drawTitleScreen(Graphics2D g2) {
		BufferedImage titleScreenImage = null;
		try {
			titleScreenImage = ImageIO.read(getClass().getResourceAsStream("/sprites/NPC_test.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		String text = "WELCOME TO THE GAME";
		g2.drawImage(titleScreenImage, 0, 0, screenWidth, screenHeight, null);
		g2.drawString(text, screenWidth / 2 - tileSize, screenHeight / 2);
		g2.drawString("PRESS ENTER TO BEGIN", screenWidth / 2 - tileSize, screenHeight / 2 + tileSize);
	}

	public void addProjectile(Projectile p) {
		projectiles.add(p);
		
	}

	public void playSE(int index) {
		se.setFile(index);
		se.play();
	}

	public void playMusic(int index) {
		music.setFile(index);
		music.play();
		music.loop();
	}

	public void stopMusic() {
		music.stop();
	}

	public void addExplosion(Explosion explosion) {
        explosions.add(explosion);
    }
	
	

}
