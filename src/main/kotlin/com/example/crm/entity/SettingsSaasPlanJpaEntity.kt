package com.example.crm.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "settings_saas_plan")
class SettingsSaasPlanEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(nullable = false, length = 255)
    var name: String = "",

    @Column(columnDefinition = "text")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    var category: PlanCategory = PlanCategory.BUSINESS,

    @OneToMany(mappedBy = "plan", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var benefits: MutableList<SettingsSaasPlanBenefitEntity> = mutableListOf()
) : BaseEntity()
