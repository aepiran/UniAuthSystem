package ai.uniauth.models.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Trạng thái của người dùng trong hệ thống
 */
public enum UserStatus {
    ACTIVE("Hoạt động", true),
    INACTIVE("Không hoạt động", false),
    PENDING("Chờ xử lý", false),
    SUSPENDED("Tạm ngưng", false),
    LOCKED("Đã khóa", false),
    DELETED("Đã xóa", false),
    EXPIRED("Hết hạn", false),
    ARCHIVED("Đã lưu trữ", false),
    DISABLED("Vô hiệu hóa", false),
    INVITED("Đã mời", false),
    VERIFICATION_PENDING("Chờ xác minh", false),
    PASSWORD_RESET_REQUIRED("Yêu cầu đổi mật khẩu", true),
    FIRST_LOGIN("Lần đăng nhập đầu", true),
    COMPROMISED("Bị xâm phạm", false),
    QUARANTINE("Cách ly", false);

    private final String description;
    private final boolean canLogin;

    UserStatus(String description, boolean canLogin) {
        this.description = description;
        this.canLogin = canLogin;
    }

    public static UserStatus fromString(String value) {
        for (UserStatus status : UserStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return INACTIVE;
    }

    public String getDescription() {
        return description;
    }

    public boolean canLogin() {
        return canLogin;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PASSWORD_RESET_REQUIRED || this == FIRST_LOGIN;
    }

    public boolean isInactive() {
        return !isActive();
    }

    public boolean isLocked() {
        return this == LOCKED || this == SUSPENDED;
    }

    public boolean requiresAction() {
        return this == PENDING ||
                this == VERIFICATION_PENDING ||
                this == PASSWORD_RESET_REQUIRED ||
                this == INVITED ||
                this == FIRST_LOGIN;
    }

    /**
     * DEPRECATED - Sử dụng phương thức mới trong ActionType
     * Để đảm bảo consistency, chuyển logic này sang ActionType
     */
    @Deprecated
    public UserStatus getNextStatusAfterAction(ActionType action) {
        return action.getNextUserStatus(this);
    }

    /**
     * Lấy danh sách các ActionType có thể áp dụng cho trạng thái hiện tại
     */
    public List<ActionType> getAllowedActions() {
        List<ActionType> allowedActions = new ArrayList<>();

        for (ActionType action : ActionType.values()) {
            if (action.isApplicableToUserStatus(this)) {
                allowedActions.add(action);
            }
        }

        return allowedActions;
    }

    /**
     * Kiểm tra xem action có thể áp dụng cho trạng thái hiện tại không
     */
    public boolean canApplyAction(ActionType action) {
        return action.isApplicableToUserStatus(this);
    }

    /**
     * Lấy trạng thái tiếp theo sau khi áp dụng action
     * (Tương thích ngược với code cũ)
     */
    public UserStatus applyAction(ActionType action) {
        return action.getNextUserStatus(this);
    }
}