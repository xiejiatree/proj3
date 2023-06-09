import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.List;

public class App {
    /*
     * Main class for GUI. Creates an empty window.
     */

    private int windowHeight;
    private int windowWidth;
    private String windowTitle;
    private Graph graph;
    private List<Node> path;

    public App(String configPath, Graph graph, List<Node> path) {
        loadSettings(configPath);
        this.graph = graph;
        this.path = path;
    }

    private void loadSettings(String configPath) {
        /*
         * Loads settings from a config file. The config file is a .properties file.
         */

        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(configPath)) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        windowHeight = Integer.parseInt(properties.getProperty("windowHeight"));
        windowWidth = Integer.parseInt(properties.getProperty("windowWidth"));
        windowTitle = properties.getProperty("windowTitle");

    }

    public void createAndShowGUI() {
        /*
         * Creates the GUI and displays it. For thread safety, this method should be
         * invoked from the event-dispatching thread.
         */

        JFrame frame = new JFrame(windowTitle);
        frame.setSize(windowWidth, windowHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DrawingPanel panel = new DrawingPanel(graph, path);
        frame.add(panel);

        frame.setVisible(true);

    }

    private class DrawingPanel extends JPanel
            implements MouseListener, MouseWheelListener, MouseMotionListener, ComponentListener {

        /*
         * A JPanel that contains a graph that can be zoomed and translated.
         */

        private int translateX; // The x coordinate of the translation. Used for dragging.
        private int translateY; // The y coordinate of the translation. Used for dragging.
        private Point lastMousePoint; // The last mouse point. Used for dragging.

        private double zoomScale; // The zoom scale. Used for zooming using the mouse wheel.

        private double offsetX; // The smallest x coordinate of the graph. Used for scaling. Smallest coordinate
                                // is the origin.
        private double offsetY; // The smallest y coordinate of the graph. Used for scaling. Smallest coordinate
                                // is the origin.

        private double scaleX; // The x scale factor. Used for scaling the window.
        private double scaleY; // The y scale factor. Used for scaling the window.

        private double coordinateMultiplier = 100000; // Used for scaling up the coordinate numbers. Java Swing doesn't
                                                      // support floating point coordinates. As such, the coordinates
                                                      // are multiplied by this number to make them integers.
        private int width = 800; // Those values are hardcoded. We planned on user being able to set size window
                                 // in config file but due to lack of time abandoned this idea.
        private int height = 572;

        private List<Node> nodes; // The nodes of the graph.
        private List<Edge> edges; // The edges of the graph.

        private List<Node> path; // The path to be drawn. If null, no path is drawn.

        public DrawingPanel(Graph graph, List<Node> path) {
            graphSpecificSettings(graph.getFileName());
            this.lastMousePoint = null;

            this.offsetX = graph.getSmallestX();
            this.offsetY = graph.getSmallestY();

            this.scaleX = 1.0;
            this.scaleY = 1.0;

            this.nodes = graph.getNodes();
            this.edges = graph.getEdges();

            this.path = path;

            addMouseListener(this);
            addMouseWheelListener(this);
            addMouseMotionListener(this);
            addComponentListener(this);

            setBackground(new Color(102, 204, 102));
        }

        private void graphSpecificSettings(String fileName) {
            /*
             * Sets the zoom and translation settings for each graph to make it look good.
             */

            switch (fileName) {
                case "ur.txt":
                    this.translateX = 138;
                    this.translateY = 399;
                    this.zoomScale = 1.6105100000000008;
                    break;
                case "monroe.txt":
                    this.translateX = -20;
                    this.translateY = 331;
                    this.zoomScale = 0.022094928152179918;
                    break;
                case "nys.txt":
                    this.translateX = -37;
                    this.translateY = 301;
                    this.zoomScale = 0.001853884953141781;
                    break;
                default:
                    this.translateX = 0;
                    this.translateY = 0;
                    this.zoomScale = 1;
                    break;
            }

        }

        // DRAWING METHODS

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform affineTransform = new AffineTransform();

            // When graph is drawn without the following code snippet, it is rotated 270
            // degrees.
            // This code snippet rotates it back.

            double centerX = getWidth() / 2.0;
            double centerY = getHeight() / 2.0;
            affineTransform.translate(centerX, centerY);
            affineTransform.rotate(3 * Math.PI / 2);
            affineTransform.translate(-centerX, -centerY);

            affineTransform.translate(-translateY, translateX); // translateX and translateY are switched because the
                                                                // graph is
            // rotated 270 degrees. X is also negated because of
            // the way the coordinate system works.
            affineTransform.scale(zoomScale * scaleY, zoomScale * scaleX); // scaleX and scaleY are switched because the
                                                                           // graph is
            // rotated 270 degrees

            g2d.setTransform(affineTransform);

            for (Node node : nodes) {
                drawNode(g2d, node);
            }

            for (Edge edge : edges) {
                drawEdge(g2d, edge);
            }

        }

        private void drawNode(Graphics2D g2d, Node node) {

            /*
             * A method to draw Nodes. Changes color and stroke based on whether Dijkstra
             * was invoked.
             */

            int nodeRadius = 5;
            if (path != null) {
                if (path.contains(node)) {
                    g2d.setColor(new Color(0, 0, 0));
                } else {
                    g2d.setColor(new Color(245, 245, 245));
                }
            } else {
                g2d.setColor(new Color(245, 245, 245));
            }

            int x = (int) ((node.getLatitude() - offsetX) * coordinateMultiplier);
            int y = (int) ((node.getLongitude() - offsetY) * coordinateMultiplier);

            g2d.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);

        }

        private void drawEdge(Graphics2D g2d, Edge edge) {

            /*
             * A method to draw Edges. Changes color and stroke based on whether Dijkstra
             * was invoked.
             */

            Node startingNode = edge.getStart();
            Node endingNode = edge.getEnd();

            if (path != null) {
                if (path.contains(startingNode) && path.contains(endingNode)) {
                    g2d.setColor(new Color(0, 0, 0));
                    g2d.setStroke(new BasicStroke(5));
                }

                else {
                    g2d.setColor(new Color(245, 245, 245));
                    g2d.setStroke(new BasicStroke(2));
                }
            } else {
                g2d.setColor(new Color(245, 245, 245));
                g2d.setStroke(new BasicStroke(2));
            }

            int x1 = (int) ((startingNode.getLatitude() - offsetX) * coordinateMultiplier);
            int y1 = (int) ((startingNode.getLongitude() - offsetY) * coordinateMultiplier);
            int x2 = (int) ((endingNode.getLatitude() - offsetX) * coordinateMultiplier);
            int y2 = (int) ((endingNode.getLongitude() - offsetY) * coordinateMultiplier);

            g2d.drawLine(x1, y1, x2, y2);

        }

        // ZOOMING METHODS

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            /*
             * Zooms in or out depending on the direction of the mouse wheel movement.
             */

            int notches = e.getWheelRotation();
            double zoomFactor = 1.1;

            if (notches < 0) {
                zoomScale *= zoomFactor;
            } else {
                zoomScale /= zoomFactor;
            }
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            /*
             * Stores the mouse point when the left mouse button is pressed.
             */

            if (SwingUtilities.isLeftMouseButton(e)) {
                lastMousePoint = e.getPoint();
            }
        }

        // DRAGGING METHODS

        @Override
        public void mouseDragged(MouseEvent e) {
            /*
             * Translates the view when the left mouse button is pressed and the mouse is
             * dragged.
             * The translation is relative to the last mouse point.
             * 
             */

            if (SwingUtilities.isLeftMouseButton(e) && lastMousePoint != null) {
                int dx = e.getX() - lastMousePoint.x;
                int dy = e.getY() - lastMousePoint.y;

                translateX += dx;
                translateY += dy;

                lastMousePoint = e.getPoint();

                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            /*
             * Clears the last mouse point when the left mouse button is released.
             */

            if (SwingUtilities.isLeftMouseButton(e)) {
                lastMousePoint = null;
            }
        }

        // SCALING METHODS

        @Override
        public void componentResized(ComponentEvent e) {
            int newWidth = e.getComponent().getWidth();
            int newHeight = e.getComponent().getHeight();

            scaleX *= (double) newWidth / width;
            scaleY *= (double) newHeight / height;

            repaint();
        }

        /* Unnecessary interface methods */

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void componentMoved(ComponentEvent e) {
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }

    }

}