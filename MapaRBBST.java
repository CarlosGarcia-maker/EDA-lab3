import edu.princeton.cs.algs4.RedBlackBST;

/**
 * Adaptador mediante composición para el Árbol de Búsqueda Rojo-Negro (RedBlackBST) de algs4.
 * Especificado en la sección 5.1 del enunciado.
 *
 * @param <K> Tipo de la clave, debe ser Comparable
 * @param <V> Tipo del valor
 */
public class MapaRBBST<K extends Comparable<K>, V> implements Mapa<K, V> {
    private final RedBlackBST<K, V> rbbst;

    /**
     * Constructor por defecto sin capacidad explícita.
     */
    public MapaRBBST() {
        this.rbbst = new RedBlackBST<K, V>();
    }

    @Override
    public void put(K clave, V valor) {
        if (clave == null || valor == null) {
            throw new IllegalArgumentException("La clave y el valor no pueden ser nulos.");
        }
        rbbst.put(clave, valor);
    }

    @Override
    public V get(K clave) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser nula.");
        }
        return rbbst.get(clave);
    }

    @Override
    public int size() {
        return rbbst.size();
    }
}
