// CODIGO NUEVO
import edu.princeton.cs.algs4.BreadthFirstPaths;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.LinkedQueue;
import java.util.ArrayList;
import java.util.List;

/**
Prepara las tablas de ruteo y administra las ejecuciones de la red
*/
public class Simulador {
  private Generador generador;
  private Mapa<String, Integer>[] tablas;

    /**
    Construye el simulador y prepara las tablas para una alternativa de Mapa.
    @param generador Instancia con la topologia, grafo, BFS y demanda listos
    @param tablas Arreglo de n mapas instanciados (vacios) correspondientes a la implementacion a evaluar
    */
  public Simulador(Generador generador, Mapa<String, Integer>[] tablas) {
    this.generador = generador;
    this.tablas = tablas;
    prepararTablas();
  }
    /**
    Llena las tablas de ruteo eligiendo el vecino que reduce la distancia al destino en uno.
    Si hay empate, elige el vecino de menor indice.
    */
    
    /**
     * Construye el simulador y prepara las tablas insertando las claves en el orden indicado.
     * @param generador Instancia con la topologia, grafo, BFS y demanda listos
     * @param tablas Arreglo de n mapas instanciados (vacios) correspondientes a la implementacion a evaluar
     * @param ordenClaves Lista con los n identificadores en el orden de insercion deseado
     */
    


  
  public Simulador(Generador generador, Mapa<String, Integer>[] tablas, List<String> ordenClaves) {
    if (generador == null || tablas == null || ordenClaves == null) {
      throw new IllegalArgumentException("Los argumentos del simulador no pueden ser nulos.");
    }
    if (tablas.length != generador.n() || ordenClaves.size() != generador.n()) {
      throw new IllegalArgumentException("Las dimensiones de tablas y ordenClaves deben coincidir con n.");
    }
    this.generador = generador;
    this.tablas = tablas;
    prepararTablas(ordenClaves);
  }

  private static List<String> ordenPorDefecto(Generador generador) {
    if (generador == null) {
      throw new IllegalArgumentException("El generador no puede ser nulo.");
    }
    int n = generador.n();
    GeneradorTopologia topo = generador.topologia();
    List<String> lista = new ArrayList<String>(n);
    for (int i = 0; i < n; i++) {
      lista.add(topo.identificador(i));
    }
    return lista;
  }

    /**
     * Llena las tablas de ruteo eligiendo el vecino que reduce la distancia al destino en uno.
     * Si hay empate, elige el vecino de menor indice.
     * @param ordenClaves Lista de identificadores en el orden en que se insertaran en cada tabla
     */

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
              if (mejorVecino == -1 || w < mejorVecino){
                mejorVecino = w;
              }
            }
          }
          if (mejorVecino != -1){
            tablas[i].put(topo.identificador(j), mejorVecino);
          }
        }
      }
    }
  }
    /**
    Crea y retorna una nueva ejecucion con estado independiente.
    @return Una intancia de Ejecucion lista para simular
    */
  public Ejecucion nuevaEjecucion() {
    return new Ejecucion();
  }
    /**
    Clase anidada para encapsular los datos de retorno de la simulacion
    */
  public static class Resultado {
    public int entregados;
    public int transmisiones;
    public int periodos;
    
    public Resultado( int entregados, int transmisiones, int periodos){
      this.entregados = entregados;
      this.transmisiones = transmisiones;
      this.periodos = periodos;
    }
  }
  
    /**
     * Representa el estado aislado de una ejecucion unica, compartiendo las tablas del Simulador.
     */

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

  
        /**
         * Operacion auxiliar para agregar un vertice a la cola de activos sin duplicarlo.
         * @param v Indice del vertice a agendar
         */
    private void agendar(int v) {
      if (!enAgenda[v]) {
        activos.enqueue(v);
        enAgenda[v] = true;
      }
    }

        /**
         * Verifica si todas las filas de mensajes de los vertices estan vacias.
         * @return true si ninguna estacion tiene mensajes pendientes; false en caso contrario
         */
    public boolean filasVacias() {
      for (int i = 0; i < filas.length; i++) {
        if (!filas[i].isEmpty()) {
          return false;
        }
      }
      return true;
    }

        /**
         * Ejecuta el ruteo completo de los mensajes hasta que la red quede inactiva.
         * @return Objeto Resultado con los contadores finales
         */
    public Resultado simular() {
      int n = generador.n();
      GeneradorTopologia topo = generador.topologia();
      int periodo = 0;
      int entregados = 0;
      int transmisiones = 0;
      for (int v = 0; v < n; v++) {
        List<Mensaje> demanda = generador.demanda(v);
        for (Mensaje m : demanda) {
          filas[v].enqueue(m);
        }
        if (!filas[v].isEmpty()) {
          agendar(v);
        }
      }
      while (!activos.isEmpty()) {
        int cantidad = activos.size();
        for (int i = 0; i < cantidad; i++) {
          int v = activos.dequeue();
          enAgenda[v] = false;
          Mensaje m = filas[v].dequeue();
          if (!filas[v].isEmpty()) {
            agendar(v);
          }
          int w = tablas[v].get(m.destino());
          transmisiones++;
          if (m.destino().equals(topo.identificador(w))) {
            entregados++;
          } else {
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
