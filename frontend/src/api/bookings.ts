import { apiClient } from './client';
import { Booking, BookingRequest } from '../types';

export const bookingApi = {
    getByTutor: (tutorId: number) =>
        apiClient.get<Booking[]>(`/bookings/by-tutor/${tutorId}`),

    create: (data: BookingRequest) =>
        apiClient.post<Booking>('/bookings', data),

    updateStatus: (id: number, status: string) =>
        apiClient.patch(`/bookings/${id}/status?status=${status}`),

    delete: (id: number) =>
        apiClient.delete(`/bookings/${id}`),
};
