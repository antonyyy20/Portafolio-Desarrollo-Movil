package com.example.frontend.data.repository

import com.example.frontend.core.network.NetworkErrors
import com.example.frontend.core.network.QuickvntApi
import com.example.frontend.data.dto.AnalyticsResponse
import com.example.frontend.data.dto.CheckinRequest
import com.example.frontend.data.dto.CheckinResponse
import com.example.frontend.data.dto.TicketCreateRequest
import com.example.frontend.data.dto.TicketResponse

class TicketRepository(
    private val api: QuickvntApi
) {
    suspend fun registerToEvent(
        eventId: String,
        formResponse: Map<String, Any?>
    ): Result<TicketResponse> = runCatching {
        api.registerToEvent(
            eventId = eventId,
            body = TicketCreateRequest(eventId = eventId, formResponse = formResponse)
        )
    }.mapError()

    suspend fun myTickets(): Result<List<TicketResponse>> = runCatching {
        api.myTickets()
    }.mapError()

    suspend fun getTicket(id: String): Result<TicketResponse> = runCatching {
        api.getTicket(id)
    }.mapError()

    suspend fun validateCheckin(request: CheckinRequest): Result<CheckinResponse> = runCatching {
        api.validateCheckin(request)
    }.mapError()

    suspend fun getAnalytics(eventId: String): Result<AnalyticsResponse> = runCatching {
        api.getEventAnalytics(eventId)
    }.mapError()
}

private fun <T> Result<T>.mapError(): Result<T> = fold(
    onSuccess = { Result.success(it) },
    onFailure = { Result.failure(IllegalStateException(NetworkErrors.userMessage(it))) }
)
