DEV ?= true
ENV := $(if $(filter true,$(DEV)),.env.dev,.env.prod)
EMAIL_ENV = mailserver.env
COMMON_COMPOSE= -f docker-compose.yaml
COMPOSE_CMD = docker-compose --env-file $(ENV) $(COMMON_COMPOSE)
EMAIL_COMPOSE_CMD = docker-compose --env-file $(EMAIL_ENV) $(COMMON_COMPOSE)
SERVICES = db app

help:
	@echo "\nUsage: make [command] [SERVICE=<service>] [DEV=true|false]\n"
	@echo "Available commands:"
	@echo "  up                Start all services (uses ENV=${ENV})"
	@echo "  down              Stop all services"
	@echo "  up SERVICE=name   Start a specific service (email, db, app, etc.)"
	@echo "  down SERVICE=name Stop a specific service (email, db, app, etc.)"
	@echo "  logs              View logs of all running services"
	@echo "  ps                Show running containers"

up:
	$(EMAIL_COMPOSE_CMD) -f docker-compose.email.yaml up -d
	$(COMPOSE_CMD) $(foreach svc,$(SERVICES),-f docker-compose.$(svc).yaml) up -d

down:
	$(EMAIL_COMPOSE_CMD) -f docker-compose.email.yaml down
	$(COMPOSE_CMD) $(foreach svc,$(SERVICES),-f docker-compose.$(svc).yaml) down

up-%:
	@if [ "$*" = "email" ]; then \
		$(EMAIL_COMPOSE_CMD) -f docker-compose.$*.yaml up -d; \
	else \
		$(COMPOSE_CMD) -f docker-compose.$*.yaml up -d; \
	fi

down-%:
	@if [ "$*" = "email" ]; then \
		$(EMAIL_COMPOSE_CMD) -f docker-compose.$*.yaml down; \
	else \
		$(COMPOSE_CMD) -f docker-compose.$*.yaml down; \
	fi

logs:
	$(COMPOSE_CMD) logs -f

ps:
	$(COMPOSE_CMD) ps
