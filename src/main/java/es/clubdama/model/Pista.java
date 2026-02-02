package es.clubdama.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una pista deportiva del club.
 * Incluye tipo de deporte, descripción y disponibilidad.
 */
@Entity
@Table(name = "pistas")
public class Pista {
    @Id
    @Column(name = "id_pista", length = 36)
    private String idPista;

    @Column(name = "deporte", length = 50)
    private String deporte;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "disponible")
    private boolean disponible;

    @OneToMany(mappedBy = "pista", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Reserva> reservas = new ArrayList<>();

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

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }

    /** @return representación concisa de la pista */
    @Override
    public String toString(){ return idPista+" - "+deporte+" ("+descripcion+")"; }
}
