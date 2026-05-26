package com.example.nobelserver.routing

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureDocsRoutes() {
    routing {
        get("/docs") {
            call.respondText(docsHtml, ContentType.Text.Html)
        }

        get("/openapi.yaml") {
            call.respondText(openApiYaml, ContentType.Text.Plain)
        }
    }
}

private val docsHtml = """
    <!doctype html>
    <html lang="ru">
    <head>
        <meta charset="utf-8">
        <title>Nobel Prize API Docs</title>
        <link rel="stylesheet" href="https://unpkg.com/swagger-ui-dist@5/swagger-ui.css">
    </head>
    <body>
        <div id="swagger-ui"></div>
        <script src="https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js"></script>
        <script>
            window.onload = () => {
                window.ui = SwaggerUIBundle({
                    url: '/openapi.yaml',
                    dom_id: '#swagger-ui'
                });
            };
        </script>
    </body>
    </html>
""".trimIndent()

private val openApiYaml = """
    openapi: 3.0.3
    info:
      title: Nobel Prize API
      version: 1.0.0
      description: Ktor backend with PostgreSQL, JWT auth and favorite Nobel prizes.
    servers:
      - url: http://localhost:8080
    paths:
      /login:
        post:
          summary: Login and receive JWT token
          requestBody:
            required: true
            content:
              application/json:
                schema:
                  ${'$'}ref: '#/components/schemas/LoginRequest'
          responses:
            '200':
              description: JWT token
            '401':
              description: Invalid credentials
      /prizes:
        get:
          summary: List Nobel prizes stored in PostgreSQL
          responses:
            '200':
              description: Prize list
      /prizes/{year}/{category}:
        get:
          summary: Get prize details
          parameters:
            - name: year
              in: path
              required: true
              schema:
                type: integer
            - name: category
              in: path
              required: true
              schema:
                type: string
          responses:
            '200':
              description: Prize details
            '404':
              description: Prize was not found
      /prizes/{year}/{category}/laureates:
        get:
          summary: Get laureates for prize
          parameters:
            - name: year
              in: path
              required: true
              schema:
                type: integer
            - name: category
              in: path
              required: true
              schema:
                type: string
          responses:
            '200':
              description: Laureate list
      /users/me:
        get:
          security:
            - bearerAuth: []
          summary: Current user profile
          responses:
            '200':
              description: User profile
      /users/me/prizes:
        get:
          security:
            - bearerAuth: []
          summary: Current user favorite prizes
          responses:
            '200':
              description: Favorite prize list
      /users/me/prizes/{prizeId}:
        post:
          security:
            - bearerAuth: []
          summary: Add prize to favorites
          parameters:
            - name: prizeId
              in: path
              required: true
              schema:
                type: integer
          responses:
            '201':
              description: Prize was added
        delete:
          security:
            - bearerAuth: []
          summary: Remove prize from favorites
          parameters:
            - name: prizeId
              in: path
              required: true
              schema:
                type: integer
          responses:
            '200':
              description: Prize was removed
    components:
      securitySchemes:
        bearerAuth:
          type: http
          scheme: bearer
          bearerFormat: JWT
      schemas:
        LoginRequest:
          type: object
          required: [username, password]
          properties:
            username:
              type: string
            password:
              type: string
""".trimIndent()
