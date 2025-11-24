package es.clubdama.model;

/**
 * Representa un socio del club.
 * Contiene datos personales y de contacto.
 */
public class Socio {
    private String idSocio;
    private String dni;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String email;

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

    /**
     * Representación concisa del socio.
     * @return cadena con nombre, apellidos y dni
     */
    @Override
    public String toString(){ return nombre+" "+apellidos+" ("+dni+")"; }
}
