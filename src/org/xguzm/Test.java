package org.xguzm;

import org.jgrapht.*;
    import org.jgrapht.alg.*;
    import org.jgrapht.graph.*;
    import java.util.List;

    public class Test {
        public static void main(String args[]) {
        	
        	SimpleWeightedGraph<String, DefaultWeightedEdge>  graph = 
            new SimpleWeightedGraph<String, DefaultWeightedEdge>
            (DefaultWeightedEdge.class); 
            graph.addVertex("vertex1");
            graph.addVertex("vertex2");
            graph.addVertex("vertex3");
            graph.addVertex("vertex4");
            
            
            DefaultWeightedEdge e1 = graph.addEdge("vertex1", "vertex2"); 
            graph.setEdgeWeight(e1, 5);
            
            DefaultWeightedEdge e2 = graph.addEdge("vertex1", "vertex3"); 
            graph.setEdgeWeight(e2, 5); 

            DefaultWeightedEdge e3 = graph.addEdge("vertex2", "vertex4"); 
            graph.setEdgeWeight(e3, 1); 
            
            DefaultWeightedEdge e4 = graph.addEdge("vertex3", "vertex4"); 
            graph.setEdgeWeight(e4, 7); 
            
            



            System.out.println("Shortest path from vertex1 to vertex5:");
            List<DefaultWeightedEdge> shortest_path =   DijkstraShortestPath.findPathBetween(graph, "vertex4", "vertex1");
            System.out.println(shortest_path);

        }
    }