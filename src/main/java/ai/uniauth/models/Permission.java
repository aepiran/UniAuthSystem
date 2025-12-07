package ai.uniauth.models;

import ai.uniauth.models.enums.RiskLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "code")
        },
        indexes = {
                @Index(name = "idx_permissions_code", columnList = "code"),
                @Index(name = "idx_permissions_system", columnList = "system_id"),
                @Index(name = "idx_permissions_category", columnList = "category"),
                @Index(name = "idx_permissions_module", columnList = "module")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"uniSystem", "rolePermissions"})
@ToString(exclude = {"uniSystem", "rolePermissions"})
public class Permission extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100, unique = true)
    private String code;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", nullable = false, columnDefinition = "BINARY(16)")
    private UniSystem uniSystem;

    @Size(max = 50)
    @Column(length = 50)
    private String category;

    @Size(max = 50)
    @Column(length = 50)
    private String subcategory;

    @Size(max = 100)
    @Column(length = 100)
    private String module;

    @Column(name = "is_sensitive")
    private Boolean isSensitive = false;

    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    // Relationships
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RolePermission> rolePermissions = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (isSensitive == null) {
            isSensitive = false;
        }
        if (requiresApproval == null) {
            requiresApproval = false;
        }
        if (sortOrder == null) {
            sortOrder = 0;
        }
    }
}