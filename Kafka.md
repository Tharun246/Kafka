```markdown
# Docker Compose for Kafka (KRaft Mode)

This Docker Compose file sets up Kafka and zookeeper. As written each kafka cluster requires a separate volume

```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"
    networks:
      - kafka-network

  kafka1:
    image: confluentinc/cp-kafka:latest
    container_name: kafka-1
    ports:
      - "9190:9092"
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-1:9092
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 2
    volumes:
      - kafka-data-1:/var/lib/kafka/data
    networks:
      - kafka-network

  kafka2:
    image: confluentinc/cp-kafka:latest
    container_name: kafka-2
    ports:
      - "9191:9092"
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-2:9092
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 2
    volumes:
      - kafka-data-2:/var/lib/kafka/data
    networks:
      - kafka-network

volumes:
  kafka-data-1:
  kafka-data-2:

networks:
  kafka-network:
    driver: bridge


```

## Usage

1. Save the above content to a file named `docker-compose.yml`.
2. Run `docker-compose up -d` to start the Kafka broker in KRaft mode.
3. Use `docker-compose down` to stop and remove the containers.

## Creating Kafka Topic
<pre>
docker exec -it kafka-1 kafka-topics --bootstrap-server kafka-1:9092 --create --topic test-topic --partitions 3 --replication-factor 2
</pre>
```
--bootstrap-server kafka-1:9092 make sure to use correct container name before port 
```

## Produce data into the kafka-topic 
<pre>
docker exec -it kafka-1 kafka-console-producer --bootstrap-server kafka-1:9092 --topic test-topic
</pre>

## Consumer
<pre>
docker exec -it kafka-1 kafka-console-consumer --bootstrap-server kafka-1:9092 --topic test-topic
</pre>

**To describe a topic**
```
--bootstarp-server localhost:9092 --describe topic-1
```

**List all Topics**
<pre>
docker exec -it kafka-1 kafka-topics --bootstrap-server kafka-1:9092 --list
</pre>

**delete a topic**
```docker
docker exec -it kafka_container /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic kafka-topic-1 
```
## Multiple brokers
```docker
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"
    networks:
      - kafka-network

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092-9101:9092"
    environment:
      KAFKA_BROKER_ID: ${BROKER_ID}
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-${BROKER_ID}:9092
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
    volumes:
      - kafka-data-${BROKER_ID}:/var/lib/kafka/data
    networks:
      - kafka-network

volumes:
  kafka-data-1:
  kafka-data-2:
  kafka-data-3:
  kafka-data-4:
  kafka-data-5:
  kafka-data-6:
  kafka-data-7:
  kafka-data-8:
  kafka-data-9:
  kafka-data-10:

networks:
  kafka-network:
    driver: bridge
```