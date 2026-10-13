package pe.edu.utp.uniticket_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "resolucion")
public class Resolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nid_resolucion")
    private Long nId_Resolucion;

    @OneToOne
    @JoinColumn(name = "n_id_ticket", nullable = false, unique = true)
    private Ticket ticket;

    @Column(name = "nid_admin", nullable = false)
    private Long nId_Admin;

    @Column(name = "stexto_respuesta", nullable = false, length = 1000)
    private String sTexto_Respuesta;

    @Column(name = "dfecha_respuesta", nullable = false)
    private LocalDateTime dFecha_Respuesta;

    public Resolucion() {
    }

    public Resolucion(Long nId_Resolucion, Ticket ticket, Long nId_Admin,
                     String sTexto_Respuesta, LocalDateTime dFecha_Respuesta) {
        this.nId_Resolucion = nId_Resolucion;
        this.ticket = ticket;
        this.nId_Admin = nId_Admin;
        this.sTexto_Respuesta = sTexto_Respuesta;
        this.dFecha_Respuesta = dFecha_Respuesta;
    }

    public Long getNId_Resolucion() {
        return nId_Resolucion;
    }

    public void setNId_Resolucion(Long nId_Resolucion) {
        this.nId_Resolucion = nId_Resolucion;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Long getNId_Admin() {
        return nId_Admin;
    }

    public void setNId_Admin(Long nId_Admin) {
        this.nId_Admin = nId_Admin;
    }

    public String getSTexto_Respuesta() {
        return sTexto_Respuesta;
    }

    public void setSTexto_Respuesta(String sTexto_Respuesta) {
        this.sTexto_Respuesta = sTexto_Respuesta;
    }

    public LocalDateTime getDFecha_Respuesta() {
        return dFecha_Respuesta;
    }

    public void setDFecha_Respuesta(LocalDateTime dFecha_Respuesta) {
        this.dFecha_Respuesta = dFecha_Respuesta;
    }
}
