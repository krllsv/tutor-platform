export interface Subject {
    id: number;
    name: string;
    category: string;
    description: string;
}

export interface SubjectRequest {
    name: string;
    category: string;
    description: string;
}

export interface Tutor {
    id: number;
    fullname: string;
    email: string;
    hourlyRate: number;
    experienceYears: number;
    subjectId: number;
    subjectName: string;
}

export interface TutorRequest {
    firstName: string;
    lastName: string;
    hourlyRate: number;
    startYear: number;
    email: string;
    subjectId: number | null;
}

export interface Student {
    id: number;
    fullName: string;
    phone: string;
    email: string;
    budget: number;
    subjects?: Subject[];
}

export interface StudentRequest {
    firstName: string;
    lastName: string;
    phone: string;
    email: string;
    budget: number;
    subjectIds: number[];
}

export interface Review {
    id: number;
    rating: number;
    comment: string;
    createdAt: string;
    studentId: number;
    studentName: string;
    tutorId: number;
    tutorName: string;
}

export interface ReviewRequest {
    rating: number;
    comment: string;
    studentId: number;
    tutorId: number;
}

export interface Booking {
    id: number;
    dateTime: string;
    durationMinutes: number;
    endTime: string;
    status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
    message: string;
    studentId: number;
    studentName: string;
    tutorId: number;
    tutorName: string;
}

export interface BookingRequest {
    dateTime: string;
    durationMinutes: number;
    message: string;
    studentId: number;
    tutorId: number;
}

export interface AsyncTask {
    taskId: string;
    status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
    operationType: string;
    progress: number;
    message: string;
    createdAt: string;
    completedAt: string | null;
    result: any;
    errorMessage: string | null;
}

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}