# Environment Configuration
DEV ?= true
ENV := $(if $(filter true,$(DEV)),.env.dev,.env.prod)
EMAIL_ENV := mailserver.env
COMMON_COMPOSE := -f docker-compose.yaml
COMPOSE_CMD := docker-compose --env-file $(ENV) $(COMMON_COMPOSE)
EMAIL_COMPOSE_CMD := docker-compose --env-file $(EMAIL_ENV) $(COMMON_COMPOSE)
SERVICES := db app
NETWORK_FILE := docker-compose.yaml  # Ensure network file is included

# Help Menu
help:
	@echo "\nUsage: make [command] [SERVICE=<service>] [DEV=true|false]\n"
	@echo "Available commands:"
	@echo "  up                Start all services (ENV=${ENV})"
	@echo "  down              Stop all services"
	@echo "  up SERVICE=name   Start a specific service (email, db, app, etc.)"
	@echo "  down SERVICE=name Stop a specific service (email, db, app, etc.)"
	@echo "  logs              View logs of all running services"
	@echo "  ps                Show running containers"

# Merge Compose Files into a Single File
merge:
	@echo "Merging all docker-compose files..."
	@docker compose $(foreach svc,$(SERVICES),-f docker-compose.$(svc).yaml) -f $(NETWORK_FILE) config > docker-compose.merged.yaml
	@echo "Merged compose files into docker-compose.merged.yaml successfully!"

# Convert docker-compose to Helm (Windows Compatible)
helm-convert:
	@echo "Converting docker-compose.merged.yaml to Helm charts..."
	@if not exist helm mkdir helm
	@kompose --file docker-compose.merged.yaml convert --chart --out helm
	@echo "Helm charts generated successfully in the helm/ directory."

# Start Services
up:
	$(COMPOSE_CMD) $(foreach svc,$(SERVICES),-f docker-compose.$(svc).yaml) up -d

# Stop Services
down:
	$(COMPOSE_CMD) $(foreach svc,$(SERVICES),-f docker-compose.$(svc).yaml) down

# Start a Specific Service
up-%:
ifeq ($*,email)
	$(EMAIL_COMPOSE_CMD) -f docker-compose.email.yaml up -d
else
	$(COMPOSE_CMD) -f docker-compose.$*.yaml up -d
endif

# Stop a Specific Service
down-%:
ifeq ($*,email)
	$(EMAIL_COMPOSE_CMD) -f docker-compose.email.yaml down
else
	$(COMPOSE_CMD) -f docker-compose.$*.yaml down
endif


# View Logs
logs:
	$(COMPOSE_CMD) logs -f

# Show Running Containers
ps:
	$(COMPOSE_CMD) ps

# Run Tests and Generate Surefire Report
report:
	@echo "Running tests and generating Surefire report..."
	mvn clean surefire-report:report
	@echo "Surefire report generated in target/reports/surefire.html"