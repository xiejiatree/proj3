import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
<<<<<<< HEAD
        CommandLine.UserInteraction(args);
=======
        Graph graph = Graph.readData(args[0]);
        App app = new App("config.properties", graph);
        SwingUtilities.invokeLater(app::createAndShowGUI);
>>>>>>> 0dbfe350a6e6b60bb9c5f1cde947ad2d24c04bbf
    }
}

  
    

