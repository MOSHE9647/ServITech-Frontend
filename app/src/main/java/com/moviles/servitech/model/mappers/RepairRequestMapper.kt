package com.moviles.servitech.model.mappers

import android.content.Context
import com.moviles.servitech.common.Utils.doubleToRequestBody
import com.moviles.servitech.common.Utils.textToRequestBody
import com.moviles.servitech.database.entities.repairRequest.RepairRequestEntity
import com.moviles.servitech.database.entities.repairRequest.RepairRequestWithImagesEntity
import com.moviles.servitech.model.RepairRequest
import com.moviles.servitech.network.requests.repairRequest.CreateRepairRequest
import com.moviles.servitech.network.requests.repairRequest.UpdateRepairRequest
import com.moviles.servitech.network.responses.repairRequest.RepairRequestResponse

fun RepairRequest.toEntity(): RepairRequestEntity {
    return RepairRequestEntity(
        id = this.id,
        receiptNumber = this.receiptNumber,
        customerName = this.customerName,
        customerPhone = this.customerPhone,
        customerEmail = this.customerEmail,
        articleName = this.articleName,
        articleType = this.articleType,
        articleBrand = this.articleBrand,
        articleModel = this.articleModel,
        articleSerialNumber = this.articleSerialNumber,
        articleAccesories = this.articleAccesories,
        articleProblem = this.articleProblem,
        repairStatus = this.repairStatus,
        repairDetails = this.repairDetails,
        repairPrice = this.repairPrice,
        receivedAt = this.receivedAt,
        repairedAt = this.repairedAt
    )
}

fun RepairRequest.toCreateRequest(): CreateRepairRequest {
    return CreateRepairRequest(
        customerName = textToRequestBody(this.customerName),
        customerPhone = textToRequestBody(this.customerPhone),
        customerEmail = textToRequestBody(this.customerEmail),
        articleName = textToRequestBody(this.articleName),
        articleType = textToRequestBody(this.articleType),
        articleBrand = textToRequestBody(this.articleBrand),
        articleModel = textToRequestBody(this.articleModel),
        articleSerialNumber = textToRequestBody(this.articleSerialNumber ?: ""),
        articleAccesories = textToRequestBody(this.articleAccesories ?: ""),
        articleProblem = textToRequestBody(this.articleProblem),
        repairStatus = textToRequestBody(this.repairStatus),
        repairDetails = textToRequestBody(this.repairDetails ?: ""),
        repairPrice = doubleToRequestBody(this.repairPrice ?: 0.0),
        receivedAt = textToRequestBody(this.receivedAt),
        repairedAt = textToRequestBody(this.repairedAt ?: ""),
        images = this.images?.mapNotNull { image -> image.file }
    )
}

fun RepairRequest.toUpdateRequest(): UpdateRepairRequest {
    return UpdateRepairRequest(
        articleSerialNumber = textToRequestBody(this.articleSerialNumber ?: ""),
        articleAccesories = textToRequestBody(this.articleAccesories ?: ""),
        repairStatus = textToRequestBody(this.repairStatus),
        repairDetails = textToRequestBody(this.repairDetails ?: ""),
        repairPrice = doubleToRequestBody(this.repairPrice ?: 0.0),
        repairedAt = textToRequestBody(this.repairedAt ?: ""),
    )
}

fun RepairRequestWithImagesEntity.toModel(context: Context? = null): RepairRequest {
    return RepairRequest(
        id = this.repairRequest.id,
        receiptNumber = this.repairRequest.receiptNumber,
        customerName = this.repairRequest.customerName,
        customerPhone = this.repairRequest.customerPhone,
        customerEmail = this.repairRequest.customerEmail,
        articleName = this.repairRequest.articleName,
        articleType = this.repairRequest.articleType,
        articleBrand = this.repairRequest.articleBrand,
        articleModel = this.repairRequest.articleModel,
        articleSerialNumber = this.repairRequest.articleSerialNumber,
        articleAccesories = this.repairRequest.articleAccesories,
        articleProblem = this.repairRequest.articleProblem,
        repairStatus = this.repairRequest.repairStatus,
        repairDetails = this.repairRequest.repairDetails,
        repairPrice = this.repairRequest.repairPrice,
        receivedAt = this.repairRequest.receivedAt,
        repairedAt = this.repairRequest.repairedAt,
        images = this.images?.map { it.toModel(context) }
    )
}

fun RepairRequestWithImagesEntity.toEntity(): RepairRequestEntity {
    return RepairRequestEntity(
        id = this.repairRequest.id,
        receiptNumber = this.repairRequest.receiptNumber,
        customerName = this.repairRequest.customerName,
        customerPhone = this.repairRequest.customerPhone,
        customerEmail = this.repairRequest.customerEmail,
        articleName = this.repairRequest.articleName,
        articleType = this.repairRequest.articleType,
        articleBrand = this.repairRequest.articleBrand,
        articleModel = this.repairRequest.articleModel,
        articleSerialNumber = this.repairRequest.articleSerialNumber,
        articleAccesories = this.repairRequest.articleAccesories,
        articleProblem = this.repairRequest.articleProblem,
        repairStatus = this.repairRequest.repairStatus,
        repairDetails = this.repairRequest.repairDetails,
        repairPrice = this.repairRequest.repairPrice,
        receivedAt = this.repairRequest.receivedAt,
        repairedAt = this.repairRequest.repairedAt
    )
}

fun RepairRequestResponse.toModel(): RepairRequest {
    return RepairRequest(
        receiptNumber = this.receiptNumber,
        customerName = this.customerName,
        customerPhone = this.customerPhone,
        customerEmail = this.customerEmail,
        articleName = this.articleName,
        articleType = this.articleType,
        articleBrand = this.articleBrand,
        articleModel = this.articleModel,
        articleSerialNumber = this.articleSerialNumber,
        articleAccesories = this.articleAccesories,
        articleProblem = this.articleProblem,
        repairStatus = this.repairStatus,
        repairDetails = this.repairDetails,
        repairPrice = this.repairPrice,
        receivedAt = this.receivedAt,
        repairedAt = this.repairedAt,
        images = this.images?.toModelList()
    )
}

fun List<RepairRequest>.modelToEntityList(): List<RepairRequestEntity> = this.map { it.toEntity() }
fun List<RepairRequestResponse>.responseToModelList(): List<RepairRequest> =
    this.map { it.toModel() }
fun List<RepairRequestWithImagesEntity>.withImagesToModelList(context: Context? = null): List<RepairRequest> =
    this.map { it.toModel(context) }