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
        title: 'Página Inicial'
    },
    {
        path: 'login',
        component: Login,
        title: 'Página de Login'
    },
    {
        path: 'register',
        component: Register,
        title: 'Página de Cadastro'
    },
    {
        path: 'game/:id',
        component: Game,
        title: 'Página do Jogo',
        canActivate: [authGuard]
    },
    {
        path: '**',
        redirectTo: 'home'
    }
];
