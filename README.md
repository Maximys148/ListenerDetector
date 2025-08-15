# 🚀 ListenerDetector - Real-time Drone Detection System

![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1.5-6DB33F?logo=springboot)
![WebSocket](https://img.shields.io/badge/WebSocket-010101?logo=websocket&logoColor=white)
![Kafka](https://img.shields.io/badge/Kafka-231F20?logo=apachekafka&logoColor=white)
![Protocol Buffers](https://img.shields.io/badge/Protocol_Buffers-3178C6?logo=protobuf&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-24.0-2496ED?logo=docker)

Real-time система обнаружения дронов через анализ радиочастотных сигналов с использованием Spring WebSocket и Apache Kafka.

## 🌟 Key Features
- **Real-time обработка** сигналов через WebSocket
- **Асинхронная обработка** событий через Kafka
- **Эффективная сериализация** с Protocol Buffers
- **Масштабируемая архитектура** микросервисов
- **Логирование** ключевых операций

## 🛠️ Технологический стек
| Компонент            | Назначение                          |
|----------------------|-------------------------------------|
| Spring WebSocket     | Real-time коммуникация              |
| Apache Kafka         | Очереди сообщений                   |
| Protocol Buffers     | Бинарная сериализация               |
| PostgreSQL           | Хранение данных                     |
| Docker               | Контейнеризация                     |

## 🚀 Быстрый старт

### Требования
- JDK 17+
- Docker 24.0+
- Kafka 3.4+

```bash
# 1. Клонировать репозиторий
git clone https://github.com/Maximys148/ListenerDetector.git
cd ListenerDetector

# 2. Запустить сервисы
docker-compose up -d

# 3. Собрать и запустить приложение
mvn spring-boot:run
