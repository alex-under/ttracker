package ru.alexunder.ttracker.core

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Task

@JsonCreator
constructor(@JsonProperty("id") val id: Long,
            @JsonProperty("name") val name: String) {


    // todo simplify equals/hashCode
    override fun equals(other: Any?): Boolean =
            when (other) {
                null -> false
                is Task -> this.id == other.id
                else -> false
            }

    override fun hashCode(): Int =
            id.hashCode()
}