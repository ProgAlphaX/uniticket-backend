package pe.edu.utp.uniticket_backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")

public class Usuario {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    
    @Column (name = "id_usuario")
    private Long nId_Usuario;

    @Column(name = "sombre_completo", length = 100)
    private String sNombreCompleto;

    @Column(name = "semail", length = 100, unique = true)
    private String sEmail;

    @Column(name = "sdni", length = 20)
    private String sDni;

    @Column(name = "stelefono", length = 20)
    private String sTelefono;

    @Column(name = "spassword_hash", length = 255)
    private String sPasswordHash;

    @Column(name = "srol", length = 50)
    private String sRol;

    @Column(name = "sestado", length = 50)
    private String sEstado;

    @Column(name = "dfecha_creacion")
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

	public Usuario orElseThrow(Object object) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
	}
}