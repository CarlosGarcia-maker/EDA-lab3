//CODIGO NUEVO
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StopwatchCPU;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

  /**
  Experimento preliminar pra validar la integración de la simulación.
  */
public class Experimento {
    private static final int[] ALEATORIA = {128, 256, 512, 1024, 2048};
    private static final int[] CRECIENTE = {128, 256, 512};
    private static final String[] IMPLEMENTACIONES = {"BST", "RBBST", "SCH", "LPH"};
    private static final int REPETICIONES = 5;
    private static final int CALENTAMIENTO = 3;
    private static final int B_MIN = 16;
    private static final int B_MAX = 512;
    private static final double UMBRAL_SEGUNDOS = 0.05;

    /**
     * Punto de entrada principal para ejecutar los 160 lotes oficiales y generar simulacion.csv.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        Out csv = new Out("simulacion.csv");
        csv.println("serie,n,filas,columnas,repeticion,seedTopologia,seedDemanda,seedOrden,implementacion,B,tiempoLote,tiempoSimulacion,mensajes,transmisiones,periodos");

        int[] lotesPorTamano = new int[ALEATORIA.length];

        // 1. Serie Aleatoria (100 lotes) y calibracion de B_n con r = 0
        for (int idx = 0; idx < ALEATORIA.length; idx++) {
            int n = ALEATORIA[idx];
            int k = Integer.numberOfTrailingZeros(n);
            int filas = 1 << (k / 2);
            int columnas = 1 << ((k + 1) / 2);

            GeneradorTopologia topo = new GeneradorTopologia(filas, columnas);
            List<String> ordenBase = obtenerOrdenCreciente(topo);

            int Bn = calibrarB(topo, ordenBase);
            lotesPorTamano[idx] = Bn;
            System.out.println("Calibracion n=" + n + " -> B_n=" + Bn);

            ejecutarSerie("Aleatoria", topo, ordenBase, Bn, csv);
        }

        // 2. Serie Creciente por identificador (60 lotes)
        for (int idx = 0; idx < CRECIENTE.length; idx++) {
            int n = CRECIENTE[idx];
            int k = Integer.numberOfTrailingZeros(n);
            int filas = 1 << (k / 2);
            int columnas = 1 << ((k + 1) / 2);

            GeneradorTopologia topo = new GeneradorTopologia(filas, columnas);
            List<String> ordenBase = obtenerOrdenCreciente(topo);
            int Bn = lotesPorTamano[idx];

            ejecutarSerie("Creciente", topo, ordenBase, Bn, csv);
        }

        csv.close();
        System.out.println("Experimento finalizado. Archivo simulacion.csv generado correctamente.");
    }

    /**
     * Ejecuta las 5 repeticiones y las 4 alternativas de mapa para una serie y tamano dados.
     */
    private static void ejecutarSerie(String serie, GeneradorTopologia topo, List<String> ordenBase, int Bn, Out csv) {
        int n = topo.cantidadVertices();
        int filas = topo.filas();
        int columnas = topo.columnas();

        for (int r = 0; r < REPETICIONES; r++) {
            long seedTopologia = (long) n + r;
            long seedDemanda = 2000003L + 31L * n + r;
            long seedOrden = 4000037L + 31L * n + r;

            Generador generador = new Generador(topo, seedTopologia, seedDemanda);

            List<String> ordenClaves;
            if ("Aleatoria".equals(serie)) {
                ordenClaves = new ArrayList<String>(ordenBase);
                Collections.shuffle(ordenClaves, new Random(seedOrden));
            } else {
                ordenClaves = ordenBase;
            }

            int periodosEsperados = -1;

            // Rotacion del orden BST, RBBST, SCH, LPH con desplazamiento r mod 4
            for (int offset = 0; offset < IMPLEMENTACIONES.length; offset++) {
                String impl = IMPLEMENTACIONES[(r + offset) % IMPLEMENTACIONES.length];
                double[] medicion = ejecutarLote(generador, ordenClaves, impl, Bn);

                double tiempoLote = medicion[0];
                double tiempoSimulacion = tiempoLote / Bn;
                int mensajes = (int) medicion[1];
                int transmisiones = (int) medicion[2];
                int periodos = (int) medicion[3];

                if (periodosEsperados == -1) {
                    periodosEsperados = periodos;
                } else if (periodos != periodosEsperados) {
                    throw new IllegalStateException("Diferencia en periodos entre implementaciones para n=" + n + ", r=" + r);
                }

                csv.println(
                    serie + "," + n + "," + filas + "," + columnas + "," + r + ","
                    + seedTopologia + "," + seedDemanda + "," + seedOrden + ","
                    + impl + "," + Bn + ","
                    + String.format(Locale.US, "%.9f", tiempoLote) + ","
                    + String.format(Locale.US, "%.9f", tiempoSimulacion) + ","
                    + mensajes + "," + transmisiones + "," + periodos
                );
            }
            System.out.println("Completado: serie=" + serie + ", n=" + n + ", r=" + r);
        }
    }

    /**
     * Calibra B_n usando la instancia r=0 de la serie Aleatoria hasta alcanzar 0.05s o B=512.
     */
    private static int calibrarB(GeneradorTopologia topo, List<String> ordenBase) {
        int n = topo.cantidadVertices();
        long seedTopologia = (long) n;
        long seedDemanda = 2000003L + 31L * n;
        long seedOrden = 4000037L + 31L * n;

        Generador generador = new Generador(topo, seedTopologia, seedDemanda);
        List<String> ordenAleatoria = new ArrayList<String>(ordenBase);
        Collections.shuffle(ordenAleatoria, new Random(seedOrden));

        int B = B_MIN;
        while (B < B_MAX) {
            double menorTiempo = Double.POSITIVE_INFINITY;
            for (int i = 0; i < IMPLEMENTACIONES.length; i++) {
                double tLote = ejecutarLote(generador, ordenAleatoria, IMPLEMENTACIONES[i], B)[0];
                if (tLote < menorTiempo) {
                    menorTiempo = tLote;
                }
            }
            if (menorTiempo >= UMBRAL_SEGUNDOS) {
                return B;
            }
            B *= 2;
        }
        return B_MAX;
    }

    /**
     * Construye las tablas, calienta y mide exclusivamente un lote de B ejecuciones para un mapa.
     */
    private static double[] ejecutarLote(Generador generador, List<String> ordenClaves, String impl, int B) {
        int n = generador.n();
        Mapa<String, Integer>[] tablas = crearTablas(n, impl);
        Simulador simulador = new Simulador(generador, tablas, ordenClaves);

        for (int c = 0; c < CALENTAMIENTO; c++) {
            simulador.nuevaEjecucion().simular();
        }

        Simulador.Ejecucion[] ejecuciones = new Simulador.Ejecucion[B];
        for (int b = 0; b < B; b++) {
            ejecuciones[b] = simulador.nuevaEjecucion();
        }
        Simulador.Resultado[] resultados = new Simulador.Resultado[B];

        StopwatchCPU reloj = new StopwatchCPU();
        for (int b = 0; b < B; b++) {
            resultados[b] = ejecuciones[b].simular();
        }
        double tiempoLote = reloj.elapsedTime();

        int periodosRef = resultados[0].periodos;
        for (int b = 0; b < B; b++) {
            if (resultados[b].entregados != 4 * n) {
                throw new IllegalStateException("Entregados incorrectos en lote.");
            }
            if (resultados[b].transmisiones != 16 * n) {
                throw new IllegalStateException("Transmisiones incorrectas en lote.");
            }
            if (!ejecuciones[b].filasVacias()) {
                throw new IllegalStateException("Quedaron mensajes pendientes en las filas.");
            }
            if (resultados[b].periodos != periodosRef) {
                throw new IllegalStateException("Periodos inconsistentes dentro del lote.");
            }
        }

        return new double[] {tiempoLote, resultados[0].entregados, resultados[0].transmisiones, periodosRef};
    }

    /**
     * Retorna la lista de los n identificadores ordenada lexicograficamente mediante String.compareTo.
     */
    private static List<String> obtenerOrdenCreciente(GeneradorTopologia topo) {
        int n = topo.cantidadVertices();
        List<String> lista = new ArrayList<String>(n);
        for (int i = 0; i < n; i++) {
            lista.add(topo.identificador(i));
        }
        Collections.sort(lista);
        return lista;
    }

    /**
     * Instancia el arreglo de n mapas vacios segun el nombre de la implementacion.
     */
    @SuppressWarnings("unchecked")
    private static Mapa<String, Integer>[] crearTablas(int n, String impl) {
        Mapa<String, Integer>[] tablas = (Mapa<String, Integer>[]) new Mapa[n];
        for (int i = 0; i < n; i++) {
            switch (impl) {
                case "BST":
                    tablas[i] = new MapaBST<String, Integer>();
                    break;
                case "RBBST":
                    tablas[i] = new MapaRBBST<String, Integer>();
                    break;
                case "SCH":
                    tablas[i] = new MapaSCH<String, Integer>();
                    break;
                case "LPH":
                    tablas[i] = new MapaLPH<String, Integer>();
                    break;
                default:
                    throw new IllegalArgumentException("Implementacion desconocida: " + impl);
            }
        }
        return tablas;
    }
}
