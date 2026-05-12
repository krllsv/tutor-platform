import React, { useEffect, useState } from 'react';
import { Spin, message, Modal, Form, Input, InputNumber, Select, Button, Card, Row, Col, Typography } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { studentApi } from '../api/students';
import { subjectApi } from '../api/subjects';
import { StudentCard } from '../components/StudentCard';
import { Student, Subject } from '../types';

const { Text } = Typography;

export const StudentsPage: React.FC = () => {
    const [allStudents, setAllStudents] = useState<Student[]>([]);
    const [filteredStudents, setFilteredStudents] = useState<Student[]>([]);
    const [subjects, setSubjects] = useState<Subject[]>([]);
    const [loading, setLoading] = useState(true);
    const [modalVisible, setModalVisible] = useState(false);
    const [editingStudent, setEditingStudent] = useState<Student | null>(null);
    const [form] = Form.useForm();

    const [searchName, setSearchName] = useState('');
    const [selectedSubjects, setSelectedSubjects] = useState<number[]>([]);

    useEffect(() => {
        loadData();
    }, []);

    useEffect(() => {
        applyFilters();
    }, [allStudents, searchName, selectedSubjects]);

    const loadData = async () => {
        setLoading(true);
        try {
            const [studentsRes, subjectsRes] = await Promise.all([
                studentApi.getAll(),
                subjectApi.getAll(),
            ]);

            setAllStudents(studentsRes.data || []);
            setSubjects(subjectsRes.data || []);
        } catch (error) {
            message.error('Ошибка загрузки данных');
            setAllStudents([]);
            setSubjects([]);
        } finally {
            setLoading(false);
        }
    };

    const applyFilters = () => {
        if (!Array.isArray(allStudents)) {
            setFilteredStudents([]);
            return;
        }

        let filtered = [...allStudents];

        if (searchName) {
            filtered = filtered.filter(student =>
                student.fullName?.toLowerCase().includes(searchName.toLowerCase())
            );
        }

        if (selectedSubjects.length > 0) {
            filtered = filtered.filter(student =>
                student.subjects?.some(s => selectedSubjects.includes(s.id))
            );
        }

        setFilteredStudents(filtered);
    };

    const handleDelete = (id: number) => {
        Modal.confirm({
            title: 'Удаление',
            content: 'Вы уверены?',
            onOk: async () => {
                try {
                    await studentApi.delete(id);
                    message.success('Удалено');
                    loadData();
                } catch (error) {
                    message.error('Ошибка удаления');
                }
            },
        });
    };

    const handleEdit = (student: Student) => {
        setEditingStudent(student);
        const nameParts = (student.fullName || '').split(' ');
        form.setFieldsValue({
            firstName: nameParts[0] || '',
            lastName: nameParts[1] || '',
            email: student.email,
            phone: student.phone,
            budget: student.budget,
            subjectIds: student.subjects?.map(s => s.id) || [],
        });
        setModalVisible(true);
    };

    const handleSubmit = async (values: any) => {
        try {
            const data = {
                firstName: values.firstName,
                lastName: values.lastName,
                phone: values.phone || '',
                email: values.email,
                budget: values.budget,
                subjectIds: values.subjectIds || [],
            };
            if (editingStudent) {
                await studentApi.update(editingStudent.id, data);
                message.success('Обновлено');
            } else {
                await studentApi.create(data);
                message.success('Создано');
            }
            setModalVisible(false);
            form.resetFields();
            loadData();
        } catch (error) {
            message.error('Ошибка сохранения');
        }
    };

    const handleResetFilters = () => {
        setSearchName('');
        setSelectedSubjects([]);
    };

    if (loading) return <Spin size="large" style={{ display: 'block', margin: '50px auto' }} />;

    return (
        <div style={{ padding: 24 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 24 }}>
                <h1>Студенты</h1>
                <Button type="primary" onClick={() => {
                    setEditingStudent(null);
                    form.resetFields();
                    setModalVisible(true);
                }}>+ Добавить студента</Button>
            </div>

            <Card style={{ marginBottom: 24 }}>
                <Row gutter={16} align="middle">
                    <Col flex="auto">
                        <Input
                            placeholder="Поиск по имени"
                            value={searchName}
                            onChange={(e) => setSearchName(e.target.value)}
                            allowClear
                            prefix={<SearchOutlined />}
                        />
                    </Col>
                    <Col flex="auto">
                        <Select
                            mode="multiple"
                            placeholder="Фильтр по предметам"
                            value={selectedSubjects}
                            onChange={setSelectedSubjects}
                            allowClear
                            style={{ width: '100%' }}
                        >
                            {subjects.map(subject => (
                                <Select.Option key={subject.id} value={subject.id}>
                                    {subject.name}
                                </Select.Option>
                            ))}
                        </Select>
                    </Col>
                    <Col>
                        <Button onClick={handleResetFilters}>Сбросить</Button>
                    </Col>
                </Row>
                <Row style={{ marginTop: 16 }}>
                    <Col span={24}>
                        <Text type="secondary">
                            Найдено студентов: {filteredStudents.length} из {allStudents.length}
                        </Text>
                    </Col>
                </Row>
            </Card>

            {filteredStudents.map(student => (
                <StudentCard
                    key={student.id}
                    student={student}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                />
            ))}

            {filteredStudents.length === 0 && !loading && (
                <div style={{ textAlign: 'center', padding: 50 }}>
                    <Text type="secondary">Студенты не найдены</Text>
                </div>
            )}

            <Modal
                title={editingStudent ? 'Редактировать студента' : 'Добавить студента'}
                open={modalVisible}
                onCancel={() => setModalVisible(false)}
                footer={null}
                width={600}
            >
                <Form form={form} layout="vertical" onFinish={handleSubmit}>
                    <Form.Item name="firstName" label="Имя" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="lastName" label="Фамилия" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="phone" label="Телефон">
                        <Input placeholder="+375 (29) 123-45-67" />
                    </Form.Item>
                    <Form.Item name="budget" label="Бюджет (Br)" rules={[{ required: true }]}>
                        <InputNumber min={0} style={{ width: '100%' }} />
                    </Form.Item>
                    <Form.Item name="subjectIds" label="Изучаемые предметы (ManyToMany)">
                        <Select mode="multiple" placeholder="Выберите предметы">
                            {subjects.map(subject => (
                                <Select.Option key={subject.id} value={subject.id}>
                                    {subject.name}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit">Сохранить</Button>
                        <Button style={{ marginLeft: 8 }} onClick={() => setModalVisible(false)}>Отмена</Button>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};