package com.moviles.servitech.model.mappers

import com.moviles.servitech.common.Utils.doubleToRequestBody
import com.moviles.servitech.common.Utils.textToRequestBody
import com.moviles.servitech.database.entities.repairRequest.RepairRequestEntity
import com.moviles.servitech.model.RepairRequest
import com.moviles.servitech.network.requests.repairRequest.CreateRepairRequest
import com.moviles.servitech.network.responses.repairRequest.RepairRequestResponse

fun RepairRequest.toEntity(): RepairRequestEntity {
    return RepairRequestEntity(
        id = this.id ?: 0,
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

fun RepairRequestEntity.toModel(): RepairRequest {
    return RepairRequest(
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

fun RepairRequestEntity.toResponse(): RepairRequestResponse {
    return RepairRequestResponse(
        receiptNumber = this.receiptNumber ?: "",
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

fun List<RepairRequestEntity>.entityToModelList(): List<RepairRequest> = this.map { it.toModel() }
fun List<RepairRequestResponse>.responseToModelList(): List<RepairRequest> =
    this.map { it.toModel() }