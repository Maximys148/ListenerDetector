# 📡 ListenerDetector - Система обработки сигналов от радиочастотника (Farfator)

Real-time система обнаружения сигналов радиочастотника (Farfator) с использованием Spring WebSocket и Apache Kafka. Система слушает через websocket сигналы, далее прокидывает их в обработчик, который преобразует .proto файлы в java класс (DTO) и прокидывает их в топик кафки.

## 🌟 Особенности
- **Real-time обработка** сигналов через WebSocket
- **Асинхронная обработка** событий через Kafka
- **Эффективная сериализация** с Protocol Buffers
- **Масштабируемая архитектура** микросервисов
- **Логирование** ключевых операций

## 📦 Основные компоненты

### `client/`
- `DetectorClient` - Подключается к радиочастотнику (Фарватору), слушает сигналы по Websocket, перенаправляет сигналы в сервис для дальнейшей обработки

### `config/`
- `KafkaConfig.java` - Настройки продюсеров Kafka
- `AppConfig.java` - Устанавливает дефолтное устройство (deviceModel) из класса resources/SEH.json

### `controllers/`
- `MoveController` - Отвечает за запрос на передвижение фарватора по карте для фронта

### `dto/`
- Различные DTO: устройство (Фарватор), сигнал, местоположение

### `exceptions/`
- `DetectorException` - Обрабатывает все ошибки приложения

### `kafka/`
- `KafkaProducer` - Отправляет сообщения о сигнале и устройстве в Kafka

### `model/`
- `Job` - Модель "задания" (подробнее в документации)

### `services/`
- `MessageService` - Обрабатывает сигналы и отправляет их в Kafka через KafkaProducer
- `MoveService` - Управляет передвижением устройства (Фарватора), изменяя его координаты

### `utils/`
- `TimeConverter` - Конвертирует время

### `ListenerDetectorApplication`
- Главный класс приложения, который запускает его

## 📄 Документация
[Описание API](https://github.com/user-attachments/files/21795144/API.1.docx)

## 🛠️ Технологический стек
<div align="left">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring_WebSocket-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring WebSocket">
  <img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white" alt="Kafka">
  <img src="https://img.shields.io/badge/Protocol_Buffers-3178C6?style=for-the-badge&logo=protobuf&logoColor=white" alt="Protobuf">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
  <img src="https://img.shields.io/badge/Log4j-1F1F1F?style=for-the-badge&logo=apache&logoColor=white" alt="Log4j">
</div>

## 🚀 Быстрый старт
1. Собрать проект:
```bash
mvn clean package
