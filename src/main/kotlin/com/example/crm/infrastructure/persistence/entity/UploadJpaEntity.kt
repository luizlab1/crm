package com.example.crm.infrastructure.persistence.entity

import com.example.crm.domain.model.FileType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "uploads")
class UploadJpaEntity(
    @Id
    @Column(nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "file_type", nullable = false, length = 60)
    @Enumerated(EnumType.STRING)
    var fileType: FileType = FileType.PRODUCT,

    @Column(name = "entity_id", nullable = false)
    var entityId: Long = 0,

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0,

    @Column(name = "item_id")
    var itemId: Long? = null,

    @Column(name = "category_id")
    var categoryId: Long? = null,

    @Column(name = "customer_id")
    var customerId: Long? = null,

    @Column(name = "worker_id")
    var workerId: Long? = null,

    @Column(name = "file_name", nullable = false, length = 255)
    var fileName: String = "",

    @Column(name = "file_path", nullable = false, length = 1000)
    var filePath: String = "",

    @Column(name = "content_type", length = 150)
    var contentType: String = "",

    @Column(name = "size")
    var size: Long = 0,

    @Column(name = "width")
    var width: Int? = null,

    @Column(name = "height")
    var height: Int? = null,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(name = "title", length = 200)
    var title: String? = null,

    @Column(name = "subtitle", length = 300)
    var subtitle: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now()
)
