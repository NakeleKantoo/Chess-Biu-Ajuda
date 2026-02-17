import { Routes } from '@angular/router';
import { Login } from './features/auth/pages/login/login';
import { Register } from './features/auth/pages/register/register';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'register', // Trocar pela home depois
        pathMatch: 'full'
    },
    {
        path: 'login',
        component: Login,
        title: 'Login Page'
    },
    {
        path: 'register',
        component: Register,
        title: 'Register Page'
    },
    {
        path: '**',
        redirectTo: 'register' // Trocar pela home depois
    }
];
