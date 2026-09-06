package pe.edu.utp.uniticket_backend.model;

import java.time.LocalDateTime;

public class Ticket {
    private Long nId_Ticket;
    private String sNum_Ticket;
    private Long nId_Usuario;
    private String sTipo;
    private String sAsunto;
    private String sDescripcion;
    private String sEstado;
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
