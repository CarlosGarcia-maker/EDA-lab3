import edu.princeton.cs.algs4.BreadthFirstPaths;
import edu.princeton.cs.algs4.Graph;
import java.util.List;

/**
 * Suite de pruebas de ruteo y equivalencia.
 * Verifica la demanda, validez de tablas de ruteo, invariantes de agenda,
 * vaciado de filas y equivalencia funcional entre los cuatro adaptadores de Mapa.
 */
public class PruebasRuteo {

    /**
     * Punto de entrada para ejecutar todas las pruebas de ruteo y equivalencia.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DE RUTEO Y EQUIVALENCIA ===");

        pruebaGeneradorYDemanda();
        pruebaValidezTablasDeRuteo();
        pruebaEquivalenciaMapas();
        pruebaOraculoMatematicoYFilasVacias();
        pruebaIndependenciaEjecuciones();
        pruebaTopologiaSinCaminosA4Saltos();

        System.out.println("=== TODAS LAS PRUEBAS PASARON EXITOSAMENTE ===");
    }

    /**
     * Prueba 1: Verifica reproducibilidad del grafo y demanda, 4 mensajes por origen
     * y distancia exacta de 4 saltos hacia cada destino.
     */
    private static void pruebaGeneradorYDemanda() {
        GeneradorTopologia topo = new GeneradorTopologia(8, 8);
        Generador gen1 = new Generador(topo, 64L, 2002003L);
        Generador gen2 = new Generador(topo, 64L, 2002003L);

        int n = gen1.n();
        verificar(gen1.grafo().E() == gen2.grafo().E(), "El grafo generado debe ser reproducible");

        for (int s = 0; s < n; s++) {
            List<Mensaje> lista1 = gen1.demanda(s);
            List<Mensaje> lista2 = gen2.demanda(s);
            verificar(lista1.size() == 4, "Cada origen debe generar exactamente 4 mensajes");
            verificar(lista2.size() == 4, "La demanda reproducida debe tener 4 mensajes por origen");

            BreadthFirstPaths bfsS = gen1.bfs(s);
            for (int k = 0; k < 4; k++) {
                Mensaje m1 = lista1.get(k);
                Mensaje m2 = lista2.get(k);
                verificar(m1.id() == m2.id() && m1.destino().equals(m2.destino()),
                        "La demanda debe ser identica con iguales semillas");
                int d = topo.indice(m1.destino());
                verificar(bfsS.distTo(d) == 4,
                        "Todo mensaje de demanda debe tener destino a distancia 4");
            }
        }
        System.out.println("[OK] Prueba 1: Reproducibilidad de demanda, 4 mensajes/origen y distancia 4.");
    }

    /**
     * Prueba 2: Verifica que cada tabla de ruteo tenga tamano n-1 y que el siguiente
     * salto sea un vecino adyacente que reduzca la distancia al destino en 1.
     */
    private static void pruebaValidezTablasDeRuteo() {
        GeneradorTopologia topo = new GeneradorTopologia(6, 8);
        Generador generador = new Generador(topo, 48L, 12345L);
        int n = generador.n();
        Graph grafo = generador.grafo();

        Mapa<String, Integer>[] tablas = crearArregloMapas(n, "RBBST");
        new Simulador(generador, tablas);

        for (int u = 0; u < n; u++) {
            verificar(tablas[u].size() == n - 1,
                    "Cada tabla debe contener exactamente n-1 destinos");
            for (int d = 0; d < n; d++) {
                if (u != d) {
                    Integer w = tablas[u].get(topo.identificador(d));
                    verificar(w != null, "Toda ruta hacia otro vertice debe existir en la tabla");

                    boolean esAdyacente = false;
                    for (int vecino : grafo.adj(u)) {
                        if (vecino == w) {
                            esAdyacente = true;
                            break;
                        }
                    }
                    verificar(esAdyacente, "El siguiente salto w debe ser adyacente a u");

                    BreadthFirstPaths bfsD = generador.bfs(d);
                    verificar(bfsD.distTo(w) == bfsD.distTo(u) - 1,
                            "El siguiente salto debe reducir la distancia al destino en exactamente 1");
                }
            }
        }
        System.out.println("[OK] Prueba 2: Tablas de tamano n-1 y siguiente salto optimo adyacente.");
    }

    /**
     * Prueba 3: Verifica que BST, RBBST, SCH y LPH arrojen exactamente los mismos
     * entregados, transmisiones y periodos, dejando las filas vacias.
     */
    private static void pruebaEquivalenciaMapas() {
        int filas = 8;
        int columnas = 16;
        int n = filas * columnas;
        long seedTopologia = 128L;
        long seedDemanda = 2003971L;

        GeneradorTopologia topo = new GeneradorTopologia(filas, columnas);
        Generador generador = new Generador(topo, seedTopologia, seedDemanda);

        Simulador.Resultado resBST = ejecutarConMapa(generador, n, "BST");
        Simulador.Resultado resRBBST = ejecutarConMapa(generador, n, "RBBST");
        Simulador.Resultado resSCH = ejecutarConMapa(generador, n, "SCH");
        Simulador.Resultado resLPH = ejecutarConMapa(generador, n, "LPH");

        verificar(resBST.entregados == resRBBST.entregados, "Entregados distintos entre BST y RBBST");
        verificar(resBST.transmisiones == resRBBST.transmisiones, "Transmisiones distintas entre BST y RBBST");
        verificar(resBST.periodos == resRBBST.periodos, "Periodos distintos entre BST y RBBST");

        verificar(resBST.entregados == resSCH.entregados, "Entregados distintos entre BST y SCH");
        verificar(resBST.transmisiones == resSCH.transmisiones, "Transmisiones distintas entre BST y SCH");
        verificar(resBST.periodos == resSCH.periodos, "Periodos distintos entre BST y SCH");

        verificar(resBST.entregados == resLPH.entregados, "Entregados distintos entre BST y LPH");
        verificar(resBST.transmisiones == resLPH.transmisiones, "Transmisiones distintas entre BST y LPH");
        verificar(resBST.periodos == resLPH.periodos, "Periodos distintos entre BST y LPH");

        System.out.println("[OK] Prueba 3: Equivalencia exacta entre BST, RBBST, SCH y LPH "
                + "(Entregados=" + resBST.entregados
                + ", Transmisiones=" + resBST.transmisiones
                + ", Periodos=" + resBST.periodos + ").");
    }

    /**
     * Prueba 4: Verifica el oraculo matematico (4*n entregas, 16*n transmisiones,
     * minimo 4 periodos por limite de 1 salto/periodo y filas vacias al finalizar).
     */
    private static void pruebaOraculoMatematicoYFilasVacias() {
        int filas = 6;
        int columnas = 6;
        int n = filas * columnas;

        GeneradorTopologia topo = new GeneradorTopologia(filas, columnas);
        Generador generador = new Generador(topo, 42L, 99L);

        Mapa<String, Integer>[] tablas = crearArregloMapas(n, "RBBST");
        Simulador simulador = new Simulador(generador, tablas);
        Simulador.Ejecucion ejecucion = simulador.nuevaEjecucion();

        verificar(ejecucion.filasVacias(), "Las filas deben iniciar vacias antes de simular");
        Simulador.Resultado res = ejecucion.simular();

        verificar(ejecucion.filasVacias(), "Todas las filas deben quedar vacias al terminar");
        verificar(res.entregados == 4 * n, "Se esperaban " + (4 * n) + " entregados, pero hubo " + res.entregados);
        verificar(res.transmisiones == 16 * n, "Se esperaban " + (16 * n) + " transmisiones, pero hubo " + res.transmisiones);
        verificar(res.periodos >= 4, "Un mensaje a 4 saltos requiere al menos 4 periodos (max 1 salto/periodo)");

        System.out.println("[OK] Prueba 4: Oraculo matematico (4n/16n), limite 1 salto/periodo y filas vacias.");
    }

    /**
     * Prueba 5: Verifica que ejecutar multiples simulaciones sobre el mismo Simulador
     * mantenga el estado aislado y produzca resultados deterministas.
     */
    private static void pruebaIndependenciaEjecuciones() {
        int filas = 6;
        int columnas = 8;
        int n = filas * columnas;

        GeneradorTopologia topo = new GeneradorTopologia(filas, columnas);
        Generador generador = new Generador(topo, 10L, 20L);
        Mapa<String, Integer>[] tablas = crearArregloMapas(n, "LPH");
        Simulador simulador = new Simulador(generador, tablas);

        Simulador.Ejecucion ejec1 = simulador.nuevaEjecucion();
        Simulador.Resultado corrida1 = ejec1.simular();
        verificar(ejec1.filasVacias(), "La primera ejecucion debe dejar sus filas vacias");

        Simulador.Ejecucion ejec2 = simulador.nuevaEjecucion();
        verificar(ejec2.filasVacias(), "La segunda ejecucion debe iniciar con filas vacias");
        Simulador.Resultado corrida2 = ejec2.simular();

        verificar(corrida1.entregados == corrida2.entregados, "Fallo de determinismo en entregados");
        verificar(corrida1.transmisiones == corrida2.transmisiones, "Fallo de determinismo en transmisiones");
        verificar(corrida1.periodos == corrida2.periodos, "Fallo de determinismo en periodos");

        System.out.println("[OK] Prueba 5: Independencia de estado y determinismo entre ejecuciones.");
    }

    /**
     * Prueba 6: Verifica que Generador lance IllegalStateException si la red es
     * demasiado pequena para tener vertices a 4 saltos de distancia.
     */
    private static void pruebaTopologiaSinCaminosA4Saltos() {
        GeneradorTopologia topoPequena = new GeneradorTopologia(2, 2);
        boolean lanzoExcepcion = false;

        try {
            new Generador(topoPequena, 1L, 2L);
        } catch (IllegalStateException e) {
            lanzoExcepcion = true;
        }

        verificar(lanzoExcepcion, "Se esperaba IllegalStateException en una grilla 2x2 sin rutas de 4 saltos");
        System.out.println("[OK] Prueba 6: Validacion de excepcion en topologia sin destinos a distancia 4.");
    }

    private static Simulador.Resultado ejecutarConMapa(Generador generador, int n, String tipoMapa) {
        Mapa<String, Integer>[] tablas = crearArregloMapas(n, tipoMapa);
        Simulador simulador = new Simulador(generador, tablas);
        Simulador.Ejecucion ejecucion = simulador.nuevaEjecucion();
        Simulador.Resultado res = ejecucion.simular();
        verificar(ejecucion.filasVacias(), "Las filas deben quedar vacias tras simular con " + tipoMapa);
        return res;
    }

    @SuppressWarnings("unchecked")
    private static Mapa<String, Integer>[] crearArregloMapas(int n, String tipoMapa) {
        Mapa<String, Integer>[] tablas = (Mapa<String, Integer>[]) new Mapa[n];
        for (int i = 0; i < n; i++) {
            switch (tipoMapa) {
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
                    throw new IllegalArgumentException("Tipo de mapa no soportado: " + tipoMapa);
            }
        }
        return tablas;
    }

    private static void verificar(boolean condicion, String mensajeError) {
        if (!condicion) {
            throw new AssertionError("FALLO EN PRUEBA: " + mensajeError);
        }
    }
}
