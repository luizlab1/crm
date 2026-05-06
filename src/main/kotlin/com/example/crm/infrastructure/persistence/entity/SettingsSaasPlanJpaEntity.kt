package com.example.crm.infrastructure.persistence.entity

import com.example.crm.domain.model.PlanCategory
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
class SettingsSaasPlanJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(nullable = false, length = 255)
    var name: String = "",

    @Column(columnDefinition = "text")
    var description: String? = null,

    @Column(nullable = false, columnDefinition = "text")
    var subtitle: String = "",

    @Column(nullable = false, columnDefinition = "text")
    var value: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    var category: PlanCategory = PlanCategory.BUSINESS,

    @OneToMany(mappedBy = "plan", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var benefits: MutableList<SettingsSaasPlanBenefitJpaEntity> = mutableListOf()
) : BaseJpaEntity()
