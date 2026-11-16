import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { AuthService, Registro } from '../../core/auth.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <form class="tarjeta formulario" (ngSubmit)="registrar()" #f="ngForm">
      <article class="tarjeta paso hover-sube">
      <h2>Crear cuenta</h2>
      <p class="subtitulo">Regístrate gratis y reserva en minutos.</p>

      <label for="nombre">Nombre completo *</label>
      <input id="nombre" name="nombre" [(ngModel)]="datos.nombre"
          required maxlength="100" autocomplete="name">

      <div class="dos-columnas">
        <div>
          <label for="correo">Correo *</label>
          <input id="correo" name="correo" type="email" [(ngModel)]="datos.correo"
            required email autocomplete="email">
        </div>
        <div>
          <label for="telefono">Teléfono *</label>
          <input id="telefono" name="telefono" type="tel" [(ngModel)]="datos.telefono"
            required maxlength="20" autocomplete="tel">
        </div>
      </div>

      <label for="password">Contraseña *</label>
      <input id="password" name="password" type="password" [(ngModel)]="datos.password"
        required minlength="8" autocomplete="new-password">
      <p class="ayuda">Mínimo 8 caracteres.</p>

      @if (error) {
        <p class="alerta-error" role="alert">{{ error }}</p>
      }
      @if (exito) {
        <p class="exito">Cuenta creada. Te llevamos al inicio de sesión...</p>
      }

      <button class="ancho" type="submit" [disabled]="f.invalid || cargando">
        {{ cargando ? 'Creando cuenta...' : 'Registrarme' }}
      </button>

      <p class="pie-form">¿Ya tienes cuenta? <a routerLink="/login">Inicia sesión</a></p>
      </article>
    </form>
  `
})
export class RegistroComponent {
  datos: Registro = { nombre: '', correo: '', telefono: '', password: '' };
  error = '';
  exito = false;
  cargando = false;

  constructor(private auth: AuthService, private router: Router) {}

  registrar(): void {
    this.cargando = true;
    this.error = '';
    this.auth.registrar(this.datos).subscribe({
      next: () => { this.exito = true; setTimeout(() => this.router.navigate(['/login']), 1500); },
      error: (e: HttpErrorResponse) => {
        this.error = e.error?.mensaje ?? 'No se pudo crear la cuenta';   // p. ej. "Ya existe una cuenta con este correo"
        this.cargando = false;
      }
    });
  }
}
