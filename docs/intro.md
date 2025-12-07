## Luồng xác thực cơ bản
1. Ứng dụng con redirect user đến UniAuth Login Page
2. User đăng nhập tại UniAuth
3. UniAuth xác thực và tạo JWT token với claims:
    - username
    - userId
    - systemCode
    - permissions[]
4. Redirect user về ứng dụng con với token
5. Ứng dụng con validate token với UniAuth
6. Ứng dụng con sử dụng permissions trong token để authorize

## Các tính năng mở rộng có thể thêm

1. Single Sign-On (SSO)
2. Multi-factor Authentication
3. Audit Logging
4. API Rate Limiting
5. Webhook cho các sự kiện (user created, role changed)
6. Dashboard và báo cáo
7. Tích hợp với LDAP/Active Directory
8. Social Login (Google, Facebook)