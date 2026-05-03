package com.example.crm.infrastructure.config

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.nio.file.Files
import java.nio.file.Path

@Component
class UploadFileResourceResolver(
    private val uploadProperties: UploadProperties
) {
    fun resolveResource(filePath: String): Resource {
        val base = Path.of(uploadProperties.baseDirectory).toAbsolutePath().normalize()
        val normalizedRelative = filePath.removePrefix("/").removePrefix("\\")
        val resolved = base.resolve(normalizedRelative).normalize()
        if (!resolved.startsWith(base) || !Files.exists(resolved) || !Files.isRegularFile(resolved)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Arquivo não encontrado")
        }
        return UrlResource(resolved.toUri())
    }

    fun resolveMediaType(contentType: String): MediaType =
        runCatching { MediaType.parseMediaType(contentType) }
            .getOrDefault(MediaType.APPLICATION_OCTET_STREAM)
}
