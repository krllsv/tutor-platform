import axios from 'axios';
import { message } from 'antd';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Интерцептор для обработки ошибок
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        let errorMessage = 'Произошла ошибка';

        if (error.response) {
            // Сервер вернул ответ с ошибкой
            if (error.response.data?.message) {
                errorMessage = error.response.data.message;
            } else if (error.response.status === 409) {
                errorMessage = 'Такая запись уже существует';
            } else if (error.response.status === 400) {
                errorMessage = 'Некорректные данные';
            } else if (error.response.status === 404) {
                errorMessage = 'Запись не найдена';
            } else if (error.response.status === 500) {
                errorMessage = 'Ошибка сервера. Попробуйте позже';
            }
        } else if (error.request) {
            // Запрос был сделан, но нет ответа
            errorMessage = 'Нет соединения с сервером. Убедитесь, что бэкенд запущен';
        } else {
            // Ошибка при настройке запроса
            errorMessage = error.message || 'Произошла ошибка';
        }

        message.error(errorMessage);
        return Promise.reject(error);
    }
);