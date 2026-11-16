import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [RouterLink],
  template: `
    <!-- ========== 1. Sección principal ========== -->
    <article class="tarjeta paso hover-sube">
    <section class="tarjeta hero">
      <div class="hero-texto">
        <span class="etiqueta">COMPLEJO DEPORTIVO · RESERVAS EN LÍNEA</span>
        <h1>Reserva tu cancha en minutos</h1>
        <p>Consulta las franjas libres de cada cancha, reserva la hora que necesitas
          y administra tus reservas sin llamar al complejo.</p>
        <div class="acciones">
          <a routerLink="/registro" class="boton">Crear cuenta</a>
          <a routerLink="/login" class="boton secundario">Iniciar sesión</a>
        </div>
      </div>
      <!-- Dibujo de la cancha hecho con SVG (no necesita imagen) -->
      <svg class="hero-dibujo" viewBox="0 0 420 270" role="img" aria-label="Cancha vista desde arriba">
        <rect width="420" height="270" rx="16" fill="#1F3A2B" />
        <g fill="none" stroke="#3DB57A" stroke-width="3">
          <rect x="22" y="22" width="376" height="226" rx="4" />
          <line x1="210" y1="22" x2="210" y2="248" />
          <circle cx="210" cy="135" r="38" />
          <rect x="22" y="88" width="48" height="94" />
          <rect x="350" y="88" width="48" height="94" /></g>
        <circle cx="210" cy="135" r="4" fill="#3DB57A" />
      </svg>
    </section>
  </article>
    <!-- ========== 2. ¿Cómo funciona? ========== -->
    <article class="tarjeta paso hover-sube">
    <section class="bloque">
      <h2>¿Cómo funciona?</h2>
      <div class="pasos">
        @for (paso of pasos; track paso.numero) {
          <article class="tarjeta paso">
            <span class="numero">{{ paso.numero }}</span>
            <h3>{{ paso.titulo }}</h3>
            <p>{{ paso.texto }}</p>
          </article>
        }
      </div>
    </section>
    </article>
    <!-- ========== 3. Tarifas + llamado a la acción ========== -->
    <article class="tarjeta paso hover-sube">
    <section class="fila">
      <div class="tarjeta tarifas">
        <h2>Tarifas por hora</h2>
        <div class="tarifas-grid">
          @for (t of tarifas; track t.franja) {
            <div class="tarifa">
              <span class="tarifa-franja">{{ t.franja }} · {{ t.horario }}</span>
              <span class="tarifa-valor" [class.destacada]="t.destacada">{{ t.valor }}</span>
            </div>
          }
        </div>
      </div>
      <div class="cta">
        <h2>¿Listo para jugar?</h2>
        <p>Abierto todos los días de 6:00am a 11:00pm </p>
        <a routerLink="/registro" class="boton">Crear cuenta</a>
      </div>
    </section>
    </article>
    <!-- ========== 4.Footer ========== -->
    <footer class="pie">
      <span>GoReserva · Universidad de Caldas · 2026</span>
      <span>Gestión de Proyectos de Software</span>
    </footer>
  `,
//ESTILOS CSS
  styles: [`
    :host { 
      display: flex; 
      flex-direction: column; 
      gap: 32px; 
    }
    p { 
      margin: 0; 
      line-height: 1.6; 
      color: var(--texto-suave); 
    }

    /* 1. Sección principal */
    .hero { 
      display: flex; 
      flex-wrap: wrap; 
      align-items: center; 
      gap: 40px; 
      padding: 48px 40px; 
    }
    .hero-texto { 
      flex: 1 1 440px; 
      display: flex; 
      flex-direction: column; 
      gap: 18px; 
    }
    .etiqueta { 
      font-size: 13px; 
      font-weight: 600; 
      letter-spacing: .06em;
      color: var(--texto-suave); 
    }
    h1 { 
      font-size: 48px; 
      line-height: 1.05; 
    }
    .hero-texto p { 
      font-size: 17px; 
      max-width: 520px; 
    }
    .acciones { 
      display: flex; 
      flex-wrap: wrap; 
      gap: 12px; padding-top: 6px;
    }
    .hero-dibujo { 
      flex: 1 1 340px; 
      max-width: 420px; 
      width: 100%; 
      height: auto; 
      margin: 0 auto; 
    }
    /* 2. ¿Cómo funciona? */
    .bloque { 
      display: flex; 
      flex-direction: column; 
      gap: 20px; 
    }
    h2 { 
      font-size: 26px;
    }
    .pasos { 
      display: grid; 
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 16px; 
    }
    .paso { 
      display: flex; 
      flex-direction: column; 
      gap: 10px; 
      padding: 24px; 
    }
    .numero {
      width: 40px; 
      height: 40px; 
      border-radius: 10px; 
      display: grid; 
      place-items: center;
      background: var(--verde-oscuro); color: var(--verde);
      font-family: var(--fuente-titulos); font-weight: 700; font-size: 19px;
    }
    h3 { 
      font-size: 18px; 
    }

    /* 3. Tarifas + llamado a la acción */
    .fila { 
      display: flex; 
      flex-wrap: wrap; 
      gap: 16px; }
    .tarifas { 
      flex: 2 1 420px; 
      display: flex; 
      flex-direction: column; 
      gap: 16px; 
      padding: 24px; }
    .tarifas-grid { 
      display: grid; 
      grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
      gap: 12px; 
    }
    .tarifa { 
      background: var(--superficie-2); 
      border-radius: 10px; 
      padding: 18px; 
      display: flex; 
      flex-direction: column; 
      gap: 4px; 
    }
    .tarifa-franja { 
      font-size: 13px; 
      font-weight: 600; 
      color: var(--texto-suave); 
    }
    .tarifa-valor { 
      font-family: var(--fuente-titulos); 
      font-size: 30px; 
      font-weight: 700; 
    }
    .tarifa-valor.destacada { 
      color: var(--verde); 
    }
    .cta {
      flex: 1 1 280px; 
      display: flex; 
      flex-direction: column; 
      justify-content: center; 
      gap: 12px;
      padding: 24px; 
      border-radius: var(--radio);
      background: var(--verde-oscuro); 
      border: 1px solid #2E5A41;
    }
    .cta p { 
      color: #C9D3CC; 
    }
    .cta .boton { 
      align-self: flex-start; 
    }

    /* 4. Footer */
    .prueba { 
      font-size: 14px; 
      color: var(--texto-tenue); }
    .prueba strong { 
      color: var(--texto-suave); 
    }
    .pie {
      display: flex; 
      flex-wrap: wrap; 
      justify-content: space-between; 
      gap: 12px;
      padding-top: 20px; 
      border-top: 1px solid var(--borde);
      font-size: 14px; 
      color: var(--texto-tenue);
    }
    /* Celular */
    @media (max-width: 600px) {
      .hero { padding: 28px 20px; }
      h1 { font-size: 34px; }
    }
  `]
})
export class InicioComponent {
  pasos = [
    { numero: 1, titulo: 'Elige la cancha', 
      texto: 'Fútbol 5, fútbol 8, voleibol o baloncesto.' },
    { numero: 2, titulo: 'Escoge fecha y hora', 
      texto: 'Ves al instante qué franjas están libres.' },
    { numero: 3, titulo: 'Confirma y juega', 
      texto: 'Tu reserva queda guardada en "Mis reservas".' },
  ];

  tarifas = [
    { franja: 'DIURNA', horario: '6:00 a 18:00', valor: '$80.000', destacada: false },
    { franja: 'NOCTURNA', horario: '18:00 a 23:00', valor: '$110.000', destacada: true },
  ];
}
