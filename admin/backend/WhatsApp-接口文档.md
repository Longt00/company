# WhatsApp 接口功能文档

## 概述

本文档描述了系统中WhatsApp相关的接口功能，包括数据库存储、管理后台更新和前端链接跳转功能。

## 数据库结构

### CompanyContact 表中的 WhatsApp 字段

```sql
-- WhatsApp账号字段（已存在）
@Column(name = "whatsapp", length = 50)
private String whatsapp;
```

**字段说明：**
- 字段名：`whatsapp`
- 数据类型：VARCHAR(50)
- 描述：存储WhatsApp联系方式，支持国际格式

## 后端接口

### 1. 管理后台接口

#### 1.1 获取联系方式信息
```
GET /api/company/admin/contact/info
```
**功能：** 获取完整的联系方式信息（包含WhatsApp）
**权限：** 需要管理员认证

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "addressCn": "广东省深圳市南山区科技园",
    "addressEn": "Nanshan Science and Technology Park, Shenzhen, Guangdong Province",
    "phone": "+86-0755-88888888",
    "fax": "+86-0755-88888889",
    "email": "info@company.com",
    "websiteUrl": "https://www.company.com",
    "contactPersonCn": "李经理",
    "contactPersonEn": "Manager Li",
    "wechat": "company_wechat",
    "whatsapp": "+8613800138000",
    "qq": "987654321",
    "status": 1,
    "createTime": "2025-11-29T22:00:00",
    "updateTime": "2025-11-29T22:00:00"
  }
}
```

#### 1.2 更新联系方式信息
```
PUT /api/company/admin/contact/info
```
**功能：** 更新联系方式信息（包含WhatsApp字段）
**权限：** 需要管理员认证

**请求体：**
```json
{
  "addressCn": "广东省深圳市南山区科技园",
  "addressEn": "Nanshan Science and Technology Park, Shenzhen, Guangdong Province",
  "phone": "+86-0755-88888888",
  "fax": "+86-0755-88888889",
  "email": "info@company.com",
  "websiteUrl": "https://www.company.com",
  "contactPersonCn": "李经理",
  "contactPersonEn": "Manager Li",
  "wechat": "company_wechat",
  "whatsapp": "+8613800138000",
  "qq": "987654321",
  "status": 1
}
```

#### 1.3 获取WhatsApp信息（用于前端跳转）
```
GET /api/company/admin/contact/whatsapp
```
**功能：** 获取WhatsApp号码和聊天链接
**权限：** 需要管理员认证

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "whatsapp": "+8613800138000",
    "whatsappUrl": "https://wa.me/8613800138000"
  }
}
```

### 2. 公开接口（无需认证）

#### 2.1 获取WhatsApp联系方式
```
GET /api/company/public/contact/whatsapp
```
**功能：** 获取WhatsApp号码
**权限：** 无需认证

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "+8613800138000"
}
```

#### 2.2 获取WhatsApp聊天链接
```
GET /api/company/public/contact/whatsapp/link
```
**功能：** 获取WhatsApp聊天链接
**权限：** 无需认证

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "https://wa.me/8613800138000"
}
```

#### 2.3 获取完整联系方式
```
GET /api/company/public/contact
```
**功能：** 获取完整联系方式信息
**权限：** 无需认证

## 前端集成说明

### 1. 管理后台集成

**前端管理页面需要实现以下功能：**

1. **联系方式管理表单**
   - 包含WhatsApp输入字段
   - 支持国际电话格式（如 +86、+1 等）
   - 表单验证：只允许数字、+、-、空格

2. **WhatsApp字段验证**
```javascript
// 前端验证示例
function validateWhatsApp(phone) {
  // 支持国际格式：+86-13800138000, +1 555-123-4567
  const whatsappRegex = /^\+?[1-9]\d{1,3}[-\s]?\d{1,4}[-\s]?\d{1,4}[-\s]?\d{1,9}$/;
  return whatsappRegex.test(phone);
}
```

3. **API调用示例**
```javascript
// 更新联系方式
async function updateContactInfo(contactData) {
  const response = await fetch('/api/company/admin/contact/info', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + token
    },
    body: JSON.stringify(contactData)
  });
  return await response.json();
}
```

### 2. 前台展示集成

**前台页面需要实现WhatsApp聊天功能：**

1. **获取WhatsApp链接**
```javascript
async function getWhatsAppLink() {
  const response = await fetch('/api/company/public/contact/whatsapp/link');
  const result = await response.json();
  return result.data; // 返回: "https://wa.me/8613800138000"
}
```

2. **WhatsApp聊天按钮**
```html
<button onclick="openWhatsAppChat()">
  <i class="fab fa-whatsapp"></i> WhatsApp客服
</button>

<script>
async function openWhatsAppChat() {
  const whatsappUrl = await getWhatsAppLink();
  if (whatsappUrl) {
    window.open(whatsappUrl, '_blank');
  } else {
    alert('WhatsApp联系方式未设置');
  }
}
</script>
```

3. **WhatsApp链接处理逻辑**
- 系统会自动清理号码中的非数字字符
- 生成标准的 `https://wa.me/` 链接格式
- 支持点击后直接跳转到WhatsApp聊天界面

## 号码格式处理

### 1. 存储格式
- 支持多种输入格式：`+86-13800138000`、`+86 13800138000`、`8613800138000`
- 系统会自动清理非数字字符生成WhatsApp链接

### 2. 链接生成规则
```java
// Java后端处理逻辑示例
String cleanNumber = whatsappNumber.replaceAll("[^0-9]", "");
String whatsappUrl = "https://wa.me/" + cleanNumber;
```

### 3. 支持的号码格式
- **中国：** +86-13800138000 → https://wa.me/8613800138000
- **美国：** +1-555-123-4567 → https://wa.me/15551234567
- **英国：** +44-20-1234-5678 → https://wa.me/442012345678

## 数据库初始化

系统提供了默认数据初始化脚本：
```sql
-- V4__insert_default_contact_data.sql
INSERT INTO company_contact (
    -- 默认数据包含WhatsApp字段
    whatsapp,
    -- 其他字段...
) VALUES (
    '+8613800138000',
    -- 其他值...
);
```



## 注意事项

1. **安全性：** 管理接口需要JWT认证
2. **数据验证：** WhatsApp字段有长度限制（50字符）
3. **错误处理：** 所有接口都有完整的错误处理机制
4. **日志记录：** 管理操作会记录到操作日志表

4. 