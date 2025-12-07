package ai.uniauth.models.enums;

public enum RiskLevel {
    NONE("Không có rủi ro", 0, "#10B981"),
    LOW("Rủi ro thấp", 1, "#3B82F6"),
    MEDIUM("Rủi ro trung bình", 2, "#F59E0B"),
    HIGH("Rủi ro cao", 3, "#EF4444"),
    CRITICAL("Rủi ro nghiêm trọng", 4, "#DC2626"),
    EXTREME("Rủi ro cực cao", 5, "#991B1B");

    private final String description;
    private final int level;
    private final String color;

    RiskLevel(String description, int level, String color) {
        this.description = description;
        this.level = level;
        this.color = color;
    }

    public static RiskLevel fromLevel(int level) {
        for (RiskLevel risk : RiskLevel.values()) {
            if (risk.getLevel() == level) {
                return risk;
            }
        }
        return NONE;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public String getColor() {
        return color;
    }

    public boolean isHigherThan(RiskLevel other) {
        return this.level > other.level;
    }

    public boolean isLowerThan(RiskLevel other) {
        return this.level < other.level;
    }

    public boolean isAtLeast(RiskLevel other) {
        return this.level >= other.level;
    }

    public boolean requiresApproval() {
        return this.level >= MEDIUM.level;
    }

    public boolean requiresMFA() {
        return this.level >= HIGH.level;
    }

    public boolean requiresAdminReview() {
        return this.level >= CRITICAL.level;
    }
}
