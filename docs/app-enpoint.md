## API Endpoints chính
### 4.1 Authentication APIs
```  
   POST   /api/v1/auth/login           # Đăng nhập
   POST   /api/v1/auth/refresh         # Refresh token
   POST   /api/v1/auth/logout          # Đăng xuất
   POST   /api/v1/auth/validate        # Validate token
```
### 4.2 System Management APIs
```  
   POST   /api/v1/systems/register     # Đăng ký hệ thống mới
   GET    /api/v1/systems/{code}       # Lấy thông tin hệ thống
   PUT    /api/v1/systems/{code}       # Cập nhật hệ thống
   GET    /api/v1/systems/{code}/users # Lấy users của hệ thống
```
### 4.3 Permission APIs
```
   GET    /api/v1/permissions/check    # Kiểm tra quyền
   GET    /api/v1/permissions/user/{username}/system/{systemCode}
   POST   /api/v1/permissions/assign   # Gán quyền cho role

```
### 4.4 User Management APIs (Admin)
```  
   GET    /api/v1/admin/users          # Danh sách users
   POST   /api/v1/admin/users          # Tạo user mới
   PUT    /api/v1/admin/users/{id}     # Cập nhật user
   POST   /api/v1/admin/users/{id}/assign-system # Gán user vào hệ thống
```