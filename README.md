# 🚀 ListenerDetector - Система обработки сигналов от радиочастотника (Farfator)

Real-time система обнаружения дронов через анализ радиочастотных сигналов с использованием Spring WebSocket и Apache Kafka. Система слушает через websocket сигналы, далее прокидывает их в обработчик, который преобразует .proto файлы в java класс (DTO) и прокидывает их в топик кафки.

## 🌟 Особенности
- **Real-time обработка** сигналов через WebSocket
- **Асинхронная обработка** событий через Kafka
- **Эффективная сериализация** с Protocol Buffers
- **Масштабируемая архитектура** микросервисов
- **Логирование** ключевых операций

## 🛠️ Технологический стек
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?logo=springboot)
![WebSocket](https://img.shields.io/badge/WebSocket-010101?logo=websocket&logoColor=white)
![Kafka](https://img.shields.io/badge/Kafka-231F20?logo=apachekafka&logoColor=white)
![Protocol Buffers](https://img.shields.io/badge/Protocol_Buffers-3178C6?logo=protobuf&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-24.0-2496ED?logo=docker)
