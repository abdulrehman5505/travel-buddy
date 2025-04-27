package com.project.travelbuddy.model

data class Country(
    val name: Name,
    val capital: List<String>?,
    val region: String,
    val population: Long,
    val flags: Flags
)

data class Name(
    val common: String,
    val official: String
)

data class Flags(
    val png: String
)