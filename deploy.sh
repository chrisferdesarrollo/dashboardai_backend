#!/bin/bash

# ============================================
# DashboardAI Production Deployment Script
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

# Check if running as root
check_root() {
    if [[ $EUID -eq 0 ]]; then
        print_error "This script should not be run as root"
        exit 1
    fi
}

# Check prerequisites
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed"
        exit 1
    fi
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed"
        exit 1
    fi
    
    # Check Git
    if ! command -v git &> /dev/null; then
        print_error "Git is not installed"
        exit 1
    fi
    
    print_success "Prerequisites check passed"
}

# Create necessary directories
setup_directories() {
    print_info "Setting up directories..."
    
    sudo mkdir -p /var/dashboardai/{data/{postgres,redis},logs/{backend,nginx}}
    sudo mkdir -p /var/www/certbot
    sudo mkdir -p $BACKUP_DIR
    
    # Set permissions
    sudo chown -R $USER:$USER /var/dashboardai
    sudo chown -R $USER:$USER $BACKUP_DIR
    
    print_success "Directories created"
}

# Backup current deployment
backup_current() {
    if [ -d "$DEPLOY_DIR" ]; then
        print_info "Creating backup of current deployment..."
        
        TIMESTAMP=$(date +%Y%m%d_%H%M%S)
        BACKUP_FILE="$BACKUP_DIR/backup_$TIMESTAMP.tar.gz"
        
        tar -czf $BACKUP_FILE -C $(dirname $DEPLOY_DIR) $(basename $DEPLOY_DIR)
        
        print_success "Backup created: $BACKUP_FILE"
    fi
}

# Clone or update repository
update_code() {
    print_info "Updating code..."
    
    if [ -d "$DEPLOY_DIR" ]; then
        cd $DEPLOY_DIR
        git fetch origin
        git reset --hard origin/main
        git clean -fd
    else
        git clone $REPO_URL $DEPLOY_DIR
        cd $DEPLOY_DIR
    fi
    
    print_success "Code updated"
}

# Setup environment variables
setup_env() {
    print_info "Setting up environment variables..."
    
    cat > $DEPLOY_DIR/.env.production << EOF
# Database Configuration
POSTGRES_DB=dashboard_ai_db
POSTGRES_USER=desarrolladorchristian
POSTGRES_PASSWORD=Dashboard_Agents_2025!

# Application URLs (Update with your domain)
DOMAIN_NAME=your-domain.com
FRONTEND_URL=https://your-domain.com
BACKEND_URL=https://your-domain.com/api

# Email Configuration
MAIL_USERNAME=ferchris82@gmail.com
MAIL_PASSWORD=TU_APP_PASSWORD_AQUI

# n8n Configuration
N8N_API_URL=https://n8n-n8n.hrxtio.easypanel.host/api/v1
N8N_API_TOKEN=your_n8n_api_token_here
N8N_WEBHOOK_URL=https://n8n-n8n.hrxtio.easypanel.host/webhook/evolution-api

# Security
JWT_SECRET=myVerySecureAndLongSecretKeyForJWTAuthentication2025WithMoreThan32Characters
REDIS_PASSWORD=Dashboard_Redis_2025!

# SSL/TLS
SSL_EMAIL=ferchris82@gmail.com
EOF

    print_success "Environment variables configured"
}

# Build and deploy containers
deploy_containers() {
    print_info "Building and deploying containers..."
    
    cd $DEPLOY_DIR
    
    # Stop existing containers
    docker-compose -f docker-compose.prod.yml down || true
    
    # Build and start new containers
    docker-compose -f docker-compose.prod.yml build --no-cache
    docker-compose -f docker-compose.prod.yml up -d
    
    print_success "Containers deployed"
}

# Setup SSL certificates
setup_ssl() {
    print_info "Setting up SSL certificates..."
    
    # Install certbot if not present
    if ! command -v certbot &> /dev/null; then
        sudo apt update
        sudo apt install -y certbot python3-certbot-nginx
    fi
    
    # Generate certificates (you'll need to run this manually first time)
    print_warning "SSL setup requires manual intervention for first-time setup"
    print_info "Run: sudo certbot --nginx -d your-domain.com -d www.your-domain.com"
    
    # Setup renewal cron job
    echo "0 12 * * * /usr/bin/certbot renew --quiet" | sudo crontab -
    
    print_success "SSL setup completed"
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
    
    # Check frontend
    if curl -f http://localhost/health &> /dev/null; then
        print_success "Frontend is healthy"
    else
        print_error "Frontend health check failed"
        return 1
    fi
    
    print_success "All services are healthy"
}

# Cleanup old images and containers
cleanup() {
    print_info "Cleaning up old Docker resources..."
    
    docker system prune -f
    docker image prune -a -f
    
    print_success "Cleanup completed"
}

# Main deployment function
deploy() {
    print_info "Starting deployment of DashboardAI..."
    
    check_root
    check_prerequisites
    setup_directories
    backup_current
    update_code
    setup_env
    deploy_containers
    
    if health_check; then
        cleanup
        print_success "Deployment completed successfully!"
        print_info "Application is available at: https://your-domain.com"
    else
        print_error "Deployment failed health check"
        exit 1
    fi
}

# Rollback function
rollback() {
    print_warning "Rolling back to previous version..."
    
    if [ -z "$(ls -A $BACKUP_DIR)" ]; then
        print_error "No backups available"
        exit 1
    fi
    
    LATEST_BACKUP=$(ls -t $BACKUP_DIR/backup_*.tar.gz | head -n1)
    
    if [ -f "$LATEST_BACKUP" ]; then
        print_info "Restoring from: $LATEST_BACKUP"
        
        # Stop current containers
        cd $DEPLOY_DIR
        docker-compose -f docker-compose.prod.yml down
        
        # Restore backup
        rm -rf $DEPLOY_DIR
        tar -xzf $LATEST_BACKUP -C $(dirname $DEPLOY_DIR)
        
        # Restart containers
        cd $DEPLOY_DIR
        docker-compose -f docker-compose.prod.yml up -d
        
        print_success "Rollback completed"
    else
        print_error "Backup file not found"
        exit 1
    fi
}

# Show logs
show_logs() {
    cd $DEPLOY_DIR
    docker-compose -f docker-compose.prod.yml logs -f
}

# Show status
show_status() {
    cd $DEPLOY_DIR
    docker-compose -f docker-compose.prod.yml ps
}

# Help function
show_help() {
    echo "DashboardAI Production Deployment Script"
    echo ""
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  deploy     Full deployment (default)"
    echo "  rollback   Rollback to previous version"
    echo "  logs       Show application logs"
    echo "  status     Show services status"
    echo "  ssl        Setup SSL certificates"
    echo "  health     Run health check"
    echo "  help       Show this help"
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
        show_logs
        ;;
    status)
        show_status
        ;;
    ssl)
        setup_ssl
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
