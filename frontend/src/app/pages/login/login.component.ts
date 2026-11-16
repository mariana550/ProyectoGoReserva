import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <form class="tarjeta formulario" (ngSubmit)="entrar()" #f="ngForm">
      <article class="tarjeta paso hover-sube">
      <h2>Iniciar sesión</h2>
      <p class="subtitulo">Entra para reservar y ver tus reservas.</p>

      <label for="correo">Correo electrónico</label>
      <input id="correo" name="correo" type="email" [(ngModel)]="correo"
        required autocomplete="email" placeholder="tucorreo@ejemplo.com">

      <label for="password">Contraseña</label>
      <input id="password" name="password" type="password" [(ngModel)]="password"
        required autocomplete="current-password">

      @if (error) {
        <p class="alerta-error" role="alert">{{ error }}</p>
      }

      <button class="ancho" type="submit" [disabled]="f.invalid || cargando">
        {{ cargando ? 'Entrando...' : 'Entrar' }}
      </button>

      <p class="pie-form">¿No tienes cuenta? <a routerLink="/registro">Regístrate</a></p>
    </article>
    </form>
  `
})
export class LoginComponent {
  correo = '';
  password = '';
  error = '';
  cargando = false;

  constructor(private auth: AuthService, private router: Router) {}

  entrar(): void {
    this.cargando = true;
    this.error = '';
    this.auth.login(this.correo, this.password).subscribe({
      next: u => this.router.navigate([u.rol === 'ADMIN' ? '/admin' : '/canchas']),
      // Mensaje genérico (HU-07): no dice qué dato falló
      error: () => { this.error = 'Correo o contraseña incorrectos'; this.cargando = false; }
    });
  }
}
