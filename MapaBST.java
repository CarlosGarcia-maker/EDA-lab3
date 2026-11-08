import edu.princeton.cs.algs4.BST;

/**
 * Adaptador mediante composición para el Árbol de Búsqueda Binaria (BST) de algs4.
 * Cumple con la especificación de la sección 5.1 del enunciado.
 *
 * @param <K> Tipo de la clave, debe ser Comparable
 * @param <V> Tipo del valor
 */
public class MapaBST<K extends Comparable<K>, V> implements Mapa<K, V> {
    private final BST<K, V> bst;

    /**
     * Constructor por defecto sin capacidad explícita.
     */
    public MapaBST() {
        this.bst = new BST<K, V>();
    }

    @Override
    public void put(K clave, V valor) {
        if (clave == null || valor == null) {
            throw new IllegalArgumentException("La clave y el valor no pueden ser nulos.");
        }
        bst.put(clave, valor);
    }

    @Override
    public V get(K clave) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser nula.");
        }
        return bst.get(clave);
    }

    @Override
    public int size() {
        return bst.size();
    }
}
