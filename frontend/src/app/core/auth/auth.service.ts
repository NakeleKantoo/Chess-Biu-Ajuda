import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ILoginRequest, ILoginResponse, IRegisterRequest } from '../../shared/models/auth.model';
import { appSettings } from '../../app.config';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private http = inject(HttpClient);
    private readonly API_URL = appSettings.API_URL;
    private readonly TOKEN_KEY = 'auth-token';

    private authStatus = new BehaviorSubject<boolean>(this.hasToken());

    public isAuthenticated$ = this.authStatus.asObservable();
    get isAuthenticated(): boolean {
        const isLogged = this.authStatus.value;
        const hasToken = !!this.getToken();

        if (isLogged && !hasToken) {
            this.authStatus.next(false);
            return false;
        }
        return isLogged;
    }

    login(credentials: ILoginRequest): Observable<ILoginResponse> {
        return this.http.post<ILoginResponse>(`${this.API_URL}/auth/login`, credentials).pipe(
            tap(res => this.setSession(res))
        );
    }

    register(userData: IRegisterRequest): Observable<ILoginResponse> {
        return this.http.post<ILoginResponse>(`${this.API_URL}/users`, userData).pipe(
            tap(res => this.setSession(res))
        );
    }

    private setSession(authResult: ILoginResponse) {
        localStorage.setItem(this.TOKEN_KEY, authResult.accessToken);
        this.authStatus.next(true);
    }

    logout() {
        localStorage.removeItem(this.TOKEN_KEY);
        this.authStatus.next(false);
    }

    getToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    private hasToken(): boolean {
        return !!this.getToken();
    }

}