# ПЛАТФОРМА ПОИСКА И ВЫБОРА РЕПЕТИТОРОВ

## REST API проект на Java, фреймворк Spring, Maven.

Проект предоставляет набор готовых программных функций (API-запросов), с помощью которых можно управлять каталогом репетиторов. Например, фронтенд-разработчик или мобильное приложение может использовать этот API, чтобы:
1. Найти репетитора
2. Посмотреть карточку преподавателя
3. Добавить нового репетитора

---

1. Подключить реляционную БД к проекту.
2. В модели данных реализовать минимум 5 сущностей:
   - минимум одну связь OneToMany
   - минимум одну связь ManyToMany
3. Реализовать CRUD операции.
4. Настроить и обосновать использование CascadeType и FetchType.
5. Продемонстрировать проблему N+1 и решить её через @EntityGraph или fetch join.
6. Реализовать метод, сохраняющий несколько связанных сущностей. Продемонстрировать частичное сохранение данных без @Transactional и полное откатывание операции с @Transactional при возникновении ошибки.
7. Нарисовать ER-диаграмму с указанием PK/FK и связей.

---

**Сонар**: https://sonarcloud.io/projects

## ER-диаграмма
## ER-диаграмма

```mermaid
erDiagram
    TUTOR ||--o{ SUBJECT : teaches
    TUTOR ||--o{ BOOKING : has
    TUTOR ||--o{ REVIEW : receives
    STUDENT ||--o{ BOOKING : makes
    STUDENT ||--o{ REVIEW : writes
    STUDENT }o--o{ SUBJECT : studies
    
    TUTOR {
        Long id PK
        string first_name
        string last_name
        decimal hourly_rate
        int start_year
        string email UK
        Long subject_id FK
    }
    
    STUDENT {
        Long id PK
        string first_name
        string last_name
        string phone
        string email UK
        decimal budget
    }
    
    SUBJECT {
        Long id PK
        string name UK
        string category
        string description
    }
    
    BOOKING {
        Long id PK
        datetime date_time
        int duration_minutes
        string status
        string message
        Long student_id FK
        Long tutor_id FK
    }
    
    REVIEW {
        Long id PK
        int rating
        string comment
        datetime created_at
        Long student_id FK
        Long tutor_id FK
    }
```




