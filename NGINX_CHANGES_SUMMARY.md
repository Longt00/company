# Nginx 配置修改总结

## 修改时间
2025年12月9日

## 修改目的
解决 HTTPS 部署后 API 请求超时（timeout of 10000ms exceeded）的问题

---

## ✅ 已完成的修改

### 1. 🔴 修改 API 代理地址（最关键）

**HTTP Server 块 (第 151 行)：**
```nginx
# 修改前
proxy_pass http://localhost:33380;

# 修改后
proxy_pass http://39.97.60.191:33380/api/;
```

**HTTPS Server 块 (第 227 行)：**
```nginx
# 修改前
proxy_pass http://localhost:33380;

# 修改后
proxy_pass http://39.97.60.191:33380/api/;
```

**原因：** 原配置使用 `localhost:33380`，但后端实际在 `39.97.60.191:33380`，导致无法连接。

---

### 2. ⏱️ 添加超时配置

**HTTP Server 块 (第 157-160 行)：**
```nginx
# 超时设置
proxy_connect_timeout 60s;
proxy_send_timeout 60s;
proxy_read_timeout 60s;
```

**HTTPS Server 块 (第 233-236 行)：**
```nginx
# 超时设置（解决10000ms超时错误）
proxy_connect_timeout 60s;
proxy_send_timeout 60s;
proxy_read_timeout 60s;
```

**原因：** 增加超时时间，避免后端响应慢时出现超时错误。

---

### 3. 🔌 添加 WebSocket 支持

**HTTP Server 块 (第 162-164 行)：**
```nginx
# WebSocket 支持
proxy_set_header Upgrade $http_upgrade;
proxy_set_header Connection "upgrade";
```

**HTTPS Server 块 (第 238-240 行)：**
```nginx
# WebSocket 支持
proxy_set_header Upgrade $http_upgrade;
proxy_set_header Connection "upgrade";
```

**原因：** 支持 WebSocket 连接，提升实时通信能力。

---

### 4. 🚫 禁用代理缓冲

**HTTP Server 块 (第 166-168 行)：**
```nginx
# 禁用缓冲
proxy_buffering off;
proxy_request_buffering off;
```

**HTTPS Server 块 (第 242-244 行)：**
```nginx
# 禁用缓冲
proxy_buffering off;
proxy_request_buffering off;
```

**原因：** 提高实时性，特别是对于大文件上传或流式响应。

---

### 5. 🌐 优化 CORS 配置

**HTTP Server 块 (第 171-173 行)：**
```nginx
# CORS设置
add_header Access-Control-Allow-Origin * always;
add_header Access-Control-Allow-Methods 'GET, POST, PUT, DELETE, OPTIONS' always;
add_header Access-Control-Allow-Headers 'Authorization, Content-Type, X-Requested-With' always;
```

**HTTPS Server 块 (第 247-249 行)：**
```nginx
# CORS设置（适配HTTPS）
add_header Access-Control-Allow-Origin * always;
add_header Access-Control-Allow-Methods 'GET, POST, PUT, DELETE, OPTIONS' always;
add_header Access-Control-Allow-Headers 'Authorization, Content-Type, X-Requested-With' always;
```

**原因：** 添加 `always` 标志，确保 CORS 头在所有响应中生效（包括错误响应）。

---

### 6. 🔒 添加 HTTP 到 HTTPS 自动跳转

**HTTP Server 块 (第 38-44 行)：**
```nginx
# HTTP 自动跳转到 HTTPS（针对 c-jeans.com 域名）
if ($host = "c-jeans.com") {
    return 301 https://$host$request_uri;
}
if ($host = "www.c-jeans.com") {
    return 301 https://$host$request_uri;
}
```

**原因：** 强制用户使用 HTTPS 访问，提升安全性。

---

## 📋 修改前后对比

### API 代理配置对比

| 配置项 | 修改前 | 修改后 |
|--------|--------|--------|
| proxy_pass | `http://localhost:33380` | `http://39.97.60.191:33380/api/` |
| 超时设置 | ❌ 无 | ✅ 60秒 |
| WebSocket | ❌ 无 | ✅ 支持 |
| 缓冲 | ✅ 默认启用 | ❌ 禁用 |
| CORS always | ❌ 无 | ✅ 有 |
| HTTPS 跳转 | ❌ 无 | ✅ 有 |

---

## 🎯 预期效果

修改完成后，应该解决以下问题：

1. ✅ **API 超时错误消失**
   - 不再出现 "timeout of 10000ms exceeded"
   - 所有 `/api/` 请求能正常返回数据

2. ✅ **混合内容问题解决**
   - HTTPS 页面可以正常请求后端 API
   - 浏览器控制台不再有 Mixed Content 警告

3. ✅ **数据正常显示**
   - 产品列表、轮播图、公司信息等都能正常加载
   - 不再出现 ReferenceError 错误

4. ✅ **自动 HTTPS 跳转**
   - 访问 `http://c-jeans.com` 自动跳转到 `https://c-jeans.com`

---

## 🚀 部署步骤

### 1. 上传修改后的配置文件到服务器

```bash
# 使用 SCP 上传
scp c-jeans_nginx.conf user@your-server:/usr/local/nginx/conf/nginx.conf

# 或使用 SFTP 工具上传
```

### 2. 备份原配置（重要！）

```bash
# SSH 登录服务器后
sudo cp /usr/local/nginx/conf/nginx.conf /usr/local/nginx/conf/nginx.conf.backup
```

### 3. 测试配置文件语法

```bash
sudo /usr/local/nginx/sbin/nginx -t
```

**预期输出：**
```
nginx: the configuration file /usr/local/nginx/conf/nginx.conf syntax is ok
nginx: configuration file /usr/local/nginx/conf/nginx.conf test is successful
```

### 4. 重启 Nginx

```bash
# 方法 1：重启
sudo /usr/local/nginx/sbin/nginx -s reload

# 方法 2：如果上面不行，使用强制重启
sudo /usr/local/nginx/sbin/nginx -s stop
sudo /usr/local/nginx/sbin/nginx
```

### 5. 验证修改是否生效

#### 5.1 检查 Nginx 进程
```bash
ps aux | grep nginx
```

#### 5.2 测试 API 代理
```bash
# 在服务器上测试
curl -I https://c-jeans.com/api/products?page=1&limit=10
```

应该返回 200 状态码和数据。

#### 5.3 浏览器测试
1. 打开 `https://c-jeans.com`
2. 按 F12 打开开发者工具
3. 切换到 Network 标签
4. 刷新页面
5. 检查所有 `/api/` 请求是否返回数据（不再是 0 B）

---

## 🔍 故障排查

### 如果修改后仍有问题

#### 1. 检查 Nginx 错误日志
```bash
sudo tail -f /usr/local/nginx/logs/error.log
```

#### 2. 检查后端服务是否运行
```bash
curl http://39.97.60.191:33380/api/products?page=1&limit=10
```

#### 3. 检查防火墙
```bash
# 确保 33380 端口开放
sudo firewall-cmd --list-ports
```

#### 4. 回滚到备份配置
```bash
sudo cp /usr/local/nginx/conf/nginx.conf.backup /usr/local/nginx/conf/nginx.conf
sudo /usr/local/nginx/sbin/nginx -s reload
```

---

## 📞 技术支持

如果遇到问题，请提供：
1. Nginx 错误日志
2. 浏览器控制台错误截图
3. Network 标签中失败请求的详细信息
4. 后端服务器日志

---

## ✅ 修改完成检查清单

- [x] 修改 HTTP server 块的 proxy_pass
- [x] 修改 HTTPS server 块的 proxy_pass
- [x] 添加超时配置（HTTP 和 HTTPS）
- [x] 添加 WebSocket 支持（HTTP 和 HTTPS）
- [x] 禁用代理缓冲（HTTP 和 HTTPS）
- [x] 优化 CORS 配置（HTTP 和 HTTPS）
- [x] 添加 HTTP 到 HTTPS 自动跳转
- [ ] 上传配置到服务器
- [ ] 测试配置语法
- [ ] 重启 Nginx
- [ ] 验证网站正常运行

---

**修改完成时间：** 2025年12月9日 21:05
**修改人：** Cascade AI Assistant
**状态：** ✅ 本地修改完成，等待部署到服务器
