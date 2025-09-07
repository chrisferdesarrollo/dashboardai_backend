#!/bin/bash

# ============================================
# DashboardAI Docker Management Script
# ============================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Create necessary directories
create_directories() {
    print_info "Creating necessary directories..."
    
    mkdir -p data/postgres
    mkdir -p data/redis
    mkdir -p logs/backend
    mkdir -p database/init
    
    print_success "Directories created successfully"
}

# Build and start all services
start_all() {
    print_info "Starting DashboardAI application..."
    
    create_directories
    
    print_info "Building and starting containers..."
    docker-compose up -d --build
    
    print_success "Application started successfully!"
    print_info "Frontend: http://localhost:3000"
    print_info "Backend:  http://localhost:8080"
    print_info "Database: localhost:5432"
    print_info "Redis:    localhost:6379"
}

# Stop all services
stop_all() {
    print_info "Stopping DashboardAI application..."
    docker-compose down
    print_success "Application stopped successfully!"
}

# Restart all services
restart_all() {
    print_info "Restarting DashboardAI application..."
    docker-compose down
    docker-compose up -d --build
    print_success "Application restarted successfully!"
}

# Show logs
show_logs() {
    if [ -z "$1" ]; then
        print_info "Showing logs for all services..."
        docker-compose logs -f
    else
        print_info "Showing logs for service: $1"
        docker-compose logs -f "$1"
    fi
}

# Show status
show_status() {
    print_info "Service status:"
    docker-compose ps
    
    print_info "\nContainer health status:"
    docker-compose exec backend curl -f http://localhost:8080/actuator/health 2>/dev/null || print_warning "Backend health check failed"
    docker-compose exec frontend curl -f http://localhost:80/health 2>/dev/null || print_warning "Frontend health check failed"
}

# Clean up
cleanup() {
    print_warning "This will remove all containers, volumes, and data. Are you sure? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        print_info "Cleaning up..."
        docker-compose down -v --rmi all
        docker system prune -f
        print_success "Cleanup completed!"
    else
        print_info "Cleanup cancelled"
    fi
}

# Database operations
db_backup() {
    print_info "Creating database backup..."
    docker-compose exec database pg_dump -U desarrolladorchristian dashboard_ai_db > "backup_$(date +%Y%m%d_%H%M%S).sql"
    print_success "Database backup created successfully!"
}

db_restore() {
    if [ -z "$1" ]; then
        print_error "Please provide backup file path"
        exit 1
    fi
    
    print_info "Restoring database from $1..."
    docker-compose exec -T database psql -U desarrolladorchristian dashboard_ai_db < "$1"
    print_success "Database restored successfully!"
}

# Help function
show_help() {
    echo "DashboardAI Docker Management Script"
    echo ""
    echo "Usage: $0 [command] [options]"
    echo ""
    echo "Commands:"
    echo "  start          Start all services"
    echo "  stop           Stop all services"
    echo "  restart        Restart all services"
    echo "  logs [service] Show logs (all services or specific service)"
    echo "  status         Show service status and health"
    echo "  cleanup        Remove all containers and data"
    echo "  db-backup      Create database backup"
    echo "  db-restore     Restore database from backup"
    echo "  help           Show this help message"
    echo ""
    echo "Services: frontend, backend, database, redis"
    echo ""
    echo "Examples:"
    echo "  $0 start"
    echo "  $0 logs backend"
    echo "  $0 db-restore backup_20240307_120000.sql"
}

# Main script logic
case "$1" in
    start)
        start_all
        ;;
    stop)
        stop_all
        ;;
    restart)
        restart_all
        ;;
    logs)
        show_logs "$2"
        ;;
    status)
        show_status
        ;;
    cleanup)
        cleanup
        ;;
    db-backup)
        db_backup
        ;;
    db-restore)
        db_restore "$2"
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac
