import edu.princeton.cs.algs4.Graph;

/**
 * Suite de pruebas unitarias iniciales (Semana 1) para el Rol A.
 * Verifica los 4 adaptadores de Mapa, la clase Mensaje y el GeneradorTopologia.
 */
public class PruebasIniciales {

    public static void main(String[] args) {
        System.out.println("=== INICIANDO SUITE DE PRUEBAS INICIALES (SEMANA 1) ===");

        probarAdaptadoresMapa();
        probarGeneradorTopologia();
        probarMensaje();

        System.out.println("\n[EXITO] ¡Todas las pruebas unitarias pasaron correctamente!");
    }

    private static void probarAdaptadoresMapa() {
        System.out.println("\n-> Probando los 4 adaptadores de Mapa (BST, RBBST, SCH, LPH)...");
        
        @SuppressWarnings("unchecked")
        Mapa<String, Integer>[] mapas = new Mapa[] {
            new MapaBST<String, Integer>(),
            new MapaRBBST<String, Integer>(),
            new MapaSCH<String, Integer>(),
            new MapaLPH<String, Integer>()
        };

        for (Mapa<String, Integer> mapa : mapas) {
            mapa.put("estacion-alpha", 100);
            mapa.put("estacion-beta", 200);

            if (mapa.size() != 2) {
                throw new AssertionError("Error en size() para " + mapa.getClass().getSimpleName());
            }
            if (mapa.get("estacion-alpha") != 100 || mapa.get("estacion-beta") != 200) {
                throw new AssertionError("Error en get() para " + mapa.getClass().getSimpleName());
            }
            if (mapa.get("estacion-inexistente") != null) {
                throw new AssertionError("Error: la clave inexistente debe retornar null.");
            }

            // Verificación de excepción por clave nula
            try {
                mapa.put(null, 500);
                throw new AssertionError("Debio lanzar IllegalArgumentException por clave nula.");
            } catch (IllegalArgumentException e) {
                // Comportamiento esperado
            }
        }
        System.out.println("   [OK] Adaptadores de Mapa probados con exito.");
    }

    private static void probarGeneradorTopologia() {
        System.out.println("\n-> Probando la clase docente GeneradorTopologia...");
        int filas = 6;
        int columnas = 8;
        GeneradorTopologia topo1 = new GeneradorTopologia(filas, columnas);

        // 1. Prueba de Mapeo Biunivoco: v = f * C + c
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

        // 2. Prueba de Estabilidad de Nombres entre instancias
        GeneradorTopologia topo2 = new GeneradorTopologia(filas, columnas);
        String idTopo1 = topo1.identificador(0);
        String idTopo2 = topo2.identificador(0);
        if (!idTopo1.equals(idTopo2)) {
            throw new AssertionError("Error: Los nombres generados deben ser deterministicos y estables.");
        }

        // 3. Prueba de Reproducibilidad con la misma semilla
        long semilla = 123456789L;
        Graph g1 = topo1.generar(semilla);
        Graph g2 = topo1.generar(semilla);
        if (g1.V() != g2.V() || g1.E() != g2.E()) {
            throw new AssertionError("Error: Misma semilla debe generar grafos identicos en V y E.");
        }

        // 4. Prueba de Excepciones por indices o coordenadas invalidos
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

        System.out.println("   [OK] GeneradorTopologia supero las 4 pruebas de verificacion.");
    }

    private static void probarMensaje() {
        System.out.println("\n-> Probando la clase inmutable Mensaje...");
        Mensaje m = new Mensaje(42, "estacion-origen", "estacion-destino");
        if (m.id() != 42 || !m.origen().equals("estacion-origen") || !m.destino().equals("estacion-destino")) {
            throw new AssertionError("Error en los atributos del Mensaje.");
        }

        try {
            new Mensaje(1, null, "estacion-destino");
            throw new AssertionError("Debio lanzar IllegalArgumentException por origen nulo.");
        } catch (IllegalArgumentException e) {
            // Comportamiento esperado
        }
        System.out.println("   [OK] Clase Mensaje probada con exito.");
    }
}
