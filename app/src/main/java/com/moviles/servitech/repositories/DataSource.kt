package com.moviles.servitech.repositories

sealed class DataSource {
    object Remote : DataSource()
    object Local : DataSource()
}