package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "person_legal")
class PersonLegalJpaEntity(
    @Id
    @Column(name = "person_id")
    val personId: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "person_id")
    var person: PersonJpaEntity? = null,

    @Column(name = "corporate_name", nullable = false, length = 150)
    var corporateName: String = "",

    @Column(name = "trade_name", length = 150)
    var tradeName: String? = null,

    @Column(nullable = false, length = 18)
    var cnpj: String = ""
)

