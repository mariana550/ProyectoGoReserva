import { Component, OnInit, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Cancha, CanchaService } from '../../core/cancha.service';

@Component({
  selector: 'app-canchas',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './canchas.component.html',
  styleUrl: './canchas.component.css'
})
export class CanchasComponent implements OnInit {
  canchas = signal<Cancha[]>([]);
  tipos = signal<string[]>(['Todos']);
  filtro = signal('Todos');
  busqueda = signal('');
  cargando = signal(true);
  error = signal('');

  // Se recalcula solo cuando cambia el filtro, la búsqueda o la lista
  visibles = computed(() => {
    const f = this.filtro();
    const b = this.busqueda().trim().toLowerCase();
    return this.canchas().filter(c =>
      (f === 'Todos' || c.tipo === f) && c.nombre.toLowerCase().includes(b));
  });

  constructor(private servicio: CanchaService) {}

  ngOnInit(): void {
    this.servicio.tipos().subscribe({
      next: t => this.tipos.set(['Todos', ...t])
    });
    this.servicio.listar().subscribe({
      next: c => { this.canchas.set(c); this.cargando.set(false); },
      error: () => {
        this.error.set('No se pudieron cargar las canchas. Revisa que el backend esté corriendo.');
        this.cargando.set(false);
      }
    });
  }

  buscar(evento: Event): void {
    this.busqueda.set((evento.target as HTMLInputElement).value);
  }

  /** 80000 → "$80.000" */
  precio(valor: number | null): string {
    return valor == null ? '—' : '$' + valor.toLocaleString('es-CO');
  }
}