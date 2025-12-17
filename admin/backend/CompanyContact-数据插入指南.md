# CompanyContact 数据插入完整解决方案

## 🚨 当前状态
数据库连接数过多，无法直接连接。但您有多种解决方案可以选择。

## 📋 插入的基础数据内容

我们将插入一条包含以下信息的CompanyContact记录：

| 字段 | 值 | 说明 |
|------|-----|------|
| **WhatsApp** | `+8613800138000` | WhatsApp联系方式 ⭐ |
| **电话** | `+86-0755-2660-8888` | 公司电话 |
| **邮箱** | `contact@company.com` | 公司邮箱 |
| **地址(中文)** | 广东省深圳市南山区科技园南区深圳湾科技生态园 | 中文地址 |
| **地址(英文)** | Nanshan Science and Technology Park, Southern District, Shenzhen Bay Technology & Ecology Park, Shenzhen | 英文地址 |
| **联系人** | 王经理 / Manager Wang | 联系人信息 |
| **微信** | company_wechat_official | 微信公众号 |
| **QQ** | 123456789 | QQ号码 |
| **官网** | https://www.company.com | 公司网站 |
| **状态** | 1 (启用) | 启用状态 |

## 🛠️ 解决方案

### 方案一：等待数据库连接恢复后直接执行 ⭐推荐

1. **等待几分钟后重试**（连接数会自动释放）
2. **执行SQL命令**：
```bash
mysql -h 39.97.60.191 -P 33306 -u root -p123456 manage_system < insert-base-contact.sql
```

或者使用脚本文件中的单独SQL语句。

### 方案二：重启应用服务自动初始化

由于我已经创建了 `DataInitializer.java`，当应用成功启动时会自动检查并插入数据：

1. **停止当前的Java进程**：
```bash
taskkill /F /IM java.exe
```

2. **等待连接释放**（2-3分钟）

3. **重新启动应用**：
```bash
mvn spring-boot:run
```

4. **查看启动日志**，应该会看到：
```
开始初始化CompanyContact基础数据...
CompanyContact基础数据初始化成功，ID: 1, WhatsApp: +8613800138000
生成的WhatsApp聊天链接: https://wa.me/8613800138000
```

### 方案三：通过数据库管理工具

如果您有数据库管理工具（如Navicat、phpMyAdmin、DBeaver等）：

1. **连接数据库**：
   - 主机：`39.97.60.191`
   - 端口：`33306`
   - 用户名：`root`
   - 密码：`123456`
   - 数据库：`manage_system`

2. **执行SQL**：
```sql
INSERT INTO company_contact (
    address_cn, address_en, phone, fax, email, website_url,
    contact_person_cn, contact_person_en, wechat, whatsapp, qq,
    status, created_by, updated_by, create_time, update_time
) VALUES (
    '广东省深圳市南山区科技园南区深圳湾科技生态园',
    'Nanshan Science and Technology Park, Southern District, Shenzhen Bay Technology & Ecology Park, Shenzhen',
    '+86-0755-2660-8888', '+86-0755-2660-8889', 'contact@company.com', 'https://www.company.com',
    '王经理', 'Manager Wang', 'company_wechat_official', '+8613800138000', '123456789',
    1, 1, 1, NOW(), NOW()
);
```

### 方案四：联系数据库管理员

如果以上方法都不行，可以：
1. 联系数据库管理员重启MySQL服务
2. 或者增加最大连接数配置
3. 然后执行上述SQL语句

## ✅ 验证数据插入成功

插入完成后，可以通过以下方式验证：

### 1. 数据库查询验证
```sql
SELECT id, whatsapp, phone, email, status FROM company_contact ORDER BY id DESC LIMIT 1;
```

### 2. API接口验证（应用启动后）
```bash
# 测试WhatsApp公开接口
curl http://localhost:33380/api/company/public/contact/whatsapp

# 测试WhatsApp链接生成
curl http://localhost:33380/api/company/public/contact/whatsapp/link

# 预期返回：
# {"code":200,"message":"操作成功","data":"+8613800138000"}
# {"code":200,"message":"操作成功","data":"https://wa.me/8613800138000"}
```

### 3. 前端功能验证
- 前端管理后台应该能看到WhatsApp字段
- 前台页面应该能点击WhatsApp按钮跳转到聊天页面

## 🎯 最终效果

数据插入成功后，您的系统将具备：

1. **WhatsApp管理功能**：
   - 管理后台可以修改WhatsApp号码
   - 数据库实时存储更新

2. **WhatsApp展示功能**：
   - 前台获取WhatsApp聊天链接
   - 点击直接跳转到WhatsApp聊天

3. **智能链接生成**：
   - 自动处理国际号码格式
   - 生成标准`https://wa.me/`链接

## 📝 相关文件

我已经创建了以下支持文件：

1. **`insert-base-contact.sql`** - SQL插入脚本
2. **`DataInitializer.java`** - 自动初始化类
3. **`CompanyContactController.java`** - 管理接口
4. **`DatabaseSetup.md`** - 详细设置指南

## 🆘 故障排除

如果遇到问题：

1. **连接超时**：等待2-3分钟后重试
2. **表不存在**：先运行`V2__create_home_page_background_table.sql`等相关迁移脚本
3. **权限问题**：确认数据库用户有INSERT权限

## 🎉 完成

一旦数据插入成功，您的WhatsApp接口功能就完全可用了！