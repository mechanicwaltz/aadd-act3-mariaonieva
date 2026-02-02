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
 * Representa un socio del club.
 * Contiene datos personales y de contacto.
 */
@Entity
@Table(name = "socios")
public class Socio {
    @Id
    @Column(name = "id_socio", length = 36)
    private String idSocio;

    @Column(name = "dni", nullable = false, length = 16)
    private String dni;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellidos", length = 200)
    private String apellidos;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "email", length = 200)
    private String email;

    @OneToMany(mappedBy = "socio", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Reserva> reservas = new ArrayList<>();

    /** Constructor por defecto. */
    public Socio() {}

    /**
     * Constructor completo.
     * @param idSocio identificador único del socio
     * @param dni documento nacional de identidad
     * @param nombre nombre del socio
     * @param apellidos apellidos del socio
     * @param telefono teléfono de contacto
     * @param email correo electrónico
     */
    public Socio(String idSocio, String dni, String nombre, String apellidos,
                 String telefono, String email) {
        this.idSocio = idSocio; this.dni = dni; this.nombre = nombre;
        this.apellidos = apellidos; this.telefono = telefono; this.email = email;
    }

    /** @return identificador del socio */
    public String getIdSocio() { return idSocio; }
    /** @param idSocio establece el identificador del socio */
    public void setIdSocio(String idSocio) { this.idSocio = idSocio; }
    /** @return dni del socio */
    public String getDni() { return dni; }
    /** @param dni establece el dni */
    public void setDni(String dni) { this.dni = dni; }
    /** @return nombre */
    public String getNombre() { return nombre; }
    /** @param nombre establece el nombre */
    public void setNombre(String nombre) { this.nombre = nombre; }
    /** @return apellidos */
    public String getApellidos() { return apellidos; }
    /** @param apellidos establece los apellidos */
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    /** @return teléfono */
    public String getTelefono() { return telefono; }
    /** @param telefono establece el teléfono */
    public void setTelefono(String telefono) { this.telefono = telefono; }
    /** @return email */
    public String getEmail() { return email; }
    /** @param email establece el correo electrónico */
    public void setEmail(String email) { this.email = email; }

    /** Retorna reservas asociadas (posible lista vacía). */
    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }

    /**
     * Representación concisa del socio.
     * @return cadena con nombre, apellidos y dni
     */
    @Override
    public String toString(){ return nombre+" "+apellidos+" ("+dni+")"; }
}
