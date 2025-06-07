package com.moviles.servitech.repositories.helpers

/**
 * Interface representing the status of an operation.
 * It can either be a success with data of type T,
 * or an error with a message and optional field errors.
 *
 * @param T The type of data returned on success.
 */
interface Result<out T>