package ru.alexunder.ttracker.core

data class Task(
        val id: Long,
        val name: String) {

    override fun equals(other: Any?): Boolean =
            when (other) {
                null -> false
                is Task -> this.id == other.id
                else -> false
            }

    override fun hashCode(): Int =
            id.hashCode()
}