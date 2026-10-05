# Makefile to manage all Spring Boot microservices in WSL

.PHONY: build run stop status clean logs

# Set JAVA_HOME for WSL environment
export JAVA_HOME = /usr/lib/jvm/java-21-openjdk-amd64

# Build the entire multi-module project
build:
	chmod +x mvnw
	./mvnw clean package -DskipTests

# Start all microservices in the background, exporting .env variables
run:
	@echo "Loading environment variables from .env and starting services..."
	@chmod +x mvnw
	@set -a && [ -f .env ] && . ./.env && set +a && \
	export MAVEN_OPTS="-Djava.net.preferIPv4Stack=true" && \
	./mvnw spring-boot:run -pl user-service > user-service.log 2>&1 & \
	./mvnw spring-boot:run -pl payment-service > payment-service.log 2>&1 & \
	./mvnw spring-boot:run -pl transaction-service > transaction-service.log 2>&1 & \
	./mvnw spring-boot:run -pl notification-service > notification-service.log 2>&1 & \
	./mvnw spring-boot:run -pl worker-service > worker-service.log 2>&1 & \
	./mvnw spring-boot:run -pl api-gateway > api-gateway.log 2>&1 &
	@echo "All microservices launched in the background!"
	@echo "To view logs, run: make logs"
	@echo "To stop them, run: make stop"

# Tail logs of all services
logs:
	tail -f *.log

# Check running status of the services
status:
	@ps aux | grep -i 'spring-boot' | grep -v grep || echo "No microservices currently running."

# Stop all microservices
stop:
	@echo "Stopping all microservices..."
	-pkill -9 -f 'spring-boot'
	-pkill -9 -f 'payment-gateway'
	-pkill -9 -f 'java'
	@echo "Stopped."

# Clean up log files and build artifacts
clean:
	rm -f *.log
	./mvnw clean
