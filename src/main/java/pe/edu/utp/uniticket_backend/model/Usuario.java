package pe.edu.utp.uniticket_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nid_usuario")
    private Long nId_Usuario;

    @Column(name = "snombrecompleto", nullable = false, length = 150)
    private String sNombreCompleto;

    @Column(name = "semail", nullable = false, unique = true, length = 100)
    private String sEmail;

    @Column(name = "sdni", nullable = false, length = 8)
    private String sDni;

    @Column(name = "stelefono", length = 15)
    private String sTelefono;

    @Column(name = "spasswordhash", nullable = false, length = 255)
    private String sPasswordHash;

    @Column(name = "srol", nullable = false, length = 20)
    private String sRol;

    @Column(name = "sestado", nullable = false, length = 20)
    private String sEstado;

    @Column(name = "dfecha_creacion", nullable = false)
    private LocalDateTime dFecha_Creacion;

    public Usuario() {
    }

    public Usuario(Long nId_Usuario, String sNombreCompleto, String sEmail, String sDni,
            String sTelefono, String sPasswordHash, String sRol, String sEstado,
            LocalDateTime dFecha_Creacion) {
        this.nId_Usuario = nId_Usuario;
        this.sNombreCompleto = sNombreCompleto;
        this.sEmail = sEmail;
        this.sDni = sDni;
        this.sTelefono = sTelefono;
        this.sPasswordHash = sPasswordHash;
        this.sRol = sRol;
        this.sEstado = sEstado;
        this.dFecha_Creacion = dFecha_Creacion;
    }

    public Long getNId_Usuario() {
        return nId_Usuario;
    }

    public void setNId_Usuario(Long nId_Usuario) {
        this.nId_Usuario = nId_Usuario;
    }

    public String getSNombreCompleto() {
        return sNombreCompleto;
    }

    public void setSNombreCompleto(String sNombreCompleto) {
        this.sNombreCompleto = sNombreCompleto;
    }

    public String getSEmail() {
        return sEmail;
    }

    public void setSEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public String getSDni() {
        return sDni;
    }

    public void setSDni(String sDni) {
        this.sDni = sDni;
    }

    public String getSTelefono() {
        return sTelefono;
    }

    public void setSTelefono(String sTelefono) {
        this.sTelefono = sTelefono;
    }

    public String getSPasswordHash() {
        return sPasswordHash;
    }

    public void setSPasswordHash(String sPasswordHash) {
        this.sPasswordHash = sPasswordHash;
    }

    public String getSRol() {
        return sRol;
    }

    public void setSRol(String sRol) {
        this.sRol = sRol;
    }

    public String getSEstado() {
        return sEstado;
    }

    public void setSEstado(String sEstado) {
        this.sEstado = sEstado;
    }

    public LocalDateTime getDFecha_Creacion() {
        return dFecha_Creacion;
    }

    public void setDFecha_Creacion(LocalDateTime dFecha_Creacion) {
        this.dFecha_Creacion = dFecha_Creacion;
    }
}