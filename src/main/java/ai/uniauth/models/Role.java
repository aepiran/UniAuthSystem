package ai.uniauth.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name"),
                @UniqueConstraint(columnNames = "code")
        },
        indexes = {
                @Index(name = "idx_roles_code", columnList = "code"),
                @Index(name = "idx_roles_system", columnList = "system_id"),
                @Index(name = "idx_roles_is_system", columnList = "is_system_role")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"users", "permissions", "parent", "children"})
@ToString(exclude = {"users", "permissions", "parent", "children"})
public class Role extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50, unique = true)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", columnDefinition = "BINARY(16)")
    private UniSystem uniSystem;

    @Column(name = "is_system_role")
    private Boolean isSystemRole = false;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_role_id", columnDefinition = "BINARY(16)")
    private Role parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Role> children = new HashSet<>();

    @Column(name = "priority")
    private Integer priority = 0;

    // Relationships
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RolePermission> permissions = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (isSystemRole == null) {
            isSystemRole = false;
        }
        if (isDefault == null) {
            isDefault = false;
        }
        if (priority == null) {
            priority = 0;
        }
    }
}