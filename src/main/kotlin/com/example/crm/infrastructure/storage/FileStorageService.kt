package com.example.crm.infrastructure.storage

import com.example.crm.application.port.output.FileStorage
import com.example.crm.application.port.output.StoreFileCommand
import com.example.crm.application.port.output.StoredFile
import com.example.crm.infrastructure.config.UploadProperties
import org.springframework.stereotype.Component
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.Locale
import java.util.UUID
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.MemoryCacheImageOutputStream
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Component
class FileStorageService(
    private val properties: UploadProperties
) : FileStorage {

    override fun store(command: StoreFileCommand): StoredFile {
        val extension = normalizeExtension(command.extension)
        val fileName = "${UUID.randomUUID()}.$extension"
        val relative = relativePath(command, fileName)
        val absolute = Path.of(properties.baseDirectory).resolve(relative).normalize()
        Files.createDirectories(absolute.parent)

        val (bytes, width, height) = processBytes(command, extension)
        writeAtomic(absolute, bytes)

        return StoredFile(
            fileName = fileName,
            filePath = "/$relative".replace('\\', '/'),
            contentType = mimeFor(extension, command.contentType),
            size = bytes.size.toLong(),
            width = width,
            height = height
        )
    }

    private fun relativePath(command: StoreFileCommand, fileName: String): String {
        val typeDir = command.fileType.name.lowercase(Locale.ROOT)
        return "uploads/${command.tenantId}/$typeDir/${command.entityId}/$fileName"
    }

    private fun writeAtomic(target: Path, bytes: ByteArray) {
        val tmp = Files.createTempFile(target.parent, "upl-", ".part")
        Files.write(tmp, bytes)
        try {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } catch (_: Exception) {
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun processBytes(command: StoreFileCommand, extension: String): Triple<ByteArray, Int?, Int?> {
        val source = ImageIO.read(ByteArrayInputStream(command.content))
            ?: return Triple(command.content, null, null)
        val resized = resizeIfNeeded(source, command.targetWidth, command.targetHeight)
        val bytes = encode(resized, extension, command.quality)
        return Triple(bytes, resized.width, resized.height)
    }

    private fun resizeIfNeeded(src: BufferedImage, targetWidth: Int?, targetHeight: Int?): BufferedImage {
        if (targetWidth == null && targetHeight == null) return src

        val widthScale = targetWidth?.toDouble()?.div(src.width) ?: Double.POSITIVE_INFINITY
        val heightScale = targetHeight?.toDouble()?.div(src.height) ?: Double.POSITIVE_INFINITY
        val scale = min(widthScale, heightScale)
        val newWidth = max(1, (src.width * scale).roundToInt())
        val newHeight = max(1, (src.height * scale).roundToInt())

        return if (newWidth == src.width && newHeight == src.height) {
            src
        } else {
            val out = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
            val g: Graphics2D = out.createGraphics()
            try {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
                g.drawImage(src, 0, 0, newWidth, newHeight, null)
            } finally {
                g.dispose()
            }
            out
        }
    }

    private fun encode(image: BufferedImage, extension: String, quality: Int?): ByteArray {
        val format = when (extension) {
            "jpg", "jpeg" -> "jpeg"
            else -> extension
        }
        val output = ByteArrayOutputStream()
        val writers = ImageIO.getImageWritersByFormatName(format)
        if (!writers.hasNext()) {
            ImageIO.write(image, format, output)
            return output.toByteArray()
        }
        val writer = writers.next()
        val target = if (format == "jpeg") flatten(image) else image
        MemoryCacheImageOutputStream(output).use { ios ->
            writer.output = ios
            val param = writer.defaultWriteParam
            if (quality != null && param.canWriteCompressed()) {
                param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                param.compressionType = param.compressionTypes?.firstOrNull() ?: param.compressionType
                param.compressionQuality = quality.toFloat() / MAX_QUALITY
            }
            writer.write(null, IIOImage(target, null, null), param)
            writer.dispose()
        }
        return output.toByteArray()
    }

    private fun flatten(image: BufferedImage): BufferedImage {
        if (image.type == BufferedImage.TYPE_INT_RGB) return image
        val flat = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        val g = flat.createGraphics()
        try {
            g.drawImage(image, 0, 0, java.awt.Color.WHITE, null)
        } finally {
            g.dispose()
        }
        return flat
    }

    private fun normalizeExtension(ext: String): String {
        val lower = ext.lowercase(Locale.ROOT).trim().removePrefix(".")
        return if (lower.isBlank()) "bin" else lower
    }

    private fun mimeFor(extension: String, fallback: String): String = when (extension) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "webp" -> "image/webp"
        else -> fallback
    }

    companion object {
        private const val MAX_QUALITY: Float = 100f
    }
}
