/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.event.GraphEvent.Edge;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author Crap
 */
public class JungTest {

    private int vertexCount = 0;
    private int edgeCount = 0;
    private ArrayList<String> lmd = new ArrayList<>();
    private DirectedSparseGraph g = new DirectedSparseGraph();

    public JungTest() {
        //fillGraph(1, lmd.get(0));

    }

    public void addVertex(String vertex) {
        g.addVertex(vertex);
    }

    public void addEdge(String edge, String parent, String child) {
        g.addEdge(edge, parent, child);
    }

    public DirectedSparseGraph getGraph() {
        return g;
    }

    public void visualize() {
        VisualizationImageServer vs
                = new VisualizationImageServer(
                        new CircleLayout(g), new Dimension(1500, 800));

        Transformer transformer = new Transformer() {

            @Override
            public Object transform(Object i) {
                return ((String) i).replace(" ", "");
            }
        };
        vs.getRenderContext().setVertexLabelTransformer(transformer);

        JFrame frame = new JFrame();

        JPanel container = new JPanel();
        JScrollPane scrPane = new JScrollPane(container);
        frame.add(scrPane); // similar to getContentPane().add(scrPane);
        container.add(vs);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1500, 1000);
    }

    public void fillGraph(int i, String parent) {

        System.err.println("IIIIIIIIIIIIIII: " + i);

        if (i == lmd.size()) {
            return;
        }
        /* g.addVertex(lmd.get(0));
        String prev = lmd.get(0);
        for(String str : lmd.subList( 1, lmd.size()) ){
            String[] s = str.split(" ");
        }*/
        if (i >= lmd.size()) {
            return;
        }
        String[] arr = lmd.get(i).split(" ");
        for (String str : arr) {

            while (g.containsVertex(str)) {
                str = str + " ";
            }
            while (g.containsEdge(i)) {
                i = i + 1000000;
            }

            System.out.println("DONE: " + str);
            if (!Parser.getTerminals().contains(parent)) {
                g.addVertex(str);

                g.addEdge(i, parent, str);
            } else {
                g.addEdge(i, parent, parent + " ");
            }

            // System.out.println("---"+str+"---" + " " + "---"+ parent+"---");
            i++;

            fillGraph(i, str);
        }

    }

}
