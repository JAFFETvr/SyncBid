package com.jaffetvr.syncbid.features.admin.data.repositories

import android.content.Context
import android.net.Uri
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.api.AdminApi
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.mapper.toDomain
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.CreateAuctionRequestDto
import com.jaffetvr.syncbid.features.admin.domain.entities.AdminStats
import com.jaffetvr.syncbid.features.admin.domain.entities.CreatedAuction
import com.jaffetvr.syncbid.features.admin.domain.entities.InventoryItem
import com.jaffetvr.syncbid.features.admin.domain.repositories.AdminRepository
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.ApiResponseDto
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.CreateAuctionResponseDto
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.InventoryItemDto
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.AdminStatsDto
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val adminApi: AdminApi,
    @ApplicationContext private val context: Context
) : AdminRepository {

    override suspend fun createAuction(
        name: String,
        description: String,
        basePrice: Double,
        durationMinutes: Int,
        imageUri: Uri?
    ): Result<CreatedAuction> = try {
        val endTimeIso = LocalDateTime.now().plusMinutes(durationMinutes.toLong()).toString()

        // 2. DTO con los 4 campos exactos de tu AuctionCreateRequest.java
        val requestDto = CreateAuctionRequestDto(
            title = name,
            description = description,
            startingPrice = basePrice,
            endTime = endTimeIso
        )

        // 3. Crear subasta (Paso 1)
        val response = adminApi.createAuction(requestDto)
        val apiResponse = response.body()

        if (response.isSuccessful && apiResponse?.data != null) {
            val createdAuction = apiResponse.data.toDomain()

            // 4. Subir imagen si existe (Paso 2)
            if (imageUri != null) {
                uploadImage(apiResponse.data.id, imageUri)
            }

            Result.success(createdAuction)
        } else {
            Result.failure(Exception(apiResponse?.message ?: "Error al crear subasta"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getInventory(): Result<List<InventoryItem>> = try {
        val response = adminApi.getInventory()
        val apiResponse = response.body()

        // Accedemos a apiResponse.data porque tu API envuelve todo en ApiResponse.java
        if (response.isSuccessful && apiResponse?.data != null) {
            Result.success(apiResponse.data.map { it.toDomain() })
        } else {
            Result.failure(Exception(apiResponse?.message ?: "Error al cargar inventario"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getStats(): Result<AdminStats> = try {
        val response = adminApi.getStats()
        val apiResponse = response.body()
        if (response.isSuccessful && apiResponse?.data != null) {
            Result.success(apiResponse.data.toDomain())
        } else {
            Result.failure(Exception(apiResponse?.message ?: "Error al cargar estadísticas"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun uploadImage(auctionId: Long, uri: Uri) {
        val file = uriToFile(uri) ?: return
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        // "file" coincide con @RequestParam("file") en tu AuctionController.java
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        adminApi.uploadAuctionImage(auctionId, body)
        file.delete()
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { output -> inputStream.copyTo(output) }
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}