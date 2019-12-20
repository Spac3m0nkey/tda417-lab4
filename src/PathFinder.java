
import org.w3c.dom.Node;

import java.util.*;

import java.util.stream.Collectors;


public class PathFinder<V> {

    private DirectedGraph<V> graph;
    private long startTimeMillis;


    public PathFinder(DirectedGraph<V> graph) {
        this.graph = graph;
    }


    public class Result<V> {
        public final boolean success;
        public final V start;
        public final V goal;
        public final double cost;
        public final List<V> path;
        public final int visitedNodes;
        public final double elapsedTime;

        public Result(boolean success, V start, V goal, double cost, List<V> path, int visitedNodes) {
            this.success = success;
            this.start = start;
            this.goal = goal;
            this.cost = cost;
            this.path = path;
            this.visitedNodes = visitedNodes;
            this.elapsedTime = (System.currentTimeMillis() - startTimeMillis) / 1000.0;
        }

        public String toString() {
            String s = "";
            s += String.format("Visited nodes: %d\n", visitedNodes);
            s += String.format("Elapsed time: %.1f seconds\n", elapsedTime);
            if (success) {
                s += String.format("Total cost from %s -> %s: %s\n", start, goal, cost);
                s += "Path: " + path.stream().map(x -> x.toString()).collect(Collectors.joining(" -> "));
            } else {
                s += String.format("No path found from %s", start);
            }
            return s;
        }
    }


    public Result<V> search(String algorithm, V start, V goal) {
        startTimeMillis = System.currentTimeMillis();
        switch (algorithm) {
            case "random":
                return searchRandom(start, goal);
            case "dijkstra":
                return searchDijkstra(start, goal);
            case "astar":
                return searchAstar(start, goal);
        }
        throw new IllegalArgumentException("Unknown search algorithm: " + algorithm);
    }


    public Result<V> searchRandom(V start, V goal) {
        int visitedNodes = 0;
        LinkedList<V> path = new LinkedList<>();
        double cost = 0.0;
        Random random = new Random();

        V current = start;
        path.add(current);

        while (current != null) {
            visitedNodes++;
            if (current.equals(goal)) {
                return new Result<>(true, start, current, cost, path, visitedNodes);
            }

            List<DirectedEdge<V>> neighbours = graph.outgoingEdges(start);
            if (neighbours == null || neighbours.size() == 0) {
                break;
            } else {
                DirectedEdge<V> edge = neighbours.get(random.nextInt(neighbours.size()));
                cost += edge.weight();
                current = edge.to();
                path.add(current);
            }
        }
        return new Result<>(false, start, null, -1, null, visitedNodes);
    }


    public Result<V> searchDijkstra(V start, V goal) {
        int visitedNodes = 0;
        HashMap<V, DirectedEdge> edgeTo = new HashMap<>();
        HashMap<V, Double> distTo = new HashMap<>();
        Comparator<V> comp = Comparator.comparing(distTo::get);
        PriorityQueue<V> queue = new PriorityQueue<>(comp);
        HashSet<V> visited = new HashSet<>();

        queue.add(start);
        distTo.put(start, 0.0);


        while (!queue.isEmpty()) {
            V node = queue.poll();

            if (!visited.contains(node)) {
                visitedNodes++;
                visited.add(node);
                if (node.equals( goal)) {
                    double lenght = 0;
                    ArrayList<V> path = new ArrayList<>();
                    V current = goal;
                    while (true) {
                        path.add(current);
                        if (!current.equals(start)){
                            lenght += edgeTo.get(current).weight();
                        }
                        System.out.println(lenght + " --- " + current + " ---- " + edgeTo.get(goal).weight());

                        if (current.equals(start)) {
                            break;
                        }
                        current = (V) edgeTo.get(current).from();

                    }
                    Collections.reverse(path);

                    return new Result<>(true, start, goal, lenght, path, visitedNodes);
                }
                for (DirectedEdge edge : graph.outgoingEdges(node)) {

                    V nextnode = (V) edge.to();
                    if (visited.contains(nextnode)){
                        continue;
                    }
                    Double newDistance = distTo.get(node) + edge.weight();

                    if (distTo.containsKey(nextnode)) {
                        if (distTo.get(nextnode) > newDistance) {
                            distTo.put(nextnode, newDistance);
                            edgeTo.put(nextnode, edge);
                            queue.add(nextnode);
                        }
                    } else {
                        distTo.put(nextnode, newDistance);
                        edgeTo.put(nextnode, edge);
                        queue.add(nextnode);
                    }
                }
            }
        }
        return new Result<>(false, start, null, -1, null, visitedNodes);
    }


    public Result<V> searchAstar(V start, V goal) {
        int visitedNodes = 0;
        HashMap<V, DirectedEdge> edgeTo = new HashMap<>();
        HashMap<V, Double> distTo = new HashMap<>();
        Comparator<V> comp = (v1, v2) -> {
            double fst = distTo.get(v1) + graph.guessCost(v1, goal);
            double snd = distTo.get(v2) + graph.guessCost(v2, goal);
            return Double.compare(fst, snd);
        };
        PriorityQueue<V> queue = new PriorityQueue<>(comp);
        HashSet<V> visited = new HashSet<>();

        queue.add(start);
        distTo.put(start, 0.0);


        while (!queue.isEmpty()) {
            V node = queue.poll();

            if (!visited.contains(node)) {
                visitedNodes++;
                visited.add(node);
                if (node.equals( goal)) {
                    double lenght = 0;
                    ArrayList<V> path = new ArrayList<>();
                    V current = goal;
                    while (true) {
                        path.add(current);
                        if (!current.equals(start)){
                            lenght += edgeTo.get(current).weight();
                        }
                        System.out.println(lenght + " --- " + current + " ---- " + edgeTo.get(goal).weight());

                        if (current.equals(start)) {
                            break;
                        }
                        current = (V) edgeTo.get(current).from();

                    }
                    Collections.reverse(path);

                    return new Result<>(true, start, goal, lenght, path, visitedNodes);
                }
                for (DirectedEdge edge : graph.outgoingEdges(node)) {

                    V nextnode = (V) edge.to();
                    if (visited.contains(nextnode)){
                        continue;
                    }
                    double newDistance = distTo.get(node) + edge.weight();

                    if (distTo.containsKey(nextnode)) {
                        if (distTo.get(nextnode) > newDistance) {
                            distTo.put(nextnode, newDistance );
                            edgeTo.put(nextnode, edge);
                            queue.add(nextnode);
                        }
                    } else {
                        distTo.put(nextnode, newDistance);
                        edgeTo.put(nextnode, edge);
                        queue.add(nextnode);
                    }
                }
            }
        }
        return new Result<>(false, start, null, -1, null, visitedNodes);
    }

}
