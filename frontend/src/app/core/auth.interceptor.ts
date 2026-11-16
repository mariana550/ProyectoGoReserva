import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/** HU-07: envía el token JWT en cada petición; si el backend responde 401, cierra la sesión. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.token();
  const peticion = token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  return next(peticion).pipe(
    catchError((e: HttpErrorResponse) => {
      if (e.status === 401 && token) {
        auth.logout();
        router.navigate(['/login']);
      }
      return throwError(() => e);
    })
  );
};
