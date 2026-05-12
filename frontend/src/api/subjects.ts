import { apiClient } from './client';
import { Subject, SubjectRequest } from '../types';

export const subjectApi = {
    getAll: () => apiClient.get<Subject[]>('/subjects'),
    getById: (id: number) => apiClient.get<Subject>(`/subjects/${id}`),
    create: (data: SubjectRequest) => apiClient.post<Subject>('/subjects', data),
    update: (id: number, data: SubjectRequest) => apiClient.put<Subject>(`/subjects/${id}`, data),
    delete: (id: number) => apiClient.delete(`/subjects/${id}`),
};