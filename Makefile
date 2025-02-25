# Define the default environment (change to prod if needed)
ENV ?= dev

# Define individual Compose files
COMPOSE_DB=docker-compose.db.yaml
COMPOSE_EMAIL=docker-compose.email.yaml
COMPOSE_APP=docker-compose.yaml

# Function to run Docker Compose with the selected environment
define COMPOSE_CMD
	env $(shell cat .env.$(ENV) | xargs) docker-compose -f $(1)
endef

.PHONY: up down restart logs build

# Start all services
up:
	$(call COMPOSE_CMD, $(COMPOSE_DB) $(COMPOSE_EMAIL) $(COMPOSE_APP)) up -d

# Stop all services
down:
	$(call COMPOSE_CMD, $(COMPOSE_DB) $(COMPOSE_EMAIL) $(COMPOSE_APP)) down

# Start individual services
up-db:
	$(call COMPOSE_CMD, $(COMPOSE_DB)) up -d

up-email:
	$(call COMPOSE_CMD, $(COMPOSE_EMAIL)) up -d

up-app:
	$(call COMPOSE_CMD, $(COMPOSE_APP)) up -d

# Stop individual services
down-db:
	$(call COMPOSE_CMD, $(COMPOSE_DB)) down

down-email:
	$(call COMPOSE_CMD, $(COMPOSE_EMAIL)) down

down-app:
	$(call COMPOSE_CMD, $(COMPOSE_APP)) down

# Restart all services
restart:
	$(MAKE) down ENV=$(ENV)
	$(MAKE) up ENV=$(ENV)

# View logs from all services
logs:
	$(call COMPOSE_CMD, $(COMPOSE_DB) $(COMPOSE_EMAIL) $(COMPOSE_APP)) logs -f

# Build images and start all services
build:
	$(call COMPOSE_CMD, $(COMPOSE_DB) $(COMPOSE_EMAIL) $(COMPOSE_APP)) up --build -d
