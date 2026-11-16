    import { Injectable } from '@angular/core';
    import { HttpClient } from '@angular/common/http';
    import { Observable } from 'rxjs';
    import { environment } from '../../environments/environment';

    export interface Cancha {
    id: number;
    nombre: string;
    tipo: string;
    descripcion: string | null;
    tarifaDiurna: number | null;
    tarifaNocturna: number | null;
    }

    export interface Franja {
    horaInicio: string;
    horaFin: string;
    estado: 'LIBRE' | 'OCUPADA' | 'PASADA';
    valor: number | null;
    franja: 'DIURNA' | 'NOCTURNA' | null;
    }

    export interface Disponibilidad {
    canchaId: number;
    fecha: string;
    abierto: boolean;
    franjas: Franja[];
    }

    @Injectable({ providedIn: 'root' })
    export class CanchaService {
    private url = `${environment.apiUrl}/canchas`;

    constructor(private http: HttpClient) {}

    listar(): Observable<Cancha[]> {
        return this.http.get<Cancha[]>(this.url);
    }

    tipos(): Observable<string[]> {
        return this.http.get<string[]>(`${this.url}/tipos`);
    }

    obtener(id: number): Observable<Cancha> {
        return this.http.get<Cancha>(`${this.url}/${id}`);
    }

    disponibilidad(id: number, fecha: string): Observable<Disponibilidad> {
        return this.http.get<Disponibilidad>(`${this.url}/${id}/disponibilidad`, { params: { fecha } });
    }
    }