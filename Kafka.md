```markdown
# Docker Compose for Kafka (KRaft Mode)

This Docker Compose file sets up a Kafka broker in KRaft mode, which does not require Zookeeper.

```yaml
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    container_name: kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"

    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:9093
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,PLAINTEXT_HOST://0.0.0.0:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

    volumes:
      - kafka-data:/var/lib/kafka/data

volumes:
  kafka-data:
    driver: local



```

## Usage

1. Save the above content to a file named `docker-compose.yml`.
2. Run `docker-compose up -d` to start the Kafka broker in KRaft mode.
3. Use `docker-compose down` to stop and remove the containers.

## Creating Kafka Topic
<pre>
docker exec kafka kafka-topics --create --topic test-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
</pre>

## Produce data into the kafka-topic 
<pre>
docker exec -it kafka kafka-console-producer --topic test-topic --bootstrap-server localhost:9092
</pre>

## Consumer
<pre>
docker exec -it kafka kafka-console-consumer --topic test-topic --bootstrap-server localhost:9092 --from-beginning
</pre>

**To describe a topic**
```
--bootstarp-server localhost:9092 --describe topic-1
```

**delete a topic**
```docker
docker exec -it kafka_container /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic kafka-topic-1 
```
## Multiple kafka brokers
**Update the `docker-comppose` file**
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
    environment:
      KAFKA_BROKER_ID: ${BROKER_ID:-1}  # Default to 1 if not provided
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-${BROKER_ID:-1}:9092
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
    volumes:
      - kafka-data:/var/lib/kafka/data
    networks:
      - kafka-network

volumes:
  kafka-data:

networks:
  kafka-network:
    driver: bridge
```
**Now run the below command while starting**
```
docker-compose up scale kafka=3 -d ( 3 here is the number of brokers you want)
```