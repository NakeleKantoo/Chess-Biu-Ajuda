export interface IRegisterRequest {
    username: string;
    password: string;
    confirmPassword: string;
}

export interface ILoginRequest {
    username: string;
    password?: string;
}

export interface ILoginResponse {
    accessToken: string;
    tokenType: string;
    username: string;
    role: ERole;
}

export enum ERole {
    USER = 'USER',
    ADMIN = 'ADMIN'
}