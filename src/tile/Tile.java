package tile;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Tile {
	//delete the posX and posY, we can save that in the map instead. We
	//will be saving the color instead
	//public int posX;
	//public int posY;
	public boolean collision;

	public BufferedImage image;
	
	public Color color;
}
