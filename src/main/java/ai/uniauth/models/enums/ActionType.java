package ai.uniauth.models.enums;

/**
 * Loại hành động được ghi lại trong audit log
 */
public enum ActionType {
    // Authentication Actions
    LOGIN("Đăng nhập"),
    LOGOUT("Đăng xuất"),
    REGISTER("Đăng ký"),
    VERIFY_EMAIL("Xác minh email"),
    VERIFY_PHONE("Xác minh số điện thoại"),
    REQUEST_PASSWORD_RESET("Yêu cầu đặt lại mật khẩu"),
    RESET_PASSWORD("Đặt lại mật khẩu"),
    CHANGE_PASSWORD("Thay đổi mật khẩu"),
    ENABLE_MFA("Bật xác thực 2 yếu tố"),
    DISABLE_MFA("Tắt xác thực 2 yếu tố"),
    VERIFY_MFA("Xác minh mã 2 yếu tố"),
    REFRESH_TOKEN("Làm mới token"),
    REVOKE_TOKEN("Thu hồi token"),

    // User Management Actions - CẬP NHẬT THÊM
    CREATE_USER("Tạo người dùng"),
    UPDATE_USER("Cập nhật người dùng"),
    DELETE_USER("Xóa người dùng"),
    ACTIVATE_USER("Kích hoạt người dùng"),
    DEACTIVATE_USER("Vô hiệu hóa người dùng"),
    LOCK_USER("Khóa người dùng"),
    UNLOCK_USER("Mở khóa người dùng"),
    SUSPEND_USER("Tạm ngưng người dùng"),
    UNSUSPEND_USER("Gỡ tạm ngưng người dùng"),
    ARCHIVE_USER("Lưu trữ người dùng"),
    RESTORE_USER("Khôi phục người dùng"),
    IMPORT_USERS("Nhập người dùng"),
    EXPORT_USERS("Xuất người dùng"),

    // BỔ SUNG CÁC ACTION CÒN THIẾU CHO UserStatus
    APPROVE("Phê duyệt"),
    REJECT("Từ chối"),
    VERIFY("Xác minh"),
    REACTIVATE("Kích hoạt lại"),

    // Role & Permission Actions
    CREATE_ROLE("Tạo vai trò"),
    UPDATE_ROLE("Cập nhật vai trò"),
    DELETE_ROLE("Xóa vai trò"),
    ASSIGN_ROLE("Gán vai trò"),
    REVOKE_ROLE("Thu hồi vai trò"),
    CREATE_PERMISSION("Tạo quyền hạn"),
    UPDATE_PERMISSION("Cập nhật quyền hạn"),
    DELETE_PERMISSION("Xóa quyền hạn"),
    GRANT_PERMISSION("Cấp quyền hạn"),
    REVOKE_PERMISSION("Thu hồi quyền hạn"),

    // System Integration Actions
    REGISTER_SYSTEM("Đăng ký hệ thống"),
    UPDATE_SYSTEM("Cập nhật hệ thống"),
    DELETE_SYSTEM("Xóa hệ thống"),
    ENABLE_SYSTEM("Bật hệ thống"),
    DISABLE_SYSTEM("Tắt hệ thống"),
    TEST_CONNECTION("Kiểm tra kết nối"),
    SYNC_DATA("Đồng bộ dữ liệu"),

    // API Management Actions
    CREATE_API_KEY("Tạo API Key"),
    UPDATE_API_KEY("Cập nhật API Key"),
    DELETE_API_KEY("Xóa API Key"),
    ROTATE_API_KEY("Xoay API Key"),
    REVOKE_API_KEY("Thu hồi API Key"),
    VALIDATE_API_KEY("Xác thực API Key"),
    UPDATE_RATE_LIMIT("Cập nhật giới hạn tốc độ"),

    // Audit & Monitoring Actions
    VIEW_AUDIT_LOG("Xem nhật ký kiểm toán"),
    EXPORT_AUDIT_LOG("Xuất nhật ký kiểm toán"),
    CLEAR_AUDIT_LOG("Xóa nhật ký kiểm toán"),
    CREATE_ALERT("Tạo cảnh báo"),
    UPDATE_ALERT("Cập nhật cảnh báo"),
    DELETE_ALERT("Xóa cảnh báo"),
    ACKNOWLEDGE_ALERT("Xác nhận cảnh báo"),

    // Configuration Actions
    UPDATE_CONFIG("Cập nhật cấu hình"),
    RESET_CONFIG("Đặt lại cấu hình"),
    BACKUP_CONFIG("Sao lưu cấu hình"),
    RESTORE_CONFIG("Khôi phục cấu hình"),

    // Workflow & Approval Actions
    CREATE_REQUEST("Tạo yêu cầu"),
    SUBMIT_REQUEST("Gửi yêu cầu"),
    APPROVE_REQUEST("Phê duyệt yêu cầu"),
    REJECT_REQUEST("Từ chối yêu cầu"),
    CANCEL_REQUEST("Hủy yêu cầu"),
    ESCALATE_REQUEST("Chuyển yêu cầu lên cấp cao hơn"),
    DELEGATE_REQUEST("Ủy quyền yêu cầu"),
    COMPLETE_TASK("Hoàn thành công việc"),
    REOPEN_TASK("Mở lại công việc"),

    // Reporting Actions
    GENERATE_REPORT("Tạo báo cáo"),
    SCHEDULE_REPORT("Lập lịch báo cáo"),
    DOWNLOAD_REPORT("Tải báo cáo"),
    SHARE_REPORT("Chia sẻ báo cáo"),

    // Notification Actions
    SEND_NOTIFICATION("Gửi thông báo"),
    READ_NOTIFICATION("Đọc thông báo"),
    DELETE_NOTIFICATION("Xóa thông báo"),
    ARCHIVE_NOTIFICATION("Lưu trữ thông báo"),
    UPDATE_PREFERENCES("Cập nhật tùy chọn thông báo"),

    // System Operations
    START_SERVICE("Khởi động dịch vụ"),
    STOP_SERVICE("Dừng dịch vụ"),
    RESTART_SERVICE("Khởi động lại dịch vụ"),
    BACKUP_SYSTEM("Sao lưu hệ thống"),
    RESTORE_SYSTEM("Khôi phục hệ thống"),
    CLEANUP_DATA("Dọn dẹp dữ liệu"),
    RUN_MAINTENANCE("Chạy bảo trì"),

    // Security Actions
    SCAN_VULNERABILITIES("Quét lỗ hổng bảo mật"),
    PATCH_SYSTEM("Cập nhật bản vá"),
    BLOCK_IP("Chặn IP"),
    UNBLOCK_IP("Bỏ chặn IP"),
    QUARANTINE_USER("Cách ly người dùng"),
    RELEASE_QUARANTINE("Giải tỏa cách ly"),

    // Data Management
    EXPORT_DATA("Xuất dữ liệu"),
    IMPORT_DATA("Nhập dữ liệu"),
    ANONYMIZE_DATA("Ẩn danh dữ liệu"),
    DELETE_DATA("Xóa dữ liệu"),
    PURGE_DATA("Xóa vĩnh viễn dữ liệu"),

    // Administrative Actions
    GRANT_ADMIN("Cấp quyền quản trị"),
    REVOKE_ADMIN("Thu hồi quyền quản trị"),
    VIEW_ADMIN_LOG("Xem nhật ký quản trị"),
    OVERRIDE("Ghi đè"),
    BYPASS("Bỏ qua"),
    EXCEPTION("Ngoại lệ"),

    // Bổ sung thêm các action cho các trường hợp đặc biệt
    FORCE_PASSWORD_RESET("Buộc đặt lại mật khẩu"),
    SEND_INVITATION("Gửi lời mời"),
    CANCEL_INVITATION("Hủy lời mời"),
    EXTEND_EXPIRATION("Gia hạn thời hạn"),
    TERMINATE_SESSION("Chấm dứt phiên"),
    IMPERSONATE("Mạo danh"),
    STOP_IMPERSONATION("Dừng mạo danh"),
    UPDATE_PROFILE("Cập nhật hồ sơ"),
    UPLOAD_AVATAR("Tải lên ảnh đại diện"),
    UPDATE_SETTINGS("Cập nhật cài đặt"),
    RESEND_VERIFICATION("Gửi lại xác minh"),

    // Miscellaneous
    SEARCH("Tìm kiếm"),
    FILTER("Lọc"),
    SORT("Sắp xếp"),
    DOWNLOAD("Tải xuống"),
    UPLOAD("Tải lên"),
    COPY("Sao chép"),
    PASTE("Dán"),
    PRINT("In"),
    SHARE("Chia sẻ"),
    COMMENT("Bình luận"),
    RATE("Đánh giá"),
    SUBSCRIBE("Đăng ký"),
    UNSUBSCRIBE("Hủy đăng ký"),
    VIEW("Xem"),
    EDIT("Chỉnh sửa"),
    CLONE("Nhân bản"),
    MERGE("Gộp"),
    SPLIT("Tách"),
    VALIDATE("Xác thực"),
    TEST("Kiểm thử"),
    DEBUG("Gỡ lỗi"),
    OPTIMIZE("Tối ưu hóa");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }

    public static ActionType fromString(String value) {
        for (ActionType type : ActionType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ActionType: " + value);
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        if (name().endsWith("_USER") || name().contains("USER_")) return "USER_MANAGEMENT";
        if (name().endsWith("_ROLE") || name().contains("ROLE_")) return "ROLE_MANAGEMENT";
        if (name().endsWith("_PERMISSION") || name().contains("PERMISSION_")) return "PERMISSION_MANAGEMENT";
        if (name().endsWith("_SYSTEM") || name().contains("SYSTEM_")) return "SYSTEM_MANAGEMENT";
        if (name().endsWith("_API") || name().contains("API_")) return "API_MANAGEMENT";
        if (name().endsWith("_AUDIT") || name().contains("AUDIT_")) return "AUDITING";
        if (name().endsWith("_CONFIG") || name().contains("CONFIG_")) return "CONFIGURATION";
        if (name().endsWith("_REQUEST") || name().contains("REQUEST_")) return "WORKFLOW";
        if (name().endsWith("_REPORT") || name().contains("REPORT_")) return "REPORTING";
        if (name().endsWith("_NOTIFICATION") || name().contains("NOTIFICATION_")) return "NOTIFICATION";
        if (name().endsWith("_SECURITY") || name().contains("SECURITY_")) return "SECURITY";
        if (name().endsWith("_DATA") || name().contains("DATA_")) return "DATA_MANAGEMENT";
        if (name().endsWith("_ADMIN") || name().contains("ADMIN_")) return "ADMINISTRATION";
        if (name().contains("LOGIN") || name().contains("LOGOUT") || name().contains("PASSWORD"))
            return "AUTHENTICATION";
        if (name().contains("VERIFY") || name().contains("MFA"))
            return "VERIFICATION";
        return "GENERAL";
    }

    public RiskLevel getRiskLevel() {
        switch (this) {
            case DELETE_USER:
            case DELETE_ROLE:
            case DELETE_PERMISSION:
            case DELETE_SYSTEM:
            case REVOKE_ADMIN:
            case PURGE_DATA:
            case OVERRIDE:
            case BYPASS:
            case IMPERSONATE:
                return RiskLevel.CRITICAL;

            case CREATE_USER:
            case UPDATE_USER:
            case LOCK_USER:
            case UNLOCK_USER:
            case CREATE_ROLE:
            case UPDATE_ROLE:
            case ASSIGN_ROLE:
            case REVOKE_ROLE:
            case GRANT_PERMISSION:
            case REVOKE_PERMISSION:
            case REGISTER_SYSTEM:
            case CREATE_API_KEY:
            case REVOKE_API_KEY:
            case BLOCK_IP:
            case UNBLOCK_IP:
            case FORCE_PASSWORD_RESET:
            case TERMINATE_SESSION:
                return RiskLevel.HIGH;

            case CHANGE_PASSWORD:
            case RESET_PASSWORD:
            case ENABLE_MFA:
            case DISABLE_MFA:
            case UPDATE_SYSTEM:
            case ENABLE_SYSTEM:
            case DISABLE_SYSTEM:
            case ROTATE_API_KEY:
            case UPDATE_CONFIG:
            case APPROVE_REQUEST:
            case REJECT_REQUEST:
            case APPROVE:           // Thêm cho UserStatus
            case REJECT:            // Thêm cho UserStatus
            case VERIFY:            // Thêm cho UserStatus
            case REACTIVATE:        // Thêm cho UserStatus
            case QUARANTINE_USER:
            case RELEASE_QUARANTINE:
                return RiskLevel.MEDIUM;

            default:
                return RiskLevel.LOW;
        }
    }

    public boolean requiresApproval() {
        return getRiskLevel().requiresApproval();
    }

    public boolean requiresAuditLog() {
        return true; // Tất cả actions đều cần audit log
    }

    /**
     * Kiểm tra action có thể áp dụng cho UserStatus không
     */
    public boolean isApplicableToUserStatus(UserStatus currentStatus) {
        switch (this) {
            case APPROVE:
                return currentStatus == UserStatus.PENDING ||
                        currentStatus == UserStatus.INVITED;
            case REJECT:
                return currentStatus == UserStatus.PENDING ||
                        currentStatus == UserStatus.INVITED;
            case VERIFY:
                return currentStatus == UserStatus.VERIFICATION_PENDING;
            case ACTIVATE_USER:
                return currentStatus == UserStatus.INACTIVE ||
                        currentStatus == UserStatus.SUSPENDED;
            case DEACTIVATE_USER:
                return currentStatus == UserStatus.ACTIVE;
            case LOCK_USER:
                return currentStatus == UserStatus.ACTIVE ||
                        currentStatus == UserStatus.PASSWORD_RESET_REQUIRED;
            case UNLOCK_USER:
                return currentStatus == UserStatus.LOCKED;
            case SUSPEND_USER:
                return currentStatus == UserStatus.ACTIVE;
            case UNSUSPEND_USER:
                return currentStatus == UserStatus.SUSPENDED;
            case REACTIVATE:
                return currentStatus == UserStatus.SUSPENDED ||
                        currentStatus == UserStatus.EXPIRED;
            case ARCHIVE_USER:
                return currentStatus != UserStatus.DELETED &&
                        currentStatus != UserStatus.ARCHIVED;
            case RESTORE_USER:
                return currentStatus == UserStatus.ARCHIVED ||
                        currentStatus == UserStatus.DELETED;
            case FORCE_PASSWORD_RESET:
                return currentStatus == UserStatus.ACTIVE ||
                        currentStatus == UserStatus.PASSWORD_RESET_REQUIRED;
            case RESET_PASSWORD:
                return currentStatus == UserStatus.PASSWORD_RESET_REQUIRED;
            default:
                return false;
        }
    }

    /**
     * Lấy trạng thái UserStatus mới sau khi áp dụng action
     */
    public UserStatus getNextUserStatus(UserStatus currentStatus) {
        if (!isApplicableToUserStatus(currentStatus)) {
            return currentStatus;
        }

        switch (this) {
            case APPROVE:
                if (currentStatus == UserStatus.PENDING) return UserStatus.ACTIVE;
                if (currentStatus == UserStatus.INVITED) return UserStatus.ACTIVE;
                break;
            case REJECT:
                if (currentStatus == UserStatus.PENDING) return UserStatus.INACTIVE;
                if (currentStatus == UserStatus.INVITED) return UserStatus.INACTIVE;
                break;
            case VERIFY:
                if (currentStatus == UserStatus.VERIFICATION_PENDING) return UserStatus.ACTIVE;
                break;
            case ACTIVATE_USER:
                if (currentStatus == UserStatus.INACTIVE) return UserStatus.ACTIVE;
                if (currentStatus == UserStatus.SUSPENDED) return UserStatus.ACTIVE;
                break;
            case DEACTIVATE_USER:
                if (currentStatus == UserStatus.ACTIVE) return UserStatus.INACTIVE;
                break;
            case LOCK_USER:
                if (currentStatus == UserStatus.ACTIVE ||
                        currentStatus == UserStatus.PASSWORD_RESET_REQUIRED)
                    return UserStatus.LOCKED;
                break;
            case UNLOCK_USER:
                if (currentStatus == UserStatus.LOCKED) return UserStatus.ACTIVE;
                break;
            case SUSPEND_USER:
                if (currentStatus == UserStatus.ACTIVE) return UserStatus.SUSPENDED;
                break;
            case UNSUSPEND_USER:
                if (currentStatus == UserStatus.SUSPENDED) return UserStatus.ACTIVE;
                break;
            case REACTIVATE:
                if (currentStatus == UserStatus.SUSPENDED) return UserStatus.ACTIVE;
                if (currentStatus == UserStatus.EXPIRED) return UserStatus.ACTIVE;
                break;
            case ARCHIVE_USER:
                if (currentStatus != UserStatus.DELETED &&
                        currentStatus != UserStatus.ARCHIVED)
                    return UserStatus.ARCHIVED;
                break;
            case RESTORE_USER:
                if (currentStatus == UserStatus.ARCHIVED) return UserStatus.ACTIVE;
                if (currentStatus == UserStatus.DELETED) return UserStatus.ACTIVE;
                break;
            case FORCE_PASSWORD_RESET:
                if (currentStatus == UserStatus.ACTIVE ||
                        currentStatus == UserStatus.PASSWORD_RESET_REQUIRED)
                    return UserStatus.PASSWORD_RESET_REQUIRED;
                break;
            case RESET_PASSWORD:
                if (currentStatus == UserStatus.PASSWORD_RESET_REQUIRED) return UserStatus.ACTIVE;
                break;
        }

        return currentStatus;
    }
}