package com.example.crm.infrastructure.web.mapper

import com.example.crm.domain.model.*
import com.example.crm.infrastructure.web.dto.request.*
import com.example.crm.infrastructure.web.dto.response.*
import org.springframework.stereotype.Component

@Component
class PersonWebMapper {
    fun toDomain(request: PersonRequest) = Person(
        tenantId = request.tenantId, isActive = request.isActive,
        physical = request.physical?.let { PersonPhysical(it.fullName, it.cpf, it.birthDate) },
        legal = request.legal?.let { PersonLegal(it.corporateName, it.tradeName, it.cnpj) },
        contacts = request.contacts.map { Contact(type = it.type, contactValue = it.contactValue, isPrimary = it.isPrimary, isActive = it.isActive) }
    )

    fun toResponse(domain: Person) = PersonResponse(
        id = domain.id, tenantId = domain.tenantId, code = domain.code, isActive = domain.isActive,
        physical = domain.physical?.let { PersonPhysicalResponse(it.fullName, it.cpf, it.birthDate) },
        legal = domain.legal?.let { PersonLegalResponse(it.corporateName, it.tradeName, it.cnpj) },
        contacts = domain.contacts.map {
            ContactResponse(it.id, it.type, it.contactValue, it.isPrimary, it.isActive, it.createdAt, it.updatedAt)
        },
        createdAt = domain.createdAt, updatedAt = domain.updatedAt
    )
}

@Component
class CustomerWebMapper {
    fun toDomain(request: CustomerRequest): Customer {
        val person = if (request.physical != null || request.legal != null || request.contacts.isNotEmpty()) {
            Person(
                tenantId = request.tenantId,
                physical = request.physical?.let { PersonPhysical(it.fullName, it.cpf, it.birthDate) },
                legal = request.legal?.let { PersonLegal(it.corporateName, it.tradeName, it.cnpj) },
                contacts = request.contacts.map {
                    Contact(
                        type = it.type,
                        contactValue = it.contactValue,
                        isPrimary = it.isPrimary,
                        isActive = it.isActive
                    )
                }
            )
        } else null
        return Customer(
            tenantId = request.tenantId, fullName = request.fullName,
            email = request.email, phone = request.phone, document = request.document,
            isActive = request.isActive, person = person
        )
    }
    fun toResponse(d: Customer) = CustomerResponse(
        id = d.id, code = d.code, tenantId = d.tenantId, personId = d.personId,
        fullName = d.fullName, email = d.email, phone = d.phone, document = d.document,
        isActive = d.isActive, createdAt = d.createdAt, updatedAt = d.updatedAt,
        physical = d.person?.physical?.let { PersonPhysicalResponse(it.fullName, it.cpf, it.birthDate) },
        legal = d.person?.legal?.let { PersonLegalResponse(it.corporateName, it.tradeName, it.cnpj) },
        contacts = d.person?.contacts?.map {
            ContactResponse(it.id, it.type, it.contactValue, it.isPrimary, it.isActive, it.createdAt, it.updatedAt)
        } ?: emptyList()
    )
}

@Component
class OrderWebMapper {
    fun toDomain(request: OrderRequest) = Order(
        tenantId = request.tenantId, customerId = request.customerId, userId = request.userId,
        status = request.status, subtotalCents = request.subtotalCents,
        discountCents = request.discountCents, totalCents = request.totalCents,
        currencyCode = request.currencyCode, notes = request.notes,
        items = request.items.map { OrderItem(itemId = it.itemId, quantity = it.quantity, unitPriceCents = it.unitPriceCents, totalPriceCents = it.totalPriceCents) }
    )
    fun toResponse(d: Order) = OrderResponse(
        id = d.id, code = d.code, tenantId = d.tenantId, customerId = d.customerId,
        userId = d.userId, status = d.status, subtotalCents = d.subtotalCents,
        discountCents = d.discountCents, totalCents = d.totalCents,
        currencyCode = d.currencyCode, notes = d.notes,
        items = d.items.map { OrderItemResponse(it.id, it.itemId, it.quantity, it.unitPriceCents, it.totalPriceCents, it.createdAt) },
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class LeadWebMapper {
    fun toDomain(request: LeadRequest) = Lead(
        tenantId = request.tenantId, flowId = request.flowId, customerId = request.customerId,
        status = request.status, source = request.source,
        estimatedValueCents = request.estimatedValueCents, notes = request.notes
    )
    fun toResponse(d: Lead) = LeadResponse(
        id = d.id, code = d.code, tenantId = d.tenantId, flowId = d.flowId,
        customerId = d.customerId, status = d.status, source = d.source,
        estimatedValueCents = d.estimatedValueCents, notes = d.notes,
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
    fun messageToDomain(leadId: Long, request: LeadMessageRequest) = LeadMessage(
        leadId = leadId, message = request.message, channel = request.channel,
        createdByUserId = request.createdByUserId
    )
    fun toResponse(d: LeadMessage) = LeadMessageResponse(
        id = d.id, leadId = d.leadId, message = d.message,
        channel = d.channel, createdByUserId = d.createdByUserId, createdAt = d.createdAt
    )
}

@Component
class PipelineFlowWebMapper {
    fun toDomain(request: PipelineFlowRequest) = PipelineFlow(
        tenantId = request.tenantId, code = request.code, name = request.name,
        description = request.description, isActive = request.isActive,
        steps = request.steps.map {
            PipelineFlowStep(stepOrder = it.stepOrder, code = it.code, name = it.name,
                description = it.description, stepType = it.stepType, isTerminal = it.isTerminal)
        }
    )
    fun toResponse(d: PipelineFlow) = PipelineFlowResponse(
        id = d.id, tenantId = d.tenantId, code = d.code, name = d.name,
        description = d.description, isActive = d.isActive,
        steps = d.steps.map {
            PipelineFlowStepResponse(it.id, it.stepOrder, it.code, it.name, it.description, it.stepType, it.isTerminal, it.createdAt, it.updatedAt)
        },
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class TenantWebMapper {
    fun toDomain(request: TenantRequest) = Tenant(
        parentTenantId = request.parentTenantId, name = request.name,
        category = request.category, isActive = request.isActive
    )
    fun toResponse(d: Tenant) = TenantResponse(
        id = d.id, parentTenantId = d.parentTenantId, code = d.code,
        name = d.name, category = d.category, isActive = d.isActive,
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class UserWebMapper {
    fun toDomain(request: UserRequest): User {
        val person = if (request.physical != null || request.legal != null || request.contacts.isNotEmpty()) {
            Person(
                tenantId = request.tenantId,
                physical = request.physical?.let { PersonPhysical(it.fullName, it.cpf, it.birthDate) },
                legal = request.legal?.let { PersonLegal(it.corporateName, it.tradeName, it.cnpj) },
                contacts = request.contacts.map {
                    Contact(
                        type = it.type,
                        contactValue = it.contactValue,
                        isPrimary = it.isPrimary,
                        isActive = it.isActive
                    )
                }
            )
        } else null
        return User(
            tenantId = request.tenantId, email = request.email,
            passwordHash = request.passwordHash, isActive = request.isActive, person = person
        )
    }
    fun toResponse(d: User) = UserResponse(
        id = d.id, tenantId = d.tenantId, personId = d.personId, code = d.code,
        email = d.email, isActive = d.isActive, createdAt = d.createdAt, updatedAt = d.updatedAt,
        physical = d.person?.physical?.let { PersonPhysicalResponse(it.fullName, it.cpf, it.birthDate) },
        legal = d.person?.legal?.let { PersonLegalResponse(it.corporateName, it.tradeName, it.cnpj) },
        contacts = d.person?.contacts?.map {
            ContactResponse(it.id, it.type, it.contactValue, it.isPrimary, it.isActive, it.createdAt, it.updatedAt)
        } ?: emptyList()
    )
}

@Component
class WorkerWebMapper {
    fun toDomain(request: WorkerRequest): Worker {
        val person = if (request.physical != null || request.legal != null || request.contacts.isNotEmpty()) {
            Person(
                tenantId = request.tenantId,
                physical = request.physical?.let { PersonPhysical(it.fullName, it.cpf, it.birthDate) },
                legal = request.legal?.let { PersonLegal(it.corporateName, it.tradeName, it.cnpj) },
                contacts = request.contacts.map {
                    Contact(
                        type = it.type,
                        contactValue = it.contactValue,
                        isPrimary = it.isPrimary,
                        isActive = it.isActive
                    )
                }
            )
        } else null
        // personId = 0 é placeholder — o use case vai criar/atualizar a person e preencher o personId real
        return Worker(
            tenantId = request.tenantId, personId = 0L,
            userId = request.userId, isActive = request.isActive, person = person
        )
    }
    fun toResponse(d: Worker) = WorkerResponse(
        id = d.id, code = d.code, tenantId = d.tenantId, personId = d.personId,
        userId = d.userId, isActive = d.isActive, createdAt = d.createdAt, updatedAt = d.updatedAt,
        physical = d.person?.physical?.let { PersonPhysicalResponse(it.fullName, it.cpf, it.birthDate) },
        legal = d.person?.legal?.let { PersonLegalResponse(it.corporateName, it.tradeName, it.cnpj) },
        contacts = d.person?.contacts?.map {
            ContactResponse(it.id, it.type, it.contactValue, it.isPrimary, it.isActive, it.createdAt, it.updatedAt)
        } ?: emptyList()
    )
}

@Component
class ItemWebMapper {
    fun toDomain(request: ItemRequest) = Item(
        tenantId = request.tenantId, categoryId = request.categoryId,
        type = request.type, name = request.name, sku = request.sku, isActive = request.isActive
    )
    fun toResponse(d: Item) = ItemResponse(
        id = d.id, code = d.code, tenantId = d.tenantId, categoryId = d.categoryId,
        type = d.type, name = d.name, sku = d.sku, isActive = d.isActive,
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class ItemCategoryWebMapper {
    fun toDomain(request: ItemCategoryRequest) = ItemCategory(tenantId = request.tenantId, name = request.name)
    fun toResponse(d: ItemCategory) = ItemCategoryResponse(
        id = d.id, tenantId = d.tenantId, name = d.name, createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class AddressWebMapper {
    fun toDomain(request: AddressRequest) = Address(
        street = request.street, number = request.number, complement = request.complement,
        neighborhood = request.neighborhood, cityId = request.cityId, postalCode = request.postalCode,
        latitude = request.latitude, longitude = request.longitude, isActive = request.isActive
    )
    fun toResponse(d: Address) = AddressResponse(
        id = d.id, street = d.street, number = d.number, complement = d.complement,
        neighborhood = d.neighborhood, cityId = d.cityId, postalCode = d.postalCode,
        latitude = d.latitude, longitude = d.longitude, isActive = d.isActive,
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class AppointmentWebMapper {
    fun toDomain(request: AppointmentRequest) = Appointment(
        status = request.status, scheduledAt = request.scheduledAt,
        startedAt = request.startedAt, finishedAt = request.finishedAt,
        totalCents = request.totalCents, notes = request.notes
    )
    fun toResponse(d: Appointment) = AppointmentResponse(
        id = d.id, code = d.code, status = d.status, scheduledAt = d.scheduledAt,
        startedAt = d.startedAt, finishedAt = d.finishedAt,
        totalCents = d.totalCents, notes = d.notes,
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class ScheduleWebMapper {
    fun toDomain(request: ScheduleRequest) = Schedule(
        tenantId = request.tenantId, customerId = request.customerId,
        appointmentId = request.appointmentId, description = request.description, isActive = request.isActive
    )
    fun toResponse(d: Schedule) = ScheduleResponse(
        id = d.id, code = d.code, tenantId = d.tenantId, customerId = d.customerId,
        appointmentId = d.appointmentId, description = d.description, isActive = d.isActive,
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class RoleWebMapper {
    fun toDomain(request: RoleRequest) = Role(name = request.name, description = request.description, isActive = request.isActive)
    fun toResponse(d: Role) = RoleResponse(
        id = d.id, name = d.name, description = d.description, isActive = d.isActive,
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class PermissionWebMapper {
    fun toDomain(request: PermissionRequest) = Permission(code = request.code, description = request.description, isActive = request.isActive)
    fun toResponse(d: Permission) = PermissionResponse(
        id = d.id, code = d.code, description = d.description, isActive = d.isActive,
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class CountryWebMapper {
    fun toResponse(d: Country) = CountryResponse(
        id = d.id, iso2 = d.iso2, iso3 = d.iso3, country = d.country,
        createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class StateWebMapper {
    fun toResponse(d: State) = StateResponse(
        id = d.id, countryId = d.countryId, acronym = d.acronym,
        state = d.state, ibgeCode = d.ibgeCode, createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class CityWebMapper {
    fun toResponse(d: City) = CityResponse(
        id = d.id, stateId = d.stateId, city = d.city,
        ibgeCode = d.ibgeCode, createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

@Component
class UnitOfMeasureWebMapper {
    fun toResponse(d: UnitOfMeasure) = UnitOfMeasureResponse(
        id = d.id, code = d.code, name = d.name, symbol = d.symbol,
        isActive = d.isActive, createdAt = d.createdAt, updatedAt = d.updatedAt
    )
}

