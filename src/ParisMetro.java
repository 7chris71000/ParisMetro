import net.datastructures.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class ParisMetro {

    //ParisMetro,readMetro and print taken from lab 9 and added code
    static Graph<String,Integer> sGraph;
    static Hashtable<String,Vertex> vertices = new Hashtable<>();


    public ParisMetro( String file ) throws Exception{
        sGraph = new AdjacencyMapGraph<String,Integer>(true);//this being true means that it is directed
        readMetro(file);
    }

    public static Graph<String, Integer> getsGraph() {
        return sGraph;
    }

    protected static void readMetro(String fileName ) throws Exception {
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
                //set all -1 paths (walking) to 90 since they will take 90 seconds to traverse
                if(weight == -1){
                    weight = 90;
                }
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

    protected Vertex<String> getVertex(String vert) throws Exception {
        // Go through vertex list to find vertex -- why is this not a map
        for (Vertex<String> vs : sGraph.vertices()) {
            if (vs.getElement().equals(vert)) {
                return vs;
            }
        }
        throw new Exception("Vertex not in graph: " + vert);
    }

    void printAllShortestDistances(String fromVert) throws Exception {
        Vertex<String> vSource = getVertex(fromVert);

        GraphAlgorithms dj = new GraphAlgorithms();
        Map<Vertex<String>, Integer> result = dj.shortestPathLengths(sGraph, vSource);

        // Print shortest path to named cities
        for (Vertex<String> vGoal : sGraph.vertices()) {
            if (vGoal.getElement().length() > 0) {
                System.out.println(vSource.getElement() + " to " + vGoal.getElement() + " = " + result.get(vGoal));
            }
        }
        return;
    }

    public static String readVertex() throws IOException {
        System.out.print("[Input] Vertex: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }
    //this is to be used with 2i)
//    public static <V,E> PositionalList<Edge<E>> findPath(Graph<V,E> g,Vertex <V> u,Vertex<V> v) throws Exception{
//        GraphAlgorithms graphAlgorithm = new GraphAlgorithms();
//
//        Map<Vertex<V>,Edge<E>> map = new ProbeHashMap<>();
//
//        try{
//            map = DFSComplete(g);
//        } catch (Exception f){
//            System.out.print("test6");
//        }
//
//        PositionalList<Edge<E>> positionalList = new LinkedPositionalList<>();
//
//        try{
//            positionalList = graphAlgorithm.constructPath(g,u,v,map);
//        } catch(Exception w){
//            System.out.println(w);
//        }
//
//
//        return positionalList;
//    }



    public static <V> Set<Vertex<V>> DFS(Graph<V,Integer> g, Vertex<V> u, Set<Vertex<V>> known, Map<Vertex<V>,Edge<Integer>> forest) {
        known.add(u);                              // u has been discovered
        for (Edge<Integer> e : g.outgoingEdges(u)) {     // for every outgoing edge from u
            Vertex<V> v = g.opposite(u, e);
            int eLength = e.getElement();
            if(eLength != 90){
                if (!known.contains(v)) {
                    forest.put(v, e);                      // e is the tree edge that discovered v
                    DFS(g, v, known, forest);              // recursively explore from v
                }
            }
        }
        return known;
    }

    public void findPath(Vertex<String> vertex){//this is a merged method inspired by datastructures GraphAlgorithms methods DFS, constructPath and completeDFS
        Set<Vertex<String>> known = new HashSet<>();
        Map<Vertex<String>,Edge<Integer>> forest = new ProbeHashMap<>();
        Set<Vertex<String>> result = DFS(sGraph, vertex, known, forest);

        System.out.println("All Stations belonging to the same line of: "+vertex.getElement());
        for(Vertex<String> s : result){
            System.out.println(s.getElement());
        }
    }

    public static void main(String[] args){
//        if ( args.length < 1 ) {
//            System.err.println( "Usage: java SimpleGraph fileName" );
//            System.exit(-1);
//        }
        try{
            ParisMetro sGraph = new ParisMetro("metro.txt");
//            sGraph.print();
            System.out.println("Source Vertex for shortest path: ");
            sGraph.printAllShortestDistances(readVertex());

            Vertex<String> u = vertices.get("1");
            Vertex<String> v = vertices.get("334");
//            System.out.println(u);
//            System.out.println(v);

//            System.out.println(sGraph.findPath(u));
            sGraph.findPath(u);

//            System.out.println(sGraph.toString());

        } catch(Exception e){
            System.out.println("test");
        }
    }
}
