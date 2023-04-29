import java.io.IOException;
import javax.swing.SwingUtilities;

public class CommandLine {
    public static void UserInteraction(String[] args) throws IOException {
        boolean show = false;
        boolean directions = false;
        int d = 0;

        for (int i = 0; i < args.length; i++) {
            String input = args[i];
            if (input.contains("--show")) {
                show = true;
            } else if (input.contains("--directions")) {
                directions = true;
                d = i;
            }

            if (i > 2) {
                break;
            }
        }
        Graph graph = new Graph(args[0]);

        if (show) {
            App app = new App("config.properties", graph);
            SwingUtilities.invokeLater(app::createAndShowGUI);
        }

        if (directions) {
            Node start = graph.getNodeById(args[d + 1]);
            Node end = graph.getNodeById(args[d + 2]);
            graph.dijkstra(graph, start, end);
        }
    }
}