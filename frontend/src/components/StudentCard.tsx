import React from 'react';
import { Card, Avatar, Tag, Space, Button } from 'antd';
import { UserOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons';
import { Student } from '../types';

interface StudentCardProps {
    student: Student;
    onEdit?: (student: Student) => void;
    onDelete?: (id: number) => void;
}

export const StudentCard: React.FC<StudentCardProps> = ({ student, onEdit, onDelete }) => {
    return (
        <Card
            style={{ marginBottom: 16 }}
            title={
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
                    <Space>
                        <Avatar size={48} icon={<UserOutlined />} />
                        <div>
                            <div style={{ fontSize: 18, fontWeight: 'bold' }}>{student.fullName}</div>
                            <div>
                                <Tag color="green">Бюджет: {student.budget} Br</Tag>
                            </div>
                            <div style={{ fontSize: 12, color: '#666' }}>📧 {student.email}  📞 {student.phone || 'Не указан'}</div>
                        </div>
                    </Space>
                    <Space>
                        {onEdit && <Button icon={<EditOutlined />} onClick={() => onEdit(student)}>Редактировать</Button>}
                        {onDelete && <Button icon={<DeleteOutlined />} danger onClick={() => onDelete(student.id)}>Удалить</Button>}
                    </Space>
                </div>
            }
        >
            <div>
                <strong>📚 Изучаемые предметы:</strong>
                <div style={{ marginTop: 8, display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                    {student.subjects && student.subjects.length > 0 ? (
                        student.subjects.map(subject => (
                            <Tag key={subject.id} color="blue">
                                {subject.name}
                            </Tag>
                        ))
                    ) : (
                        <Tag color="default">Нет предметов</Tag>
                    )}
                </div>
            </div>
        </Card>
    );
};