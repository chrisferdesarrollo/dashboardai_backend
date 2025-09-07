# ============================================
# DashboardAI Docker Management Script (PowerShell)
# ============================================

param(
    [Parameter(Position=0)]
    [string]$Command,
    
    [Parameter(Position=1)]
    [string]$Service
)

# Colors for output
function Write-Info($message) {
    Write-Host "[INFO] $message" -ForegroundColor Blue
}

function Write-Success($message) {
    Write-Host "[SUCCESS] $message" -ForegroundColor Green
}

function Write-Warning($message) {
    Write-Host "[WARNING] $message" -ForegroundColor Yellow
}

function Write-Error($message) {
    Write-Host "[ERROR] $message" -ForegroundColor Red
}

# Create necessary directories
function New-Directories {
    Write-Info "Creating necessary directories..."
    
    $directories = @(
        "data\postgres",
        "data\redis", 
        "logs\backend",
        "database\init"
    )
    
    foreach ($dir in $directories) {
        if (!(Test-Path $dir)) {
            New-Item -ItemType Directory -Path $dir -Force | Out-Null
        }
    }
    
    Write-Success "Directories created successfully"
}

# Build and start all services
function Start-All {
    Write-Info "Starting DashboardAI application..."
    
    New-Directories
    
    Write-Info "Building and starting containers..."
    docker-compose up -d --build
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Application started successfully!"
        Write-Info "Frontend: http://localhost:3000"
        Write-Info "Backend:  http://localhost:8080"
        Write-Info "Database: localhost:5432"
        Write-Info "Redis:    localhost:6379"
    } else {
        Write-Error "Failed to start application"
    }
}

# Stop all services
function Stop-All {
    Write-Info "Stopping DashboardAI application..."
    docker-compose down
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Application stopped successfully!"
    } else {
        Write-Error "Failed to stop application"
    }
}

# Restart all services
function Restart-All {
    Write-Info "Restarting DashboardAI application..."
    docker-compose down
    docker-compose up -d --build
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Application restarted successfully!"
    } else {
        Write-Error "Failed to restart application"
    }
}

# Show logs
function Show-Logs {
    param([string]$ServiceName)
    
    if ([string]::IsNullOrEmpty($ServiceName)) {
        Write-Info "Showing logs for all services..."
        docker-compose logs -f
    } else {
        Write-Info "Showing logs for service: $ServiceName"
        docker-compose logs -f $ServiceName
    }
}

# Show status
function Show-Status {
    Write-Info "Service status:"
    docker-compose ps
    
    Write-Info "`nChecking container health..."
    
    # Check backend health
    try {
        $backendHealth = docker-compose exec backend curl -f http://localhost:8080/actuator/health 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Backend is healthy"
        } else {
            Write-Warning "Backend health check failed"
        }
    } catch {
        Write-Warning "Backend health check failed"
    }
    
    # Check frontend health
    try {
        $frontendHealth = docker-compose exec frontend curl -f http://localhost:80/health 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Frontend is healthy"
        } else {
            Write-Warning "Frontend health check failed"
        }
    } catch {
        Write-Warning "Frontend health check failed"
    }
}

# Clean up
function Remove-All {
    Write-Warning "This will remove all containers, volumes, and data. Are you sure? (y/N)"
    $response = Read-Host
    
    if ($response -match "^[Yy]([Ee][Ss])?$") {
        Write-Info "Cleaning up..."
        docker-compose down -v --rmi all
        docker system prune -f
        Write-Success "Cleanup completed!"
    } else {
        Write-Info "Cleanup cancelled"
    }
}

# Database operations
function Backup-Database {
    Write-Info "Creating database backup..."
    $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $backupFile = "backup_$timestamp.sql"
    
    docker-compose exec database pg_dump -U desarrolladorchristian dashboard_ai_db > $backupFile
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Database backup created: $backupFile"
    } else {
        Write-Error "Database backup failed"
    }
}

function Restore-Database {
    param([string]$BackupFile)
    
    if ([string]::IsNullOrEmpty($BackupFile)) {
        Write-Error "Please provide backup file path"
        return
    }
    
    if (!(Test-Path $BackupFile)) {
        Write-Error "Backup file not found: $BackupFile"
        return
    }
    
    Write-Info "Restoring database from $BackupFile..."
    Get-Content $BackupFile | docker-compose exec -T database psql -U desarrolladorchristian dashboard_ai_db
    
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Database restored successfully!"
    } else {
        Write-Error "Database restore failed"
    }
}

# Help function
function Show-Help {
    Write-Host "DashboardAI Docker Management Script (PowerShell)" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Usage: .\docker-manage.ps1 [command] [options]"
    Write-Host ""
    Write-Host "Commands:"
    Write-Host "  start          Start all services"
    Write-Host "  stop           Stop all services"
    Write-Host "  restart        Restart all services"
    Write-Host "  logs [service] Show logs (all services or specific service)"
    Write-Host "  status         Show service status and health"
    Write-Host "  cleanup        Remove all containers and data"
    Write-Host "  db-backup      Create database backup"
    Write-Host "  db-restore     Restore database from backup"
    Write-Host "  help           Show this help message"
    Write-Host ""
    Write-Host "Services: frontend, backend, database, redis"
    Write-Host ""
    Write-Host "Examples:"
    Write-Host "  .\docker-manage.ps1 start"
    Write-Host "  .\docker-manage.ps1 logs backend"
    Write-Host "  .\docker-manage.ps1 db-restore backup_20240307_120000.sql"
}

# Main script logic
switch ($Command.ToLower()) {
    "start" {
        Start-All
    }
    "stop" {
        Stop-All
    }
    "restart" {
        Restart-All
    }
    "logs" {
        Show-Logs -ServiceName $Service
    }
    "status" {
        Show-Status
    }
    "cleanup" {
        Remove-All
    }
    "db-backup" {
        Backup-Database
    }
    "db-restore" {
        Restore-Database -BackupFile $Service
    }
    "help" {
        Show-Help
    }
    default {
        if ([string]::IsNullOrEmpty($Command)) {
            Write-Error "No command provided"
        } else {
            Write-Error "Unknown command: $Command"
        }
        Show-Help
    }
}
