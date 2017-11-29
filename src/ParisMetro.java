import net.datastructures.AdjacencyMapGraph;
import net.datastructures.Edge;
import net.datastructures.Graph;
import net.datastructures.Vertex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ParisMetro {

    //ParisMetro,readMetro and print taken from lab 9 and added code
    static Graph<String,Integer> sGraph;
    static Hashtable<String,Vertex> vertices = new Hashtable<>();


    public ParisMetro( String file ) throws Exception{
        sGraph = new AdjacencyMapGraph<String,Integer>(true);//this being false means that it is not directed
        readMetro(file);
    }

    protected static void readMetro( String fileName ) throws Exception {
        BufferedReader graphFile = new BufferedReader( new FileReader(fileName));

        // Create a hash map to store all the vertices read


        // Read the edges and insert
        String line;
        boolean start = false;


        while( ( line = graphFile.readLine( ) ) != null ) {


            if(start) {

                //progress to the correct numbers
                StringTokenizer st = new StringTokenizer(line);
                //this is where the string is broken up
                if (st.countTokens() != 3)
                    throw new IOException("Incorrect input file at line " + line);
                String source = st.nextToken();
                String dest = st.nextToken();
                Integer weight = new Integer(st.nextToken());
                Vertex<String> sv = vertices.get(source);
                if (sv == null) {
                    // Source vertex not in graph -- insert
                    sv = sGraph.insertVertex(source);
                    vertices.put(source, sv);
                }
                Vertex<String> dv = vertices.get(dest);
                if (dv == null) {
                    // Destination vertex not in graph -- insert
                    dv = sGraph.insertVertex(dest);
                    vertices.put(dest, dv);
                }
                // check if edge is already in graph
                if (sGraph.getEdge(sv, dv) == null) {
                    // edge not in graph -- add
                    sGraph.insertEdge(sv, dv, weight);
                }
            }
            if(line.equals("$")){
                start = true;
            }
        }
    }

    void print() {
        System.out.println( "Vertices: " + sGraph.numVertices() +
                " Edges: " + sGraph.numEdges());

        for( Vertex<String> vs : sGraph.vertices() ) {
            System.out.println( vs.getElement() );
        }
        System.out.println(" ");
        for( Edge<Integer> es : sGraph.edges() ) {
            System.out.println( es.getElement() );
        }
//        this is not actually used but can be when i need an edge
//        if(sGraph.getEdge(vertices.get("361"),vertices.get("208")) != null){
//            System.out.println("Edge: "+sGraph.getEdge(vertices.get("361"),vertices.get("208")).getElement());
//        }
        return;
    }

    public static void main(String[] args){
//        if ( args.length < 1 ) {
//            System.err.println( "Usage: java SimpleGraph fileName" );
//            System.exit(-1);
//        }
        try{
            ParisMetro sGraph = new ParisMetro("metro.txt");
            sGraph.print();
        } catch(Exception e){
            System.out.println(e);
        }
    }
}
