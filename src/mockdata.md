Cách sử dụng MockDataService:
1. Tạo dữ liệu demo cơ bản:
   bash
   POST /api/v1/mock-data/generate
   Response:

json
{
"success": true,
"message": "Test data generated successfully",
"systemCode": "DEMO",
"apiKey": "UA-DEMO-ABC123XY",
"usersCount": 6,
"rolesCount": 4,
"permissionsCount": 30,
"demoCredentials": {
"admin": "Password123!",
"manager": "Password123!",
"john.doe": "Password123!",
"jane.smith": "Password123!",
"demo.user": "Password123!"
}
}
2. Tạo hệ thống mới với dữ liệu mẫu:
   bash
   POST /api/v1/mock-data/generate-system?systemCode=HRM&systemName=Human%20Resources
3. Tạo dataset lớn cho testing:
   bash
   POST /api/v1/mock-data/generate-large?users=5000&roles=100&permissions=500
4. Xem thống kê:
   bash
   GET /api/v1/mock-data/statistics
5. Lấy credentials mẫu:
   bash
   GET /api/v1/mock-data/credentials
6. Xóa dữ liệu test:
   bash
   DELETE /api/v1/mock-data/cleanup
7. Reset demo data:
   bash
   POST /api/v1/mock-data/reset-demo