const TokenKey = 'lims_token'
const UserKey = 'lims_user'

export function getToken(): string | null {
  return localStorage.getItem(TokenKey)
}

export function setToken(token: string): void {
  localStorage.setItem(TokenKey, token)
}

export function removeToken(): void {
  localStorage.removeItem(TokenKey)
}

export function getUser(): any {
  const userStr = localStorage.getItem(UserKey)
  return userStr ? JSON.parse(userStr) : null
}

export function setUser(user: any): void {
  localStorage.setItem(UserKey, JSON.stringify(user))
}

export function removeUser(): void {
  localStorage.removeItem(UserKey)
}

export function clearStorage(): void {
  removeToken()
  removeUser()
}
