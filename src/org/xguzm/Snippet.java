package org.xguzm;

import java.util.List;

import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import org.xguzm.pathfinding.grid.finders.GridFinderOptions;

public class Snippet {
	public static void main(String[] args) {
		
		//these should be stored as [x][y]
		GridCell[][] cells = new GridCell[5][5];
		for ( int x=0;x<5;x++) {
			for ( int y=0;y<5;y++) {
				cells[x][y] = new GridCell(x, y);
			}
		}
		//create your cells with whatever data you need
		cells[2][0].setWalkable(false);
		cells[2][1].setWalkable(false);
		cells[2][2].setWalkable(false);
		cells[2][3].setWalkable(false);
		

		
		List<GridCell> pathToEnd = finder.findPath(4, 4, 0, 0, navGrid);
		for (GridCell c : pathToEnd) {
			System.out.println(c.x+":"+c.y);
		}
	}
}

