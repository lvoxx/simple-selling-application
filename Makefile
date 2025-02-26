# Environment Configuration
DEV ?= true
ENV := $(if $(filter true,$(DEV)),.env.dev,.env.prod)
EMAIL_ENV := mailserver.env
COMMON_COMPOSE := -f docker-compose.yaml
COMPOSE_CMD := docker-compose --env-file $(ENV) $(COMMON_COMPOSE)
EMAIL_COMPOSE_CMD := docker-compose --env-file $(EMAIL_ENV) $(COMMON_COMPOSE)
SERVICES := db app

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

# Start Services
up:
	$(EMAIL_COMPOSE_CMD) -f docker-compose.email.yaml up -d --remove-orphans
	$(COMPOSE_CMD) $(foreach svc,$(SERVICES),-f docker-compose.$(svc).yaml) up -d --remove-orphans

# Stop Services
down:
	$(EMAIL_COMPOSE_CMD) -f docker-compose.email.yaml down
	$(COMPOSE_CMD) $(foreach svc,$(SERVICES),-f docker-compose.$(svc).yaml) down

# Start a Specific Service
up-%:
	$(COMPOSE_CMD) -f docker-compose.$*.yaml up -d --remove-orphans

# Stop a Specific Service
down-%:
	$(COMPOSE_CMD) -f docker-compose.$*.yaml down

# View Logs
logs:
	$(COMPOSE_CMD) logs -f

# Show Running Containers
ps:
	$(COMPOSE_CMD) ps
