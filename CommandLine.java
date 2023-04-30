import java.io.IOException;
import javax.swing.SwingUtilities;
import java.util.List;

public class CommandLine {

    /* The class that is responsible for interacting with user in the terminal. */

    public static void UserInteraction(String[] args) throws IOException {
        /*
         * This method is responsible for interacting with the user in the terminal.
         * If Dijkstra's algorithm is to be run, then it might take some time before
         * GUI is shown.
         */

        boolean show = false;
        boolean directions = false;
        List<Node> path = null;

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

        if (directions) {
            Node start = graph.getNodeById(args[d + 1]);
            Node end = graph.getNodeById(args[d + 2]);
            path = graph.dijkstra(graph, start, end);
        }

        if (show) {
            App app = new App("config.properties", graph, path);
            SwingUtilities.invokeLater(app::createAndShowGUI);
        }

    }

}