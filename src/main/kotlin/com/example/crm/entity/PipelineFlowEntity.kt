package com.example.crm.entity

import jakarta.persistence.*

@Entity
@Table(name = "pipeline_flow")
class PipelineFlowEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(nullable = false, length = 60)
    var code: String = "",

    @Column(nullable = false, length = 150)
    var name: String = "",

    @Column(length = 255)
    var description: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @OneToMany(mappedBy = "pipelineFlowId", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    var steps: MutableList<PipelineFlowStepEntity> = mutableListOf()
) : BaseEntity()
