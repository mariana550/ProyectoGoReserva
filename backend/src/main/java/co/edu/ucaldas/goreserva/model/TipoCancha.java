package co.edu.ucaldas.goreserva.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_cancha")
public class TipoCancha {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
