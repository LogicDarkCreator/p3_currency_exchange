# Currency Exchange Service

Сервис для управления и конвертации валют с автоматическим обновлением курсов из API Центрального Банка России.

## Navigation


## 📋 Функциональность

- ✅ Получение информации о валюте по ID
- ✅ Конвертация суммы из выбранной валюты в рубли
- ✅ Получение списка всех доступных валют
- ✅ Автоматическое обновление курсов валют из ЦБ РФ (каждый час)
- ✅ Ручное обновление курсов через API
- ✅ Создание новых валют

## 🚀 Технологии

- **Java 17**
- **Spring Boot 2.7.0**
- **Spring Data JPA**
- **PostgreSQL**
- **Liquibase** (миграции БД)
- **MapStruct** (маппинг DTO/Entity)
- **JAXB** (парсинг XML)
- **Lombok**

## 📦 Установка и запуск

### Требования

- Docker и Docker Compose (для запуска PostgreSQL)
- Java 17
- Maven

### 1. Клонирование репозитория

```bash
git clone <your-repository-url>
cd currency-exchange
```

### 2. Запуск PostgreSQL через Docker
```shell
docker run --name postgres-currency \
  -e POSTGRES_DB=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15
```

### 3. Конфигурация
Файл `application.yml` уже настроен для локальной разработки:
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres

cbr:
  url: https://www.cbr.ru/scripts/XML_valFull.asp
```

### 4. Сборка проекта
```shell
mvn clean package
```

### 5. Запуск приложения
```shell
mvn spring-boot:run
```
или
```shell
java -jar target/currency-exchange-1.0.0.jar
```

## 🔌 API Endpoints
### Получить валюту по ID
```http request
GET /api/currency/{id}
```
### Response
```json
{
  "id": 1333,
  "name": "Доллар США",
  "nominal": 1,
  "value": 93.5224,
  "isoNumCode": 840,
  "isoCharCode": "USD"
}
```
### Получить все валюты
```http request
GET /api/currency/
```
### Response
```json
{
  "currencies": [
    {
      "name": "Доллар США",
      "value": 93.5224
    },
    {
      "name": "Евро",
      "value": 99.5534
    }
  ]
}
```
### Конвертация валюты
```http request
GET /api/currency/convert?value={value}&numCode={numCode}
```

| Parameter |	Type	| Description                                     |
|-----------|-----------|-------------------------------------------------|
| `value`	    | Long      | Сумма для конвертации                           |
| `numCode`	| Long	    | Числовой код ISO валюты (например, 840 для USD) |
**Response:** `Double` (сумма в рублях)

### Создать новую валюту
```http request
POST /api/currency/create
Content-Type: application/json

{
  "name": "Новая валюта",
  "nominal": 1,
  "value": 100.0,
  "isoNumCode": 999,
  "isoCharCode": "NEW"
}
```
### Ручное обновление курсов валют
```http request
POST /api/currency/update-from-cbr
```
**Response:** `"Currencies updated successfully from CBR"`

### 🤖 Автоматическое обновление
Приложение автоматически обновляет курсы валют из API ЦБ РФ **каждый час** (в 00 минут каждого часа).

Логирование обновления:
```text
2024-01-15 10:00:00 INFO  - Starting scheduled currency update from CBR
2024-01-15 10:00:01 INFO  - Successfully parsed 50 currencies from CBR
2024-01-15 10:00:02 INFO  - Currency update completed: 30 updated, 20 created
```
### 🗄️ Структура базы данных
Таблица `currency`:

| Колонка	    | Тип	        | Описание            |
|---------------|---------------|---------------------|
| id	        |  BIGINT	    | Первичный ключ      |
| name	        | VARCHAR(255)	| Наименование валюты |
| nominal	    | BIGINT	    | Номинал             |
| value	        | NUMERIC(19,5)	| Стоимость в рублях  |
| iso_num_code	| BIGINT	    | Числовой код ISO    |
| iso_char_code	| VARCHAR(3)	| Буквенный код ISO   |

### 📝 Миграции Liquibase
Миграции применяются автоматически при запуске приложения:
1. `create-sequence.xml` - создание последовательности для **ID**
2. `create-currency-table.xml` - создание таблицы **currency**
3. `insert-currency.xml` - начальное заполнение данными
4. `add-iso-char-code-column.xml` - добавление поля **iso_char_code**

### 🧪 Тестирование
#### Примеры запросов с curl
```shell
# Получить все валюты
curl -X GET http://localhost:8080/api/currency/

# Получить валюту по ID
curl -X GET http://localhost:8080/api/currency/1333

# Конвертировать 100 долларов в рубли
curl -X GET "http://localhost:8080/api/currency/convert?value=100&numCode=840"

# Создать новую валюту
curl -X POST http://localhost:8080/api/currency/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Китайский юань","nominal":1,"value":12.766,"isoNumCode":156,"isoCharCode":"CNY"}'

# Обновить курсы валют вручную
curl -X POST http://localhost:8080/api/currency/update-from-cbr
```
### 📁 Структура проекта
```text
src/
├── main/
│   ├── java/ru/skillbox/currency/exchange/
│   │   ├── config/                 # Конфигурации
│   │   │   ├── CbrConfig.java
│   │   │   └── RestTemplateConfig.java
│   │   ├── controller/             # REST контроллеры
│   │   │   └── CurrencyController.java
│   │   ├── dto/                    # Data Transfer Objects
│   │   │   ├── CurrencyDto.java
│   │   │   ├── CurrencyShortDto.java
│   │   │   └── CurrenciesResponseDto.java
│   │   ├── entity/                 # JPA сущности
│   │   │   └── Currency.java
│   │   ├── mapper/                 # MapStruct мапперы
│   │   │   └── CurrencyMapper.java
│   │   ├── repository/             # Spring Data репозитории
│   │   │   └── CurrencyRepository.java
│   │   ├── scheduler/              # Планировщики задач
│   │   │   └── CurrencyUpdateScheduler.java
│   │   ├── service/                # Бизнес-логика
│   │   │   ├── CbrParserService.java
│   │   │   └── CurrencyService.java
│   │   ├── xml/                    # JAXB классы для XML
│   │   │   ├── CurrencyXml.java
│   │   │   ├── ObjectFactory.java
│   │   │   └── ValCurs.java
│   │   └── Application.java
│   └── resources/
│       ├── application.yml
│       └── db/
│           └── changelog/          # Liquibase миграции
└── test/                           # Тесты
```
### 🔧 Настройка периода обновления
Для изменения интервала обновления измените cron-выражение в `CurrencyUpdateScheduler.java`:
```java
@Scheduled(cron = "0 0 * * * *") // Каждый час
@Scheduled(cron = "0 */30 * * * *") // Каждые 30 минут
@Scheduled(cron = "0 0 9,12,15 * * *") // В 9:00, 12:00, 15:00
```
### ⚠️ Возможные проблемы и решения
**База данных не доступна**
- Проверьте, запущен ли Docker контейнер: `docker ps`
- Проверьте порт: `netstat -an | grep 5432`

**Ошибка парсинга XML от ЦБ РФ**
- Проверьте доступность URL: `curl https://www.cbr.ru/scripts/XML_valFull.asp`
- Возможно изменение формата ответа ЦБ РФ - обновите **JAXB** классы

**Конфликт версий Lombok и MapStruct**
- Убедитесь, что используется Java 17
- Очистите кэш Maven: `mvn clean`

### 📄 Лицензия
Этот проект разработан в рамках учебного задания Skillbox.