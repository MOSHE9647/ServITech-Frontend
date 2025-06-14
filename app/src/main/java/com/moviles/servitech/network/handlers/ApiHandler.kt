package com.moviles.servitech.network.handlers

import android.util.Log
import com.google.gson.Gson
import com.moviles.servitech.network.responses.ApiResponse
import com.moviles.servitech.network.responses.ErrorResponse
import com.moviles.servitech.repositories.helpers.Result
import retrofit2.Response

/**
 * ApiHandler is a utility object that provides methods to handle API calls.
 * It processes the response from the API, transforming it into a desired format
 * and handling errors appropriately.
 */
object ApiHandler {

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
     * @param transformTo Function to transform the From type to To type.
     * @param onCallError Function to handle errors, returning an instance of R.
     * @param onRemoteSuccess Function to handle successful responses, returning an instance of R.
     * @return An [Result] instance of R, which can be either a success or an error.
     */
    suspend inline fun <From, To, R : Result<To>> handleApiCall(
        errorMessage: String,
        logClass: String? = "HandleApiCall",
        remoteCall: () -> Response<ApiResponse<From>>,
        noinline localCall: (suspend () -> R)? = null,
        crossinline transformTo: (From) -> To,
        noinline onCallError: (message: String, fieldErrors: Map<String, String>) -> R,
        crossinline onRemoteSuccess: suspend (To) -> R,
    ): R {
        return try {
            val response = remoteCall()
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    val transformed = transformTo(it)
                    return onRemoteSuccess(transformed)
                } ?: return onCallError(response.body()?.message ?: errorMessage, emptyMap())
            } else {
                parseErrorResponse(response.errorBody()?.string(), errorMessage, onCallError)
            }
        } catch (e: Exception) {
            Log.e("$logClass - handleApiCall", "Exception: ${e.message}")
            localCall?.invoke() ?: onCallError(e.localizedMessage ?: errorMessage, emptyMap())
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
        errorMessage: String,
        onError: (String, Map<String, String>) -> R
    ): R {
        return if (!errorBody.isNullOrEmpty()) {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            onError(errorResponse.message, errorResponse.errors)
        } else {
            onError(errorMessage, emptyMap())
        }
    }

    /**
     * Handles a any action safely, catching exceptions and returning an error result.
     *
     * @param T The type of data expected in the result.
     * @param R The type of result to return, extending Result<T>.
     * @param errorMessage The default error message to return if an exception occurs.
     * @param logClass Optional class name for logging purposes.
     * @param onSuccess Function to handle successful actions, returning an instance of R.
     * @param onError Function to handle errors, returning an instance of R.
     * @return An instance of R, which can be either a success or an error.
     */
    suspend inline fun <T, R : Result<T>> handleActionSafely(
        errorMessage: String,
        logClass: String? = "HandleLocalCall",
        crossinline onSuccess: suspend () -> R,
        noinline onError: (message: String, fieldErrors: Map<String, String>) -> R
    ): R {
        return try {
            onSuccess()
        } catch (e: Exception) {
            Log.e("$logClass - handleLocalCall", "Exception: ${e.message}")
            onError(e.localizedMessage ?: errorMessage, emptyMap())
        }
    }
}