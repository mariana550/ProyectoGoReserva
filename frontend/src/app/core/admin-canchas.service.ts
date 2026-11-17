import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

/** Cancha como la ve el administrador (incluye las desactivadas). */
export interface CanchaAdmin {
  id: number;
  nombre: string;
  tipo: string;
  descripcion: string | null;
  activa: boolean;
  tarifaDiurna: number | null;
  tarifaNocturna: number | null;
}

/** Lo que se envía al crear o editar una cancha (HU-09 y HU-11). */
export interface CanchaForm {
  nombre: string;
  tipo: string;
  descripcion: string;
  tarifaDiurna: number | null;
  tarifaNocturna: number | null;
}

/** Horario de un día: 1 = lunes ... 7 = domingo (HU-10). */
export interface HorarioDia {
  diaSemana: number;
  horaApertura: string;   // "06:00" (el back puede devolver "06:00:00")
  horaCierre: string;
}

@Injectable({ providedIn: 'root' })
export class AdminCanchasService {
  private url = `${environment.apiUrl}/admin/canchas`;

  constructor(private http: HttpClient) {}

  listar(): Observable<CanchaAdmin[]> {
    return this.http.get<CanchaAdmin[]>(this.url);
  }

  tipos(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.apiUrl}/canchas/tipos`);
  }

  crear(c: CanchaForm): Observable<CanchaAdmin> {
    return this.http.post<CanchaAdmin>(this.url, c);
  }

  editar(id: number, c: CanchaForm): Observable<CanchaAdmin> {
    return this.http.put<CanchaAdmin>(`${this.url}/${id}`, c);
  }

  cambiarEstado(id: number, activa: boolean): Observable<CanchaAdmin> {
    return this.http.put<CanchaAdmin>(`${this.url}/${id}/${activa ? 'activar' : 'desactivar'}`, {});
  }

  horarios(id: number): Observable<HorarioDia[]> {
    return this.http.get<HorarioDia[]>(`${this.url}/${id}/horarios`);
  }

  guardarHorarios(id: number, semana: HorarioDia[]): Observable<HorarioDia[]> {
    return this.http.put<HorarioDia[]>(`${this.url}/${id}/horarios`, semana);
  }
}
