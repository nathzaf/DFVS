import java.util.Scanner;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Graph {
    final private int numberOfVertices;

    final private int numberOfEdges;

    private Map<Integer, List<Integer>> neighbours;

    private List<Edge> edges;

    private List<Integer> prefix;
    private List<Integer> suffix;

    private List<Integer> verticesToDeleteTimeLimit;
    final CountDownLatch exit_now = new CountDownLatch(1);

    public Graph(int numberOfVertices, int numberOfEdges) {
        this.numberOfVertices = numberOfVertices;
        this.numberOfEdges = numberOfEdges;
    }

    public static Graph readStdInToGraph() {
        Scanner stdin = new Scanner(System.in);
        int i = 0, numberOfVertices = 1;
        String buff;
        Graph graph = null;
        List<Integer> parameters;
        while (i <= numberOfVertices) {
            buff = stdin.nextLine();

            if (!buff.equals("")) {
                if (buff.charAt(0) == '%') continue;
                else if (i == 0) {
                    parameters = Stream.of(buff.split(" "))
                            .map(t -> Integer.parseInt(String.valueOf(t)))
                            .collect(Collectors.toList());
                    numberOfVertices = parameters.get(0);
                    graph = new Graph(numberOfVertices, parameters.get(1));
                    graph.setNeighbours(new HashMap<>());
                    graph.setEdges(new ArrayList<>());
                    i = i + 1;
                    continue;
                } else {
                    graph.getNeighbours().put(i, Stream.of(buff.split(" "))
                            .map(t -> Integer.parseInt(String.valueOf(t)))
                            .collect(Collectors.toList()));

                    for (int j : graph.getNeighbours().get(i)) graph.getEdges().add(new Edge(i, j));
                }

            } else {
                assert graph != null;
                graph.getNeighbours().put(i, List.of(-1));
            }

            i = i + 1;
        }
        stdin.close();
        return graph;
    }

    public void printGraphToStdErr() {
        System.err.println("\nNumbers of vertices : " + this.getNumberOfVertices());
        System.err.println("Numbers of edges : " + this.getNumberOfEdges());
        this.getNeighbours().forEach((vertex, neighbours) ->
                System.err.println(vertex + " : " + neighbours.toString())
        );
    }

    public void DFS_Iterative() {
        final int n = this.getNumberOfVertices();
        List<Integer> pref = initList(n);
        List<Integer> suff = initList(n);
        Stack<Integer> stack = new Stack<>();
        int p = 1, s = 1, j0 = 0, v;
        boolean existNeighbour;
        for (int i = 1; i <= n; i++) {
            if (pref.get(i - 1) == 0) {
                stack.push(i);
                pref.set(i - 1, p++);
                while (!stack.isEmpty()) {
                    existNeighbour = false;
                    v = stack.peek();
                    for (Integer j : this.getNeighbours().get(v)) {
                        if (j != -1 && pref.get(j - 1) == 0) {
                            existNeighbour = true;
                            j0 = j;
                            break;
                        }
                    }
                    if (existNeighbour) {
                        stack.push(j0);
                        pref.set(j0 - 1, p++);
                    } else {
                        i = stack.pop();
                        suff.set(i - 1, s++);
                    }
                }
            }
        }
        this.setPrefix(pref);
        this.setSuffix(suff);
    }

    public List<Integer> setEdgesType() {
        final List<Integer> pref = this.getPrefix();
        final List<Integer> suff = this.getSuffix();
        List<Integer> countArcArriere = initList(this.numberOfVertices);
        int i, j;
        for (Edge edge : this.getEdges()) {
            i = edge.getI() - 1;
            j = edge.getJ() - 1;
            if (pref.get(i) > pref.get(j) && suff.get(i) < suff.get(j)){
                edge.setArriere(true);
                countArcArriere.set(edge.getI() - 1, countArcArriere.get(edge.getI() - 1) + 1);
                countArcArriere.set(edge.getJ() - 1, countArcArriere.get(edge.getJ() - 1) + 1);
            }
            else edge.setArriere(false);
        }
        return countArcArriere;
    }

    public List<Integer> findVerticesToDelete_V2(){
        Signal.handle(new Signal("TERM"), termHandler);
        List<Integer> countArcArriere = this.setEdgesType();
        setVerticesToDeleteTimeLimit(new ArrayList<>(this.getNeighbours().keySet().stream()
                .filter(t -> countArcArriere.get(t-1) > 0).collect(Collectors.toSet())));
        List<Integer> verticesToDelete = new ArrayList<>();
        for(Edge edge : this.getEdges()){
            if(edge.isArriere()){
                if (!verticesToDelete.contains(edge.getI()) && !verticesToDelete.contains(edge.getJ())) {
                    if (countArcArriere.get(edge.getI() - 1) > countArcArriere.get(edge.getJ() - 1)) {
                        verticesToDelete.add(edge.getI());
                    } else {
                        verticesToDelete.add(edge.getJ());
                    }
                }
            }
        }
        return verticesToDelete;
    }

    private final SignalHandler termHandler = new SignalHandler() {
        @Override
        public void handle(Signal sig) {
            for(int vertex : getVerticesToDeleteTimeLimit()) System.out.println(vertex);
            exit_now.countDown();
            System.exit(0);
        }
    };

    private static List<Integer> initList(int n) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(0);
        }
        return list;
    }

    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }


    public Map<Integer, List<Integer>> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Map<Integer, List<Integer>> neighbours) {
        this.neighbours = neighbours;
    }

    public List<Integer> getPrefix() {
        return prefix;
    }

    public void setPrefix(List<Integer> prefix) {
        this.prefix = prefix;
    }

    public List<Integer> getSuffix() {
        return suffix;
    }

    public void setSuffix(List<Integer> suffix) {
        this.suffix = suffix;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Integer> getVerticesToDeleteTimeLimit() {
        return verticesToDeleteTimeLimit;
    }

    public void setVerticesToDeleteTimeLimit(List<Integer> verticesToDeleteTimeLimit) {
        this.verticesToDeleteTimeLimit = verticesToDeleteTimeLimit;
    }

}
