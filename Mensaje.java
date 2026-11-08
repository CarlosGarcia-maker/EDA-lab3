/**
 * Representa un mensaje inmutable en la red de simulacion.
 * Especificado en la seccion 5.2 del enunciado.
 */
public final class Mensaje {
    private final int id;
    private final String origen;
    private final String destino;

    /**
     * Construye un mensaje inmutable.
     *
     * @param id identificador unico del mensaje
     * @param origen identificador String de la estacion origen
     * @param destino identificador String de la estacion destino
     * @throws IllegalArgumentException si origen o destino son nulos
     */
    public Mensaje(int id, String origen, String destino) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("El origen y el destino no pueden ser nulos.");
        }
        this.id = id;
        this.origen = origen;
        this.destino = destino;
    }

    /** @return identificador unico del mensaje */
    public int id() {
        return id;
    }

    /** @return identificador String de la estacion origen */
    public String origen() {
        return origen;
    }

    /** @return identificador String de la estacion destino */
    public String destino() {
        return destino;
    }
}
