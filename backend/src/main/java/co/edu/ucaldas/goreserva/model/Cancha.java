package co.edu.ucaldas.goreserva.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cancha")
public class Cancha {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "tipo_id")
    private TipoCancha tipo;
    @Column(nullable = false, unique = true, length = 80)
    private String nombre;
    private String descripcion;
    @Column(nullable = false)
    private boolean activa = true;

    public Long getId() { return id; }
    public TipoCancha getTipo() { return tipo; }
    public void setTipo(TipoCancha tipo) { this.tipo = tipo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
