package com.example.crm.application.usecase

import com.example.crm.application.port.input.SettingsSaasPlanBenefitInput
import com.example.crm.application.port.input.SettingsSaasPlanUpsertInput
import com.example.crm.application.port.input.SettingsSaasPlanUseCase
import com.example.crm.domain.exception.RequestValidationException
import com.example.crm.domain.exception.ValidationError
import com.example.crm.domain.model.PlanCategory
import com.example.crm.domain.model.SettingsSaasPlan
import com.example.crm.domain.model.SettingsSaasPlanBenefit
import com.example.crm.domain.repository.SettingsSaasPlanRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SettingsSaasPlanUseCaseImpl(
    private val repository: SettingsSaasPlanRepository
) : SettingsSaasPlanUseCase {

    private companion object {
        const val MAX_NAME_LENGTH = 255
        const val MAX_BENEFIT_FIELD_LENGTH = 255
    }

    @Transactional(readOnly = true)
    override fun list(tenantId: Long, name: String?, category: PlanCategory?): List<SettingsSaasPlan> =
        repository.findByTenantIdAndFilters(tenantId, name?.trim(), category)

    @Transactional(readOnly = true)
    override fun getById(id: Long, tenantId: Long): SettingsSaasPlan =
        repository.findByIdAndTenantId(id, tenantId)
            ?: throw NoSuchElementException("SettingsSaasPlan not found: $id")

    override fun create(tenantId: Long, input: SettingsSaasPlanUpsertInput): SettingsSaasPlan {
        val sanitized = sanitize(input)
        val plan = SettingsSaasPlan(
            tenantId = tenantId,
            name = sanitized.name,
            description = sanitized.description,
            subtitle = sanitized.subtitle,
            value = sanitized.value,
            category = sanitized.category,
            benefits = sanitized.benefits.map { SettingsSaasPlanBenefit(description = it.description) }
        )
        return repository.save(plan)
    }

    override fun update(id: Long, tenantId: Long, input: SettingsSaasPlanUpsertInput): SettingsSaasPlan {
        val existing = repository.findByIdAndTenantId(id, tenantId)
            ?: throw NoSuchElementException("SettingsSaasPlan not found: $id")
        val sanitized = sanitize(input)
        val updated = existing.copy(
            name = sanitized.name,
            description = sanitized.description,
            subtitle = sanitized.subtitle,
            value = sanitized.value,
            category = sanitized.category,
            benefits = sanitized.benefits.map { SettingsSaasPlanBenefit(description = it.description) }
        )
        return repository.save(updated)
    }

    override fun delete(id: Long, tenantId: Long) {
        repository.findByIdAndTenantId(id, tenantId)
            ?: throw NoSuchElementException("SettingsSaasPlan not found: $id")
        repository.deleteById(id)
    }

    private fun sanitize(input: SettingsSaasPlanUpsertInput): SanitizedInput {
        val errors = mutableListOf<ValidationError>()

        if (input.tenantId != null) {
            errors += ValidationError("tenant_id", "tenant_id nao e permitido no payload", "ForbiddenField")
        }

        val normalizedName = input.name?.trim().orEmpty()
        if (normalizedName.isBlank()) {
            errors += ValidationError("name", "Nome e obrigatorio", "NotBlank")
        } else if (normalizedName.length > MAX_NAME_LENGTH) {
            errors += ValidationError("name", "Nome deve ter no maximo 255 caracteres", "Size")
        }

        val category = input.category
        if (category == null) {
            errors += ValidationError("category", "Categoria e obrigatoria", "NotNull")
        }

        val normalizedSubtitle = validateBenefitField(
            value = input.subtitle,
            field = "subtitle",
            requiredMessage = "Subtitulo e obrigatorio",
            maxLengthMessage = "Subtitulo deve ter no maximo 255 caracteres",
            errors = errors
        )
        val normalizedValue = validateBenefitField(
            value = input.value,
            field = "value",
            requiredMessage = "Valor e obrigatorio",
            maxLengthMessage = "Valor deve ter no maximo 255 caracteres",
            errors = errors
        )

        if (input.benefits.isEmpty()) {
            errors += ValidationError("benefits", "Beneficios e obrigatorio e deve ter no minimo 1 item", "Size")
        }

        val normalizedBenefits = sanitizeBenefits(input.benefits, errors)

        if (errors.isNotEmpty()) {
            throw RequestValidationException(errors)
        }

        return SanitizedInput(
            name = normalizedName,
            description = input.description?.trim()?.takeIf { it.isNotEmpty() },
            subtitle = normalizedSubtitle,
            value = normalizedValue,
            category = category!!,
            benefits = normalizedBenefits
        )
    }

    private fun sanitizeBenefits(
        benefits: List<SettingsSaasPlanBenefitInput>,
        errors: MutableList<ValidationError>
    ): List<SanitizedBenefitInput> =
        benefits.mapIndexed { index, benefit ->
            val normalizedDescription = validateBenefitField(
                value = benefit.description,
                field = "benefits[$index].description",
                requiredMessage = "Descricao do beneficio e obrigatoria",
                maxLengthMessage = "Descricao do beneficio deve ter no maximo 255 caracteres",
                errors = errors
            )
            SanitizedBenefitInput(description = normalizedDescription)
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

    private data class SanitizedInput(
        val name: String,
        val description: String?,
        val subtitle: String,
        val value: String,
        val category: PlanCategory,
        val benefits: List<SanitizedBenefitInput>
    )

    private data class SanitizedBenefitInput(
        val description: String
    )
}
