import { apiClient } from './client';
import { Tutor, TutorRequest, PageResponse } from '../types';

export const tutorApi = {
    getAll: () => apiClient.get<Tutor[]>('/tutors'),
    getAllWithPagination: (page: number, size: number) =>
        apiClient.get<PageResponse<Tutor>>(`/tutors/by-subject?subject=&page=${page}&size=${size}`),
    getAllWithPaginationNoCache: (page: number, size: number) =>
        apiClient.get<PageResponse<Tutor>>(`/tutors/by-subject?subject=&page=${page}&size=${size}&_=${Date.now()}`),
    getById: (id: number) => apiClient.get<Tutor>(`/tutors/${id}`),
    create: (data: TutorRequest) => apiClient.post<Tutor>('/tutors', data),
    update: (id: number, data: TutorRequest) => apiClient.put<Tutor>(`/tutors/${id}`, data),
    delete: (id: number) => apiClient.delete(`/tutors/${id}`),
    increaseAllRates: (percentage: number) =>
        apiClient.post(`/async/tutors/update-rates?percentageIncrease=${percentage}`),
    getAsyncTaskStatus: (taskId: string) =>
        apiClient.get(`/async/tasks/${taskId}`),
};