package es.clubdama.model;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa una reserva de una pista por un socio en una fecha y hora.
 */
public class Reserva {
    private String idReserva;
    private String idSocio;
    private String idPista;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private int duracionMin;
    private double precio;

    /** Constructor por defecto. */
    public Reserva() {}

    /** @return id de la reserva */
    public String getIdReserva(){return idReserva;}
    /** @param idReserva establece el identificador */
    public void setIdReserva(String idReserva){this.idReserva=idReserva;}
    /** @return id del socio */
    public String getIdSocio(){return idSocio;}
    /** @param idSocio establece el id del socio */
    public void setIdSocio(String idSocio){this.idSocio=idSocio;}
    /** @return id de la pista */
    public String getIdPista(){return idPista;}
    /** @param idPista establece el id de la pista */
    public void setIdPista(String idPista){this.idPista=idPista;}
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
    public double getPrecio(){return precio;}
    /** @param precio establece el precio */
    public void setPrecio(double precio){this.precio=precio;}

    /** @return representación concisa de la reserva */
    @Override
    public String toString(){ return idReserva + " - " + idPista + " - " + fecha + " " + horaInicio; }
}
