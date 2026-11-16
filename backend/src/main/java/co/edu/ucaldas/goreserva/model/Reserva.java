package co.edu.ucaldas.goreserva.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reserva")
public class Reserva {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @ManyToOne(optional = false) @JoinColumn(name = "cancha_id")
    private Cancha cancha;
    @Column(nullable = false)
    private LocalDate fecha;
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private EstadoReserva estado = EstadoReserva.CONFIRMADA;
    /** Columna calculada por MySQL (1 si CONFIRMADA, NULL si CANCELADA); la usa el índice único. */
    @Column(insertable = false, updatable = false)
    private Integer activa;
    @Column(name = "creada_en", nullable = false, updatable = false)
    private LocalDateTime creadaEn = LocalDateTime.now();

    public void cancelar() { this.estado = EstadoReserva.CANCELADA; }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Cancha getCancha() { return cancha; }
    public void setCancha(Cancha cancha) { this.cancha = cancha; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public EstadoReserva getEstado() { return estado; }
    public LocalDateTime getCreadaEn() { return creadaEn; }
}
