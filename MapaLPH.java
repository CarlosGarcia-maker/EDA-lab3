import edu.princeton.cs.algs4.LinearProbingHashST;

/**
 * Adaptador mediante composición para la Tabla Hash con Sondeo Lineal (LinearProbingHashST) de algs4.
 * Especificado en la sección 5.1 del enunciado.
 *
 * @param <K> Tipo de la clave
 * @param <V> Tipo del valor
 */
public class MapaLPH<K, V> implements Mapa<K, V> {
    private final LinearProbingHashST<K, V> lph;

    /**
     * Constructor por defecto sin capacidad explícita.
     */
    public MapaLPH() {
        this.lph = new LinearProbingHashST<K, V>();
    }

    @Override
    public void put(K clave, V valor) {
        if (clave == null || valor == null) {
            throw new IllegalArgumentException("La clave y el valor no pueden ser nulos.");
        }
        lph.put(clave, valor);
    }

    @Override
    public V get(K clave) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser nula.");
        }
        return lph.get(clave);
    }

    @Override
    public int size() {
        return lph.size();
    }
}
