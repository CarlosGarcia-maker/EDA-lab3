/**
 * Suite de pruebas de ruteo y equivalencia (Rol A - Semana 2).
 * Verifica que las 4 implementaciones de Mapa produzcan exactamente
 * los mismos resultados de simulacion bajo identicas condiciones.
 */
public class PruebasRuteo {

    /**
     * Punto de entrada para ejecutar todas las pruebas de ruteo y equivalencia.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DE RUTEO Y EQUIVALENCIA ===");

        pruebaEquivalenciaMapas();
        pruebaOraculoMatematico();
        pruebaIndependenciaEjecuciones();
        pruebaTopologiaSinCaminosA4Saltos();

        System.out.println("=== TODAS LAS PRUEBAS PASARON EXITOSAMENTE ===");
    }

    /**
     * Prueba 1 (Principal): Verifica que BST, RBBST, SCH y LPH arrojen
     * exactamente los mismos entregados, transmisiones y periodos.
     */
    private static void pruebaEquivalenciaMapas() {
        int filas = 8;
        int columnas = 16;
        int n = filas * columnas;
        long seedTopologia = 128L;
        long seedDemanda = 2003971L;

        // 1. Construimos una misma topologia y demanda base para los 4 escenarios
        GeneradorTopologia topo = new GeneradorTopologia(filas, columnas);
        Generador generador = new Generador(topo, seedTopologia, seedDemanda);

        // 2. Ejecutamos la simulacion completa con cada estructura de datos
        Simulador.Resultado resBST = ejecutarConMapa(generador, n, "BST");
        Simulador.Resultado resRBBST = ejecutarConMapa(generador, n, "RBBST");
        Simulador.Resultado resSCH = ejecutarConMapa(generador, n, "SCH");
        Simulador.Resultado resLPH = ejecutarConMapa(generador, n, "LPH");

        // 3. Comparamos RBBST contra BST
        verificar(resBST.entregados == resRBBST.entregados, "Entregados distintos entre BST y RBBST");
        verificar(resBST.transmisiones == resRBBST.transmisiones, "Transmisiones distintas entre BST y RBBST");
        verificar(resBST.periodos == resRBBST.periodos, "Periodos distintos entre BST y RBBST");

        // 4. Comparamos SCH contra BST
        verificar(resBST.entregados == resSCH.entregados, "Entregados distintos entre BST y SCH");
        verificar(resBST.transmisiones == resSCH.transmisiones, "Transmisiones distintas entre BST y SCH");
        verificar(resBST.periodos == resSCH.periodos, "Periodos distintos entre BST y SCH");

        // 5. Comparamos LPH contra BST
        verificar(resBST.entregados == resLPH.entregados, "Entregados distintos entre BST y LPH");
        verificar(resBST.transmisiones == resLPH.transmisiones, "Transmisiones distintas entre BST y LPH");
        verificar(resBST.periodos == resLPH.periodos, "Periodos distintos entre BST y LPH");

        System.out.println("[OK] Prueba 1: Equivalencia exacta entre BST, RBBST, SCH y LPH "
                + "(Entregados=" + resBST.entregados
                + ", Transmisiones=" + resBST.transmisiones
                + ", Periodos=" + resBST.periodos + ").");
    }

    /**
     * Prueba 2: Verifica el modelo teorico. Cada uno de los n nodos envia 4 mensajes
     * a distancia exacta de 4 saltos, por lo que debe haber 4*n entregas y 16*n transmisiones.
     */
    private static void pruebaOraculoMatematico() {
        int filas = 6;
        int columnas = 6;
        int n = filas * columnas;

        GeneradorTopologia topo = new GeneradorTopologia(filas, columnas);
        Generador generador = new Generador(topo, 42L, 99L);

        Simulador.Resultado res = ejecutarConMapa(generador, n, "RBBST");

        // 1. Cada nodo genera 4 mensajes y todos deben llegar a destino (4*n)
        verificar(res.entregados == 4 * n, "Se esperaban " + (4 * n) + " entregados, pero hubo " + res.entregados);

        // 2. Cada mensaje recorre exactamente 4 saltos (4 * 4*n = 16*n transmisiones)
        verificar(res.transmisiones == 16 * n, "Se esperaban " + (16 * n) + " transmisiones, pero hubo " + res.transmisiones);

        // 3. Recorrer 4 saltos requiere al menos 4 periodos de tiempo
        verificar(res.periodos >= 4, "La cantidad de periodos debe ser al menos 4");

        System.out.println("[OK] Prueba 2: Oraculo matematico cumplido (4*n entregas y 16*n transmisiones).");
    }

    /**
     * Prueba 3: Verifica que ejecutar multiples simulaciones sobre el mismo Simulador
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

        // 1. Ejecutamos dos corridas independientes sobre la misma instancia de Simulador
        Simulador.Resultado corrida1 = simulador.nuevaEjecucion().simular();
        Simulador.Resultado corrida2 = simulador.nuevaEjecucion().simular();

        // 2. Verificamos que las tablas de ruteo efectivamente almacenaron rutas
        verificar(tablas[0].size() > 0, "Las tablas de ruteo deberian contener entradas tras simular");

        // 3. Verificamos determinismo e independencia entre ambas corridas
        verificar(corrida1.entregados == corrida2.entregados, "Fallo de determinismo en entregados");
        verificar(corrida1.transmisiones == corrida2.transmisiones, "Fallo de determinismo en transmisiones");
        verificar(corrida1.periodos == corrida2.periodos, "Fallo de determinismo en periodos");

        System.out.println("[OK] Prueba 3: Independencia de estado y determinismo entre ejecuciones.");
    }

    /**
     * Prueba 4: Verifica que Generador lance IllegalStateException si la red es
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
        System.out.println("[OK] Prueba 4: Validacion de excepcion en topologia sin destinos a distancia 4.");
    }

    /**
     * Crea un arreglo de mapas del tipo solicitado y ejecuta una simulacion completa.
     *
     * @param generador instancia con la topologia y demanda precalculadas
     * @param n cantidad de estaciones de la red
     * @param tipoMapa identificador del adaptador ("BST", "RBBST", "SCH" o "LPH")
     * @return resultado metrico de la simulacion ejecutada
     */
    private static Simulador.Resultado ejecutarConMapa(Generador generador, int n, String tipoMapa) {
        Mapa<String, Integer>[] tablas = crearArregloMapas(n, tipoMapa);
        Simulador simulador = new Simulador(generador, tablas);
        return simulador.nuevaEjecucion().simular();
    }

    /**
     * Instancia un arreglo de n tablas de simbolos vacias del adaptador indicado.
     *
     * @param n cantidad de mapas a crear (uno por estacion)
     * @param tipoMapa nombre corto del adaptador ("BST", "RBBST", "SCH" o "LPH")
     * @return arreglo de n implementaciones de Mapa
     */
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

    /**
     * Evalua una condicion logica y lanza AssertionError si no se cumple.
     *
     * @param condicion expresion booleana esperada como verdadera
     * @param mensajeError descripcion del fallo en caso de incumplimiento
     */
    private static void verificar(boolean condicion, String mensajeError) {
        if (!condicion) {
            throw new AssertionError("FALLO EN PRUEBA: " + mensajeError);
        }
    }
}