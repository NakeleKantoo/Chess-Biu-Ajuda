import { Routes } from '@angular/router';
import { Login } from './features/auth/pages/login/login';
import { Register } from './features/auth/pages/register/register';
import { Home } from './features/pages/home/home';
import { Game } from './features/pages/game/game';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
    },
    {
        path: 'home',
        component: Home,
        title: 'Home Page'
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
        path: 'game',
        component: Game,
        title: 'Game Page',
        canActivate: [authGuard]
    },
    {
        path: '**',
        redirectTo: 'home'
    }
];
