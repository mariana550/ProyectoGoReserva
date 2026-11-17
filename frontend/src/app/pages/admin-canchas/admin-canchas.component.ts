import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { AdminCanchasService, CanchaAdmin, CanchaForm, HorarioDia } from '../../core/admin-canchas.service';

/** Fila del editor de horario: un día de la semana */
interface DiaEditable {
  diaSemana: number;
  nombre: string;
  abierto: boolean;
  apertura: string;   // "06:00"
  cierre: string;     // "23:00"
}

const DIAS = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];

function formVacio(): CanchaForm {
  return { nombre: '', tipo: '', descripcion: '', tarifaDiurna: null, tarifaNocturna: null };
}

/** Gestión de canchas (HU-09), horarios (HU-10) y precios (HU-11). Solo ADMIN. */
@Component({
  selector: 'app-admin-canchas',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './admin-canchas.component.html',
  styleUrl: './admin-canchas.component.css'
})
export class AdminCanchasComponent implements OnInit {
  canchas = signal<CanchaAdmin[]>([]);
  tipos = signal<string[]>([]);
  cargando = signal(true);
  error = signal('');
  mensaje = signal('');

  // Formulario crear / editar
  formAbierto = signal(false);
  editandoId = signal<number | null>(null);
  form: CanchaForm = formVacio();
  errorForm = signal('');
  guardando = signal(false);

  // Editor de horario
  horarioDe = signal<CanchaAdmin | null>(null);
  dias = signal<DiaEditable[]>([]);
  errorHorario = signal('');

  constructor(private servicio: AdminCanchasService) {}

  ngOnInit(): void {
    this.servicio.tipos().subscribe({ next: t => this.tipos.set(t ?? []) });
    this.cargar();
  }

  cargar(): void {
    this.cargando.set(true);
    this.servicio.listar().subscribe({
      next: c => { this.canchas.set(c); this.cargando.set(false); },
      error: () => {
        this.error.set('No se pudieron cargar las canchas. Revisa que el backend esté corriendo.');
        this.cargando.set(false);
      }
    });
  }

  // ===================== HU-09 y HU-11: crear y editar =====================

  nueva(): void {
    this.cerrarHorario();
    this.form = formVacio();
    this.form.tipo = this.tipos()[0] ?? '';
    this.editandoId.set(null);
    this.errorForm.set('');
    this.formAbierto.set(true);
  }

  editar(c: CanchaAdmin): void {
    this.cerrarHorario();
    this.form = {
      nombre: c.nombre, tipo: c.tipo, descripcion: c.descripcion ?? '',
      tarifaDiurna: c.tarifaDiurna, tarifaNocturna: c.tarifaNocturna
    };
    this.editandoId.set(c.id);
    this.errorForm.set('');
    this.formAbierto.set(true);
  }

  cancelarForm(): void {
    this.formAbierto.set(false);
    this.errorForm.set('');
  }

  guardar(): void {
    const f = this.form;
    if (!f.nombre.trim() || !f.tipo) { this.errorForm.set('El nombre y el tipo son obligatorios'); return; }
    if (!f.tarifaDiurna || f.tarifaDiurna <= 0 || !f.tarifaNocturna || f.tarifaNocturna <= 0) {
      this.errorForm.set('Los dos precios son obligatorios y deben ser mayores que cero');
      return;
    }
    this.guardando.set(true);
    this.errorForm.set('');
    const id = this.editandoId();
    const peticion = id == null ? this.servicio.crear(f) : this.servicio.editar(id, f);
    peticion.subscribe({
      next: c => {
        this.mensaje.set(id == null ? `Se creó ${c.nombre}.` : `Se guardaron los cambios de ${c.nombre}.`);
        this.formAbierto.set(false);
        this.guardando.set(false);
        this.cargar();
      },
      error: (e: HttpErrorResponse) => {
        this.errorForm.set(e.error?.mensaje ?? 'No se pudo guardar la cancha');
        this.guardando.set(false);
      }
    });
  }

  /** Activar o desactivar: una cancha desactivada no aparece en el catálogo del cliente */
  cambiarEstado(c: CanchaAdmin): void {
    this.mensaje.set('');
    this.servicio.cambiarEstado(c.id, !c.activa).subscribe({
      next: act => {
        this.canchas.update(lista => lista.map(x => x.id === act.id ? act : x));
        this.mensaje.set(`${act.nombre} quedó ${act.activa ? 'activa' : 'desactivada'}.`);
      },
      error: (e: HttpErrorResponse) => this.error.set(e.error?.mensaje ?? 'No se pudo cambiar el estado')
    });
  }

  // ===================== HU-10: horario semanal =====================

  abrirHorario(c: CanchaAdmin): void {
    this.formAbierto.set(false);
    this.errorHorario.set('');
    this.servicio.horarios(c.id).subscribe({
      next: lista => {
        this.dias.set(DIAS.map((nombre, i) => {
          const h = lista.find(x => x.diaSemana === i + 1);
          return {
            diaSemana: i + 1, nombre, abierto: !!h,
            apertura: h ? h.horaApertura.slice(0, 5) : '06:00',
            cierre: h ? h.horaCierre.slice(0, 5) : '23:00'
          };
        }));
        this.horarioDe.set(c);
      },
      error: (e: HttpErrorResponse) => this.error.set(e.error?.mensaje ?? 'No se pudo cargar el horario')
    });
  }

  cerrarHorario(): void {
    this.horarioDe.set(null);
    this.errorHorario.set('');
  }

  /** Cuántas franjas de una hora salen con ese horario */
  franjas(d: DiaEditable): number {
    if (!d.abierto) return 0;
    const [ha, ma] = d.apertura.split(':').map(Number);
    const [hc, mc] = d.cierre.split(':').map(Number);
    return Math.max(0, Math.floor(((hc * 60 + mc) - (ha * 60 + ma)) / 60));
  }

  guardarHorario(): void {
    const c = this.horarioDe();
    if (!c) return;
    const malo = this.dias().find(d => d.abierto && d.cierre <= d.apertura);
    if (malo) {
      this.errorHorario.set(`${malo.nombre}: la hora de cierre debe ser posterior a la de apertura`);
      return;
    }
    const semana: HorarioDia[] = this.dias()
      .filter(d => d.abierto)
      .map(d => ({ diaSemana: d.diaSemana, horaApertura: d.apertura, horaCierre: d.cierre }));
    this.servicio.guardarHorarios(c.id, semana).subscribe({
      next: () => {
        this.mensaje.set(`Horario de ${c.nombre} guardado. Las reservas ya confirmadas no cambian.`);
        this.cerrarHorario();
      },
      error: (e: HttpErrorResponse) => this.errorHorario.set(e.error?.mensaje ?? 'No se pudo guardar el horario')
    });
  }

  /** 80000 → "$80.000" */
  precio(valor: number | null): string {
    return valor == null ? '—' : '$' + valor.toLocaleString('es-CO');
  }
}
