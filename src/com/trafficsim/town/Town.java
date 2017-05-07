package com.trafficsim.town;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class Town {
	
	public static Logger logger = Logger.getGlobal();
	
	private Tile[][] tiles = null;
	private int sizeX, sizeY;
	
	public Town(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	/**
	 * Gibt an, ob die Tiles bereits erzeugt wurden.
	 */
	public boolean areTilesReady() {
		if (tiles == null) return false;
		else return true;
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
	
	/**
	 * Gibt alle StreetTiles zur¸ck.
	 * @return
	 */
	public ArrayList<StreetTile> getStreetTiles() {
		if (!areTilesReady()) throw new NullPointerException("Bisher keine Tiles erzeugt. (Tiles sind nicht ready)");
		
		ArrayList<StreetTile> back = new ArrayList<StreetTile>();
		for (int x=0;x<tiles.length;x++) {
			for (int y=0;y<tiles[0].length;y++) {
				if (tiles[x][y] instanceof StreetTile) {
					back.add((StreetTile)tiles[x][y]);
				}
			}
		}
		
		return back;
	}
	
	/**
	 * Setzt alle Tiles einer Stadt, erstmal zum debuggen, wird eventuell hinterher wieder entfernt
	 * 
	 * @param tiles
	 * 			Die neuen Tiles.
	 */
	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}
	
	//Test
	public static void main(String[] args) {
		Town t = new Town(2,2);
		Tile[][] tiles = new Tile[2][2];
		tiles[0][0] = new StreetTile(0,0, 5f);
		tiles[0][1] = new StreetTile(0, 1, 2f);
		tiles[1][0] = new HouseTile(1, 0, 5);
		tiles[1][1] = new HouseTile(1, 1, 10);
		t.tiles = tiles;
		System.out.println(Arrays.toString(t.getStreetTiles().toArray()));
	}
	
	
}
