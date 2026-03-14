package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "person_physical")
class PersonPhysicalJpaEntity(
    @Id
    @Column(name = "person_id")
    val personId: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "person_id")
    var person: PersonJpaEntity? = null,

    @Column(name = "full_name", nullable = false, length = 150)
    var fullName: String = "",

    @Column(nullable = false, length = 14)
    var cpf: String = "",

    @Column(name = "birth_date")
    var birthDate: LocalDate? = null
)

