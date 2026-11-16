    import { Component, OnInit, computed, signal } from '@angular/core';
    import { HttpErrorResponse } from '@angular/common/http';
    import { ActivatedRoute, RouterLink } from '@angular/router';
    import { AuthService } from '../../core/auth.service';
    import { Cancha, CanchaService, Franja } from '../../core/cancha.service';
    import { Reserva, ReservaService } from '../../core/Reserva.service';

    @Component({
    selector: 'app-confirmar-reserva',
    standalone: true,
    imports: [RouterLink],
    templateUrl: './confirmar-reserva.component.html',
    styleUrl: './confirmar-reserva.component.css'
    })
    export class ConfirmarReservaComponent implements OnInit {
    canchaId = 0;
    fecha = '';
    hora = '';

    cancha = signal<Cancha | null>(null);
    franja = signal<Franja | null>(null);
    reserva = signal<Reserva | null>(null);
    acepta = signal(false);
    cargando = signal(true);
    enviando = signal(false);
    error = signal('');

    fechaLarga = computed(() => {
        const texto = this.reserva()?.fecha ?? this.fecha;
        if (!texto) return '';
        const [a, m, d] = texto.split('-').map(Number);
        return new Date(a, m - 1, d).toLocaleDateString('es-CO', { weekday: 'long', day: 'numeric', month: 'long' });
    });

    constructor(
        private route: ActivatedRoute,
        public auth: AuthService,
        private canchas: CanchaService,
        private reservas: ReservaService
    ) {}

    ngOnInit(): void {
        // Lee ?cancha=1&fecha=2026-11-14&hora=18:00
        const q = this.route.snapshot.queryParamMap;
        this.canchaId = Number(q.get('cancha'));
        this.fecha = q.get('fecha') ?? '';
        this.hora = q.get('hora') ?? '';

        this.canchas.obtener(this.canchaId).subscribe({ next: c => this.cancha.set(c) });

        this.canchas.disponibilidad(this.canchaId, this.fecha).subscribe({
        next: d => {
            const f = d.franjas.find(x => x.horaInicio === this.hora) ?? null;
            if (f && f.estado !== 'LIBRE') {
            this.error.set('Esta hora ya no está disponible. Elige otra.');
            this.franja.set(null);
            } else {
            this.franja.set(f);
            }
            this.cargando.set(false);
        },
        error: (e: HttpErrorResponse) => {
            this.error.set(e.error?.mensaje ?? 'No se pudo cargar la información');
            this.cargando.set(false);
        }
        });
    }

    confirmar(): void {
        this.enviando.set(true);
        this.error.set('');
        this.reservas.crear(this.canchaId, this.fecha, this.hora).subscribe({
        next: r => this.reserva.set(r),
        error: (e: HttpErrorResponse) => {
            this.error.set(e.status === 403
            ? 'Solo los clientes pueden hacer reservas.'
            : e.error?.mensaje ?? 'No se pudo crear la reserva');
            this.enviando.set(false);
        }
        });
    }

    precio(valor: number | null): string {
        return valor == null ? '—' : '$' + valor.toLocaleString('es-CO');
    }
    }