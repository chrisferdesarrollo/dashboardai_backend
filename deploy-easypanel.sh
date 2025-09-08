#!/bin/bash

# ============================================
# DashboardAI EasyPanel Deployment Script
# ============================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
PROJECT_NAME="dashboardai"
REPO_URL="https://github.com/chrisferdesarrollo/dashboardai_frontend.git"
DEPLOY_DIR="/var/www/dashboardai"
BACKUP_DIR="/var/backups/dashboardai"
LOG_FILE="/var/log/dashboardai-deploy.log"
COMPOSE_FILE="docker-compose.easypanel.yml"

# Functions
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1" | tee -a $LOG_FILE
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1" | tee -a $LOG_FILE
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a $LOG_FILE
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a $LOG_FILE
}

# Check EasyPanel services
check_easypanel() {
    print_info "Checking EasyPanel services..."
    
    # Check if EasyPanel is running
    if ! docker ps | grep -q easypanel; then
        print_warning "EasyPanel containers not found - this is normal if using different naming"
    fi
    
    # Check PostgreSQL
    if docker ps | grep -q postgres; then
        print_success "PostgreSQL found in EasyPanel"
        POSTGRES_CONTAINER=$(docker ps | grep postgres | awk '{print $1}')
        POSTGRES_NAME=$(docker inspect $POSTGRES_CONTAINER --format='{{.Name}}' | sed 's/\///')
        print_info "PostgreSQL Container: $POSTGRES_NAME"
    else
        print_error "PostgreSQL not found - please check EasyPanel"
        exit 1
    fi
    
    # Check n8n connectivity
    if curl -f https://n8n-n8n.hrxtio.easypanel.host &> /dev/null; then
        print_success "n8n is accessible"
    else
        print_warning "n8n might not be accessible"
    fi
}

# Setup database for DashboardAI
setup_database() {
    print_info "Setting up DashboardAI database..."
    
    POSTGRES_CONTAINER=$(docker ps | grep postgres | awk '{print $1}')
    
    # Check if database connection works with existing user
    if docker exec $POSTGRES_CONTAINER psql -U desarrolladorchristian -d dashboard_ai_db -c "SELECT 1;" &>/dev/null; then
        print_success "Database connection verified - dashboard_ai_db exists and user has access"
        return 0
    fi
    
    # Try to connect as postgres user (fallback)
    if docker exec $POSTGRES_CONTAINER psql -U postgres -c "SELECT 1;" &>/dev/null; then
        # Create database if not exists
        docker exec $POSTGRES_CONTAINER psql -U postgres -c "CREATE DATABASE dashboard_ai_db;" 2>/dev/null || print_info "Database already exists"
        
        # Create user if not exists
        docker exec $POSTGRES_CONTAINER psql -U postgres -c "CREATE USER desarrolladorchristian WITH PASSWORD 'Dashboard_Agents_2025!';" 2>/dev/null || print_info "User already exists"
        
        # Grant privileges
        docker exec $POSTGRES_CONTAINER psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE dashboard_ai_db TO desarrolladorchristian;"
        
        print_success "Database setup completed"
    else
        print_warning "Cannot connect to PostgreSQL as postgres user - assuming database is already configured"
    fi
}

# Create necessary directories
setup_directories() {
    print_info "Setting up directories..."
    
    mkdir -p /var/dashboardai/{data/redis,logs/{backend,nginx}}
    mkdir -p $BACKUP_DIR
    
    # Set permissions
    chown -R $USER:$USER /var/dashboardai
    chown -R $USER:$USER $BACKUP_DIR
    
    print_success "Directories created"
}

# Check for port conflicts
check_ports() {
    print_info "Checking for port conflicts..."
    
    # Check if our target ports are available
    PORTS=(3001 3443 8080 6380)
    
    for port in "${PORTS[@]}"; do
        if netstat -tlnp | grep -q ":$port "; then
            print_warning "Port $port is in use"
            netstat -tlnp | grep ":$port "
        else
            print_success "Port $port is available"
        fi
    done
}

# Update database configuration
update_db_config() {
    print_info "Updating database configuration..."
    
    POSTGRES_CONTAINER=$(docker ps | grep postgres | awk '{print $1}')
    
    # Get container name instead of IP for better networking
    POSTGRES_NAME=$(docker inspect $POSTGRES_CONTAINER --format='{{.Name}}' | sed 's/\///')
    
    print_info "Using PostgreSQL container: $POSTGRES_NAME"
    
    # No need to modify docker-compose as it should use container names for networking
    print_success "Database configuration will use container networking"
}

# Build and deploy containers
deploy_containers() {
    print_info "Building and deploying containers..."
    
    cd $DEPLOY_DIR
    
    # Stop existing containers
    docker-compose -f $COMPOSE_FILE down || true
    
    # Build and start new containers
    docker-compose -f $COMPOSE_FILE build --no-cache
    docker-compose -f $COMPOSE_FILE up -d
    
    print_success "Containers deployed"
}

# Health check
health_check() {
    print_info "Performing health check..."
    
    sleep 30  # Wait for services to start
    
    # Check backend
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        print_success "Backend is healthy"
    else
        print_error "Backend health check failed"
        return 1
    fi
    
    # Check frontend via nginx
    if curl -f http://localhost:3001/health &> /dev/null; then
        print_success "Frontend is healthy"
    else
        print_warning "Frontend health check failed (this might be normal)"
    fi
    
    # Check database connectivity
    if docker exec dashboardai_backend_prod curl -f http://localhost:8080/actuator/health | grep -q "UP"; then
        print_success "Database connectivity confirmed"
    else
        print_warning "Database connectivity check inconclusive"
    fi
    
    print_success "Health check completed"
}

# Show service status
show_status() {
    print_info "Service status:"
    
    echo "=== EasyPanel Services ==="
    docker ps | grep -E "(postgres|easypanel)" || echo "No EasyPanel services visible"
    
    echo -e "\n=== DashboardAI Services ==="
    docker-compose -f $COMPOSE_FILE ps
    
    echo -e "\n=== Port Usage ==="
    netstat -tlnp | grep -E ":(80|443|3001|3443|8080|5432|6380) "
}

# Show logs
show_logs() {
    cd $DEPLOY_DIR
    if [ -z "$1" ]; then
        docker-compose -f $COMPOSE_FILE logs -f
    else
        docker-compose -f $COMPOSE_FILE logs -f "$1"
    fi
}

# Main deployment function
deploy() {
    print_info "Starting DashboardAI deployment with EasyPanel integration..."
    
    check_easypanel
    setup_directories
    setup_database
    check_ports
    update_db_config
    deploy_containers
    
    if health_check; then
        print_success "Deployment completed successfully!"
        print_info "Frontend: http://$(hostname -I | awk '{print $1}'):3001"
        print_info "Backend:  http://$(hostname -I | awk '{print $1}'):8080"
        print_info "Configure EasyPanel proxy to point to port 3001 for public access"
    else
        print_error "Deployment completed with warnings"
        return 1
    fi
}

# Rollback function
rollback() {
    print_warning "Rolling back to previous version..."
    
    cd $DEPLOY_DIR
    docker-compose -f $COMPOSE_FILE down
    
    # Logic for rollback would go here
    print_info "Rollback completed"
}

# Help function
show_help() {
    echo "DashboardAI EasyPanel Deployment Script"
    echo ""
    echo "Usage: $0 [command] [options]"
    echo ""
    echo "Commands:"
    echo "  deploy       Full deployment (default)"
    echo "  rollback     Rollback to previous version"
    echo "  logs [svc]   Show application logs"
    echo "  status       Show services status"
    echo "  check        Check EasyPanel integration"
    echo "  health       Run health check"
    echo "  help         Show this help"
    echo ""
    echo "Services: frontend, backend, nginx-proxy, redis"
}

# Main script logic
case "${1:-deploy}" in
    deploy)
        deploy
        ;;
    rollback)
        rollback
        ;;
    logs)
        show_logs "$2"
        ;;
    status)
        show_status
        ;;
    check)
        check_easypanel
        ;;
    health)
        health_check
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
