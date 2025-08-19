# 📡 ListenerDetector - Система обработки сигналов от радиочастотника (Farfator)

Real-time система обнаружения сигналов радиочастотника (Farfator) с использованием Spring WebSocket и Apache Kafka. Система слушает через websocket сигналы, далее прокидывает их в обработчик, который преобразует .proto файлы в java класс (DTO) и прокидывает их в топик кафки. Подробнее о .proto файлах в документации. Также есть Rest-контроллер для передвижения устройства по карте.

## 🌟 Особенности
- **Real-time обработка** сигналов через WebSocket
- **Асинхронная обработка** событий через Kafka
- **Эффективная сериализация** с Protocol Buffers
- **Масштабируемая архитектура** микросервисов
- **Логирование** ключевых операций

### MoveController
`POST /api/shtilFarvater/dashboard_post` - Изменение координат устройства  

**Тело запроса:**
```json
{
  "ip": "192.168.1.100",
  "latitude": "59.93428",
  "longitude": "30.33510",
  "port": "8080",
  "public_id": "FARV-2023-001",
  "radius": "5000"
}
```
!!! ВАЖНО
- Добавить файл application.yml в src/main/resources/ в нём ты указываешь ip и порты
Ниже приведу пример файла
```
server:
  port: 1111

websocket:
  server:
    url: ws://${API_IP:localhost}:1111  # URL API "Обнаружителя"
eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_URL:http://192.168.1.1:2222/eureka}
spring:
  application:
    name: NODE-SHTIL-FARVATER-${NUM:1}
  kafka:
    producer:
      bootstrap-servers: ${KAFKA_URL:192.168.1.1:2222}
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    properties:
      spring.json.add.type.headers: false
  output:
    ansi:
      enabled: ALWAYS
```
## 📄 Документация
[Документаци для работы с .proto файлами](https://github.com/user-attachments/files/21795144/API.1.docx)

## 🛠️ Технологический стек
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![Protocol Buffers](https://img.shields.io/badge/Protocol_Buffers-3178C6?style=for-the-badge&logo=protobuf&logoColor=white)
![gRPC](https://img.shields.io/badge/gRPC-4285F4?style=for-the-badge&logo=google&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=websocket&logoColor=white)
![Log4j](https://img.shields.io/badge/Log4j-1F1F1F?style=for-the-badge&logo=apache&logoColor=white)
