package com.example.crm.infrastructure.storage

import com.example.crm.application.port.output.StoreFileCommand
import com.example.crm.entity.FileType
import com.example.crm.infrastructure.config.UploadProperties
import com.example.crm.support.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.file.Path
import javax.imageio.ImageIO

class FileStorageServiceTest {

    @TempDir
    lateinit var tempDir: Path

    @Test
    fun `deve manter proporcao ao receber largura e altura`() {
        val service = FileStorageService(UploadProperties(baseDirectory = tempDir.toString()))

        val stored = service.store(
            command(width = 300, height = 300)
        )

        stored.width shouldBe 300
        stored.height shouldBe 150
    }

    @Test
    fun `deve manter proporcao ao receber apenas largura`() {
        val service = FileStorageService(UploadProperties(baseDirectory = tempDir.toString()))

        val stored = service.store(
            command(width = 120, height = null)
        )

        stored.width shouldBe 120
        stored.height shouldBe 60
    }

    private fun command(width: Int?, height: Int?) = StoreFileCommand(
        content = imageBytes(width = 400, height = 200),
        extension = "png",
        contentType = "image/png",
        fileType = FileType.PRODUCT,
        tenantId = 1,
        entityId = 10,
        targetWidth = width,
        targetHeight = height,
        quality = null
    )

    private fun imageBytes(width: Int, height: Int): ByteArray {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()
        try {
            graphics.color = Color.BLUE
            graphics.fillRect(0, 0, width, height)
        } finally {
            graphics.dispose()
        }
        val output = ByteArrayOutputStream()
        ImageIO.write(image, "png", output)
        return output.toByteArray()
    }
}
