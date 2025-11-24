package es.clubdama.model;

/**
 * Representa una pista deportiva del club.
 * Incluye tipo de deporte, descripción y disponibilidad.
 */
public class Pista {
    private String idPista;
    private String deporte;
    private String descripcion;
    private boolean disponible;

    /** Constructor por defecto. */
    public Pista() {}

    /**
     * Constructor completo.
     * @param idPista identificador único de la pista
     * @param deporte tipo de deporte (ej. tenis, padel, futbol_sala)
     * @param descripcion texto descriptivo opcional
     * @param disponible estado operativo de la pista
     */
    public Pista(String idPista, String deporte, String descripcion, boolean disponible) {
        this.idPista = idPista; this.deporte = deporte; this.descripcion = descripcion; this.disponible = disponible;
    }

    /** @return identificador de la pista */
    public String getIdPista(){return idPista;}
    /** @param idPista establece el identificador */
    public void setIdPista(String idPista){this.idPista=idPista;}
    /** @return deporte */
    public String getDeporte(){return deporte;}
    /** @param deporte establece el deporte */
    public void setDeporte(String deporte){this.deporte=deporte;}
    /** @return descripción */
    public String getDescripcion(){return descripcion;}
    /** @param descripcion establece la descripción */
    public void setDescripcion(String descripcion){this.descripcion=descripcion;}
    /** @return true si la pista está disponible */
    public boolean isDisponible(){return disponible;}
    /** @param disponible marca la disponibilidad */
    public void setDisponible(boolean disponible){this.disponible=disponible;}

    /** @return representación concisa de la pista */
    @Override
    public String toString(){ return idPista+" - "+deporte+" ("+descripcion+")"; }
}
