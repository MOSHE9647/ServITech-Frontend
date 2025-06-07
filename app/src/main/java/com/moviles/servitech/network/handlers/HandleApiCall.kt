package com.moviles.servitech.network.handlers

import android.util.Log
import com.google.gson.Gson
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.responses.ErrorResponse
import com.moviles.servitech.repositories.helpers.Result
import retrofit2.Response

/**
 * Handles the API call and processes the response.
 * If the response is successful, it returns the data.
 * If the response is an error, it parses the error body
 * and returns an Result.Error.
 *
 * @param From The type of data returned from the API.
 * @param To The type of data to transform the API response into.
 * @param R The type of result to return, extending Result<To>.
 * @param logClass Optional class name for logging purposes.
 * @param remoteCall The suspend function that makes the remote API call.
 * @param localCall Optional suspend function for a local fallback call.
 * @param transform Function to transform the From type to To type.
 * @param onError Function to handle errors, returning an instance of R.
 * @param onSuccess Function to handle successful responses, returning an instance of R.
 * @return An [Result] instance of R, which can be either a success or an error.
 */
suspend inline fun <From, To, R : Result<To>> handleApiCall(
    logClass: String? = "HandleApiCall",
    remoteCall: () -> Response<ApiResponse<From>>,
    noinline localCall: (suspend () -> R)? = null,
    crossinline transform: (From) -> To,
    noinline onError: (message: String, fieldErrors: Map<String, String>) -> R,
    crossinline onSuccess: (To) -> R,
): R {
    return try {
        val response = remoteCall()
        if (response.isSuccessful) {
            response.body()?.data?.let {
                val transformed = transform(it)
                return onSuccess(transformed)
            } ?: return onError(response.body()?.message ?: "Unknown error", emptyMap())
        } else {
            parseErrorResponse(response.errorBody()?.string(), onError)
        }
    } catch (e: Exception) {
        Log.e("$logClass - handleApiCall", "Exception: ${e.message}")
        localCall?.invoke() ?: onError("Connection error", emptyMap())
    }
}

/**
 * Parses the error response from the API call.
 *
 * If the error body is not null or empty,
 * it attempts to parse it into an ErrorResponse object.
 *
 * If parsing is successful, it calls the onError function
 * with the error message and field errors.
 *
 * @param T The type of data expected in the result.
 * @param R The type of result to return, extending Result<T>.
 * @param errorBody The error body string from the API response.
 * @param onError Function to handle errors, returning an instance of R.
 * @return An instance of R, which can be either a success or an error.
 */
fun <T, R : Result<T>> parseErrorResponse(
    errorBody: String?,
    onError: (String, Map<String, String>) -> R
): R {
    return if (!errorBody.isNullOrEmpty()) {
        val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
        onError(errorResponse.message, errorResponse.errors)
    } else {
        onError("Unknown error", emptyMap())
    }
}