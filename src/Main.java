import java.util.List;

public class Main{
    public static void main(final String[] args) {
        Graph graph = Graph.readStdInToGraph();
        graph.DFS_Iterative();
        List<Integer> verticesToDelete = graph.findVerticesToDelete_V2();
        for(int vertex : verticesToDelete) System.out.println(vertex);
    }
} 