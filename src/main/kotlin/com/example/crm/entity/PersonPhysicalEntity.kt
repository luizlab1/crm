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
import java.time.LocalDate

@Entity
@Table(name = "person_physical")
class PersonPhysicalEntity(
    @Id
    @Column(name = "person_id")
    val personId: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "person_id")
    var person: PersonEntity? = null,

    @Column(name = "full_name", nullable = false, length = 150)
    var fullName: String = "",

    @Column(nullable = false, length = 14)
    var cpf: String = "",

    @Column(name = "birth_date")
    var birthDate: LocalDate? = null
)
