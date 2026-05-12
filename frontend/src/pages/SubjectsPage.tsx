import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, Space, message, Popconfirm, Card, Row, Col, Typography } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';
import { subjectApi } from '../api/subjects';
import { Subject, SubjectRequest } from '../types';

const { Text } = Typography;

export const SubjectsPage: React.FC = () => {
    const [subjects, setSubjects] = useState<Subject[]>([]);
    const [filteredSubjects, setFilteredSubjects] = useState<Subject[]>([]);
    const [loading, setLoading] = useState(false);
    const [modalVisible, setModalVisible] = useState(false);
    const [editingSubject, setEditingSubject] = useState<Subject | null>(null);
    const [searchText, setSearchText] = useState('');
    const [form] = Form.useForm();

    useEffect(() => {
        loadSubjects();
    }, []);

    useEffect(() => {
        if (searchText) {
            setFilteredSubjects(
                subjects.filter(s =>
                    s.name.toLowerCase().includes(searchText.toLowerCase()) ||
                    s.category?.toLowerCase().includes(searchText.toLowerCase())
                )
            );
        } else {
            setFilteredSubjects(subjects);
        }
    }, [subjects, searchText]);

    const loadSubjects = async () => {
        setLoading(true);
        try {
            const response = await subjectApi.getAll();
            setSubjects(response.data);
            setFilteredSubjects(response.data);
        } catch (error) {
            message.error('Ошибка загрузки предметов');
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = () => {
        setEditingSubject(null);
        form.resetFields();
        setModalVisible(true);
    };

    const handleEdit = (subject: Subject) => {
        setEditingSubject(subject);
        form.setFieldsValue({
            name: subject.name,
            category: subject.category,
            description: subject.description,
        });
        setModalVisible(true);
    };

    const handleDelete = async (id: number) => {
        try {
            await subjectApi.delete(id);
            message.success('Предмет удалён');
            loadSubjects();
        } catch (error) {
            message.error('Ошибка удаления');
        }
    };

    const handleSubmit = async (values: SubjectRequest) => {
        try {
            if (editingSubject) {
                await subjectApi.update(editingSubject.id, values);
                message.success('Предмет обновлён');
            } else {
                await subjectApi.create(values);
                message.success('Предмет создан');
            }
            setModalVisible(false);
            form.resetFields();
            loadSubjects();
        } catch (error) {
            message.error('Ошибка сохранения');
        }
    };

    const columns = [
        { title: 'Название', dataIndex: 'name', key: 'name' },
        { title: 'Категория', dataIndex: 'category', key: 'category', render: (v: string) => v || '-' },
        { title: 'Описание', dataIndex: 'description', key: 'description', ellipsis: true },
        {
            title: 'Действия',
            key: 'actions',
            width: 120,
            render: (_: any, record: Subject) => (
                <Space>
                    <Button icon={<EditOutlined />} size="small" onClick={() => handleEdit(record)}></Button>
                    <Popconfirm title="Удалить предмет?" onConfirm={() => handleDelete(record.id)}>
                        <Button icon={<DeleteOutlined />} size="small" danger></Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div style={{ padding: 24 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 24 }}>
                <h1>Предметы</h1>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
                    Добавить предмет
                </Button>
            </div>

            <Card style={{ marginBottom: 24 }}>
                <Row gutter={16} align="middle">
                    <Col flex="auto">
                        <Input
                            placeholder="Поиск по названию или категории"
                            value={searchText}
                            onChange={(e) => setSearchText(e.target.value)}
                            allowClear
                            prefix={<SearchOutlined />}
                        />
                    </Col>
                    <Col>
                        <Text type="secondary">
                            Найдено: {filteredSubjects.length} из {subjects.length}
                        </Text>
                    </Col>
                </Row>
            </Card>

            <Table
                columns={columns}
                dataSource={filteredSubjects}
                rowKey="id"
                loading={loading}
                pagination={{ pageSize: 10 }}
            />

            <Modal
                title={editingSubject ? 'Редактировать предмет' : 'Добавить предмет'}
                open={modalVisible}
                onCancel={() => setModalVisible(false)}
                footer={null}
                width={500}
            >
                <Form form={form} layout="vertical" onFinish={handleSubmit}>
                    <Form.Item name="name" label="Название" rules={[{ required: true, message: 'Введите название' }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="category" label="Категория">
                        <Input placeholder="Например: Точные науки, Гуманитарные..." />
                    </Form.Item>
                    <Form.Item name="description" label="Описание">
                        <Input.TextArea rows={3} placeholder="Описание предмета..." />
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit" style={{ marginRight: 8 }}>
                            {editingSubject ? 'Сохранить' : 'Создать'}
                        </Button>
                        <Button onClick={() => setModalVisible(false)}>Отмена</Button>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};