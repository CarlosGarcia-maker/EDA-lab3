import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.BreadthFirstPaths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
prepara la topologia, los caminos mas cortso y la demanda
*/
public class Generador {
  private GeneradorTopologia topologia;
  private Graph grafo;
  private int n;
  private BreadthFirstPaths[] bfs;
  private List<Mensaje>[] demanda;

  /**
  Construye el generador, inicializa el grafo,ejucuta el BFS desde cada vertice y prepara los mensajes de demanda.
  @param topologia Instancia del generador docente
  @param semillaTpologia Semilla para la generacion del grafo.
  @param semillaDemanda Semilla para la selección de deestinos de los mensajes.
  */
  public Generador(GeneradorTopologia topologia, long semillaTopologia, long demanda){
    this.topologia = topologia;
    this.n = topologia.cantidadVertices();

    this.grafo = topologia.generar(semillaTopologia);
    this.bfs = new BreadthFirstPaths[n];
    for (int i = 0; i<n; i++){
      bfs[i] = new BreadthFirstPaths(grafo, i);
    }
    this.demanda = (List<Mensaje>[]) new ArrayList[n];
    Random demandaRandom = new Random(semillaDemanda);
    int idMensaje = 0;

    for (int i= 0; i<n; i++){
      List<Integer> candidatos = new ArrayList<>();

      for (int j = 0; j<n; j++){
        if (bfs[i].hasPathTo(j) && bfs[i].distTo(j) == 4){
          candidatos.add(j);
        }
      }
      if(candidatos.isEmpty()) {
        throw new IllegalStateException("No se encontraron candidatos a 4 saltos para el origen: " + s);
      }
      demanda[i] = new ArrayList<>();
      for (int p =0; p<4; p++){
        int a = azarDemanda.nextInt(candidatos.size());
        int n = candidatos.get(a);
        Mensaje m = new Mensaje(idMensaje, topologia.identificador(i), topologia.identificador(n));
        demanda[i].add(m);
        idMensaje++;
      }
    }
  }
  /**
  Retorna la topoligia usada.
  @return Instancia de GeneradorTopologia
  */
  public GeneradorTopologia topologia(){
    return topologia;
  }
  /**
  Retorna el grafo generado
  @return Grafo de la red
  */
  public Graph grafo() {
    return grafo; 
  }
  /**
  Retorna la cantidad de vértices de la red.
  @return Número de estaciones
  */
  public int n() {
    return n;
  }
  /**
  Permite consultar las distancias y rutas precalculadas desde un vértice dado.
  @param v Indice del vértice origen
  @return El objeto BreadthFirstPaths correspondiente al vértice
  */
  public BreadthFirstPaths bfs(int v) {
    return bfs[v];
  }
  /**
  Retorna la lsita de mensajes cuyo origen es el vértice dado.
  @param v Indice del vértice origen
  @return Lista de mensajes generados para el origen
  */
  public List<Mensaje>[] demanda(int v) {
    return demanda[v];
  }
