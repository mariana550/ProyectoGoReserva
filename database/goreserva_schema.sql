-- =====================================================================
-- GoReserva - Sistema de gestión de reservas para canchas deportivas
-- Script inicial de la base de datos (MySQL 8.0+)
-- =====================================================================
DROP DATABASE IF EXISTS goreserva;
CREATE DATABASE goreserva CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE goreserva;

CREATE TABLE usuario (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre        VARCHAR(100) NOT NULL,
  correo        VARCHAR(120) NOT NULL,
  telefono      VARCHAR(20)  NOT NULL,
  password_hash VARCHAR(100) NOT NULL,          -- BCrypt
  rol           ENUM('CLIENTE','ADMIN') NOT NULL DEFAULT 'CLIENTE',
  activo        BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_usuario_correo UNIQUE (correo)
);

CREATE TABLE tipo_cancha (
  id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  CONSTRAINT uk_tipo_cancha_nombre UNIQUE (nombre)
);

CREATE TABLE cancha (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  tipo_id     BIGINT NOT NULL,
  nombre      VARCHAR(80) NOT NULL,
  descripcion VARCHAR(255),
  activa      BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uk_cancha_nombre UNIQUE (nombre),
  CONSTRAINT fk_cancha_tipo FOREIGN KEY (tipo_id) REFERENCES tipo_cancha(id)
);

CREATE TABLE horario (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  cancha_id     BIGINT NOT NULL,
  dia_semana    TINYINT NOT NULL,               -- 1 = lunes ... 7 = domingo
  hora_apertura TIME NOT NULL,
  hora_cierre   TIME NOT NULL,
  CONSTRAINT uk_horario_cancha_dia UNIQUE (cancha_id, dia_semana),
  CONSTRAINT ck_horario_dia CHECK (dia_semana BETWEEN 1 AND 7),
  CONSTRAINT ck_horario_horas CHECK (hora_cierre > hora_apertura),
  CONSTRAINT fk_horario_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id)
);

CREATE TABLE tarifa (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  cancha_id   BIGINT NOT NULL,
  franja      ENUM('DIURNA','NOCTURNA') NOT NULL,
  hora_inicio TIME NOT NULL,
  hora_fin    TIME NOT NULL,
  valor       DECIMAL(10,2) NOT NULL,
  CONSTRAINT uk_tarifa_cancha_franja UNIQUE (cancha_id, franja),
  CONSTRAINT ck_tarifa_valor CHECK (valor > 0),
  CONSTRAINT fk_tarifa_cancha FOREIGN KEY (cancha_id) REFERENCES cancha(id)
);

CREATE TABLE reserva (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_id  BIGINT NOT NULL,
  cancha_id   BIGINT NOT NULL,
  fecha       DATE NOT NULL,
  hora_inicio TIME NOT NULL,
  hora_fin    TIME NOT NULL,
  valor       DECIMAL(10,2) NOT NULL,
  estado      ENUM('CONFIRMADA','CANCELADA') NOT NULL DEFAULT 'CONFIRMADA',
  -- 1 cuando la reserva está CONFIRMADA y NULL cuando está CANCELADA.
  -- MySQL permite varios NULL en un índice único, así que una franja
  -- cancelada se puede volver a reservar, pero nunca dos CONFIRMADAS.
  activa      TINYINT GENERATED ALWAYS AS (IF(estado = 'CONFIRMADA', 1, NULL)) STORED,
  creada_en   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uk_reserva_franja UNIQUE (cancha_id, fecha, hora_inicio, activa),   -- Riesgo R03
  CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
  CONSTRAINT fk_reserva_cancha  FOREIGN KEY (cancha_id)  REFERENCES cancha(id)
);
CREATE INDEX ix_reserva_fecha ON reserva (fecha);

-- ------------------------------------------------------------- datos iniciales
INSERT INTO tipo_cancha (nombre) VALUES ('Fútbol 5'), ('Fútbol 8'), ('Voleibol'), ('Baloncesto');

-- El administrador inicial (admin@goreserva.com / Admin123*) lo crea el backend al arrancar
-- (clase DataInitializer), para que la contraseña quede cifrada con BCrypt (HU-08).

INSERT INTO cancha (tipo_id, nombre, descripcion) VALUES
  (1, 'Cancha 1 - Fútbol 5', 'Grama sintética, cubierta'),
  (1, 'Cancha 2 - Fútbol 5', 'Grama sintética, al aire libre'),
  (2, 'Cancha 3 - Fútbol 8', 'Grama sintética, iluminación nocturna');

-- Horario de lunes a domingo, 6:00 a 23:00, para las tres canchas
INSERT INTO horario (cancha_id, dia_semana, hora_apertura, hora_cierre)
SELECT c.id, d.n, '06:00', '23:00'
FROM cancha c CROSS JOIN (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
                          UNION SELECT 5 UNION SELECT 6 UNION SELECT 7) d;

INSERT INTO tarifa (cancha_id, franja, hora_inicio, hora_fin, valor)
SELECT id, 'DIURNA',   '06:00', '18:00', 80000  FROM cancha UNION ALL
SELECT id, 'NOCTURNA', '18:00', '23:00', 110000 FROM cancha;
