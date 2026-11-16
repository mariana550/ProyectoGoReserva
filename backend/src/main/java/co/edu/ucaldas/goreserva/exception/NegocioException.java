package co.edu.ucaldas.goreserva.exception;

import org.springframework.http.HttpStatus;

/** Error de regla de negocio con el código HTTP que debe recibir el cliente. */
public class NegocioException extends RuntimeException {
    private final HttpStatus status;

    public NegocioException(HttpStatus status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }
}
