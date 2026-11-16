    import { Injectable } from '@angular/core';
    import { HttpClient } from '@angular/common/http';
    import { Observable } from 'rxjs';
    import { environment } from '../../environments/environment';

    export interface Reserva {
    id: number;
    codigo: string;
    canchaId: number;
    cancha: string;
    tipo: string;
    fecha: string;
    horaInicio: string;
    horaFin: string;
    valor: number;
    estado: 'CONFIRMADA' | 'CANCELADA';
    }

    @Injectable({ providedIn: 'root' })
    export class ReservaService {
    private url = `${environment.apiUrl}/reservas`;

    constructor(private http: HttpClient) {}

    /** POST /api/reservas */
    crear(canchaId: number, fecha: string, horaInicio: string): Observable<Reserva> {
        return this.http.post<Reserva>(this.url, { canchaId, fecha, horaInicio });
    }

    /** GET /api/reservas/mias */
    mias(): Observable<Reserva[]> {
        return this.http.get<Reserva[]>(`${this.url}/mias`);
    }

    /** PUT /api/reservas/7/cancelar */
    cancelar(id: number): Observable<Reserva> {
        return this.http.put<Reserva>(`${this.url}/${id}/cancelar`, {});
    }
    }