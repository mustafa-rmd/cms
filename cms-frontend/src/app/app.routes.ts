import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./components/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'search', loadComponent: () => import('./components/search/search.component').then(m => m.SearchComponent) },
  { path: 'browse', loadComponent: () => import('./components/browse/browse.component').then(m => m.BrowseComponent) },
  { 
    path: 'shows', 
    loadComponent: () => import('./components/shows/shows.component').then(m => m.ShowsComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'shows/new', 
    loadComponent: () => import('./components/shows/show-form/show-form.component').then(m => m.ShowFormComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'shows/:id', 
    loadComponent: () => import('./components/shows/show-detail/show-detail.component').then(m => m.ShowDetailComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'shows/:id/edit', 
    loadComponent: () => import('./components/shows/show-form/show-form.component').then(m => m.ShowFormComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'users', 
    loadComponent: () => import('./components/users/users.component').then(m => m.UsersComponent),
    canActivate: [authGuard]
  },
  { 
    path: 'import', 
    loadComponent: () => import('./components/import/import.component').then(m => m.ImportComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '/home' }
];
