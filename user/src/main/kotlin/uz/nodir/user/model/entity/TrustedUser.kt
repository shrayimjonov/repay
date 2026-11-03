package uz.nodir.user.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime


@Entity
@Table(
    name = "trusted_user",
    schema = "keycloak"
)
class TrustedUser(
    @Id
    val id: Long,

    @Column(name = "user_name")
    val userName: String? = null,

    @Column(name = "ip")
    val ip: String? = null,

    @Column(name = "created_time")
    val createdTime: LocalDateTime? = null,
) {
}