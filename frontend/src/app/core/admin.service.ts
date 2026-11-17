    import { Injectable } from '@angular/core';
    import { HttpClient, HttpParams } from '@angular/common/http';
    import { Observable } from 'rxjs';
    import { environment } from '../../environments/environment';

    export interface ReservaAdmin {
    id: number;
    codigo: string;
    cancha: string;
    tipo: string;
    fecha: string;
    horaInicio: string;
    horaFin: string;
    valor: number;
    estado: 'CONFIRMADA' | 'CANCELADA';
    cliente: string;
    correo: string;
    telefono: string;
    creadaEn: string;
    }

    export interface FiltrosAdmin {
    fecha: string;          // '' = todas
    canchaId: string;       // '' = todas
    estado: string;         // '' = todos
    }

    @Injectable({ providedIn: 'root' })
    export class AdminService {
    private url = `${environment.apiUrl}/admin`;

    constructor(private http: HttpClient) {}

    /** Solo manda los filtros que tienen valor */
    listarReservas(f: FiltrosAdmin): Observable<ReservaAdmin[]> {
        let params = new HttpParams();
        if (f.fecha) params = params.set('fecha', f.fecha);
        if (f.canchaId) params = params.set('canchaId', f.canchaId);
        if (f.estado) params = params.set('estado', f.estado);
        return this.http.get<ReservaAdmin[]>(`${this.url}/reservas`, { params });
    }

    cancelarReserva(id: number): Observable<ReservaAdmin> {
        return this.http.put<ReservaAdmin>(`${this.url}/reservas/${id}/cancelar`, {});
    }
    }