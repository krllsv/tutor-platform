import React, { useState, useEffect } from 'react';
import { Card, Avatar, Tag, Rate, Button, Select, DatePicker, Input, message, Modal, Form, List, Typography, Space, Table, Badge, Collapse, Popconfirm } from 'antd';
import { UserOutlined, EditOutlined, DeleteOutlined, StarOutlined, CalendarOutlined, BookOutlined } from '@ant-design/icons';
import { Tutor, Student, Review, Booking } from '../types';
import { reviewApi } from '../api/reviews';
import { bookingApi } from '../api/bookings';
import { studentApi } from '../api/students';
import dayjs, { Dayjs } from 'dayjs';
import utc from 'dayjs/plugin/utc';

dayjs.extend(utc);

const { TextArea } = Input;
const { Text } = Typography;
const { Panel } = Collapse;

interface TutorCardProps {
    tutor: Tutor;
    onEdit?: (tutor: Tutor) => void;
    onDelete?: (id: number) => void;
    onRefresh?: () => void;
}

export const TutorCard: React.FC<TutorCardProps> = ({ tutor, onEdit, onDelete, onRefresh }) => {
    const [reviews, setReviews] = useState<Review[]>([]);
    const [bookings, setBookings] = useState<Booking[]>([]);
    const [students, setStudents] = useState<Student[]>([]);
    const [showReviewModal, setShowReviewModal] = useState(false);
    const [selectedDate, setSelectedDate] = useState<Dayjs>(dayjs());
    const [selectedDuration, setSelectedDuration] = useState(60);
    const [selectedTime, setSelectedTime] = useState<Dayjs | null>(null);
    const [bookingForm] = Form.useForm();
    const [reviewForm] = Form.useForm();
    const [activeKey, setActiveKey] = useState<string | string[]>([]);

    useEffect(() => {
        loadReviews();
        loadBookings();
        loadStudents();
    }, [tutor.id]);

    const loadReviews = async () => {
        try {
            const response = await reviewApi.getByTutor(tutor.id);
            setReviews(response.data);
        } catch (error) {
            console.error('Ошибка загрузки отзывов', error);
        }
    };

    const loadBookings = async () => {
        try {
            const response = await bookingApi.getByTutor(tutor.id);
            setBookings(response.data);
        } catch (error) {
            console.error('Ошибка загрузки бронирований');
        }
    };

    const loadStudents = async () => {
        try {
            const response = await studentApi.getAll();
            setStudents(response.data);
        } catch (error) {
            console.error('Ошибка загрузки студентов');
        }
    };

    const handleStatusChange = async (bookingId: number, newStatus: string) => {
        try {
            await bookingApi.updateStatus(bookingId, newStatus);
            message.success(`Статус изменён на ${newStatus}`);
            loadBookings();
        } catch (error) {
            message.error('Ошибка изменения статуса');
        }
    };

    const handleDeleteBooking = async (bookingId: number) => {
        try {
            await bookingApi.delete(bookingId);
            message.success('Бронирование удалено');
            loadBookings();
        } catch (error) {
            message.error('Ошибка удаления бронирования');
        }
    };

    const handleBooking = async (values: any) => {
        if (!selectedTime) {
            message.error('Выберите время занятия');
            return;
        }
        try {
            const bookingData = {
                dateTime: dayjs(selectedTime).utc(true).toISOString(),
                durationMinutes: values.durationMinutes,
                message: values.message || '',
                studentId: values.studentId,
                tutorId: tutor.id,
            };
            await bookingApi.create(bookingData);
            message.success('Бронирование создано!');
            loadBookings();
            bookingForm.resetFields();
            setSelectedTime(null);
            setActiveKey([]);
            if (onRefresh) onRefresh();
        } catch (error) {
            message.error('Ошибка создания бронирования');
        }
    };

    const handleReview = async (values: any) => {
        try {
            await reviewApi.create({
                rating: values.rating,
                comment: values.comment,
                studentId: values.studentId,
                tutorId: tutor.id,
            });
            message.success('Отзыв добавлен!');
            setShowReviewModal(false);
            reviewForm.resetFields();
            loadReviews();
        } catch (error) {
            message.error('Ошибка добавления отзыва');
        }
    };

    const generateTimeSlots = () => {
        const slots = [];
        for (let hour = 8; hour <= 22; hour++) {
            const time = `${hour.toString().padStart(2, '0')}:00`;
            const isBooked = bookings.some(booking =>
                dayjs(booking.dateTime).hour() === hour &&
                dayjs(booking.dateTime).isSame(selectedDate, 'day')
            );
            slots.push({ time, hour, isBooked });
        }
        return slots;
    };

    const avgRating = reviews.length > 0
        ? (reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length).toFixed(1)
        : '0';

    const timeSlots = generateTimeSlots();

    const calculatePrice = (duration: number) => {
        const pricePerHour = tutor.hourlyRate;
        return (pricePerHour * duration / 60).toFixed(2);
    };

    const bookingColumns = [
        { title: 'Дата и время', dataIndex: 'dateTime', key: 'dateTime', render: (v: string) => dayjs(v).format('DD.MM.YYYY HH:mm') },
        { title: 'Студент', dataIndex: 'studentName', key: 'studentName' },
        { title: 'Длительность', dataIndex: 'durationMinutes', key: 'durationMinutes', render: (v: number) => `${v} мин` },
        {
            title: 'Стоимость',
            key: 'price',
            render: (_: any, record: Booking) => `${(tutor.hourlyRate * record.durationMinutes / 60).toFixed(2)} Br`
        },
        {
            title: 'Статус',
            dataIndex: 'status',
            key: 'status',
            render: (status: string, record: Booking) => (
                <Select
                    value={status}
                    onChange={(newStatus) => handleStatusChange(record.id, newStatus)}
                    style={{ width: 120 }}
                    size="small"
                >
                    <Select.Option value="PENDING">⏳ PENDING</Select.Option>
                    <Select.Option value="CONFIRMED">✅ CONFIRMED</Select.Option>
                    <Select.Option value="CANCELLED">❌ CANCELLED</Select.Option>
                    <Select.Option value="COMPLETED">📌 COMPLETED</Select.Option>
                </Select>
            )
        },
        {
            title: 'Действия',
            key: 'actions',
            width: 80,
            render: (_: any, record: Booking) => (
                <Popconfirm
                    title="Удалить бронирование?"
                    onConfirm={() => handleDeleteBooking(record.id)}
                    okText="Да"
                    cancelText="Нет"
                >
                    <Button danger size="small">Удалить</Button>
                </Popconfirm>
            )
        },
    ];

    return (
        <Card
            style={{ marginBottom: 16 }}
            title={
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                    <Space>
                        <Avatar size={48} icon={<UserOutlined />} />
                        <div>
                            <div style={{ fontSize: 18, fontWeight: 'bold' }}>{tutor.fullname}</div>
                            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                                <Tag color="blue">{tutor.subjectName}</Tag>
                                <Tag color="green">Опыт: {tutor.experienceYears} лет</Tag>
                                <Tag color="orange">{tutor.hourlyRate} Br/час</Tag>
                            </div>
                            <div style={{ fontSize: 12, color: '#666' }}>📧 {tutor.email || 'Не указан'}</div>
                        </div>
                    </Space>
                    <Space>
                        <Rate disabled value={Number(avgRating)} style={{ fontSize: 14 }} />
                        <Text type="secondary">({reviews.length} отзывов)</Text>
                        {onEdit && <Button icon={<EditOutlined />} onClick={() => onEdit(tutor)}>Редактировать</Button>}
                        {onDelete && <Button icon={<DeleteOutlined />} danger onClick={() => onDelete(tutor.id)}>Удалить</Button>}
                    </Space>
                </div>
            }
        >
            <Collapse
                activeKey={activeKey}
                onChange={(key) => setActiveKey(key || [])}
                style={{ background: 'transparent' }}
            >
                <Panel
                    header={<span><CalendarOutlined /> Запись на занятие</span>}
                    key="booking"
                >
                    <div>
                        <h4>Выберите дату и время</h4>
                        <DatePicker
                            value={selectedDate}
                            onChange={(date) => date && setSelectedDate(date)}
                            style={{ marginBottom: 16 }}
                        />

                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: 8, marginBottom: 16 }}>
                            {timeSlots.map(slot => (
                                <Button
                                    key={slot.hour}
                                    disabled={slot.isBooked}
                                    style={slot.isBooked ? { backgroundColor: '#f5f5f5', color: '#ccc' } : {}}
                                    onClick={() => {
                                        const dateTime = selectedDate.hour(slot.hour).minute(0);
                                        setSelectedTime(dateTime);
                                        bookingForm.setFieldsValue({
                                            durationMinutes: 60,
                                        });
                                        setSelectedDuration(60);
                                        message.success(`Выбрано время: ${slot.time}`);
                                    }}
                                >
                                    {slot.time}
                                </Button>
                            ))}
                        </div>

                        {selectedTime && (
                            <div style={{ background: '#e6f7ff', padding: 12, borderRadius: 8, marginBottom: 16 }}>
                                <Text strong>✅ Выбрано время: {selectedTime.format('DD.MM.YYYY HH:mm')}</Text>
                            </div>
                        )}

                        <Form form={bookingForm} layout="vertical" onFinish={handleBooking}>
                            <Form.Item name="studentId" label="Студент" rules={[{ required: true }]}>
                                <Select placeholder="Выберите студента">
                                    {students.map(s => (
                                        <Select.Option key={s.id} value={s.id}>{s.fullName}</Select.Option>
                                    ))}
                                </Select>
                            </Form.Item>
                            <Form.Item name="durationMinutes" label="Длительность" initialValue={60}>
                                <Select onChange={(value) => setSelectedDuration(value)}>
                                    <Select.Option value={30}>30 минут</Select.Option>
                                    <Select.Option value={60}>60 минут</Select.Option>
                                    <Select.Option value={90}>90 минут</Select.Option>
                                    <Select.Option value={120}>120 минут</Select.Option>
                                </Select>
                            </Form.Item>
                            <div style={{ background: '#f0f0f0', padding: 12, borderRadius: 8, marginBottom: 16 }}>
                                <Text strong>💰 Стоимость занятия: {calculatePrice(selectedDuration)} Br</Text>
                            </div>
                            <Form.Item name="message" label="Сообщение">
                                <TextArea rows={2} placeholder="Напишите что хотите разобрать..." />
                            </Form.Item>
                            <Form.Item>
                                <Button type="primary" htmlType="submit">Забронировать</Button>
                                <Button style={{ marginLeft: 8 }} onClick={() => {
                                    setActiveKey([]);
                                    setSelectedTime(null);
                                    bookingForm.resetFields();
                                }}>Отмена</Button>
                            </Form.Item>
                        </Form>
                    </div>
                </Panel>

                <Panel
                    header={<span><StarOutlined /> Отзывы ({reviews.length})</span>}
                    key="reviews"
                >
                    <div>
                        <Button
                            type="primary"
                            onClick={() => setShowReviewModal(true)}
                            style={{ marginBottom: 16 }}
                        >
                            + Оставить отзыв
                        </Button>
                        <List
                            dataSource={reviews}
                            renderItem={(review) => (
                                <List.Item>
                                    <List.Item.Meta
                                        avatar={<Avatar icon={<UserOutlined />} />}
                                        title={
                                            <Space>
                                                <span>{review.studentName}</span>
                                                <Rate disabled defaultValue={review.rating} style={{ fontSize: 12 }} />
                                            </Space>
                                        }
                                        description={
                                            <>
                                                <div>{review.comment}</div>
                                                <Text type="secondary" style={{ fontSize: 12 }}>
                                                    {dayjs(review.createdAt).format('DD.MM.YYYY')}
                                                </Text>
                                            </>
                                        }
                                    />
                                </List.Item>
                            )}
                        />
                        {reviews.length === 0 && <Text type="secondary">Нет отзывов</Text>}
                    </div>
                </Panel>

                <Panel
                    header={<span><BookOutlined /> Бронирования ({bookings.length})</span>}
                    key="bookings"
                >
                    <div>
                        <Table
                            dataSource={bookings}
                            columns={bookingColumns}
                            rowKey="id"
                            size="small"
                            pagination={{ pageSize: 5 }}
                        />
                        {bookings.length === 0 && <Text type="secondary">Нет бронирований</Text>}
                    </div>
                </Panel>
            </Collapse>

            <Modal
                title="Оставить отзыв"
                open={showReviewModal}
                onCancel={() => setShowReviewModal(false)}
                footer={null}
            >
                <Form form={reviewForm} layout="vertical" onFinish={handleReview}>
                    <Form.Item name="studentId" label="Студент" rules={[{ required: true }]}>
                        <Select placeholder="Выберите студента">
                            {students.map(s => (
                                <Select.Option key={s.id} value={s.id}>{s.fullName}</Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item name="rating" label="Оценка" rules={[{ required: true }]}>
                        <Rate />
                    </Form.Item>
                    <Form.Item name="comment" label="Комментарий" rules={[{ required: true }]}>
                        <TextArea rows={3} placeholder="Поделитесь впечатлениями..." />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit">Отправить отзыв</Button>
                    </Form.Item>
                </Form>
            </Modal>
        </Card>
    );
};