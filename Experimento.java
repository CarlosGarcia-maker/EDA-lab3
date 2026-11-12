import edu.princeton.cs.algs4.StopwatchCPU;

/**
Experimento preliminar pra validar la integración de la simulación.
*/
public class Experimento {
  public static void main(String[] args){
    int n = 128;
    int filas = 8;
    int columnas = 16;
    int r = 0;

  long seedTopologia = n + r;
  long seedDemanda = 2000003L + 31L * n + r;
    System.out.println("Iniciando experimento preliminar con n = " + n);
    GeneradorTopologia topo = new GeneradorTopologia(filas, columnas);
    Generador generador = new Generador(topo, seedTopologia, seedDemanda);

  Mapa<String,Integer>[] tablas = new Mapa[n];
    for (int i=0; i<n; i++){
      tablas[i] = new MapaBST<String, Integer>();
    }
    Simulador simulador = new Simulador(generador,tablas);
    for (int i=0; i<3; i++){
      simulador.nuevaEjecucion().simular();
    }
    int B = 16;
    Simulador.Ejecucion[] lote = new Simulador.Ejecucion[B];
    for (int b=0; b<B; b++){
      lote[b] = simulador.nuevaEjecucion();
    }
    Simulador.Resultado[] resultados = new Simulador.Resultado[B];
    StopwatchCPU reloj = new StopwatchCPU();
    for (int b=0; b<B; b++){
      resultados[b] = lote[b].simular();
    }
    double tiempoLote = reloj.elapsedTime();

  Simulador.Resultado res = resultados[0];
    System.out.println("Mensajes entregados : " + res.entregados + " (Esperado: " + (4*n) + ")");
    System.out.println("Transmisiones : " + res.transmisiones + " (Esperado: " + (16*n) + ")");
    System.out.println("Tiempo total lote : " + tiempoLote + " s (B=" + B + ")");
    System.out.println("Prueba preliminar finalizada");
  }
}
    
