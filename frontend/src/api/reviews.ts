import { apiClient } from './client';
import { Review, ReviewRequest } from '../types';

export const reviewApi = {
    getByTutor: (tutorId: number) =>
        apiClient.get<Review[]>(`/reviews/by-tutor/${tutorId}`),

    create: (data: ReviewRequest) =>
        apiClient.post<Review>('/reviews', data),
};