package com.example.nobelserver.routing

import com.example.nobelserver.domain.usecase.GetNobelPrizesUseCase
import com.example.nobelserver.presentation.dto.ErrorResponse
import com.example.nobelserver.presentation.mapper.toResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configurePrizeRoutes(prizeUseCase: GetNobelPrizesUseCase) {
    routing {
        route("/prizes") {
            get {
                val prizes = prizeUseCase.getAll().map { it.toResponse() }
                call.respond(prizes)
            }

            get("/{year}/{category}") {
                val year = call.parameters["year"]?.toIntOrNull()
                val category = call.parameters["category"]

                if (year == null || category.isNullOrBlank()) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = ErrorResponse("Year must be a number and category must not be blank")
                    )
                    return@get
                }

                val prize = prizeUseCase.getByYearAndCategory(year, category)

                if (prize == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = ErrorResponse("Prize was not found")
                    )
                    return@get
                }

                call.respond(prize.toResponse())
            }

            get("/{year}/{category}/laureates") {
                val year = call.parameters["year"]?.toIntOrNull()
                val category = call.parameters["category"]

                if (year == null || category.isNullOrBlank()) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = ErrorResponse("Year must be a number and category must not be blank")
                    )
                    return@get
                }

                val laureates = prizeUseCase.getLaureates(year, category)

                if (laureates == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = ErrorResponse("Prize was not found")
                    )
                    return@get
                }

                call.respond(laureates.map { it.toResponse() })
            }
        }
    }
}
