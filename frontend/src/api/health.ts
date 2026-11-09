import { get } from '@/utils/request'

export interface HealthInfo {
  status: string
  application: string
  time: string
}

export const fetchHealth = () => get<HealthInfo>('/health')
