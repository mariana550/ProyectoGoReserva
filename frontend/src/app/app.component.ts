import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';

/** Layout básico: barra superior con las opciones del rol del usuario (HU-08). */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NgIf],
  template: `
    <header class="barra">
      <div class="barra-interna">
        <a routerLink="/" class="logo">Go<span>Reserva</span></a>
        <nav>
          @if (auth.usuario(); as u) {
            <a routerLink="/canchas" routerLinkActive="activo">Canchas</a>
            @if (u.rol === 'CLIENTE') {
              <a routerLink="/mis-reservas" routerLinkActive="activo">Mis reservas</a>
            }
            @if (u.rol === 'ADMIN') {
              <a routerLink="/admin" routerLinkActive="activo">Administración</a>
            }
          } @else {
            <a routerLink="/" routerLinkActive="activo" [routerLinkActiveOptions]="{ exact: true }">Inicio</a>
            <a routerLink="/login" routerLinkActive="activo">Iniciar sesión</a>
            <a routerLink="/registro" routerLinkActive="activo">Registrarse</a>
          }
        </nav>
        @if (auth.usuario(); as u) {
          <div class="sesion">
            <span class="nombre">{{ u.nombre }}</span>
            <button class="secundario" (click)="salir()">Cerrar sesión</button>
          </div>
        }
      </div>
    </header>
    <main class="contenedor"><router-outlet /></main>`,
  styles: [`
    .barra { background: var(--barra); border-bottom: 1px solid var(--borde); }
    .barra-interna {
      max-width: 1200px; margin: 0 auto; padding: 0 24px; min-height: 60px;
      display: flex; flex-wrap: wrap; align-items: center; gap: 28px;
    }
    .logo {
      font-family: var(--fuente-titulos); font-weight: 700; font-size: 22px;
      color: var(--texto); text-decoration: none;
    }
    .logo span { color: var(--verde); }
    nav { display: flex; flex-wrap: wrap; gap: 20px; flex: 1; }
    nav a {
      padding: 18px 0 14px; color: var(--texto-suave); text-decoration: none;
      font-weight: 500; font-size: 15px; border-bottom: 2px solid transparent;
    }
    nav a:hover { color: var(--texto); }
    nav a.activo { color: var(--texto); font-weight: 600; border-bottom-color: var(--verde); }
    .sesion { display: flex; align-items: center; gap: 12px; }
    .nombre { font-size: 14px; color: var(--texto-suave); }
  `]
})
export class AppComponent {
  constructor(public auth: AuthService, private router: Router) {}

  salir(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
