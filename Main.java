import java.io.IOException;
import javax.swing.SwingUtilities;

public class Main{
    public static void main(String[] args)throws IOException {
        Graph sieux = Graph.readData(args[0]);
        App app = new App("config.properties", sieux);
        SwingUtilities.invokeLater(app::createAndShowGUI);
        sieux.dijkstra(sieux, sieux.getNodeById("GILBERT-LONG"), sieux.getNodeById("SUEB"));
    } 
}
