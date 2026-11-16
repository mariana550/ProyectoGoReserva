package co.edu.ucaldas.goreserva.dto;

import co.edu.ucaldas.goreserva.model.Usuario;

public record UsuarioResponse(Long id, String nombre, String correo, String telefono, String rol) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getCorreo(), u.getTelefono(), u.getRol().name());
    }
}
