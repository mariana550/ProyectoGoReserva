import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

/** Solo deja pasar a usuarios con sesión iniciada. */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.usuario() ? true : inject(Router).createUrlTree(['/login']);
};

/** Solo deja pasar al rol indicado (HU-08). */
export function rolGuard(rol: 'CLIENTE' | 'ADMIN'): CanActivateFn {
  return () => {
    const u = inject(AuthService).usuario();
    const router = inject(Router);
    if (!u) return router.createUrlTree(['/login']);
    return u.rol === rol ? true : router.createUrlTree(['/canchas']);
  };
}
