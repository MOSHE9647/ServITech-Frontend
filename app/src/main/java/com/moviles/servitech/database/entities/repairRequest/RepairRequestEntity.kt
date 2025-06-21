package com.moviles.servitech.database.entities.repairRequest

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moviles.servitech.common.Constants

@Entity(tableName = Constants.REPAIR_REQ_TABLE)
data class RepairRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val receiptNumber: String? = "",
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val articleName: String,
    val articleType: String,
    val articleBrand: String,
    val articleModel: String,
    val articleSerialNumber: String? = "",
    val articleAccesories: String? = "",
    val articleProblem: String,
    val repairStatus: String,
    val repairDetails: String? = "",
    val repairPrice: Double? = 0.0,
    val receivedAt: String,
    val repairedAt: String? = ""
)