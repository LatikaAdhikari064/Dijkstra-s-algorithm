import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class DijkstraGUI extends JFrame {
    private JTextField startField;
    private JTextField endField;
    private JTextArea resultArea;
    private Graph graph;

    public DijkstraGUI() {
        setTitle("Dijkstra's Shortest Path Algorithm");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        startField = new JTextField(10);
        endField = new JTextField(10);
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        JButton findButton = new JButton("Find Shortest Path");
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startNode = startField.getText().trim();
                String endNode = endField.getText().trim();
                String result = graph.dijkstra(startNode, endNode);
                resultArea.setText(result);
            }
        });

        add(new JLabel("Start Node:"));
        add(startField);
        add(new JLabel("End Node:"));
        add(endField);
        add(findButton);
        add(new JScrollPane(resultArea));

        // Create the graph and add edges
        graph = new Graph();
        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 4);
        graph.addEdge("B", "C", 2);
        graph.addEdge("B", "D", 5);
        graph.addEdge("C", "D", 1);
        graph.addEdge("D", "E", 3);
        
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DijkstraGUI::new);
    }

    class Graph {
        private final Map<String, ArrayList<Edge>> adjacencyList;

        public Graph() {
            adjacencyList = new HashMap<>();
        }

        public void addEdge(String from, String to, int weight) {
            adjacencyList.putIfAbsent(from, new ArrayList<>());
            adjacencyList.putIfAbsent(to, new ArrayList<>());
            adjacencyList.get(from).add(new Edge(to, weight));
            adjacencyList.get(to).add(new Edge(from, weight)); // For undirected graph
        }

        public String dijkstra(String start, String end) {
            PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
            Map<String, Integer> distances = new HashMap<>();
            Map<String, String> previousNodes = new HashMap<>();

            for (String node : adjacencyList.keySet()) {
                distances.put(node, Integer.MAX_VALUE); // Initialize distances to infinity
                previousNodes.put(node, null); // Previous node in optimal path
            }
            distances.put(start, 0); // Distance from start to itself is zero
            pq.add(new Edge(start, 0));

            while (!pq.isEmpty()) {
                Edge currentEdge = pq.poll();
                String currentNode = currentEdge.node;

                if (currentNode.equals(end)) break; // Stop if we reached the destination

                for (Edge edge : adjacencyList.get(currentNode)) {
                    int newDist = distances.get(currentNode) + edge.weight;
                    if (newDist < distances.get(edge.node)) { // Relaxation step
                        distances.put(edge.node, newDist);
                        previousNodes.put(edge.node, currentNode);
                        pq.add(new Edge(edge.node, newDist));
                    }
                }
            }

            return constructPath(previousNodes, start, end); // Return formatted path
        }

        private String constructPath(Map<String, String> previousNodes, String start, String end) {
            ArrayList<String> path = new ArrayList<>();
            for (String at = end; at != null; at = previousNodes.get(at)) {
                path.add(at);
            }
            Collections.reverse(path); // Reverse to get correct order
            return path.isEmpty() || !path.get(0).equals(start) ? "No path found." : 
                   "Shortest path: " + String.join(" -> ", path) + "\nDistance: " + (path.size() - 1); // Simplified distance calculation
        }

        class Edge {
            String node;
            int weight;

            Edge(String node, int weight) {
                this.node = node;
                this.weight = weight;
            }
        }
    }
}
