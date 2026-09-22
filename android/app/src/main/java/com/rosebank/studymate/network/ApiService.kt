package com.rosebank.studymate.network

import com.rosebank.studymate.model.AuthResponse
import com.rosebank.studymate.model.LoginRequest
import com.rosebank.studymate.model.RegisterRequest
import com.rosebank.studymate.model.Task
import com.rosebank.studymate.model.UpdateSettingsRequest
import com.rosebank.studymate.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

// Matches the routes exposed by the StudyMate API (see api/src/routes/*.js).
// Every task/user endpoint requires the "Authorization: Bearer <token>"
// header, which is passed explicitly rather than via an OkHttp interceptor
// so the flow is easy to follow and demonstrate in the video.
interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @GET("api/tasks")
    suspend fun getTasks(@Header("Authorization") bearerToken: String): Response<List<Task>>

    @POST("api/tasks")
    suspend fun createTask(@Header("Authorization") bearerToken: String, @Body body: Task): Response<Task>

    @PUT("api/tasks/{id}")
    suspend fun updateTask(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<Task>

    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(@Header("Authorization") bearerToken: String, @Path("id") id: String): Response<Unit>

    @GET("api/users/me")
    suspend fun getProfile(@Header("Authorization") bearerToken: String): Response<User>

    @PUT("api/users/me")
    suspend fun updateProfile(
        @Header("Authorization") bearerToken: String,
        @Body body: UpdateSettingsRequest
    ): Response<User>
}
