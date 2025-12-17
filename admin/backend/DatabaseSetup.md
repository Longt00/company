# CompanyContact 数据库设置指南

## 方法一：直接使用MySQL命令

### 1. 连接数据库
```bash
mysql -h 39.97.60.191 -P 33306 -u root -p123456 manage_system
```

### 2. 执行插入脚本
```bash
mysql -h 39.97.60.191 -P 33306 -u root -p123456 manage_system < insert-base-contact.sql
```

### 3. 或者在MySQL客户端中直接执行
```sql
USE manage_system;

INSERT INTO company_contact (
    address_cn,
    address_en,
    phone,
    fax,
    email,
    website_url,
    contact_person_cn,
    contact_person_en,
    wechat,
    whatsapp,
    qq,
    status,
    created_by,
    updated_by,
    create_time,
    update_time
) VALUES (
    '广东省深圳市南山区科技园南区深圳湾科技生态园',
    'Nanshan Science and Technology Park, Southern District, Shenzhen Bay Technology & Ecology Park, Shenzhen',
    '+86-0755-2660-8888',
    '+86-0755-2660-8889',
    'contact@company.com',
    'https://www.company.com',
    '王经理',
    'Manager Wang',
    'company_wechat_official',
    '+8613800138000',
    '123456789',
    1,
    1,
    1,
    NOW(),
    NOW()
);
```

## 方法二：使用应用自动初始化

我已经创建了 `DataInitializer.java` 类，应用启动时会自动检查并插入基础数据。

### 1. 编译项目
```bash
mvn clean compile
```

### 2. 启动应用（确保数据库连接正常）
```bash
mvn spring-boot:run
```

应用启动时会自动：
- 检查 `company_contact` 表是否存在记录
- 如果没有记录，自动插入基础数据
- 生成WhatsApp链接并记录日志

## 方法三：使用REST API插入（需要先启动应用）

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 获取管理员Token
```bash
curl -X POST http://localhost:33380/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "your_password"
  }'
```

### 3. 使用Token插入数据
```bash
curl -X PUT http://localhost:33380/api/company/admin/contact/info \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "addressCn": "广东省深圳市南山区科技园南区深圳湾科技生态园",
    "addressEn": "Nanshan Science and Technology Park, Southern District, Shenzhen Bay Technology & Ecology Park, Shenzhen",
    "phone": "+86-0755-2660-8888",
    "fax": "+86-0755-2660-8889",
    "email": "contact@company.com",
    "websiteUrl": "https://www.company.com",
    "contactPersonCn": "王经理",
    "contactPersonEn": "Manager Wang",
    "wechat": "company_wechat_official",
    "whatsapp": "+8613800138000",
    "qq": "123456789",
    "status": 1
  }'
```

## 验证数据插入

### 1. 查询数据库
```sql
SELECT * FROM company_contact ORDER BY id DESC LIMIT 1;
```

### 2. 测试WhatsApp接口
```bash
# 获取WhatsApp号码
curl http://localhost:33380/api/company/public/contact/whatsapp

# 获取WhatsApp聊天链接
curl http://localhost:33380/api/company/public/contact/whatsapp/link

# 获取完整联系方式
curl http://localhost:33380/api/company/public/contact
```

## 预期结果

插入的基础数据包含：

| 字段 | 值 |
|------|-----|
| WhatsApp号码 | `+8613800138000` |
| WhatsApp链接 | `https://wa.me/8613800138000` |
| 电话 | `+86-0755-2660-8888` |
| 邮箱 | `contact@company.com` |
| 状态 | `1` (启用) |

## 故障排除

### 1. 数据库连接问题
- 检查数据库服务是否运行
- 验证连接参数：`39.97.60.191:33306`
- 确认用户名密码：`root/123456`

### 2. 表不存在
```sql
CREATE TABLE company_contact (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    address_cn VARCHAR(500),
    whatsapp VARCHAR(50),
    -- 其他字段...
    status INT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### 3. 应用启动失败
- 检查数据库连接池配置
- 确认没有其他应用占用连接
- 查看应用日志获取详细错误信息

## 推荐方法

**推荐使用方法一（直接MySQL命令）**，因为：
1. 最简单直接
2. 不依赖应用启动
3. 可以立即验证数据插入
4. 适用于生产环境和开发环境