package co.edu.ucaldas.goreserva.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "horario")
public class Horario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "cancha_id")
    private Cancha cancha;
    /** 1 = lunes ... 7 = domingo (igual que DayOfWeek.getValue()). */
    @Column(name = "dia_semana", nullable = false)
    private int diaSemana;
    @Column(name = "hora_apertura", nullable = false)
    private LocalTime horaApertura;
    @Column(name = "hora_cierre", nullable = false)
    private LocalTime horaCierre;

    public Long getId() { return id; }
    public Cancha getCancha() { return cancha; }
    public void setCancha(Cancha cancha) { this.cancha = cancha; }
    public int getDiaSemana() { return diaSemana; }
    public void setDiaSemana(int diaSemana) { this.diaSemana = diaSemana; }
    public LocalTime getHoraApertura() { return horaApertura; }
    public void setHoraApertura(LocalTime horaApertura) { this.horaApertura = horaApertura; }
    public LocalTime getHoraCierre() { return horaCierre; }
    public void setHoraCierre(LocalTime horaCierre) { this.horaCierre = horaCierre; }
}
