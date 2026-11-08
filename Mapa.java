/**
 * Interfaz genérica Mapa especificadora del contrato para los cuatro adaptadores.
 * Especificada en la sección 5.1 del enunciado.
 *
 * @param <K> Tipo de la clave
 * @param <V> Tipo del valor
 */
public interface Mapa<K, V> {
    /**
     * Inserta o reemplaza un par clave-valor en el mapa.
     * @param clave clave no nula
     * @param valor valor no nulo
     */
    void put(K clave, V valor);

    /**
     * Retorna el valor asociado a la clave, o null si esta ausente.
     * @param clave clave no nula a consultar
     * @return valor asociado o null
     */
    V get(K clave);

    /**
     * Retorna la cantidad de claves distintas almacenadas.
     * @return cantidad de pares clave-valor
     */
    int size();
}
