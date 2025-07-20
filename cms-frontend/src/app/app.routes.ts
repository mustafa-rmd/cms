import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./components/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'search', loadComponent: () => import('./components/search/search.component').then(m => m.SearchComponent) },
  { path: 'shows', loadComponent: () => import('./components/shows/shows.component').then(m => m.ShowsComponent) },
  { path: 'shows/new', loadComponent: () => import('./components/shows/show-form/show-form.component').then(m => m.ShowFormComponent) },
  { path: 'shows/:id', loadComponent: () => import('./components/shows/show-detail/show-detail.component').then(m => m.ShowDetailComponent) },
  { path: 'shows/:id/edit', loadComponent: () => import('./components/shows/show-form/show-form.component').then(m => m.ShowFormComponent) },
  { path: 'users', loadComponent: () => import('./components/users/users.component').then(m => m.UsersComponent) },
  { path: 'import', loadComponent: () => import('./components/import/import.component').then(m => m.ImportComponent) },
  { path: '**', redirectTo: '/home' }
];
