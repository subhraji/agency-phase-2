package com.example.agencyphase2.model.pojo.get_ephemeral_key

data class GetEphemeralKeyResponse(
    val associated_objects: List<AssociatedObject>,
    val created: Int,
    val expires: Int,
    val id: String,
    val livemode: Boolean,
    val `object`: String,
    val secret: String
)