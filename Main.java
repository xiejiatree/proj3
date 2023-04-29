import java.io.IOException;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) throws IOException {
        Graph graph = Graph.readData(args[0]);
        App app = new App("config.properties", graph);
        SwingUtilities.invokeLater(app::createAndShowGUI);
    }
}
