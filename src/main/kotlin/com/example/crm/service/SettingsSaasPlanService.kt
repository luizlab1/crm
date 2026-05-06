package com.example.crm.service

import com.example.crm.entity.PlanCategory
import com.example.crm.entity.SettingsSaasPlanJpaEntity
import com.example.crm.entity.SettingsSaasPlanBenefitJpaEntity
import com.example.crm.exception.RequestValidationException
import com.example.crm.exception.ValidationError
import com.example.crm.repository.SettingsSaasPlanRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SettingsSaasPlanService(
    private val repository: SettingsSaasPlanRepository
) {

    private companion object {
        const val MAX_NAME_LENGTH = 255
        const val MAX_BENEFIT_FIELD_LENGTH = 255
    }

    @Transactional(readOnly = true)
    fun list(tenantId: Long, name: String?, category: PlanCategory?): List<SettingsSaasPlanJpaEntity> {
        val namePattern = name?.trim()?.takeIf { it.isNotBlank() }?.lowercase()?.let { "%$it%" }
        return repository.findByTenantIdAndFilters(tenantId, namePattern, category)
    }

    @Transactional(readOnly = true)
    fun getById(id: Long, tenantId: Long): SettingsSaasPlanJpaEntity =
        repository.findOneByIdAndTenantId(id, tenantId)
            ?: throw NoSuchElementException("SettingsSaasPlan not found: $id")

    fun create(
        tenantId: Long,
        name: String?,
        description: String?,
        category: PlanCategory?,
        subtitle: String?,
        value: String?,
        benefits: List<String?>
    ): SettingsSaasPlanJpaEntity {
        val sanitized = sanitize(name, description, category, subtitle, value, benefits, tenantId = null)
        val saved = repository.save(
            SettingsSaasPlanJpaEntity(
                tenantId = tenantId,
                name = sanitized.name,
                description = sanitized.description,
                category = sanitized.category,
                subtitle = sanitized.subtitle,
                value = sanitized.value
            ).also { plan ->
                plan.benefits = sanitized.benefits.map { benefit ->
                    SettingsSaasPlanBenefitJpaEntity(
                        plan = plan,
                        description = benefit
                    )
                }.toMutableList()
            }
        )
        return saved
    }

    fun update(
        id: Long,
        tenantId: Long,
        name: String?,
        description: String?,
        category: PlanCategory?,
        subtitle: String?,
        value: String?,
        benefits: List<String?>
    ): SettingsSaasPlanJpaEntity {
        val existing = repository.findOneByIdAndTenantId(id, tenantId)
            ?: throw NoSuchElementException("SettingsSaasPlan not found: $id")
        val sanitized = sanitize(name, description, category, subtitle, value, benefits, tenantId = null)
        existing.name = sanitized.name
        existing.description = sanitized.description
        existing.category = sanitized.category
        existing.subtitle = sanitized.subtitle
        existing.value = sanitized.value
        existing.benefits.clear()
        existing.benefits.addAll(sanitized.benefits.map { benefit ->
            SettingsSaasPlanBenefitJpaEntity(
                plan = existing,
                description = benefit
            )
        })
        return repository.save(existing)
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
        subtitle: String?,
        value: String?,
        benefits: List<String?>,
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

        val normalizedSubtitle = validateBenefitField(
            value = subtitle,
            field = "subtitle",
            requiredMessage = "Subtitulo e obrigatorio",
            maxLengthMessage = "Subtitulo deve ter no maximo 255 caracteres",
            errors = errors
        )

        val normalizedValue = validateBenefitField(
            value = value,
            field = "value",
            requiredMessage = "Valor e obrigatorio",
            maxLengthMessage = "Valor deve ter no maximo 255 caracteres",
            errors = errors
        )

        if (benefits.isEmpty()) {
            errors += ValidationError("benefits", "Beneficios e obrigatorio e deve ter no minimo 1 item", "Size")
        }

        val normalizedBenefits = benefits.mapIndexed { index, benefit ->
            validateBenefitField(
                value = benefit,
                field = "benefits[$index].description",
                requiredMessage = "Descricao do beneficio e obrigatoria",
                maxLengthMessage = "Descricao do beneficio deve ter no maximo 255 caracteres",
                errors = errors
            )
        }

        if (errors.isNotEmpty()) throw RequestValidationException(errors)

        return SanitizedInput(
            name = normalizedName,
            description = description?.trim()?.takeIf { it.isNotEmpty() },
            category = category!!,
            subtitle = normalizedSubtitle,
            value = normalizedValue,
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

    // removed domain conversion: service now works directly with JPA entities

    private data class SanitizedInput(
        val name: String,
        val description: String?,
        val category: PlanCategory,
        val subtitle: String,
        val value: String,
        val benefits: List<String>
    )
}
