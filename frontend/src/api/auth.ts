import { get, post } from '@/utils/request'

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatarUrl: string | null
}

export interface LoginPayload {
  username: string
  password: string
  nickname?: string
}

export interface LoginResult {
  accessToken: string
  refreshToken: string
  expiresInSeconds: number
  user: UserInfo
}

export const login = (payload: LoginPayload) => post<LoginResult>('/auth/login', payload)

export const register = (payload: LoginPayload) => post<LoginResult>('/auth/register', payload)

export const fetchMe = () => get<UserInfo>('/auth/me')
