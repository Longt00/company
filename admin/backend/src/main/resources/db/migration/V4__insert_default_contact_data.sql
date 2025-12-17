-- 插入默认公司联系方式数据（如果表为空）
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
) SELECT
    '广东省深圳市南山区科技园',
    'Nanshan Science and Technology Park, Shenzhen, Guangdong Province',
    '+86-0755-88888888',
    '+86-0755-88888889',
    'info@company.com',
    'https://www.company.com',
    '李经理',
    'Manager Li',
    'company_wechat',
    '+8613800138000',
    '987654321',
    1,
    1,
    1,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM company_contact
);