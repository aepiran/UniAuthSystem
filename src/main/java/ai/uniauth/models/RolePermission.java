package ai.uniauth.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_permissions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"role_id", "permission_id"})
        },
        indexes = {
                @Index(name = "idx_role_permissions_role", columnList = "role_id"),
                @Index(name = "idx_role_permissions_perm", columnList = "permission_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class RolePermission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false, columnDefinition = "BINARY(16)")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_id", nullable = false, columnDefinition = "BINARY(16)")
    private Permission permission;

    @Column(name = "access_level", length = 20)
    private String accessLevel = "ALLOW";

    @Column(columnDefinition = "JSON")
    private String conditions;

    @Column(name = "scope_filter", columnDefinition = "JSON")
    private String scopeFilter;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "assigned_by")
    private String assignedBy;

    @PrePersist
    public void prePersist() {
        if (accessLevel == null) {
            accessLevel = "ALLOW";
        }
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }
}