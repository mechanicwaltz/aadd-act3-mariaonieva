package es.clubdama.model;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;

/**
 * Representa una reserva de una pista por un socio en una fecha y hora.
 */
@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @Column(name = "id_reserva", length = 36)
    private String idReserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_socio", referencedColumnName = "id_socio")
    private Socio socio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pista", referencedColumnName = "id_pista")
    private Pista pista;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "duracion_min")
    private int duracionMin;

    @Column(name = "precio")
    private BigDecimal precio;

    /** Constructor por defecto. */
    public Reserva() {}

    /** @return id de la reserva */
    public String getIdReserva(){return idReserva;}
    /** @param idReserva establece el identificador */
    public void setIdReserva(String idReserva){this.idReserva=idReserva;}

    /** @return id del socio (compatibilidad con vistas) */
    public String getIdSocio() { return socio != null ? socio.getIdSocio() : null; }
    /** @return id de la pista (compatibilidad con vistas) */
    public String getIdPista() { return pista != null ? pista.getIdPista() : null; }

    /** @return socio asociado */
    public Socio getSocio() { return socio; }
    /** @param socio establece el socio */
    public void setSocio(Socio socio) { this.socio = socio; }

    /** @return pista asociada */
    public Pista getPista() { return pista; }
    /** @param pista establece la pista */
    public void setPista(Pista pista) { this.pista = pista; }

    /** @return fecha de la reserva */
    public LocalDate getFecha(){return fecha;}
    /** @param fecha establece la fecha */
    public void setFecha(LocalDate fecha){this.fecha=fecha;}
    /** @return hora de inicio */
    public LocalTime getHoraInicio(){return horaInicio;}
    /** @param horaInicio establece la hora de inicio */
    public void setHoraInicio(LocalTime horaInicio){this.horaInicio=horaInicio;}
    /** @return duración en minutos */
    public int getDuracionMin(){return duracionMin;}
    /** @param duracionMin establece la duración */
    public void setDuracionMin(int duracionMin){this.duracionMin=duracionMin;}
    /** @return precio calculado */
    public BigDecimal getPrecio(){return precio;}
    /** @param precio establece el precio */
    public void setPrecio(BigDecimal precio){this.precio=precio;}

    /** @return representación concisa de la reserva */
    @Override
    public String toString(){ return idReserva + " - " + (pista!=null? pista.getIdPista():"?") + " - " + fecha + " " + horaInicio; }
}
