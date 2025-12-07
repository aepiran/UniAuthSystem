package ai.uniauth.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "uni_system")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_code", unique = true, nullable = false)
    private String systemCode;

    @Column(name = "system_name", nullable = false)
    private String systemName;

    private String description;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "api_key", unique = true)
    private String apiKey;

    @Column(name = "secret_key")
    private String secretKey;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "system", cascade = CascadeType.ALL)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "system", cascade = CascadeType.ALL)
    private Set<Permission> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "systems")
    private Set<User> users = new HashSet<>();
}
