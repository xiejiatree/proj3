import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.List;

public class App {
    private int windowHeight;
    private int windowWidth;
    private String windowTitle;
    private Graph graph;

    public App(String configPath, Graph graph) {
        loadSettings(configPath);
        this.graph = graph;
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

        ZoomableJPanel panel = new ZoomableJPanel(graph);
        frame.add(panel);

        frame.setVisible(true);

    }

    private class ZoomableJPanel extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {

        /*
         * A JPanel that can be zoomed in and out and dragged around.
         */

        private double scale;
        private int translateX;
        private int translateY;
        private Point lastMousePoint;
        private List<Node> nodes;
        private List<Edge> edges;
        private double offsetX = 43.125214;
        private double offsetY = -77.632098;
        private double scaleFactor = 100000;

        public ZoomableJPanel(Graph graph) {
            this.scale = 1.0;
            this.translateX = 0;
            this.translateY = 0;
            this.lastMousePoint = null;
            setBackground(new Color(102, 204, 102));

            this.nodes = graph.getNodes();
            this.edges = graph.getEdges();

            addMouseWheelListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            AffineTransform at = new AffineTransform();
            at.translate(translateX, translateY);
            at.scale(scale, scale);
            g2d.setTransform(at);

            for (Node node : nodes) {
                drawNode(g2d, node);
            }

            for (Edge edge : edges) {
                drawEdge(g2d, edge);
            }

        }

        private void drawNode(Graphics2D g2d, Node node) {
            int nodeRadius = 5;
            g2d.setColor(new Color(245, 245, 245));

            int x = (int) ((node.getLatitude() - offsetX) * scaleFactor);
            int y = (int) ((node.getLongitude() - offsetY) * scaleFactor);

            g2d.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);

        }

        private void drawEdge(Graphics2D g2d, Edge edge) {
            g2d.setColor(new Color(245, 245, 245));
            g2d.setStroke(new BasicStroke(2));

            int x1 = (int) ((edge.getNode1().getLatitude() - offsetX) * scaleFactor);
            int y1 = (int) ((edge.getNode1().getLongitude() - offsetY) * scaleFactor);
            int x2 = (int) ((edge.getNode2().getLatitude() - offsetX) * scaleFactor);
            int y2 = (int) ((edge.getNode2().getLongitude() - offsetY) * scaleFactor);

            g2d.drawLine(x1, y1, x2, y2);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            /*
             * Zooms in or out depending on the direction of the mouse wheel movement.
             */

            int notches = e.getWheelRotation();
            double scaleFactor = 1.1;

            if (notches < 0) {
                scale *= scaleFactor;
            } else {
                scale /= scaleFactor;
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

    }

}
