import React, { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { Layout, Menu, Spin, message, Modal, Form, Input, InputNumber, Select, Button, Card, Row, Col, Typography, Space, Tag, Pagination } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { tutorApi } from './api/tutors';
import { subjectApi } from './api/subjects';
import { TutorCard } from './components/TutorCard';
import { StudentsPage } from './pages/StudentsPage';
import { SubjectsPage } from './pages/SubjectsPage';
import { Tutor, TutorRequest, Subject } from './types';
import 'antd/dist/reset.css';

const { Header, Sider, Content } = Layout;
const { Text } = Typography;

const TutorsPage: React.FC = () => {
    const [allTutors, setAllTutors] = useState<Tutor[]>([]);
    const [filteredTutors, setFilteredTutors] = useState<Tutor[]>([]);
    const [subjects, setSubjects] = useState<Subject[]>([]);
    const [loading, setLoading] = useState(true);
    const [editModalVisible, setEditModalVisible] = useState(false);
    const [editingTutor, setEditingTutor] = useState<Tutor | null>(null);
    const [form] = Form.useForm();
    const [currentPage, setCurrentPage] = useState(1);
    const [totalElements, setTotalElements] = useState(0);

    const [searchName, setSearchName] = useState('');
    const [selectedSubject, setSelectedSubject] = useState<number | null>(null);

    useEffect(() => {
        loadData();
    }, [currentPage]);

    useEffect(() => {
        let filtered = [...allTutors];

        if (searchName) {
            filtered = filtered.filter(tutor =>
                tutor.fullname.toLowerCase().includes(searchName.toLowerCase())
            );
        }

        if (selectedSubject) {
            filtered = filtered.filter(tutor => tutor.subjectId === selectedSubject);
        }

        setFilteredTutors(filtered);
    }, [allTutors, searchName, selectedSubject]);

    const loadData = async (page?: number) => {
        setLoading(true);
        try {
            const pageNum = page !== undefined ? page : currentPage;

            const [tutorsRes, subjectsRes] = await Promise.all([
                tutorApi.getAllWithPagination(pageNum - 1, 10),
                subjectApi.getAll(),
            ]);

            setAllTutors(tutorsRes.data.content);
            setFilteredTutors(tutorsRes.data.content);
            setTotalElements(tutorsRes.data.totalElements);
            setSubjects(subjectsRes.data);
        } catch (error) {
            message.error('Ошибка загрузки данных');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id: number) => {
        Modal.confirm({
            title: 'Удаление',
            content: 'Вы уверены?',
            onOk: async () => {
                try {
                    await tutorApi.delete(id);
                    message.success('Удалено');
                    loadData();
                } catch (error) {
                    message.error('Ошибка удаления');
                }
            },
        });
    };

    const handleEdit = (tutor: Tutor) => {
        setEditingTutor(tutor);
        const nameParts = tutor.fullname.split(' ');
        form.setFieldsValue({
            firstName: nameParts[0] || '',
            lastName: nameParts[1] || '',
            email: tutor.email,
            hourlyRate: tutor.hourlyRate,
            experienceYears: tutor.experienceYears,
            subjectId: tutor.subjectId,
        });
        setEditModalVisible(true);
    };

    const handleSubmit = async (values: any) => {
        try {
            const data: TutorRequest = {
                firstName: values.firstName,
                lastName: values.lastName,
                email: values.email,
                hourlyRate: values.hourlyRate,
                startYear: new Date().getFullYear() - values.experienceYears,
                subjectId: values.subjectId || null,
            };
            if (editingTutor) {
                await tutorApi.update(editingTutor.id, data);
                message.success('Обновлено');
            } else {
                await tutorApi.create(data);
                message.success('Создано');
            }
            setEditModalVisible(false);
            form.resetFields();
            loadData();
        } catch (error) {
            message.error('Ошибка сохранения');
        }
    };

    const handleResetFilters = () => {
        setSearchName('');
        setSelectedSubject(null);
    };

    if (loading) return <Spin size="large" style={{ display: 'block', margin: '50px auto' }} />;

    return (
        <div style={{ padding: 24 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24, flexWrap: 'wrap', gap: 16 }}>
                <h1 style={{ margin: 0 }}>Преподаватели</h1>
                <Button
                    type="primary"
                    onClick={() => {
                        setEditingTutor(null);
                        form.resetFields();
                        setEditModalVisible(true);
                    }}
                >
                    + Добавить преподавателя
                </Button>
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
                            placeholder="Фильтр по предмету"
                            value={selectedSubject}
                            onChange={setSelectedSubject}
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
                            Найдено: {filteredTutors.length} из {totalElements}
                        </Text>
                    </Col>
                </Row>
            </Card>

            {filteredTutors.map(tutor => (
                <TutorCard
                    key={tutor.id}
                    tutor={tutor}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                    onRefresh={() => loadData()}
                />
            ))}

            {filteredTutors.length === 0 && (
                <div style={{ textAlign: 'center', padding: 50 }}>
                    <Text type="secondary">Преподаватели не найдены</Text>
                </div>
            )}

            <Pagination
                current={currentPage}
                pageSize={10}
                total={totalElements}
                onChange={(page) => setCurrentPage(page)}
                showTotal={(total) => `Всего ${total} преподавателей`}
                style={{ marginTop: 24, textAlign: 'center' }}
            />

            <Modal
                title={editingTutor ? 'Редактировать' : 'Добавить преподавателя'}
                open={editModalVisible}
                onCancel={() => setEditModalVisible(false)}
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
                    <Form.Item name="hourlyRate" label="Ставка (Br/час)" rules={[{ required: true }]}>
                        <InputNumber min={0} style={{ width: '100%' }} />
                    </Form.Item>
                    <Form.Item name="experienceYears" label="Опыт (лет)" rules={[{ required: true }]}>
                        <InputNumber min={0} max={50} style={{ width: '100%' }} />
                    </Form.Item>
                    <Form.Item name="subjectId" label="Предмет">
                        <Select allowClear placeholder="Выберите предмет">
                            {subjects.map(subject => (
                                <Select.Option key={subject.id} value={subject.id}>
                                    {subject.name}
                                </Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item>
                        <Button type="primary" htmlType="submit">Сохранить</Button>
                        <Button style={{ marginLeft: 8 }} onClick={() => setEditModalVisible(false)}>Отмена</Button>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

function App() {
    return (
        <BrowserRouter>
            <Layout style={{ minHeight: '100vh' }}>
                <Sider width={220} theme="dark">
                    <div style={{ color: 'white', textAlign: 'center', padding: 16, fontSize: 18, fontWeight: 'bold' }}>
                        Tutor Platform
                    </div>
                    <Menu theme="dark" mode="inline" defaultSelectedKeys={['1']}>
                        <Menu.Item key="1">
                            <Link to="/">Преподаватели</Link>
                        </Menu.Item>
                        <Menu.Item key="2">
                            <Link to="/students">Студенты</Link>
                        </Menu.Item>
                        <Menu.Item key="3">
                            <Link to="/subjects">📚 Предметы</Link>
                        </Menu.Item>
                    </Menu>
                </Sider>
                <Layout>
                    <Header style={{ background: '#fff', padding: '0 24px', borderBottom: '1px solid #f0f0f0' }}>
                        <h2 style={{ margin: '16px 0' }}>Tutor Platform Administration</h2>
                    </Header>
                    <Content style={{ margin: 24, padding: 24, background: '#fff', minHeight: 280 }}>
                        <Routes>
                            <Route path="/" element={<TutorsPage />} />
                            <Route path="/students" element={<StudentsPage />} />
                            <Route path="/subjects" element={<SubjectsPage />} />
                        </Routes>
                    </Content>
                </Layout>
            </Layout>
        </BrowserRouter>
    );
}

export default App;