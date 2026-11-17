import { Component, OnInit, computed, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { AdminService, FiltrosAdmin, ReservaAdmin } from '../../core/admin.service';
import { Cancha, CanchaService } from '../../core/cancha.service';

interface FilaAdmin extends ReservaAdmin {
  estadoTexto: 'Confirmada' | 'Cancelada' | 'Finalizada';
  claseEstado: string;
  cancelable: boolean;
}

/** Date → "2026-11-16" con la fecha local */
function hoyTexto(): string {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit {
  reservas = signal<ReservaAdmin[]>([]);
  canchas = signal<Cancha[]>([]);
  filtros = signal<FiltrosAdmin>({ fecha: '', canchaId: '', estado: '' });
  cargando = signal(true);
  error = signal('');
  mensaje = signal('');
  confirmandoId = signal<number | null>(null);
  cancelandoId = signal<number | null>(null);

  /** Estado visible y si se puede cancelar */
  filas = computed<FilaAdmin[]>(() => {
    const ahora = new Date();
    return this.reservas().map(r => {
      const [a, m, d] = r.fecha.split('-').map(Number);
      const [h, min] = r.horaInicio.split(':').map(Number);
      const futura = new Date(a, m - 1, d, h, min) > ahora;

      let estadoTexto: FilaAdmin['estadoTexto'] = 'Confirmada';
      if (r.estado === 'CANCELADA') estadoTexto = 'Cancelada';
      else if (!futura) estadoTexto = 'Finalizada';

      return { ...r, estadoTexto, claseEstado: estadoTexto.toLowerCase(),
               cancelable: r.estado === 'CONFIRMADA' && futura };
    });
  });

  /** Tarjetas de resumen: se calculan sobre lo que muestra la tabla */
  resumen = computed(() => {
    const lista = this.reservas();
    const confirmadas = lista.filter(r => r.estado === 'CONFIRMADA');
    return {
      total: lista.length,
      confirmadas: confirmadas.length,
      canceladas: lista.length - confirmadas.length,
      ingresos: confirmadas.reduce((suma, r) => suma + r.valor, 0)
    };
  });

  hayFiltros = computed(() => {
    const f = this.filtros();
    return !!(f.fecha || f.canchaId || f.estado);
  });

  constructor(private admin: AdminService, private canchaService: CanchaService) {}

  ngOnInit(): void {
    this.canchaService.listar().subscribe({ next: c => this.canchas.set(c) });
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.error.set('');
    this.admin.listarReservas(this.filtros()).subscribe({
      next: r => { this.reservas.set(r); this.cargando.set(false); },
      error: () => { this.error.set('No se pudieron cargar las reservas.'); this.cargando.set(false); }
    });
  }

  /** Cambia un filtro y vuelve a pedir la lista al back */
  cambiarFiltro(campo: keyof FiltrosAdmin, evento: Event): void {
    const valor = (evento.target as HTMLInputElement | HTMLSelectElement).value;
    this.filtros.update(f => ({ ...f, [campo]: valor }));
    this.cargar();
  }

  verHoy(): void {
    this.filtros.update(f => ({ ...f, fecha: hoyTexto() }));
    this.cargar();
  }

  limpiar(): void {
    this.filtros.set({ fecha: '', canchaId: '', estado: '' });
    this.cargar();
  }

  cancelar(f: FilaAdmin): void {
    this.cancelandoId.set(f.id);
    this.mensaje.set('');
    this.error.set('');
    this.admin.cancelarReserva(f.id).subscribe({
      next: actualizada => {
        this.reservas.update(lista => lista.map(x => x.id === actualizada.id ? actualizada : x));
        this.mensaje.set(`La reserva ${actualizada.codigo} de ${actualizada.cliente} fue cancelada.`);
        this.confirmandoId.set(null);
        this.cancelandoId.set(null);
      },
      error: (e: HttpErrorResponse) => {
        this.error.set(e.error?.mensaje ?? 'No se pudo cancelar la reserva');
        this.confirmandoId.set(null);
        this.cancelandoId.set(null);
      }
    });
  }

  fechaCorta(texto: string): string {
    const [a, m, d] = texto.split('-').map(Number);
    return new Date(a, m - 1, d).toLocaleDateString('es-CO', { weekday: 'short', day: 'numeric', month: 'short' });
  }

  precio(valor: number): string {
    return '$' + valor.toLocaleString('es-CO');
  }
}
