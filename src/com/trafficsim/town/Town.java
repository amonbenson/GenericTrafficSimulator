package com.trafficsim.town;

import java.util.logging.Logger;

public class Town {
	
	public static Logger logger = Logger.getGlobal();
	
	private Tile[][] tiles;
	private int sizeX, sizeY;
	
	public Town(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public int getSizeX() {
		return sizeX;
	}
	
	public int getSizeY() {
		return sizeY;
	}
	
	public void generateTiles(float[][][] list ) {
		if (sizeX < 1 || sizeY < 1) throw new NullPointerException("Größe X / Y muss größer als 0 sein! X:"+sizeX+" Y:"+sizeY);
		tiles = new Tile[sizeX][sizeY];
		
		for (int x=0;x<list.length;x++) {
			for (int y=0;y<list[0].length;y++) {
				if (list[x][y][0] == 0) { //Straße
					tiles[x][y] = new StreetTile(x, y, list[x][y][1]);
				} else if (list[x][y][0] == 1) { //Haus
					tiles[x][y] = new HouseTile(x, y, (int) list[x][y][1]);
				} else {
					logger.warning("Liste["+x+"]["+y+"][0] ist kein gültiger Typ! ("+list[x][y][0]+")");
				}
			}
		}
		
	}
	
}
