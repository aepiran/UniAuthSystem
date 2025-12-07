package ai.uniauth.models.enums;

/**
 * Loại thiết bị kết nối đến hệ thống
 */
public enum DeviceType {
    WEB_BROWSER("Trình duyệt Web"),
    MOBILE_BROWSER("Trình duyệt Mobile"),
    DESKTOP_APP("Ứng dụng Desktop"),
    MOBILE_APP_IOS("Ứng dụng iOS"),
    MOBILE_APP_ANDROID("Ứng dụng Android"),
    TABLET_APP_IOS("Ứng dụng iPad"),
    TABLET_APP_ANDROID("Ứng dụng Android Tablet"),
    SMART_TV("Smart TV"),
    GAME_CONSOLE("Máy chơi game"),
    IOT_DEVICE("Thiết bị IoT"),
    EMBEDDED_SYSTEM("Hệ thống nhúng"),
    SERVER("Máy chủ"),
    BOT("Bot/Crawler"),
    CLI_TOOL("Công cụ dòng lệnh"),
    THIRD_PARTY_API("API bên thứ ba"),
    UNKNOWN("Không xác định"),
    SMART_WATCH("Đồng hồ thông minh"),
    VOICE_ASSISTANT("Trợ lý ảo"),
    AUTOMATION_TOOL("Công cụ tự động hóa"),
    TESTING_TOOL("Công cụ kiểm thử"),
    MONITORING_TOOL("Công cụ giám sát");

    private final String description;

    DeviceType(String description) {
        this.description = description;
    }

    public static DeviceType detectFromUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return UNKNOWN;
        }

        String ua = userAgent.toLowerCase();

        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone") || ua.contains("ipad")) {
            if (ua.contains("safari") && !ua.contains("version/")) {
                return MOBILE_BROWSER;
            } else if (ua.contains("app")) {
                if (ua.contains("ios") || ua.contains("iphone") || ua.contains("ipad")) {
                    return MOBILE_APP_IOS;
                } else if (ua.contains("android")) {
                    return MOBILE_APP_ANDROID;
                }
            }
        }

        if (ua.contains("postman") || ua.contains("curl") || ua.contains("wget")) {
            return CLI_TOOL;
        }

        if (ua.contains("bot") || ua.contains("crawler") || ua.contains("spider")) {
            return BOT;
        }

        return WEB_BROWSER;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMobile() {
        return this == MOBILE_APP_IOS || this == MOBILE_APP_ANDROID ||
                this == MOBILE_BROWSER || this == TABLET_APP_IOS ||
                this == TABLET_APP_ANDROID || this == SMART_WATCH;
    }

    public boolean isWeb() {
        return this == WEB_BROWSER || this == MOBILE_BROWSER;
    }

    public boolean isDesktop() {
        return this == DESKTOP_APP;
    }

    public boolean isServer() {
        return this == SERVER || this == BOT || this == THIRD_PARTY_API;
    }
}