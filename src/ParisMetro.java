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

    void printAllShortestDistances(String vert) throws Exception {
        Vertex<String> vSource = getVertex(vert);

        GraphAlgorithms dj = new GraphAlgorithms();
        Map<Vertex<String>, Integer> result = dj.shortestPathLengths(sGraph, vSource);

        // Print shortest path to named cities
        for (Vertex<String> vGoal : sGraph.vertices()) {
            if (vGoal.getElement().length() > 2) {
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

    //Copy pasted from graphsAlgorithms
    public static <V,E> void DFS(Graph<V,E> g, Vertex<V> u, Set<Vertex<V>> known, Map<Vertex<V>,Edge<E>> forest) {
        known.add(u);                              // u has been discovered
        for (Edge<E> e : g.outgoingEdges(u)) {     // for every outgoing edge from u
            Vertex<V> v = g.opposite(u, e);
            if (!known.contains(v)) {
                forest.put(v, e);                      // e is the tree edge that discovered v
                DFS(g, v, known, forest);              // recursively explore from v
            }
        }
    }

    /**
     * Returns an ordered list of edges comprising the directed path from u to v.
     * If v is unreachable from u, or if u equals v, an empty path is returned.
     *
     * @param g Graph instance
     * @param u Vertex beginning the path
     * @param v Vertex ending the path
     * @param forest must be a map that resulting from a previous call to DFS started at u.
     */
    public static <V,E> PositionalList<Edge<E>> constructPath(Graph<V,E> g, Vertex<V> u, Vertex<V> v, Map<Vertex<V>,Edge<E>> forest) {

        PositionalList<Edge<E>> path = new LinkedPositionalList<>();

        if (forest.get(v) != null) {// v was discovered during the search
            System.out.println("gotHERE1");
            Vertex<V> walk = v;                  // we construct the path from back to front
            while (walk != u) {
                System.out.println("gotHERE2");
                Edge<E> edge = forest.get(walk);
                System.out.println("gotHERE3");

                path.addFirst(edge);               // add edge to *front* of path
                System.out.println("gotHERE4");


                try{
                    walk = g.opposite(walk, edge);     // repeat with opposite endpoint
                } catch (Exception e){
                    System.out.println(e);
                }
                System.out.println(edge.getElement());
                System.out.println(walk.getElement());
            }
        }
        return path;
    }

    /**
     * Performs DFS for the entire graph and returns the DFS forest as a map.
     *
     * @return map such that each nonroot vertex v is mapped to its discovery edge
     * (vertices that are roots of a DFS trees in the forest are not included in the map).
     */
    public static <V,E> Map<Vertex<V>,Edge<E>> DFSComplete(Graph<V,E> g) {
        Set<Vertex<V>> known = new HashSet<>();
        Map<Vertex<V>,Edge<E>> forest = new ProbeHashMap<>();
        for (Vertex<V> u : g.vertices())
            if (!known.contains(u))
                DFS(g, u, known, forest);            // (re)start the DFS process at u
        return forest;
    }


    public static <V,E> void BFS(Graph<V,E> g, Vertex<V> s,
                                 Set<Vertex<V>> known, Map<Vertex<V>,Edge<E>> forest) {
        PositionalList<Vertex<V>> level = new LinkedPositionalList<>();
        known.add(s);
        level.addLast(s);                         // first level includes only s
        while (!level.isEmpty()) {
            PositionalList<Vertex<V>> nextLevel = new LinkedPositionalList<>();
            for (Vertex<V> u : level)
                for (Edge<E> e : g.outgoingEdges(u)) {
                    Vertex<V> v = g.opposite(u, e);
                    if (!known.contains(v)) {
                        known.add(v);
                        forest.put(v, e);                 // e is the tree edge that discovered v
                        nextLevel.addLast(v);             // v will be further considered in next pass
                    }
                }
            level = nextLevel;                      // relabel 'next' level to become the current
        }
    }

    /**
     * Performs BFS for the entire graph and returns the BFS forest as a map.
     *
     * @return map such that each nonroot vertex v is mapped to its discovery edge
     * (vertices that are roots of a BFS trees in the forest are not included in the map).
     */
    public static <V,E> Map<Vertex<V>,Edge<E>> BFSComplete(Graph<V,E> g) {
        Map<Vertex<V>,Edge<E>> forest = new ProbeHashMap<>();
        Set<Vertex<V>> known = new HashSet<>();
        for (Vertex<V> u : g.vertices())
            if (!known.contains(u))
                BFS(g, u, known, forest);
        return forest;
    }






    //this is to be used with 2i)
    public static <V,E> PositionalList<Edge<E>> findPath(Graph<V,E> g,Vertex <V> u,Vertex<V> v) throws Exception{

        Map<Vertex<V>,Edge<E>> map = new ProbeHashMap<>();

        try{
            map = DFSComplete(g);
        } catch (Exception f){
            System.out.print("test6");
        }

        PositionalList<Edge<E>> positionalList = new LinkedPositionalList<>();

        try{
            positionalList = constructPath(g,u,v,map);
        } catch(Exception w){
            System.out.println(w);
        }


        return positionalList;
    }

    public static void main(String[] args){
//        if ( args.length < 1 ) {
//            System.err.println( "Usage: java SimpleGraph fileName" );
//            System.exit(-1);
//        }
        try{
            ParisMetro sGraph = new ParisMetro("metro.txt");
            sGraph.print();
            System.out.println("Source Vertex for shortest path: ");
//            sGraph.printAllShortestDistances(readVertex());

            Vertex<String> u = vertices.get("134");
            Vertex<String> v = vertices.get("334");
            System.out.println(u);
            System.out.println(v);

            System.out.println(findPath(sGraph.getsGraph(), u, v));

//            System.out.println(sGraph.toString());

        } catch(Exception e){
            System.out.println("test");
        }
    }
}
