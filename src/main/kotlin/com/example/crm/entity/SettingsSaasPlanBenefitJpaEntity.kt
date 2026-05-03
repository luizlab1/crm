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

@Entity
@Table(name = "settings_saas_plan_benefits")
class SettingsSaasPlanBenefitJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    var plan: SettingsSaasPlanJpaEntity? = null,

    @Column(nullable = false, columnDefinition = "text")
    var subtitle: String = "",

    @Column(nullable = false, columnDefinition = "text")
    var value: String = ""
) : BaseEntity()
