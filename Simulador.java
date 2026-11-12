import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.BreadhtFirstPaths;
import java.util.List;

public class Simulador {
  private Generador generador;
  private Map<String, Integer>[] tablas;

public Simulador(Generador generadro, Map<String, Integer[] tablas) {
  this.generador = generador;
  this.tablas = tablas;
  prepararTablas();
}

private void prepararTablas() {
  int n = generador.n();
  Graph grafo = generador.grafo();
  GeneradorTopologia topo = generador.topologia();
  for (int i = 0; i<n; i++){
    for (int j = 0; j<n; j++){
      if ( i!=j){
        BreadthFirstPaths bfsJ = generador.bfs(j);
        int distanciaBuscada = bfsJ.distTo(i) - 1;
        int mejorVecino = -1;
        for (int w : grafo.adj(i)){
          if (bfsJ.distTo(w) == distanciaBuscada){
            if (mejorVcino == -1 || w < mejorVecino){
              mejorVecino = w;
            }
          }
        }
        if (mejorVecino != -1){
          tablas[i].put(topo.identificador(d), mejorVecino);
        }
      }
    }
  }
}

public Ejecucion nuevaEjecucion(){
  return new Ejecucion();
}
  public static class Resultado {
    public int entregados;
    public int transmisiones;
    public int periodos;

  public Resultado( int entregados, int transmisiones, int periodos){
    this.entegados = entregados;
    this.transmisiones = transmisiones;
    this.periodos = periodos;
  }
  }

public class Ejecucion{
  private LinkedQueue<Mensaje>[] filas;
  private LinkedQueue<Integer> activos;
  private boolean[] enAgenda;

  public Ejecucion(){
    int n = generador.n();
    this.filas = (LinkedQueue<Mensaje>[]) new LinkedQueue[n];
    for (int i = 0; i<n; i++){
      filas[i] = new LinkedQueue<Mensaje>();
    }
    this.activos = new LinkedQueue<Integer>();
    this.enAgenda = new boolean[n];
  }

  private void agendar(int v){
    if (!enAgenda[v]){
      activos.enqueue(v);
      enAgenda[v] = true;
    }
  }
  public Resultado simular(){
    int n = generador.n();
    GeneradorTopologia topo = generador.topologia();
    int periodo = 0;
    int entregadod = 0;
    int transmisiones = 0;
    for (int v = 0; v<n; v++){
      List<Mensaje> demanda = generador.demanda(v);
      for (Mensaje m : demanda){
        filas[v].enqueue(m);
      }
      if (!filas[v].isEmpty()){
        agendar(v);
      }
    }
    while(!activos.isEmpty()){
      int cantidad = activos.size();
      for (int i = 0; i<cantidad; i++){
        int v = activos.dequeue();
        enAgenda[v] = false;
        Mensaje m= filas[v].dequeue();

      if (!filas[v].isEmpty()){
        agendar(v);
      }

      int w = tablas[v].get(m.destino());
        transmisiones++;
        
      if (m.destino().equals(topo.identificador(w))){
        entregados++;
      }else{
        filas[w].enqueue(m);
        agendar(w);
      }
      }
      periodo++;
    }
    return new Resultado(entregados, transmisiones, periodo);
  }
}
}
