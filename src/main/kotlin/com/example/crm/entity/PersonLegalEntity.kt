package com.example.crm.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.OneToOne
import jakarta.persistence.MapsId

@Entity
@Table(name = "person_legal")
class PersonLegalEntity(
    @Id
    @Column(name = "person_id")
    val personId: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "person_id")
    var person: PersonEntity? = null,

    @Column(name = "corporate_name", nullable = false, length = 150)
    var corporateName: String = "",

    @Column(name = "trade_name", length = 150)
    var tradeName: String? = null,

    @Column(nullable = false, length = 18)
    var cnpj: String = ""
)
