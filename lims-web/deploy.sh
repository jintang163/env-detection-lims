#!/bin/bash

# ============================================================
# LIMS 前端自动化部署脚本
# 适用于 CentOS 7/8/9 系统
# 功能：拉取代码 → 安装依赖 → 编译构建 → 部署 → 重启服务
# ============================================================

set -e

# ==================== 配置区域 ====================

# 项目配置
PROJECT_NAME="lims-web"
PROJECT_DIR="/opt/${PROJECT_NAME}"
GIT_REPO="git@github.com:your-org/your-repo.git"
GIT_BRANCH="main"

# 部署配置
DEPLOY_DIR="/usr/share/nginx/html/${PROJECT_NAME}"
BACKUP_DIR="/opt/backup/${PROJECT_NAME}"
KEEP_BACKUPS=5

# Node.js 配置
NODE_VERSION="18"
NPM_REGISTRY="https://registry.npmmirror.com"

# 服务配置
NGINX_SERVICE="nginx"
USE_PM2=false
PM2_APP_NAME="${PROJECT_NAME}"
PM2_PORT=3000

# 日志配置
LOG_FILE="/var/log/${PROJECT_NAME}/deploy-$(date +%Y%m%d).log"

# ==================== 颜色输出 ====================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# ==================== 日志函数 ====================

log_info() {
    echo -e "${BLUE}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
    echo -e "[INFO] $(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOG_FILE"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
    echo -e "[SUCCESS] $(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOG_FILE"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
    echo -e "[WARNING] $(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOG_FILE"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%Y-%m-%d %H:%M:%S') - $1"
    echo -e "[ERROR] $(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOG_FILE"
}

# ==================== 检查函数 ====================

check_root() {
    if [ "$EUID" -ne 0 ]; then
        log_error "请使用 root 用户或 sudo 执行此脚本"
        exit 1
    fi
}

check_command() {
    if ! command -v "$1" &> /dev/null; then
        return 1
    fi
    return 0
}

# ==================== 环境检查 ====================

check_environment() {
    log_info "开始检查环境..."

    # 创建日志目录
    mkdir -p "$(dirname "$LOG_FILE")"

    # 检查基础命令
    local missing_deps=()

    if ! check_command "git"; then
        missing_deps+=("git")
    fi

    if ! check_command "node"; then
        missing_deps+=("nodejs")
    fi

    if ! check_command "npm"; then
        missing_deps+=("npm")
    fi

    if ! check_command "nginx" && [ "$USE_PM2" = false ]; then
        missing_deps+=("nginx")
    fi

    if [ ${#missing_deps[@]} -gt 0 ]; then
        log_warning "检测到缺少依赖: ${missing_deps[*]}"
        read -p "是否自动安装缺少的依赖? (y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            install_dependencies "${missing_deps[@]}"
        else
            log_error "请手动安装缺少的依赖后再执行部署"
            exit 1
        fi
    fi

    # 检查 Node.js 版本
    local current_node_version
    current_node_version=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$current_node_version" -lt "$NODE_VERSION" ]; then
        log_warning "Node.js 版本过低 (v${current_node_version}.x), 建议使用 v${NODE_VERSION}.x"
        read -p "是否升级 Node.js? (y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            upgrade_node
        fi
    fi

    log_success "环境检查完成"
}

# ==================== 安装依赖 ====================

install_dependencies() {
    local deps=("$@")
    log_info "开始安装依赖: ${deps[*]}"

    # 更新系统
    yum update -y >> "$LOG_FILE" 2>&1

    for dep in "${deps[@]}"; do
        case $dep in
            git)
                log_info "安装 git..."
                yum install -y git >> "$LOG_FILE" 2>&1
                ;;
            nodejs)
                log_info "安装 Node.js v${NODE_VERSION}..."
                curl -fsSL "https://rpm.nodesource.com/setup_${NODE_VERSION}.x" | bash - >> "$LOG_FILE" 2>&1
                yum install -y nodejs >> "$LOG_FILE" 2>&1
                ;;
            npm)
                log_info "安装 npm..."
                yum install -y npm >> "$LOG_FILE" 2>&1
                ;;
            nginx)
                log_info "安装 nginx..."
                yum install -y nginx >> "$LOG_FILE" 2>&1
                systemctl enable nginx >> "$LOG_FILE" 2>&1
                ;;
        esac
    done

    log_success "依赖安装完成"
}

# ==================== 升级 Node.js ====================

upgrade_node() {
    log_info "升级 Node.js 到 v${NODE_VERSION}.x..."
    curl -fsSL "https://rpm.nodesource.com/setup_${NODE_VERSION}.x" | bash - >> "$LOG_FILE" 2>&1
    yum clean all && yum makecache >> "$LOG_FILE" 2>&1
    yum update -y nodejs >> "$LOG_FILE" 2>&1
    log_success "Node.js 升级完成，当前版本: $(node -v)"
}

# ==================== 拉取代码 ====================

pull_code() {
    log_info "开始拉取代码..."

    # 创建项目目录
    mkdir -p "$PROJECT_DIR"

    # 进入项目目录
    cd "$PROJECT_DIR"

    # 检查是否为 git 仓库
    if [ ! -d ".git" ]; then
        log_info "初始化 git 仓库并克隆代码..."
        git init >> "$LOG_FILE" 2>&1
        git remote add origin "$GIT_REPO" >> "$LOG_FILE" 2>&1
    fi

    # 拉取最新代码
    log_info "拉取分支 ${GIT_BRANCH} 的最新代码..."
    git fetch origin "$GIT_BRANCH" --depth=1 >> "$LOG_FILE" 2>&1
    git reset --hard "origin/${GIT_BRANCH}" >> "$LOG_FILE" 2>&1

    # 显示最新提交
    local latest_commit
    latest_commit=$(git log -1 --oneline)
    log_success "代码拉取完成，最新提交: ${latest_commit}"
}

# ==================== 安装项目依赖 ====================

install_project_deps() {
    log_info "开始安装项目依赖..."

    cd "${PROJECT_DIR}/lims-web"

    # 配置 npm 镜像源
    npm config set registry "$NPM_REGISTRY" >> "$LOG_FILE" 2>&1

    # 清理旧的依赖
    if [ -d "node_modules" ]; then
        log_info "清理旧的依赖..."
        rm -rf node_modules package-lock.json
    fi

    # 安装依赖
    log_info "执行 npm install..."
    npm install --no-audit --no-fund >> "$LOG_FILE" 2>&1

    local npm_version
    npm_version=$(npm -v)
    local node_version
    node_version=$(node -v)
    log_success "项目依赖安装完成 (node: ${node_version}, npm: ${npm_version})"
}

# ==================== 编译构建 ====================

build_project() {
    log_info "开始编译构建..."

    cd "${PROJECT_DIR}/lims-web"

    # 清理旧的构建产物
    if [ -d "dist" ]; then
        rm -rf dist
    fi

    # 执行构建
    log_info "执行 npm run build..."
    npm run build >> "$LOG_FILE" 2>&1

    # 检查构建结果
    if [ ! -d "dist" ]; then
        log_error "构建失败，未找到 dist 目录"
        exit 1
    fi

    local file_count
    file_count=$(find dist -type f | wc -l)
    local total_size
    total_size=$(du -sh dist | cut -f1)
    log_success "构建完成，共生成 ${file_count} 个文件，总大小: ${total_size}"
}

# ==================== 备份旧版本 ====================

backup_old_version() {
    log_info "开始备份旧版本..."

    mkdir -p "$BACKUP_DIR"

    if [ -d "$DEPLOY_DIR" ]; then
        local backup_name
        backup_name="backup-$(date +%Y%m%d-%H%M%S).tar.gz"
        local backup_path="${BACKUP_DIR}/${backup_name}"

        log_info "打包旧版本到: ${backup_path}"
        tar -czf "$backup_path" -C "$(dirname "$DEPLOY_DIR")" "$(basename "$DEPLOY_DIR")" >> "$LOG_FILE" 2>&1

        # 清理旧备份
        log_info "清理旧备份，保留最近 ${KEEP_BACKUPS} 个..."
        cd "$BACKUP_DIR"
        ls -t | tail -n +$((KEEP_BACKUPS + 1)) | xargs -r rm -rf

        log_success "旧版本备份完成"
    else
        log_warning "未找到旧版本目录，跳过备份"
    fi
}

# ==================== 部署新版本 ====================

deploy_new_version() {
    log_info "开始部署新版本..."

    # 创建部署目录
    mkdir -p "$DEPLOY_DIR"

    # 清空旧文件
    rm -rf "${DEPLOY_DIR:?}/"*

    # 复制新文件
    cp -r "${PROJECT_DIR}/lims-web/dist/"* "$DEPLOY_DIR"

    # 设置权限
    chown -R nginx:nginx "$DEPLOY_DIR"
    chmod -R 755 "$DEPLOY_DIR"

    # 创建版本信息文件
    local commit_hash
    commit_hash=$(cd "$PROJECT_DIR" && git rev-parse --short HEAD)
    local deploy_time
    deploy_time=$(date '+%Y-%m-%d %H:%M:%S')
    cat > "${DEPLOY_DIR}/version.txt" <<EOF
Project: ${PROJECT_NAME}
Version: ${commit_hash}
Branch: ${GIT_BRANCH}
Deploy Time: ${deploy_time}
Node Version: $(node -v)
NPM Version: $(npm -v)
EOF

    log_success "新版本部署完成"
}

# ==================== 配置 Nginx ====================

configure_nginx() {
    if [ "$USE_PM2" = true ]; then
        return 0
    fi

    log_info "配置 Nginx..."

    local nginx_conf="/etc/nginx/conf.d/${PROJECT_NAME}.conf"

    cat > "$nginx_conf" <<EOF
server {
    listen 80;
    server_name localhost;

    root ${DEPLOY_DIR};
    index index.html;

    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/javascript application/json image/svg+xml;

    # 主应用路由
    location / {
        try_files \$uri \$uri/ /index.html;
    }

    # API 代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
        proxy_send_timeout 60s;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 7d;
        add_header Cache-Control "public, immutable";
    }

    # 禁止隐藏文件访问
    location ~ /\. {
        deny all;
    }
}
EOF

    # 检查 Nginx 配置
    if ! nginx -t >> "$LOG_FILE" 2>&1; then
        log_error "Nginx 配置错误"
        exit 1
    fi

    log_success "Nginx 配置完成"
}

# ==================== 重启服务 ====================

restart_service() {
    log_info "重启服务..."

    if [ "$USE_PM2" = true ]; then
        # 使用 PM2 启动
        if ! check_command "pm2"; then
            log_info "安装 PM2..."
            npm install -g pm2 >> "$LOG_FILE" 2>&1
        fi

        # 检查是否已在运行
        if pm2 list | grep -q "$PM2_APP_NAME"; then
            log_info "重启 PM2 应用..."
            pm2 reload "$PM2_APP_NAME" >> "$LOG_FILE" 2>&1
        else
            log_info "启动 PM2 应用..."
            cd "$DEPLOY_DIR"
            pm2 start npx --name "$PM2_APP_NAME" -- serve -s . -l "$PM2_PORT" >> "$LOG_FILE" 2>&1
            pm2 save >> "$LOG_FILE" 2>&1
            pm2 startup systemd >> "$LOG_FILE" 2>&1
        fi

        # 等待服务启动
        sleep 3

        # 检查服务状态
        if curl -s -o /dev/null -w "%{http_code}" "http://127.0.0.1:${PM2_PORT}" | grep -q "200"; then
            log_success "PM2 服务启动成功，端口: ${PM2_PORT}"
        else
            log_error "PM2 服务启动失败"
            exit 1
        fi
    else
        # 使用 Nginx
        log_info "重启 Nginx..."
        systemctl restart "$NGINX_SERVICE" >> "$LOG_FILE" 2>&1

        # 检查服务状态
        if systemctl is-active --quiet "$NGINX_SERVICE"; then
            log_success "Nginx 重启成功"
        else
            log_error "Nginx 重启失败"
            exit 1
        fi
    fi
}

# ==================== 健康检查 ====================

health_check() {
    log_info "执行健康检查..."

    local check_url="http://127.0.0.1"
    if [ "$USE_PM2" = true ]; then
        check_url="http://127.0.0.1:${PM2_PORT}"
    fi

    # 最多重试 5 次
    local max_retries=5
    local retry_count=0

    while [ $retry_count -lt $max_retries ]; do
        local http_status
        http_status=$(curl -s -o /dev/null -w "%{http_code}" "$check_url" || echo "000")

        if [ "$http_status" = "200" ]; then
            log_success "健康检查通过，HTTP 状态码: ${http_status}"
            return 0
        else
            retry_count=$((retry_count + 1))
            log_warning "健康检查失败 (尝试 ${retry_count}/${max_retries})，HTTP 状态码: ${http_status}"
            sleep 2
        fi
    done

    log_error "健康检查失败，服务不可用"
    exit 1
}

# ==================== 显示部署信息 ====================

show_deploy_info() {
    local commit_hash
    commit_hash=$(cd "$PROJECT_DIR" && git rev-parse --short HEAD)
    local deploy_time
    deploy_time=$(date '+%Y-%m-%d %H:%M:%S')

    echo ""
    echo "============================================================"
    echo -e "${GREEN}部署完成!${NC}"
    echo "============================================================"
    echo "项目名称: ${PROJECT_NAME}"
    echo "版本: ${commit_hash}"
    echo "分支: ${GIT_BRANCH}"
    echo "部署时间: ${deploy_time}"
    echo "部署目录: ${DEPLOY_DIR}"
    if [ "$USE_PM2" = true ]; then
        echo "访问地址: http://127.0.0.1:${PM2_PORT}"
    else
        echo "访问地址: http://127.0.0.1"
    fi
    echo "日志文件: ${LOG_FILE}"
    echo "============================================================"
    echo ""
}

# ==================== 回滚函数 ====================

rollback() {
    log_info "开始回滚..."

    if [ ! -d "$BACKUP_DIR" ]; then
        log_error "备份目录不存在，无法回滚"
        exit 1
    fi

    # 列出可用备份
    local backups
    backups=$(ls -t "$BACKUP_DIR"/*.tar.gz 2>/dev/null | head -n "$KEEP_BACKUPS")

    if [ -z "$backups" ]; then
        log_error "没有找到可用的备份"
        exit 1
    fi

    echo "可用的备份:"
    local i=1
    for backup in $backups; do
        local backup_name
        backup_name=$(basename "$backup")
        local backup_time
        backup_time=$(stat -c %y "$backup" | cut -d'.' -f1)
        echo "  ${i}. ${backup_name} (${backup_time})"
        i=$((i + 1))
    done

    read -p "请选择要回滚的备份编号 (1-${KEEP_BACKUPS}): " backup_num

    local selected_backup
    selected_backup=$(echo "$backups" | sed -n "${backup_num}p")

    if [ -z "$selected_backup" ]; then
        log_error "无效的选择"
        exit 1
    fi

    log_info "回滚到: $(basename "$selected_backup")"

    # 停止服务
    if [ "$USE_PM2" = true ]; then
        pm2 stop "$PM2_APP_NAME" >> "$LOG_FILE" 2>&1 || true
    else
        systemctl stop "$NGINX_SERVICE" >> "$LOG_FILE" 2>&1 || true
    fi

    # 清空当前部署
    rm -rf "${DEPLOY_DIR:?}/"*

    # 解压备份
    tar -xzf "$selected_backup" -C / >> "$LOG_FILE" 2>&1

    # 设置权限
    chown -R nginx:nginx "$DEPLOY_DIR"
    chmod -R 755 "$DEPLOY_DIR"

    # 重启服务
    restart_service

    # 健康检查
    health_check

    log_success "回滚完成"
    exit 0
}

# ==================== 帮助信息 ====================

show_help() {
    cat <<EOF
LIMS 前端自动化部署脚本

用法:
  $0 [命令]

命令:
  deploy    执行完整部署流程 (默认)
  rollback  回滚到上一个版本
  help      显示此帮助信息

配置:
  修改脚本顶部的配置区域以适应您的环境

示例:
  $0 deploy    # 执行部署
  $0 rollback  # 执行回滚
EOF
}

# ==================== 主函数 ====================

main() {
    local command="${1:-deploy}"

    case $command in
        deploy)
            log_info "========== 开始部署流程 =========="
            check_root
            check_environment
            pull_code
            install_project_deps
            build_project
            backup_old_version
            deploy_new_version
            configure_nginx
            restart_service
            health_check
            show_deploy_info
            log_info "========== 部署流程结束 =========="
            ;;
        rollback)
            check_root
            rollback
            ;;
        help)
            show_help
            ;;
        *)
            log_error "未知命令: ${command}"
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
