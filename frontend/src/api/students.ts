import { apiClient } from './client';
import { Student, StudentRequest } from '../types';

export const studentApi = {
    getAll: () => apiClient.get<Student[]>('/students'),
    getById: (id: number) => apiClient.get<Student>(`/students/${id}`),
    create: (data: StudentRequest) => apiClient.post<Student>('/students', data),
    update: (id: number, data: StudentRequest) => apiClient.put<Student>(`/students/${id}`, data),
    delete: (id: number) => apiClient.delete(`/students/${id}`),
};