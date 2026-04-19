package com.example.crm.domain.model

enum class FileType(val displayName: String) {
    PRODUCT("Produto"),
    SERVICE("Serviço"),
    CUSTOMER("Cliente"),
    WORKER("Colaborador"),
    TENANT("Tenant"),
    USER("Usuário"),
    CATEGORY("Categoria"),
    SLIDE("Slide"),
    BANNER("Banner"),
    OTHERS("Outros")
}
