package com.moviles.servitech.repositories

/**
 * Represents the source of data in the application.
 * It can be either from a remote server or from local storage.
 *
 * @property Remote Represents data coming from a remote source, such as a web API.
 * @property Local Represents data stored locally, such as in a database or local file system.
 */
sealed class DataSource {
    object Remote : DataSource()
    object Local : DataSource()
}