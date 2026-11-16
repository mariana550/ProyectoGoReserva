import { Component, OnInit, computed, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Cancha, CanchaService, Franja } from '../../core/cancha.service';

interface Dia {
  fecha: string;   // "2026-11-14"
  nombre: string;  // "vie"
  numero: number;  // 14
  mes: string;     // "nov"
}

/** Date → "2026-11-14" usando la fecha LOCAL (no UTC). */
function aTexto(d: Date): string {
  const mes = String(d.getMonth() + 1).padStart(2, '0');
  const dia = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${mes}-${dia}`;
}

@Component({
  selector: 'app-horarios',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './horarios.component.html',
  styleUrl: './horarios.component.css'
})
export class HorariosComponent implements OnInit {
  cancha = signal<Cancha | null>(null);
  dias = signal<Dia[]>([]);
  inicioSemana = signal(0);          // cuántos días después de hoy empieza la semana mostrada
  fecha = signal('');
  franjas = signal<Franja[]>([]);
  abierto = signal(true);
  seleccion = signal<Franja | null>(null);
  cargando = signal(false);
  error = signal('');

  libres = computed(() => this.franjas().filter(f => f.estado === 'LIBRE').length);

  fechaLarga = computed(() => {
    if (!this.fecha()) return '—';
    const [a, m, d] = this.fecha().split('-').map(Number);
    return new Date(a, m - 1, d).toLocaleDateString('es-CO', { weekday: 'long', day: 'numeric', month: 'long' });
  });

  canchaId = 0;

  constructor(private route: ActivatedRoute, private router: Router, private servicio: CanchaService) {}

  ngOnInit(): void {
    // Lee el :id de la URL /canchas/1/horarios
    this.canchaId = Number(this.route.snapshot.paramMap.get('id'));

    this.servicio.obtener(this.canchaId).subscribe({
      next: c => this.cancha.set(c),
      error: () => this.error.set('La cancha no existe')
    });

    this.armarDias();
    this.elegirDia(this.dias()[0]);
  }

  /** Arma los 7 días a mostrar: hoy + inicioSemana */
  armarDias(): void {
    const hoy = new Date();
    const lista: Dia[] = [];
    for (let i = 0; i < 7; i++) {
      const d = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate() + this.inicioSemana() + i);
      lista.push({
        fecha: aTexto(d),
        nombre: d.toLocaleDateString('es-CO', { weekday: 'short' }).replace('.', ''),
        numero: d.getDate(),
        mes: d.toLocaleDateString('es-CO', { month: 'short' }).replace('.', '')
      });
    }
    this.dias.set(lista);
  }

  moverSemana(direccion: number): void {
    this.inicioSemana.update(v => v + direccion * 7);
    this.armarDias();
    this.elegirDia(this.dias()[0]);
  }

  elegirDia(d: Dia): void {
    this.fecha.set(d.fecha);
    this.seleccion.set(null);          // al cambiar de día se borra la hora elegida
    this.cargando.set(true);
    this.error.set('');

    this.servicio.disponibilidad(this.canchaId, d.fecha).subscribe({
      next: r => {
        this.franjas.set(r.franjas);
        this.abierto.set(r.abierto);
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.error.set(e.error?.mensaje ?? 'No se pudieron cargar los horarios');
        this.franjas.set([]);
        this.cargando.set(false);
      }
    });
  }

  continuar(): void {
    const f = this.seleccion();
    if (!f) return;
    this.router.navigate(['/reservas/confirmar'], {
      queryParams: { cancha: this.canchaId, fecha: this.fecha(), hora: f.horaInicio }
    });
  }

  precio(valor: number | null): string {
    return valor == null ? '—' : '$' + valor.toLocaleString('es-CO');
  }
}