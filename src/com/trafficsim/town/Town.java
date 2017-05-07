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
	
	/**
	 * Erzeugt die Tiles anhand eines dreidimensionalen Arrays.
	 * Diese ist wie folgt aufgebaut:
	 *
	 * list[x][y][0] = Typ des Tiles, 0 ist Straﬂe, 1 ist Haus
	 * list[x][y][1] = Wert des Tiles, bei einer Straﬂe die maximale Geschwindigkeit, bei einem Haus die Anzahl der Personen 
	 *
	 */
	public void generateTiles(float[][][] list ) {
		if (sizeX < 1 || sizeY < 1) throw new IllegalArgumentException("Grˆﬂe X / Y muss grˆﬂer als 0 sein! X:"+sizeX+" Y:"+sizeY);
		tiles = new Tile[sizeX][sizeY];
		
		for (int x=0;x<list.length;x++) {
			for (int y=0;y<list[0].length;y++) {
				if (list[x][y][0] == 0) { //Straﬂe
					tiles[x][y] = new StreetTile(x, y, list[x][y][1]);
					System.out.println("Street");
				} else if (list[x][y][0] == 1) { //Haus
					tiles[x][y] = new HouseTile(x, y, (int) list[x][y][1]);
					System.out.println("house");
				} else {
					logger.warning("Liste["+x+"]["+y+"][0] ist kein g¸ltiger Typ! ("+list[x][y][0]+")");
				}
			}
		}
		
	}
	

	
}
