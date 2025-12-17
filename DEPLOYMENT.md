# HTTPS 部署指南

## 修改内容总结

已将所有硬编码的 `http://39.97.60.191:33380` 改为相对路径 `/api/...`，通过 Nginx 代理到后端服务器。

### 修改的文件列表

1. **API 配置文件**
   - `src/api/config.js` - API 基础 URL
   - `src/api/company.js` - 管理系统 API URL
   - `src/api/video.js` - 视频管理 API URL
   - `src/api/homeIcons.js` - 图标 URL 处理
   - `src/api/products-management.js` - 产品图片 URL 处理

2. **Vue 组件**
   - `src/components/CompanyInfoSection.vue` - Logo URL
   - `src/components/SupplierCapabilityShowcase.vue` - Logo URL
   - `src/components/CompanyAdvantagesSection.vue` - 动态图标 URL
   - `src/components/MainProductsSection.vue` - Fetch 请求

3. **环境配置**
   - `src/config/env.js` - 环境配置
   - `.env` - 环境变量
   - `vite.config.js` - Vite 配置（添加注释）

## 部署步骤

### 1. 重新打包前端代码

```bash
# 安装依赖（如果还没有）
npm install

# 打包生产环境代码
npm run build
```

打包完成后，会在项目根目录生成 `dist` 文件夹。

### 2. 上传到服务器

将 `dist` 文件夹上传到服务器，例如：

```bash
# 使用 scp 上传（示例）
scp -r dist/* user@your-server:/var/www/html/frontend/

# 或使用 FTP/SFTP 工具上传
```

### 3. 配置 Nginx

#### 3.1 编辑 Nginx 配置文件

```bash
# 编辑配置文件
sudo nano /etc/nginx/sites-available/your-site.conf
```

#### 3.2 使用提供的配置模板

参考项目根目录的 `nginx.conf.example` 文件，修改以下内容：

- `server_name` - 替换为你的域名
- `ssl_certificate` - 替换为你的 SSL 证书路径
- `ssl_certificate_key` - 替换为你的 SSL 私钥路径
- `root` 和 `alias` - 替换为你的 dist 目录实际路径

#### 3.3 关键配置说明

**前端路由配置：**
```nginx
location /frontend/ {
    alias /path/to/your/dist/;
    try_files $uri $uri/ /frontend/index.html;
}
```

**API 代理配置（最重要）：**
```nginx
location /api/ {
    proxy_pass http://39.97.60.191:33380/api/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

### 4. 测试并重启 Nginx

```bash
# 测试配置文件语法
sudo nginx -t

# 如果测试通过，重启 Nginx
sudo systemctl restart nginx

# 或者重新加载配置
sudo systemctl reload nginx
```

### 5. 验证部署

1. **访问网站**
   - 打开浏览器，访问 `https://your-domain.com/frontend/`
   - 检查是否自动跳转到 HTTPS

2. **检查 API 请求**
   - 打开浏览器开发者工具（F12）
   - 切换到 Network 标签
   - 刷新页面
   - 检查所有 `/api/` 开头的请求是否正常返回数据

3. **检查控制台错误**
   - 切换到 Console 标签
   - 确认没有 Mixed Content 错误
   - 确认没有 CORS 错误

## 常见问题排查

### 问题 1：仍然出现 Mixed Content 错误

**原因：** 可能还有遗漏的硬编码 HTTP URL

**解决：**
```bash
# 在项目中搜索所有硬编码的 HTTP URL
grep -r "http://39.97.60.191" src/
```

### 问题 2：API 请求返回 404

**原因：** Nginx 代理配置不正确

**检查：**
1. 确认 Nginx 配置中的 `proxy_pass` 地址正确
2. 确认后端服务器 `http://39.97.60.191:33380` 可以正常访问
3. 检查 Nginx 错误日志：`sudo tail -f /var/log/nginx/error.log`

### 问题 3：API 请求返回 502 Bad Gateway

**原因：** 后端服务器无法访问或未启动

**解决：**
1. 检查后端服务器是否正常运行
2. 检查防火墙是否允许 Nginx 访问后端端口
3. 检查后端服务器日志

### 问题 4：静态资源（图片、CSS、JS）加载失败

**原因：** Nginx 静态文件路径配置不正确

**解决：**
1. 确认 `root` 或 `alias` 路径正确
2. 确认文件权限正确：`sudo chmod -R 755 /path/to/dist/`
3. 确认 Nginx 用户有读取权限

## 开发环境 vs 生产环境

### 开发环境（npm run dev）

- 使用 Vite 开发服务器
- Vite 的 proxy 配置会自动代理 `/api` 请求
- 访问地址：`http://localhost:33300`

### 生产环境（部署后）

- 使用 Nginx 服务器
- Nginx 配置代理 `/api` 请求到后端
- 访问地址：`https://your-domain.com/frontend/`

## 安全建议

1. **使用强 SSL 配置**
   - 只启用 TLSv1.2 和 TLSv1.3
   - 使用强加密套件

2. **设置安全头**
   ```nginx
   add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
   add_header X-Frame-Options "SAMEORIGIN" always;
   add_header X-Content-Type-Options "nosniff" always;
   add_header X-XSS-Protection "1; mode=block" always;
   ```

3. **限制请求大小**
   ```nginx
   client_max_body_size 10M;
   ```

4. **启用访问日志**
   ```nginx
   access_log /var/log/nginx/your-site-access.log;
   error_log /var/log/nginx/your-site-error.log;
   ```

## 回滚方案

如果部署后出现问题，可以快速回滚：

1. **保留旧版本代码**
   ```bash
   # 部署前备份
   cp -r /var/www/html/frontend /var/www/html/frontend.backup
   ```

2. **快速回滚**
   ```bash
   # 删除新版本
   rm -rf /var/www/html/frontend
   
   # 恢复旧版本
   mv /var/www/html/frontend.backup /var/www/html/frontend
   
   # 重启 Nginx
   sudo systemctl restart nginx
   ```

## 联系支持

如果遇到问题，请提供以下信息：

1. 浏览器控制台的错误信息（Console 和 Network 标签）
2. Nginx 错误日志：`sudo tail -100 /var/log/nginx/error.log`
3. 后端服务器日志
4. 访问的 URL 和预期行为
