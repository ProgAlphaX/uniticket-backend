package pe.edu.utp.uniticket_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nid_ticket")
    private Long nId_Ticket;

    @Column(name = "snum_ticket", unique = true, length = 20)
    private String sNum_Ticket;

    @Column(name = "nid_usuario", nullable = false)
    private Long nId_Usuario;

    @Column(name = "stipo", nullable = false, length = 20)
    private String sTipo;

    @Column(name = "sasunto", nullable = false, length = 150)
    private String sAsunto;

    @Column(name = "sdescripcion", nullable = false, length = 1000)
    private String sDescripcion;

    @Column(name = "sestado", nullable = false, length = 20)
    private String sEstado;

    @Column(name = "dfecha_creacion", nullable = false)
    private LocalDateTime dFecha_Creacion;

    public Ticket() {
    }

    public Ticket(Long nId_Ticket, String sNum_Ticket, Long nId_Usuario, String sTipo,
                  String sAsunto, String sDescripcion, String sEstado, LocalDateTime dFecha_Creacion) {
        this.nId_Ticket = nId_Ticket;
        this.sNum_Ticket = sNum_Ticket;
        this.nId_Usuario = nId_Usuario;
        this.sTipo = sTipo;
        this.sAsunto = sAsunto;
        this.sDescripcion = sDescripcion;
        this.sEstado = sEstado;
        this.dFecha_Creacion = dFecha_Creacion;
    }

    public Long getNId_Ticket() {
        return nId_Ticket;
    }

    public void setNId_Ticket(Long nId_Ticket) {
        this.nId_Ticket = nId_Ticket;
    }

    public String getSNum_Ticket() {
        return sNum_Ticket;
    }

    public void setSNum_Ticket(String sNum_Ticket) {
        this.sNum_Ticket = sNum_Ticket;
    }

    public Long getNId_Usuario() {
        return nId_Usuario;
    }

    public void setNId_Usuario(Long nId_Usuario) {
        this.nId_Usuario = nId_Usuario;
    }

    public String getSTipo() {
        return sTipo;
    }

    public void setSTipo(String sTipo) {
        this.sTipo = sTipo;
    }

    public String getSAsunto() {
        return sAsunto;
    }

    public void setSAsunto(String sAsunto) {
        this.sAsunto = sAsunto;
    }

    public String getSDescripcion() {
        return sDescripcion;
    }

    public void setSDescripcion(String sDescripcion) {
        this.sDescripcion = sDescripcion;
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
