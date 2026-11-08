import edu.princeton.cs.algs4.SeparateChainingHashST;

/**
 * Adaptador mediante composición para la Tabla Hash con Encadenamiento (SeparateChainingHashST) de algs4.
 * Especificado en la sección 5.1 del enunciado.
 *
 * @param <K> Tipo de la clave
 * @param <V> Tipo del valor
 */
public class MapaSCH<K, V> implements Mapa<K, V> {
    private final SeparateChainingHashST<K, V> sch;

    /**
     * Constructor por defecto sin capacidad explícita.
     */
    public MapaSCH() {
        this.sch = new SeparateChainingHashST<K, V>();
    }

    @Override
    public void put(K clave, V valor) {
        if (clave == null || valor == null) {
            throw new IllegalArgumentException("La clave y el valor no pueden ser nulos.");
        }
        sch.put(clave, valor);
    }

    @Override
    public V get(K clave) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser nula.");
        }
        return sch.get(clave);
    }

    @Override
    public int size() {
        return sch.size();
    }
}
