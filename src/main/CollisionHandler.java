package main;

import entity.Entity;
import tile.TileManager;

public class CollisionHandler {

    GamePanel gp;
    TileManager tileManager;

    public CollisionHandler(GamePanel gp, TileManager tileManager) {
        this.gp = gp;
        this.tileManager = tileManager;
    }

    public boolean isColliding(int posX, int posY, int size) {
        int tileSize = gp.tileSize;

        int leftTile = posX / tileSize;
        int rightTile = (posX + size - 1) / tileSize;
        int topTile = posY / tileSize;
        int bottomTile = (posY + size - 1) / tileSize;

        if (isTileCollidable(leftTile, topTile) ||
            isTileCollidable(rightTile, topTile) ||
            isTileCollidable(leftTile, bottomTile) ||
            isTileCollidable(rightTile, bottomTile)) {
            return true;
        }

        return false;
    }

    private boolean isTileCollidable(int col, int row) {
    	
        if (col >= 0 && col < tileManager.mapTileNum.length && row >= 0 && row < tileManager.mapTileNum[0].length) {
            int tileNum = tileManager.mapTileNum[col][row];
            return tileManager.tiles[tileNum].collision;
        }
        return false;
    }
    
    public boolean isEntityColliding(Entity ent1, Entity ent2) {
        // Get the bounding box of entity 1
        int ent1Left = ent1.solidX;
        int ent1Right = ent1.solidX + ent1.solidSizeX;
        int ent1Top = ent1.solidY;
        int ent1Bottom = ent1.solidY + ent1.solidSizeY;

        // Get the bounding box of entity 2
        int ent2Left = ent2.solidX;
        int ent2Right = ent2.solidX + ent2.solidSizeX;
        int ent2Top = ent2.solidY;
        int ent2Bottom = ent2.solidY + ent2.solidSizeY;

        // Check for overlap
        if (ent1Right > ent2Left &&
            ent1Left < ent2Right &&
            ent1Bottom > ent2Top &&
            ent1Top < ent2Bottom) {
            return true;
        }

        return false;
    }
}
