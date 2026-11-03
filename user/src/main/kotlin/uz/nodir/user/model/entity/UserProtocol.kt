package uz.nodir.user.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID


@Entity
@Table(
    name = "user_protocol",
    schema = "keycloak"
)
data class UserProtocol(

    @Id
    val id: Long,

    @Column(name = "user_id")
    val userId: String? = null,

    @Column(name = "user_name")
    val userName: String? = null,

    @Column(name = "first_name")
    val firstName: String? = null,

    @Column(name = "last_name")
    val lastName: String? = null,

    @Column(name = "ip_address")
    val ipAddress: String? = null,

    @Column(name = "created_time")
    val createdTime: String? = null,
) {
}