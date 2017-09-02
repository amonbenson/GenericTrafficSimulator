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
		
		//create a navigation grid with the cells you just created
		NavigationGrid<GridCell> navGrid = new NavigationGrid(cells);
		
		//or create your own pathfinder options:
		GridFinderOptions opt = new GridFinderOptions();
		opt.allowDiagonal = false;
			
		AStarGridFinder<GridCell> finder = new AStarGridFinder(GridCell.class, opt);
		
		List<GridCell> pathToEnd = finder.findPath(0, 0, 4, 4, navGrid);
		for (GridCell c : pathToEnd) {
			System.out.println(c.x+":"+c.y);
		}
	}
}

