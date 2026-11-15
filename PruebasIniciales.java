import edu.princeton.cs.algs4.Graph;

/**
 * Suite de pruebas unitarias iniciales (Semana 1) para el Rol A.
 * Verifica los 4 adaptadores de Mapa, la clase docente GeneradorTopologia
 * y la inmutabilidad de la clase Mensaje.
 */
public class PruebasIniciales {

    /**
     * Punto de entrada para ejecutar todas las pruebas iniciales.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SUITE DE PRUEBAS INICIALES (SEMANA 1) ===");

        probarAdaptadoresMapa();
        probarGeneradorTopologia();
        probarMensaje();

        System.out.println("\n[EXITO] ¡Todas las pruebas unitarias pasaron correctamente!");
    }

    /**
     * Verifica insercion, consulta, reemplazo, igualdad por contenido,
     * tamano y rechazo de nulos en los cuatro adaptadores de Mapa.
     */
    private static void probarAdaptadoresMapa() {
        System.out.println("\n-> Probando los 4 adaptadores de Mapa (BST, RBBST, SCH, LPH)...");

        // Instanciamos los 4 adaptadores usando la interfaz comun Mapa
        @SuppressWarnings("unchecked")
        Mapa<String, Integer>[] mapas = new Mapa[] {
            new MapaBST<String, Integer>(),
            new MapaRBBST<String, Integer>(),
            new MapaSCH<String, Integer>(),
            new MapaLPH<String, Integer>()
        };

        // Ejecutamos exactamente las mismas pruebas sobre cada adaptador
        for (Mapa<String, Integer> mapa : mapas) {
            // 1. Insercion basica y verificacion de tamano (size)
            mapa.put("estacion-alpha", 100);
            mapa.put("estacion-beta", 200);
            if (mapa.size() != 2) {
                throw new AssertionError("Error en size() para " + mapa.getClass().getSimpleName());
            }

            // 2. Consulta de claves existentes y clave ausente (debe retornar null)
            if (mapa.get("estacion-alpha") != 100 || mapa.get("estacion-beta") != 200) {
                throw new AssertionError("Error en get() para " + mapa.getClass().getSimpleName());
            }
            if (mapa.get("estacion-inexistente") != null) {
                throw new AssertionError("Error: la clave inexistente debe retornar null.");
            }

            // 3. Rechazo de clave nula en put() mediante IllegalArgumentException
            try {
                mapa.put(null, 500);
                throw new AssertionError("Debio lanzar IllegalArgumentException por clave nula.");
            } catch (IllegalArgumentException e) {
                // Comportamiento esperado
            }

            // 4. Reemplazo: sobrescribir clave existente sin aumentar el tamano
            mapa.put("estacion-alpha", 999);
            if (mapa.get("estacion-alpha") != 999 || mapa.size() != 2) {
                throw new AssertionError("Error al reemplazar un valor existente.");
            }

            // 5. Igualdad por contenido: buscar con un objeto String distinto en memoria
            String claveCopia = new String("estacion-alpha");
            if (mapa.get(claveCopia) != 999) {
                throw new AssertionError("Error: debe buscar por el texto, no por referencia.");
            }

            // 6. Rechazo de valor nulo en put() y clave nula en get()
            try {
                mapa.put("estacion-alpha", null);
                throw new AssertionError("Debio fallar con valor nulo.");
            } catch (IllegalArgumentException e) {
                // Comportamiento esperado
            }

            try {
                mapa.get(null);
                throw new AssertionError("Debio fallar con get(null).");
            } catch (IllegalArgumentException e) {
                // Comportamiento esperado
            }
        }

        System.out.println("   [OK] Adaptadores de Mapa probados con exito.");
    }

    /**
     * Valida el mapeo bidireccional entre coordenadas e indices, la estabilidad
     * de los identificadores logicos, la reproducibilidad del grafo y las excepciones.
     */
    private static void probarGeneradorTopologia() {
        System.out.println("\n-> Probando la clase docente GeneradorTopologia...");
        int filas = 6;
        int columnas = 8;
        GeneradorTopologia topo1 = new GeneradorTopologia(filas, columnas);

        // 1. Mapeo biunivoco entre coordenadas (fila, columna) e indice lineal (v = f * C + c)
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                int verticeEsperado = f * columnas + c;
                int verticeObtenido = topo1.indice(f, c);
                if (verticeEsperado != verticeObtenido) {
                    throw new AssertionError("Error en formula de mapeo bidireccional.");
                }

                int[] coord = topo1.coordenadas(verticeObtenido);
                if (coord[0] != f || coord[1] != c) {
                    throw new AssertionError("Error en reconversion a coordenadas [f, c].");
                }
            }
        }

        // 2. Estabilidad: dos objetos con mismas dimensiones generan los mismos nombres
        GeneradorTopologia topo2 = new GeneradorTopologia(filas, columnas);
        String idTopo1 = topo1.identificador(0);
        String idTopo2 = topo2.identificador(0);
        if (!idTopo1.equals(idTopo2)) {
            throw new AssertionError("Error: Los nombres generados deben ser deterministicos y estables.");
        }

        // 3. Reproducibilidad: misma semilla produce grafos con igual cantidad de vertices y aristas
        long semilla = 123456789L;
        Graph g1 = topo1.generar(semilla);
        Graph g2 = topo1.generar(semilla);
        if (g1.V() != g2.V() || g1.E() != g2.E()) {
            throw new AssertionError("Error: Misma semilla debe generar grafos identicos en V y E.");
        }

        // 4. Traduccion inversa valida: pasar de indice a String y de vuelta al mismo indice
        String nombreEstacion = topo1.identificador(5);
        if (topo1.indice(nombreEstacion) != 5) {
            throw new AssertionError("Error al traducir identificador de vuelta a indice.");
        }

        // 5. Excepciones por coordenadas fuera de rango o identificadores desconocidos
        try {
            topo1.indice(-1, 0);
            throw new AssertionError("Debio lanzar IllegalArgumentException por fila negativa.");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
        }

        try {
            topo1.indice("identificador-falso");
            throw new AssertionError("Debio lanzar IllegalArgumentException por ID no registrado.");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
        }

        System.out.println("   [OK] GeneradorTopologia supero las pruebas de verificacion.");
    }

    /**
     * Verifica la creacion correcta de un Mensaje y el rechazo de argumentos nulos.
     */
    private static void probarMensaje() {
        System.out.println("\n-> Probando la clase inmutable Mensaje...");

        // 1. Creacion valida y consulta de atributos (id, origen, destino)
        Mensaje m = new Mensaje(42, "estacion-origen", "estacion-destino");
        if (m.id() != 42 || !m.origen().equals("estacion-origen") || !m.destino().equals("estacion-destino")) {
            throw new AssertionError("Error en los atributos del Mensaje.");
        }

        // 2. Rechazo de origen nulo mediante IllegalArgumentException
        try {
            new Mensaje(1, null, "estacion-destino");
            throw new AssertionError("Debio lanzar IllegalArgumentException por origen nulo.");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
        }

        System.out.println("   [OK] Clase Mensaje probada con exito.");
    }
}
