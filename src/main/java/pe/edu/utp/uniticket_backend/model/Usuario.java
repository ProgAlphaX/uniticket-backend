package pe.edu.utp.uniticket_backend.model;

import java.time.LocalDateTime;

public class Usuario {
    private Long nId_Usuario;
    private String sNombreCompleto;
    private String sEmail;
    private String sDni;
    private String sTelefono;
    private String sPasswordHash;
    private String sRol;
    private String sEstado;
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

    public String getSTelefono() { return sTelefono; }

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