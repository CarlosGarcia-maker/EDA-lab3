import edu.princeton.cs.algs4.Graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Generador docente para el Laboratorio 3 de EDA, semestre 2026-2.
 * Construye una malla rectangular aumentada con contactos aleatorios.
 */
public final class GeneradorTopologia {
    private final int filas;
    private final int columnas;
    private final int n;
    private final String[] identificadorPorIndice;
    private final Map<String, Integer> indicePorIdentificador;

    public GeneradorTopologia(int filas, int columnas) {
        long cantidad = (long) filas * columnas;
        if (filas <= 0 || columnas <= 0 || cantidad > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Dimensiones invalidas");
        }
        this.filas = filas;
        this.columnas = columnas;
        this.n = (int) cantidad;
        this.identificadorPorIndice = new String[n];
        this.indicePorIdentificador = new HashMap<String, Integer>();
        long base = mezclar64(0x4544413230323632L ^ (long) n);
        for (int v = 0; v < n; v++) {
            String codigo = Long.toHexString(mezclar64(base + v));
            StringBuilder nombre = new StringBuilder("estacion-");
            for (int i = codigo.length(); i < 16; i++) {
                nombre.append('0');
            }
            nombre.append(codigo).append(".red-simulada");
            String id = nombre.toString();
            identificadorPorIndice[v] = id;
            indicePorIdentificador.put(id, v);
        }
    }

    public int filas() { return filas; }
    public int columnas() { return columnas; }
    public int cantidadVertices() { return n; }

    public int indice(int fila, int columna) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            throw new IllegalArgumentException("Coordenadas fuera de rango");
        }
        return fila * columnas + columna;
    }

    public int[] coordenadas(int vertice) {
        validarVertice(vertice);
        return new int[] {vertice / columnas, vertice % columnas};
    }

    public String identificador(int vertice) {
        validarVertice(vertice);
        return identificadorPorIndice[vertice];
    }

    public int indice(String identificador) {
        Integer v = indicePorIdentificador.get(identificador);
        if (v == null) {
            throw new IllegalArgumentException("Identificador desconocido");
        }
        return v;
    }

    public Graph generar(long semilla) {
        Graph grafo = new Graph(n);
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                int u = indice(f, c);
                if (c + 1 < columnas) { grafo.addEdge(u, indice(f, c + 1)); }
                if (f + 1 < filas) { grafo.addEdge(u, indice(f + 1, c)); }
            }
        }
        if (n == 1) { return grafo; }
        Random azar = new Random(semilla);
        int[] contacto = new int[n];
        Arrays.fill(contacto, -1);
        for (int u = 0; u < n; u++) {
            int v = seleccionarContacto(u, azar);
            if (distanciaManhattan(u, v) != 1 && contacto[v] != u) {
                grafo.addEdge(u, v);
            }
            contacto[u] = v;
        }
        return grafo;
    }

    private int seleccionarContacto(int u, Random azar) {
        double total = 0.0;
        for (int v = 0; v < n; v++) {
            if (v != u) { total += peso(u, v); }
        }
        double objetivo = azar.nextDouble() * total;
        double acumulado = 0.0;
        int ultimo = -1;
        for (int v = 0; v < n; v++) {
            if (v != u) {
                ultimo = v;
                acumulado += peso(u, v);
                if (objetivo < acumulado) { return v; }
            }
        }
        return ultimo;
    }

    private double peso(int u, int v) {
        double distancia = distanciaManhattan(u, v);
        return 1.0 / (distancia * distancia);
    }

    private int distanciaManhattan(int u, int v) {
        return Math.abs(u / columnas - v / columnas)
                + Math.abs(u % columnas - v % columnas);
    }

    private void validarVertice(int v) {
        if (v < 0 || v >= n) {
            throw new IllegalArgumentException("Vertice fuera de rango");
        }
    }

    private static long mezclar64(long x) {
        x = (x ^ (x >>> 30)) * 0xbf58476d1ce4e5b9L;
        x = (x ^ (x >>> 27)) * 0x94d049bb133111ebL;
        return x ^ (x >>> 31);
    }
}
