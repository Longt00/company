# 重新打包部署说明

## 问题原因

当前 `dist/index.html` 中的资源路径是 `/frontend/assets/...`，但网站部署在根路径 `https://c-jeans.com/`，导致资源加载失败。

## 已修改

✅ `vite.config.js` - 将 `base: '/frontend/'` 改为 `base: '/'`

## 立即执行的步骤

### 1. 重新打包前端代码

```bash
# 在项目根目录执行
npm run build
```

打包完成后，`dist` 目录中的 `index.html` 应该变成：

```html
<script type="module" crossorigin src="/assets/index-xxx.js"></script>
<link rel="stylesheet" href="/assets/index-xxx.css">
```

注意：路径从 `/frontend/assets/` 变成了 `/assets/`

### 2. 上传新的 dist 目录到服务器

```bash
# 方法 1：使用 SCP 上传整个 dist 目录
scp -r dist/* user@your-server:/usr/local/nginx/html/frontend/dist/

# 方法 2：使用 SFTP 工具上传
# 上传到服务器的 /usr/local/nginx/html/frontend/dist/ 目录
```

### 3. 清除浏览器缓存

上传完成后：
1. 打开浏览器
2. 按 `Ctrl + Shift + Delete`（Windows）或 `Cmd + Shift + Delete`（Mac）
3. 清除缓存和 Cookie
4. 或者使用无痕模式访问

### 4. 验证

访问 `https://c-jeans.com/`，检查：
- ✅ 页面能正常显示
- ✅ 控制台没有 404 错误
- ✅ Network 标签中所有资源都能加载（200 状态）
- ✅ API 请求能正常返回数据

## 预期结果

### 打包前（错误）
```html
<!-- dist/index.html -->
<script src="/frontend/assets/index-xxx.js"></script>
```
访问：`https://c-jeans.com/frontend/assets/index-xxx.js` ❌ 404

### 打包后（正确）
```html
<!-- dist/index.html -->
<script src="/assets/index-xxx.js"></script>
```
访问：`https://c-jeans.com/assets/index-xxx.js` ✅ 200

## 如果仍有问题

### 检查 1：确认 Nginx 配置中的根路径

```nginx
# HTTPS server 块中应该有：
location / {
    alias  html/frontend/dist/;
    index  index.html;
    try_files $uri $uri/ /index.html;
}
```

### 检查 2：确认 dist 目录权限

```bash
# SSH 登录服务器后
sudo chmod -R 755 /usr/local/nginx/html/frontend/dist/
sudo chown -R nginx:nginx /usr/local/nginx/html/frontend/dist/
```

### 检查 3：查看 Nginx 错误日志

```bash
sudo tail -f /usr/local/nginx/logs/error.log
```

## 完整部署流程总结

1. ✅ 修改 `vite.config.js` 的 `base` 为 `/`
2. ⏳ 执行 `npm run build` 重新打包
3. ⏳ 上传 `dist` 目录到服务器
4. ⏳ 清除浏览器缓存
5. ⏳ 访问 `https://c-jeans.com/` 验证

---

**当前状态：** 已完成步骤 1，等待执行步骤 2-5
