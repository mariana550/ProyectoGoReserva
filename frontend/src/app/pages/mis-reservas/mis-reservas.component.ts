import { Component, OnInit, computed, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { Reserva, ReservaService } from '../../core/Reserva.service';

type Pestana = 'proximas' | 'historial';

/** La reserva + datos calculados para mostrar */
interface Fila extends Reserva {
  estadoTexto: 'Confirmada' | 'Cancelada' | 'Finalizada';
  claseEstado: string;
  esProxima: boolean;
}

@Component({
  selector: 'app-mis-reservas',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './mis-reservas.component.html',
  styleUrl: './mis-reservas.component.css'
})
export class MisReservasComponent implements OnInit {
  reservas = signal<Reserva[]>([]);
  pestana = signal<Pestana>('proximas');
  cargando = signal(true);
  error = signal('');
  mensaje = signal('');
  confirmandoId = signal<number | null>(null);   // fila que está preguntando "¿Seguro?"
  cancelandoId = signal<number | null>(null);    // fila que se está cancelando

  /** Le agrega a cada reserva su estado visible y si es próxima */
  private filas = computed<Fila[]>(() => {
    const ahora = new Date();
    return this.reservas().map(r => {
      const [a, m, d] = r.fecha.split('-').map(Number);
      const [h, min] = r.horaInicio.split(':').map(Number);
      const futura = new Date(a, m - 1, d, h, min) > ahora;

      let estadoTexto: Fila['estadoTexto'] = 'Confirmada';
      if (r.estado === 'CANCELADA') estadoTexto = 'Cancelada';
      else if (!futura) estadoTexto = 'Finalizada';

      return {
        ...r,
        estadoTexto,
        claseEstado: estadoTexto.toLowerCase(),
        esProxima: r.estado === 'CONFIRMADA' && futura
      };
    });
  });

  /** Próximas: de la más cercana a la más lejana. Historial: de la más reciente a la más vieja. */
  proximas = computed(() => this.filas().filter(f => f.esProxima).reverse());
  historial = computed(() => this.filas().filter(f => !f.esProxima));
  visibles = computed(() => this.pestana() === 'proximas' ? this.proximas() : this.historial());

  constructor(private servicio: ReservaService) {}

  ngOnInit(): void {
    this.servicio.mias().subscribe({
      next: r => { this.reservas.set(r); this.cargando.set(false); },
      error: () => { this.error.set('No se pudieron cargar tus reservas.'); this.cargando.set(false); }
    });
  }

  cancelar(f: Fila): void {
    this.cancelandoId.set(f.id);
    this.mensaje.set('');
    this.error.set('');
    this.servicio.cancelar(f.id).subscribe({
      next: actualizada => {
        // Reemplaza solo esa reserva en la lista, sin pedir todo otra vez
        this.reservas.update(lista => lista.map(x => x.id === actualizada.id ? actualizada : x));
        this.mensaje.set(`La reserva ${actualizada.codigo} fue cancelada.`);
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