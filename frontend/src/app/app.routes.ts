import { Routes } from '@angular/router';
import { authGuard, rolGuard } from './core/auth.guard';
import { InicioComponent } from './pages/inicio/inicio.component';
import { LoginComponent } from './pages/login/login.component';
import { RegistroComponent } from './pages/registro/registro.component';
import { CanchasComponent } from './pages/canchas/canchas.component';
import { MisReservasComponent } from './pages/mis-reservas/mis-reservas.component';
import { AdminComponent } from './pages/admin/admin.component';
import { ConfirmarReservaComponent } from './pages/confirmar-reserva/confirmar-reserva.component';
import { HorariosComponent } from './pages/horarios/horarios.component';

export const routes: Routes = [
  { path: '', component: InicioComponent },
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  { path: 'canchas', component: CanchasComponent, canActivate: [authGuard] },
  { path: 'mis-reservas', component: MisReservasComponent, canActivate: [rolGuard('CLIENTE')] },
  { path: 'admin', component: AdminComponent, canActivate: [rolGuard('ADMIN')] },
  { path: 'canchas/:id/horarios', component: HorariosComponent, canActivate: [authGuard] },
  { path: 'reservas/confirmar', component: ConfirmarReservaComponent, canActivate: [rolGuard('CLIENTE')] },
  { path: '**', redirectTo: '' }
];
