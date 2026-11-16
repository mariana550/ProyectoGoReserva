import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UsuarioSesion { token: string; id: number; nombre: string; correo: string; rol: 'CLIENTE' | 'ADMIN'; }
export interface Registro { nombre: string; correo: string; telefono: string; password: string; }

const CLAVE = 'goreserva_sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly usuario = signal<UsuarioSesion | null>(this.leer());

  constructor(private http: HttpClient) {}

  registrar(datos: Registro): Observable<unknown> {
    return this.http.post(`${environment.apiUrl}/auth/registro`, datos);
  }

  login(correo: string, password: string): Observable<UsuarioSesion> {
    return this.http.post<UsuarioSesion>(`${environment.apiUrl}/auth/login`, { correo, password }).pipe(
      tap(u => { localStorage.setItem(CLAVE, JSON.stringify(u)); this.usuario.set(u); })
    );
  }

  /** HU-07: al cerrar sesión se elimina el token. */
  logout(): void {
    localStorage.removeItem(CLAVE);
    this.usuario.set(null);
  }

  token(): string | null {
    return this.usuario()?.token ?? null;
  }

  private leer(): UsuarioSesion | null {
    try {
      const s = localStorage.getItem(CLAVE);
      return s ? JSON.parse(s) as UsuarioSesion : null;
    } catch {
      return null;
    }
  }
}
