package com.example.crm.domain.model

enum class FileType(val displayName: String) {
    PRODUCT("Produto"),
    SERVICE("Serviço"),
    CUSTOMER("Cliente"),
    WORKER("Colaborador"),
    TENANT("Tenant"),
    USER("Usuário"),
    CATEGORY("Categoria"),
    SLIDE_SAAS("Slide SaaS"),
    SLIDE_OWN("Slide Próprio"),
    BANNER("Banner"),
    OTHERS("Outros")
}
