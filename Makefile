# CMS Docker Compose Makefile
# This Makefile provides convenient commands for rebuilding Docker images

# Variables
COMPOSE_FILE = docker-compose.yml
PROJECT_NAME = cms

# Colors for output
RED = \033[0;31m
GREEN = \033[0;32m
YELLOW = \033[1;33m
BLUE = \033[0;34m
PURPLE = \033[0;35m
CYAN = \033[0;36m
WHITE = \033[1;37m
NC = \033[0m # No Color

# Default target
.DEFAULT_GOAL := help

# Help target
.PHONY: help
help: ## Show this help message
	@echo "$(CYAN)Available commands:$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "$(GREEN)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(YELLOW)Examples:$(NC)"
	@echo "  make rebuild-frontend    # Rebuild only the frontend service"
	@echo "  make rebuild-backend     # Rebuild both CMS and Discovery services"
	@echo "  make rebuild-all         # Rebuild all custom services"
	@echo "  make clean               # Stop and remove all containers and volumes"
	@echo "  make fresh-start         # Clean everything and start fresh"

# =============================================================================
# BUILD TARGETS
# =============================================================================

.PHONY: rebuild-frontend
rebuild-frontend: ## Rebuild the frontend service only
	@echo "$(BLUE)Rebuilding frontend service...$(NC)"
	docker-compose -f $(COMPOSE_FILE) build --no-cache cms-frontend
	@echo "$(GREEN)Frontend rebuild complete!$(NC)"

.PHONY: rebuild-cms-service
rebuild-cms-service: ## Rebuild the CMS backend service only
	@echo "$(BLUE)Rebuilding CMS service...$(NC)"
	docker-compose -f $(COMPOSE_FILE) build --no-cache cms-service
	@echo "$(GREEN)CMS service rebuild complete!$(NC)"

.PHONY: rebuild-discovery-service
rebuild-discovery-service: ## Rebuild the CMS Discovery service only
	@echo "$(BLUE)Rebuilding CMS Discovery service...$(NC)"
	docker-compose -f $(COMPOSE_FILE) build --no-cache cms-discovery-service
	@echo "$(GREEN)CMS Discovery service rebuild complete!$(NC)"

.PHONY: rebuild-backend
rebuild-backend: ## Rebuild both CMS and Discovery services
	@echo "$(BLUE)Rebuilding backend services...$(NC)"
	docker-compose -f $(COMPOSE_FILE) build --no-cache cms-service cms-discovery-service
	@echo "$(GREEN)Backend services rebuild complete!$(NC)"

.PHONY: rebuild-all
rebuild-all: ## Rebuild all custom services (frontend + backend)
	@echo "$(BLUE)Rebuilding all custom services...$(NC)"
	docker-compose -f $(COMPOSE_FILE) build --no-cache
	@echo "$(GREEN)All services rebuild complete!$(NC)"

# =============================================================================
# START/STOP TARGETS
# =============================================================================

.PHONY: start
start: ## Start all services
	@echo "$(BLUE)Starting all services...$(NC)"
	docker-compose -f $(COMPOSE_FILE) up -d
	@echo "$(GREEN)All services started!$(NC)"

.PHONY: stop
stop: ## Stop all services
	@echo "$(YELLOW)Stopping all services...$(NC)"
	docker-compose -f $(COMPOSE_FILE) down
	@echo "$(GREEN)All services stopped!$(NC)"

.PHONY: restart
restart: ## Restart all services
	@echo "$(BLUE)Restarting all services...$(NC)"
	docker-compose -f $(COMPOSE_FILE) restart
	@echo "$(GREEN)All services restarted!$(NC)"

.PHONY: restart-frontend
restart-frontend: ## Restart only the frontend service
	@echo "$(BLUE)Restarting frontend service...$(NC)"
	docker-compose -f $(COMPOSE_FILE) restart cms-frontend
	@echo "$(GREEN)Frontend service restarted!$(NC)"

.PHONY: restart-backend
restart-backend: ## Restart backend services
	@echo "$(BLUE)Restarting backend services...$(NC)"
	docker-compose -f $(COMPOSE_FILE) restart cms-service cms-discovery-service
	@echo "$(GREEN)Backend services restarted!$(NC)"

# =============================================================================
# CLEANUP TARGETS
# =============================================================================

.PHONY: clean
clean: ## Stop and remove all containers, networks, and volumes
	@echo "$(RED)Cleaning up all containers, networks, and volumes...$(NC)"
	docker-compose -f $(COMPOSE_FILE) down -v --remove-orphans
	@echo "$(GREEN)Cleanup complete!$(NC)"

.PHONY: clean-images
clean-images: ## Remove all project images
	@echo "$(RED)Removing all project images...$(NC)"
	docker-compose -f $(COMPOSE_FILE) down --rmi all --remove-orphans
	@echo "$(GREEN)Images cleanup complete!$(NC)"

.PHONY: clean-volumes
clean-volumes: ## Remove all project volumes
	@echo "$(RED)Removing all project volumes...$(NC)"
	docker-compose -f $(COMPOSE_FILE) down -v
	docker volume rm cms_elasticsearch_data cms_rabbitmq_data cms_postgres_data 2>/dev/null || true
	@echo "$(GREEN)Volumes cleanup complete!$(NC)"

# =============================================================================
# COMBINED TARGETS
# =============================================================================

.PHONY: fresh-start
fresh-start: ## Clean everything and start fresh
	@echo "$(PURPLE)Starting fresh deployment...$(NC)"
	$(MAKE) clean
	$(MAKE) rebuild-all
	$(MAKE) start
	@echo "$(GREEN)Fresh deployment complete!$(NC)"

.PHONY: rebuild-and-restart-frontend
rebuild-and-restart-frontend: ## Rebuild and restart frontend
	@echo "$(BLUE)Rebuilding and restarting frontend...$(NC)"
	$(MAKE) rebuild-frontend
	$(MAKE) restart-frontend
	@echo "$(GREEN)Frontend rebuild and restart complete!$(NC)"

.PHONY: rebuild-and-restart-backend
rebuild-and-restart-backend: ## Rebuild and restart backend services
	@echo "$(BLUE)Rebuilding and restarting backend services...$(NC)"
	$(MAKE) rebuild-backend
	$(MAKE) restart-backend
	@echo "$(GREEN)Backend rebuild and restart complete!$(NC)"

.PHONY: rebuild-and-restart-all
rebuild-and-restart-all: ## Rebuild and restart all custom services
	@echo "$(BLUE)Rebuilding and restarting all custom services...$(NC)"
	$(MAKE) rebuild-all
	$(MAKE) restart
	@echo "$(GREEN)All services rebuild and restart complete!$(NC)"

# =============================================================================
# MONITORING TARGETS
# =============================================================================

.PHONY: logs
logs: ## Show logs for all services
	@echo "$(BLUE)Showing logs for all services...$(NC)"
	docker-compose -f $(COMPOSE_FILE) logs -f

.PHONY: logs-frontend
logs-frontend: ## Show logs for frontend service
	@echo "$(BLUE)Showing frontend logs...$(NC)"
	docker-compose -f $(COMPOSE_FILE) logs -f cms-frontend

.PHONY: logs-backend
logs-backend: ## Show logs for backend services
	@echo "$(BLUE)Showing backend logs...$(NC)"
	docker-compose -f $(COMPOSE_FILE) logs -f cms-service cms-discovery-service

.PHONY: status
status: ## Show status of all services
	@echo "$(BLUE)Service Status:$(NC)"
	docker-compose -f $(COMPOSE_FILE) ps

.PHONY: health
health: ## Check health of all services
	@echo "$(BLUE)Health Check:$(NC)"
	@echo "$(CYAN)Frontend (http://localhost:4200):$(NC)"
	@curl -s -o /dev/null -w "Status: %{http_code}\n" http://localhost:4200 || echo "Not accessible"
	@echo "$(CYAN)CMS Service (http://localhost:8078):$(NC)"
	@curl -s -o /dev/null -w "Status: %{http_code}\n" http://localhost:8078/actuator/health || echo "Not accessible"
	@echo "$(CYAN)Discovery Service (http://localhost:8079):$(NC)"
	@curl -s -o /dev/null -w "Status: %{http_code}\n" http://localhost:8079/actuator/health || echo "Not accessible"
	@echo "$(CYAN)Elasticsearch (http://localhost:9200):$(NC)"
	@curl -s -o /dev/null -w "Status: %{http_code}\n" http://localhost:9200/_cluster/health || echo "Not accessible"
	@echo "$(CYAN)RabbitMQ (http://localhost:15672):$(NC)"
	@curl -s -o /dev/null -w "Status: %{http_code}\n" http://localhost:15672 || echo "Not accessible"
	@echo "$(CYAN)PostgreSQL (localhost:5432):$(NC)"
	@docker exec cms-postgres pg_isready -U postgres -d cms_db >/dev/null 2>&1 && echo "Status: 200" || echo "Not accessible"

# =============================================================================
# DEVELOPMENT TARGETS
# =============================================================================

.PHONY: dev-start
dev-start: ## Start services for development (with logs)
	@echo "$(BLUE)Starting services for development...$(NC)"
	docker-compose -f $(COMPOSE_FILE) up

.PHONY: dev-stop
dev-stop: ## Stop development services
	@echo "$(YELLOW)Stopping development services...$(NC)"
	docker-compose -f $(COMPOSE_FILE) down
	@echo "$(GREEN)Development services stopped!$(NC)"

.PHONY: shell-frontend
shell-frontend: ## Open shell in frontend container
	@echo "$(BLUE)Opening shell in frontend container...$(NC)"
	docker-compose -f $(COMPOSE_FILE) exec cms-frontend /bin/sh

.PHONY: shell-cms
shell-cms: ## Open shell in CMS service container
	@echo "$(BLUE)Opening shell in CMS service container...$(NC)"
	docker-compose -f $(COMPOSE_FILE) exec cms-service /bin/sh

.PHONY: shell-discovery
shell-discovery: ## Open shell in Discovery service container
	@echo "$(BLUE)Opening shell in Discovery service container...$(NC)"
	docker-compose -f $(COMPOSE_FILE) exec cms-discovery-service /bin/sh

# =============================================================================
# UTILITY TARGETS
# =============================================================================

.PHONY: pull
pull: ## Pull latest images for all services
	@echo "$(BLUE)Pulling latest images...$(NC)"
	docker-compose -f $(COMPOSE_FILE) pull
	@echo "$(GREEN)Images pulled successfully!$(NC)"

.PHONY: prune
prune: ## Remove unused Docker resources
	@echo "$(YELLOW)Removing unused Docker resources...$(NC)"
	docker system prune -f
	@echo "$(GREEN)Prune complete!$(NC)"

.PHONY: info
info: ## Show Docker and system information
	@echo "$(CYAN)Docker Information:$(NC)"
	@docker --version
	@docker-compose --version
	@echo ""
	@echo "$(CYAN)System Information:$(NC)"
	@echo "OS: $(shell uname -s)"
	@echo "Architecture: $(shell uname -m)"
	@echo "Available Memory: $(shell free -h | grep Mem | awk '{print $$2}')"
	@echo "Available Disk: $(shell df -h . | tail -1 | awk '{print $$4}')" 