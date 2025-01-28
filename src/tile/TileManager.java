package tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.GamePanel;

import javax.imageio.ImageIO;

public class TileManager {

    GamePanel gp;
    public Tile[] tiles;
    public int mapTileNum[][];

    BufferedImage bg, border, platform;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        //Allow some more types of tiles but increasing the array size
        tiles = new Tile[5];
        //this will be edited to refer to the new max map sizes in GamePanel
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        try {
            bg = ImageIO.read(getClass().getResourceAsStream("/sprites/brick.png"));
            border = ImageIO.read(getClass().getResourceAsStream("/sprites/lava.png"));
            platform = ImageIO.read(getClass().getResourceAsStream("/sprites/platform.jpg"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        loadTiles();

        loadMap("/maps/battlefield.txt");
    }

    private void loadTiles() {

        tiles[0] = new Tile();
        tiles[0].collision = false;
        tiles[0].color = Color.white;
        tiles[0].image = bg;

        tiles[1] = new Tile();
        tiles[1].collision = true;
        tiles[1].color = Color.black;
        tiles[1].image = border;

        tiles[2] = new Tile();
        tiles[2].collision = true;
        tiles[2].color = Color.blue;
        tiles[2].image = platform;
    }

    private void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            String line;
            while (row < mapTileNum[0].length && (line = br.readLine()) != null) {
                String[] numbers = line.split(" ");

                for (col = 0; col < numbers.length && col < mapTileNum.length; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                }
                row++;
            }
            br.close();
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
        }
    }


    public void draw(Graphics2D g2) {
        for (int row = 0; row < mapTileNum[0].length; row++) {
            for (int col = 0; col < mapTileNum.length; col++) {
                int tileNum = mapTileNum[col][row];

                //we must add this: the world values and the screen values
                //the screenX/Y is basically where we are going to place the tile relative to player
                int worldX = col * gp.tileSize;
                int worldY = row * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;


                //this if statement will prevent tiles from being drawn if they are too far away from our player (not on the screen)
                if (worldX > gp.player.worldX - gp.player.screenX - gp.tileSize &&
                        worldX < gp.player.worldX + gp.player.screenX + gp.tileSize &&
                        worldY > gp.player.worldY - gp.player.screenX - gp.tileSize &&
                        worldY < gp.player.worldY + gp.player.screenX + gp.tileSize) {
                    // Set the color based on the tile type and draw the tile
//                    g2.setColor(tiles[tileNum].color);
//                    g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                    g2.drawImage(tiles[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }

            }
        }
    }


}
