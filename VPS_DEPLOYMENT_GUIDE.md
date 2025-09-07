# 🚀 Guía de Deployment en VPS Hostinger (con EasyPanel existente)

## 📋 Requisitos Previos

### 🔧 En tu VPS Hostinger:
- Ubuntu 20.04+ o Debian 11+
- Mínimo 2GB RAM, 20GB almacenamiento
- Acceso SSH root/sudo
- **EasyPanel ya instalado** ✅
- **PostgreSQL en EasyPanel** ✅  
- **n8n en EasyPanel** ✅
- **Evolution API en EasyPanel** ✅
- Dominio configurado apuntando al VPS

### 🌐 Configuración inicial de DNS:
```
A     your-domain.com      -> IP_DE_TU_VPS
A     www.your-domain.com  -> IP_DE_TU_VPS
```

### 📋 Servicios ya disponibles en EasyPanel:
- **PostgreSQL:** Puerto 5432 (interno)
- **n8n:** https://n8n-n8n.hrxtio.easypanel.host
- **Evolution API:** Configurado con n8n
- **EasyPanel:** Puerto 80/443 (principal)

### ⚡ Ventajas de usar EasyPanel existente:
✅ **No hay conflictos de puertos**  
✅ **Base de datos ya configurada**  
✅ **n8n ya funcional**  
✅ **Menor uso de recursos**  
✅ **Gestión unificada**

## 🛠️ Paso 1: Preparar el VPS (Adaptado para EasyPanel)

### 1.1 Conectar al VPS
```bash
ssh root@tu-ip-vps
# o
ssh tu-usuario@tu-ip-vps
```

### 1.2 Verificar servicios de EasyPanel
```bash
# Verificar que EasyPanel esté corriendo
docker ps | grep easypanel

# Verificar PostgreSQL de EasyPanel
docker ps | grep postgres

# Verificar n8n de EasyPanel
docker ps | grep n8n

# Ver puertos en uso
netstat -tlnp | grep :80
netstat -tlnp | grep :443
netstat -tlnp | grep :5432
```

### 1.3 Verificar Docker (ya debería estar instalado con EasyPanel)
```bash
docker --version
docker-compose --version

# Si Docker Compose no está instalado
curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

### 1.4 Configurar acceso a base de datos de EasyPanel
```bash
# Obtener información de la base de datos de EasyPanel
docker exec -it $(docker ps | grep postgres | awk '{print $1}') psql -U postgres -l

# Crear base de datos para DashboardAI (si no existe)
docker exec -it $(docker ps | grep postgres | awk '{print $1}') psql -U postgres -c "CREATE DATABASE dashboard_ai_db;"
docker exec -it $(docker ps | grep postgres | awk '{print $1}') psql -U postgres -c "CREATE USER desarrolladorchristian WITH PASSWORD 'Dashboard_Agents_2025!';"
docker exec -it $(docker ps | grep postgres | awk '{print $1}') psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE dashboard_ai_db TO desarrolladorchristian;"
```

## 🗂️ Paso 2: Configurar el Proyecto (Adaptado para EasyPanel)

### 2.1 Crear estructura de directorios (simplificada)
```bash
mkdir -p /var/www/dashboardai
mkdir -p /var/dashboardai/{data/redis,logs/{backend,nginx}}
mkdir -p /var/backups/dashboardai
# No necesitamos directorio postgres - lo maneja EasyPanel
```

### 2.2 Clonar el repositorio
```bash
cd /var/www
git clone https://github.com/chrisferdesarrollo/dashboardai_frontend.git dashboardai
cd dashboardai
```

### 2.3 Configurar variables de entorno (adaptadas para EasyPanel)
```bash
cp .env.production.example .env.production
nano .env.production
```

**Actualizar estos valores obligatorios:**
```env
# Tu dominio real (subdominio para evitar conflicto con EasyPanel)
DOMAIN_NAME=dashboard.tu-dominio.com
FRONTEND_URL=https://dashboard.tu-dominio.com
BACKEND_URL=https://dashboard.tu-dominio.com/api

# Email para SSL
SSL_EMAIL=tu-email@gmail.com

# Base de datos - usar la de EasyPanel
POSTGRES_HOST=host.docker.internal  # Acceso al PostgreSQL del host
POSTGRES_PORT=5432
POSTGRES_DB=dashboard_ai_db
POSTGRES_USER=desarrolladorchristian
POSTGRES_PASSWORD=Dashboard_Agents_2025!

# Puertos diferentes para evitar conflictos
NGINX_HTTP_PORT=3001
NGINX_HTTPS_PORT=3443
REDIS_PORT=6380

# n8n ya configurado en EasyPanel
N8N_API_URL=https://n8n-n8n.hrxtio.easypanel.host/api/v1
N8N_API_TOKEN=tu-token-n8n-real
N8N_WEBHOOK_URL=https://n8n-n8n.hrxtio.easypanel.host/webhook/evolution-api

# Configuración de email
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password-gmail
```

### 2.4 Configurar proxy reverso en EasyPanel
En lugar de configurar Nginx manualmente, puedes:

**Opción A: Usar EasyPanel para gestionar el proxy**
1. Ir a EasyPanel → Services → Add Service  
2. Configurar proxy hacia `localhost:3001` (tu aplicación)
3. EasyPanel manejará SSL automáticamente

**Opción B: Configurar subdominio independiente**
```bash
# Actualizar configuración de Nginx para subdominio
nano nginx/conf.d/dashboardai.conf
# Cambiar your-domain.com por dashboard.tu-dominio.com
```

## 🚀 Paso 3: Deployment (Adaptado para EasyPanel)

### 3.1 Hacer el script ejecutable
```bash
chmod +x deploy.sh
```

### 3.2 Usar Docker Compose para EasyPanel
```bash
# Usar la configuración específica para EasyPanel
docker-compose -f docker-compose.easypanel.yml up -d --build
```

**O modificar el script de deployment:**
```bash
# Editar deploy.sh para usar docker-compose.easypanel.yml
nano deploy.sh
# Cambiar todas las referencias de docker-compose.prod.yml por docker-compose.easypanel.yml
```

### 3.3 Configurar dominio/subdominio

**Opción A: Subdominio independiente**
1. Configurar DNS: `dashboard.tu-dominio.com → IP_VPS`
2. El SSL se manejará automáticamente

**Opción B: Proxy desde EasyPanel**
1. EasyPanel → Services → Add Service
2. Type: HTTP Proxy  
3. Source: `dashboard.tu-dominio.com`
4. Target: `http://localhost:3001`
5. Enable SSL: ✅

### 3.4 Verificar que no hay conflictos de puertos
```bash
# Ver puertos en uso
netstat -tlnp | grep :80    # EasyPanel
netstat -tlnp | grep :443   # EasyPanel  
netstat -tlnp | grep :3001  # Tu app HTTP
netstat -tlnp | grep :3443  # Tu app HTTPS
netstat -tlnp | grep :5432  # PostgreSQL EasyPanel
netstat -tlnp | grep :8080  # Tu backend
```

## 🔍 Paso 4: Verificación (Adaptado para EasyPanel)

### 4.1 Verificar servicios
```bash
# Ver todos los contenedores (EasyPanel + tu app)
docker ps

# Ver específicamente tu aplicación
docker-compose -f docker-compose.easypanel.yml ps
```

### 4.2 Verificar logs
```bash
# Logs de tu aplicación
docker-compose -f docker-compose.easypanel.yml logs

# Logs específicos
docker logs dashboardai_backend_prod
docker logs dashboardai_frontend_prod
docker logs dashboardai_nginx_prod
```

### 4.3 Verificar conectividad con servicios de EasyPanel
```bash
# Probar conexión a PostgreSQL de EasyPanel
docker exec dashboardai_backend_prod curl -f http://host.docker.internal:5432 || echo "Puerto cerrado - usar IP del contenedor"

# Ver IP de PostgreSQL en EasyPanel
docker inspect $(docker ps | grep postgres | awk '{print $1}') | grep IPAddress

# Probar conexión a n8n
curl -f https://n8n-n8n.hrxtio.easypanel.host/api/v1/workflows
```

### 4.4 Verificar en navegador
- **Frontend:** `http://tu-ip-vps:3001` o `https://dashboard.tu-dominio.com`
- **Backend:** `http://tu-ip-vps:8080/actuator/health`
- **EasyPanel:** `https://tu-dominio.com` (sin afectar)
- **n8n:** `https://n8n-n8n.hrxtio.easypanel.host` (sin afectar)

## 🔧 Comandos Útiles

### Gestión de la aplicación
```bash
# Ver estado
./deploy.sh status

# Ver logs en tiempo real
./deploy.sh logs

# Hacer rollback
./deploy.sh rollback

# Redesplegar
./deploy.sh deploy
```

### Gestión de Docker
```bash
# Ver contenedores
docker ps

# Ver logs específicos
docker logs dashboardai_backend_prod
docker logs dashboardai_frontend_prod

# Entrar a un contenedor
docker exec -it dashboardai_backend_prod bash
docker exec -it dashboardai_db_prod psql -U desarrolladorchristian dashboard_ai_db
```

### Gestión de base de datos
```bash
# Backup manual
docker exec dashboardai_db_prod pg_dump -U desarrolladorchristian dashboard_ai_db > backup_$(date +%Y%m%d).sql

# Restaurar backup
cat backup_20240307.sql | docker exec -i dashboardai_db_prod psql -U desarrolladorchristian dashboard_ai_db
```

## 🔒 Seguridad Adicional

### Firewall básico
```bash
# Instalar UFW
apt install -y ufw

# Configurar reglas básicas
ufw default deny incoming
ufw default allow outgoing
ufw allow ssh
ufw allow 80
ufw allow 443

# Habilitar firewall
ufw enable
```

### Actualización automática del sistema
```bash
apt install -y unattended-upgrades
dpkg-reconfigure -plow unattended-upgrades
```

## 🚨 Troubleshooting (Específico para EasyPanel)

### Problema: Backend no conecta a PostgreSQL de EasyPanel
```bash
# Opción 1: Usar IP del contenedor PostgreSQL
POSTGRES_IP=$(docker inspect $(docker ps | grep postgres | awk '{print $1}') | grep '"IPAddress"' | head -1 | awk -F'"' '{print $4}')
echo "IP de PostgreSQL: $POSTGRES_IP"

# Opción 2: Conectar a la red de EasyPanel
docker network ls | grep easypanel
docker-compose -f docker-compose.easypanel.yml down
# Editar docker-compose.easypanel.yml para usar la red de EasyPanel
docker-compose -f docker-compose.easypanel.yml up -d
```

### Problema: Conflicto de puertos con EasyPanel
```bash
# Verificar puertos en conflicto
netstat -tlnp | grep :80
netstat -tlnp | grep :443

# Cambiar puertos en docker-compose.easypanel.yml
nano docker-compose.easypanel.yml
# Cambiar ports: - "80:80" por "3001:80"
# Cambiar ports: - "443:443" por "3443:443"
```

### Problema: No se puede acceder desde EasyPanel proxy
```bash
# Verificar que el backend esté escuchando en todas las interfaces
docker exec dashboardai_backend_prod netstat -tlnp | grep :8080

# Verificar configuración de red
docker network inspect $(docker-compose -f docker-compose.easypanel.yml ps -q | head -1 | xargs docker inspect --format='{{range .NetworkSettings.Networks}}{{.NetworkID}}{{end}}')
```

### Problema: SSL/HTTPS con EasyPanel
```bash
# No configurar SSL manualmente - usar EasyPanel
# 1. EasyPanel → Services → tu-servicio → Settings
# 2. Enable SSL/TLS ✅  
# 3. EasyPanel manejará certificados automáticamente
```

## 📊 Monitoreo

### Logs importantes
```bash
# Logs de aplicación
tail -f /var/dashboardai/logs/backend/application.log
tail -f /var/dashboardai/logs/nginx/access.log
tail -f /var/dashboardai/logs/nginx/error.log

# Logs del sistema
tail -f /var/log/syslog
tail -f /var/log/auth.log
```

### Verificar recursos
```bash
# CPU y memoria
htop

# Espacio en disco
df -h

# Estado de Docker
docker system df
```

## 🔄 Actualizaciones

### Actualizar aplicación
```bash
cd /var/www/dashboardai
./deploy.sh deploy
```

### Actualizar sistema
```bash
apt update && apt upgrade -y
```

## 📞 Soporte

Si tienes problemas:

1. **Revisa los logs:** `./deploy.sh logs`
2. **Verifica el estado:** `./deploy.sh status`
3. **Prueba health check:** `./deploy.sh health`
4. **Revisa la configuración:** Variables de entorno y Nginx
5. **Contacta soporte:** Con logs específicos del error
