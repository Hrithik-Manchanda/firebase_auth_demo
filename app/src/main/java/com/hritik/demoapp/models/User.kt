package com.hritik.demoapp.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var mobile: String? = "",
    var name: String? = "",
    var email: String? = ""
)