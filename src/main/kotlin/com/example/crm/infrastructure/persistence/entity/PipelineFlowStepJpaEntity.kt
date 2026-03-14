package com.example.crm.infrastructure.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "pipeline_flow_step")
class PipelineFlowStepJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "pipeline_flow_id", nullable = false)
    var pipelineFlowId: Long = 0,

    @Column(name = "step_order", nullable = false)
    var stepOrder: Int = 0,

    @Column(nullable = false, length = 60)
    var code: String = "",

    @Column(nullable = false, length = 150)
    var name: String = "",

    @Column(length = 255)
    var description: String? = null,

    @Column(name = "step_type", nullable = false, length = 60)
    var stepType: String = "",

    @Column(name = "is_terminal", nullable = false)
    var isTerminal: Boolean = false
) : BaseJpaEntity()

