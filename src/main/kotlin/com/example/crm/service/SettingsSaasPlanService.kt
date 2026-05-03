package com.example.crm.service

import com.example.crm.entity.PlanCategory
import com.example.crm.entity.SettingsSaasPlan
import com.example.crm.entity.SettingsSaasPlanBenefit
import com.example.crm.exception.RequestValidationException
import com.example.crm.exception.ValidationError
import com.example.crm.repository.SettingsSaasPlanJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SettingsSaasPlanService(
    private val repository: SettingsSaasPlanJpaRepository
) {

    private companion object {
        const val MAX_NAME_LENGTH = 255
        const val MAX_BENEFIT_FIELD_LENGTH = 255
    }

    @Transactional(readOnly = true)
    fun list(tenantId: Long, name: String?, category: PlanCategory?): List<SettingsSaasPlan> {
        val namePattern = name?.trim()?.takeIf { it.isNotBlank() }?.lowercase()?.let { "%$it%" }
        return repository.findByTenantIdAndFilters(tenantId, namePattern, category).map { it.toDomain() }
    }

    @Transactional(readOnly = true)
    fun getById(id: Long, tenantId: Long): SettingsSaasPlan =
        repository.findOneByIdAndTenantId(id, tenantId)?.toDomain()
            ?: throw NoSuchElementException("SettingsSaasPlan not found: $id")

    fun create(
        tenantId: Long,
        name: String?,
        description: String?,
        category: PlanCategory?,
        benefits: List<Pair<String?, String?>>
    ): SettingsSaasPlan {
        val sanitized = sanitize(name, description, category, benefits, tenantId = null)
        val saved = repository.save(
            com.example.crm.entity.SettingsSaasPlanJpaEntity(
                tenantId = tenantId,
                name = sanitized.name,
                description = sanitized.description,
                category = sanitized.category
            ).also { plan ->
                plan.benefits = sanitized.benefits.map { benefit ->
                    com.example.crm.entity.SettingsSaasPlanBenefitJpaEntity(
                        plan = plan,
                        subtitle = benefit.subtitle,
                        value = benefit.value
                    )
                }.toMutableList()
            }
        )
        return saved.toDomain()
    }

    fun update(
        id: Long,
        tenantId: Long,
        name: String?,
        description: String?,
        category: PlanCategory?,
        benefits: List<Pair<String?, String?>>
    ): SettingsSaasPlan {
        val existing = repository.findOneByIdAndTenantId(id, tenantId)
            ?: throw NoSuchElementException("SettingsSaasPlan not found: $id")
        val sanitized = sanitize(name, description, category, benefits, tenantId = null)
        existing.name = sanitized.name
        existing.description = sanitized.description
        existing.category = sanitized.category
        existing.benefits.clear()
        existing.benefits.addAll(sanitized.benefits.map { benefit ->
            com.example.crm.entity.SettingsSaasPlanBenefitJpaEntity(
                plan = existing,
                subtitle = benefit.subtitle,
                value = benefit.value
            )
        })
        return repository.save(existing).toDomain()
    }

    fun delete(id: Long, tenantId: Long) {
        val existing = repository.findOneByIdAndTenantId(id, tenantId)
            ?: throw NoSuchElementException("SettingsSaasPlan not found: $id")
        repository.deleteById(existing.id)
    }

    private fun sanitize(
        name: String?,
        description: String?,
        category: PlanCategory?,
        benefits: List<Pair<String?, String?>>,
        tenantId: Long?
    ): SanitizedInput {
        val errors = mutableListOf<ValidationError>()
        if (tenantId != null) {
            errors += ValidationError("tenant_id", "tenant_id nao e permitido no payload", "ForbiddenField")
        }

        val normalizedName = name?.trim().orEmpty()
        if (normalizedName.isBlank()) {
            errors += ValidationError("name", "Nome e obrigatorio", "NotBlank")
        } else if (normalizedName.length > MAX_NAME_LENGTH) {
            errors += ValidationError("name", "Nome deve ter no maximo 255 caracteres", "Size")
        }

        if (category == null) {
            errors += ValidationError("category", "Categoria e obrigatoria", "NotNull")
        }

        if (benefits.isEmpty()) {
            errors += ValidationError("benefits", "Beneficios e obrigatorio e deve ter no minimo 1 item", "Size")
        }

        val normalizedBenefits = benefits.mapIndexed { index, benefit ->
            val subtitle = validateBenefitField(
                value = benefit.first,
                field = "benefits[$index].subtitle",
                requiredMessage = "Subtitulo do beneficio e obrigatorio",
                maxLengthMessage = "Subtitulo do beneficio deve ter no maximo 255 caracteres",
                errors = errors
            )
            val value = validateBenefitField(
                value = benefit.second,
                field = "benefits[$index].value",
                requiredMessage = "Valor do beneficio e obrigatorio",
                maxLengthMessage = "Valor do beneficio deve ter no maximo 255 caracteres",
                errors = errors
            )
            SanitizedBenefitInput(subtitle = subtitle, value = value)
        }

        if (errors.isNotEmpty()) throw RequestValidationException(errors)

        return SanitizedInput(
            name = normalizedName,
            description = description?.trim()?.takeIf { it.isNotEmpty() },
            category = category!!,
            benefits = normalizedBenefits
        )
    }

    private fun validateBenefitField(
        value: String?,
        field: String,
        requiredMessage: String,
        maxLengthMessage: String,
        errors: MutableList<ValidationError>
    ): String {
        val normalized = value?.trim().orEmpty()
        if (normalized.isBlank()) {
            errors += ValidationError(field, requiredMessage, "NotBlank")
        } else if (normalized.length > MAX_BENEFIT_FIELD_LENGTH) {
            errors += ValidationError(field, maxLengthMessage, "Size")
        }
        return normalized
    }

    private fun com.example.crm.entity.SettingsSaasPlanJpaEntity.toDomain() = SettingsSaasPlan(
        id = id,
        tenantId = tenantId,
        name = name,
        description = description,
        category = category,
        benefits = benefits.map {
            SettingsSaasPlanBenefit(
                id = it.id,
                subtitle = it.subtitle,
                value = it.value,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        },
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private data class SanitizedInput(
        val name: String,
        val description: String?,
        val category: PlanCategory,
        val benefits: List<SanitizedBenefitInput>
    )

    private data class SanitizedBenefitInput(
        val subtitle: String,
        val value: String
    )
}
