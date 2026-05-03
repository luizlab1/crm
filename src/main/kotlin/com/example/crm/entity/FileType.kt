package com.example.crm.entity

enum class FileType(val displayName: String) {
    PRODUCT("Produto"),
    SERVICE("Serviço"),
    CUSTOMER("Cliente"),
    WORKER("Colaborador"),
    TENANT("Tenant"),
    LOGO("Logo"),
    USER("Usuário"),
    CATEGORY("Categoria"),
    SLIDE_SAAS("Slide SaaS"),
    SLIDE_OWN("Slide Próprio"),
    BANNER("Banner"),
    OTHERS("Outros")
}
