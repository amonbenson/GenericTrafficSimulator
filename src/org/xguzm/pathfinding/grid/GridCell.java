package org.xguzm.pathfinding.grid;

import org.xguzm.pathfinding.NavigationGraph;
import org.xguzm.pathfinding.NavigationNode;
import org.xguzm.pathfinding.PathFinder;
import org.xguzm.pathfinding.util.ObjectIntMap;

import java.util.List;


public class GridCell implements NavigationGridGraphNode{
	public int x;
	public int y;
	
	/* for path finders*/
	private float f, g, h;
	private boolean isWalkable;
	private GridCell parent;
	private ObjectIntMap<Class<? extends PathFinder>> closedOnJob = new ObjectIntMap<Class<? extends PathFinder>>();
	private ObjectIntMap<Class<? extends PathFinder>> openedOnJob = new ObjectIntMap<Class<? extends PathFinder>>();

	//for BTree
	private int index;


	public GridCell() {}

	public GridCell(int x, int y) {
		this(x, y, true);
	}

	public GridCell(int x, int y, boolean isWalkable){
		this.y = y;
		this.x = x;
		this.isWalkable = isWalkable;
	}

	public GridCell(boolean isWalkable){
		this.isWalkable = isWalkable;
	}


	public void setIndex(int index) {
		this.index = index;
	}



	public int getIndex() {
		return index;
	}

	public boolean isWalkable() {
		return isWalkable;
	}

	public void setWalkable(boolean isWalkable) {
		this.isWalkable = isWalkable;
	}

	public float getF() {
		return f;
	}

	public void setF(float f) {
		this.f = f;
	}

	
	public float getG() {
		return g;
	}

	
	public void setG(float g) {
		this.g = g;
	}

	
	public float getH() {
		return h;
	}

	
	public void setH(float h) {
		this.h = h;
	}

	
	public NavigationNode getParent() {
		return parent;
	}

	
	public void setParent(NavigationNode parent) {
		this.parent = (GridCell)parent;
	}

	
	public int getClosedOnJob() {
		return getClosedOnJob(DummyFinder.class);
	}

	
	public void setClosedOnJob(int closedOnJob) {
		setClosedOnJob(closedOnJob, DummyFinder.class);
	}

	
	public int getOpenedOnJob() {
		return getOpenedOnJob(DummyFinder.class) ;
	}

	
	public void setOpenedOnJob(int openedOnJob) {
		setOpenedOnJob(openedOnJob, DummyFinder.class);
	}

	
	public int getClosedOnJob(Class<? extends PathFinder> clazz) {
		return closedOnJob.get(clazz, 0);
	}

	
	public void setClosedOnJob(int closedOnJob, Class<? extends PathFinder> clazz) {
		this.closedOnJob.put(clazz, closedOnJob);
	}

	
	public int getOpenedOnJob(Class<? extends PathFinder> clazz) {
		return openedOnJob.get(clazz, 0);
	}

	
	public void setOpenedOnJob(int openedOnJob, Class<? extends PathFinder> clazz) {
		this.openedOnJob.put(clazz, openedOnJob);
	}

	
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	
	public int getX() {
		return x;
	}

	
	public int getY() {
		return y;
	}

	
	public void setX(int x) {
		this.x = x;
	}

	
	public void setY(int y) {
		this.y = y;
	}

	private static final class DummyFinder<T extends GridCell> implements PathFinder<T> {
		
		public List<T> findPath(T startNode, T endNode, NavigationGraph<T> grid) {
			return null;
		}
	}
}
