# 🚀 ListenerDetector - Система обработки сигналов от радиочастотника (Farfator)

Real-time система обнаружения дронов через анализ радиочастотных сигналов с использованием Spring WebSocket и Apache Kafka. Система слушает через websocket сигналы, далее прокидывает их в обработчик, который преобразует .proto файлы в java класс (DTO) и прокидывает их в топик кафки.

## 🌟 Особенности
- **Real-time обработка** сигналов через WebSocket
- **Асинхронная обработка** событий через Kafka
- **Эффективная сериализация** с Protocol Buffers
- **Масштабируемая архитектура** микросервисов
- **Логирование** ключевых операций

- ## Структура проекта  
Описание ключевых классов и их функций:  

| Класс               | Назначение                                                                 |
|----------------------|---------------------------------------------------------------------------|
| `Main`              | Точка входа, инициализация компонентов.                                  |
| `AudioListener`     | Захват аудиопотока с микрофона.                                          |
| `Detector`          | Анализ звука на наличие событий (например, громких шумов).               |
| `Config`            | Загрузка параметров из `config.properties`.                              |
| `EventLogger`       | Логирование событий в файл.                                              |
| `FFT`               | Алгоритм БПФ для частотного анализа.                                     |
| `AudioUtils`        | Вспомогательные методы (нормализация, конвертация в dB).                |

Подробнее см. в [документации к коду](docs/CODE_DOCS.md).  

## 🛠️ Технологический стек
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring WebSocket](https://img.shields.io/badge/Spring_WebSocket-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![REST](https://img.shields.io/badge/REST-FF6C37?style=for-the-badge&logo=rest&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![Protocol Buffers](https://img.shields.io/badge/Protocol_Buffers-3178C6?style=for-the-badge&logo=protobuf&logoColor=white)
![gRPC](https://img.shields.io/badge/gRPC-4285F4?style=for-the-badge&logo=google&logoColor=white)
![Log4j](https://img.shields.io/badge/Log4j-1F1F1F?style=for-the-badge&logo=apache&logoColor=white)
