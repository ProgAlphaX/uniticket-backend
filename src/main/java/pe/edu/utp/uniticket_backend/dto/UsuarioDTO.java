package pe.edu.utp.uniticket_backend.dto;

public class UsuarioDTO {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String dni;
    private String telefono;
    private String rol;
    private String estado;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String nombreCompleto, String email, String dni, String telefono, String rol,
            String estado) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.dni = dni;
        this.telefono = telefono;
        this.rol = rol;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
