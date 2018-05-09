package ru.alexunder.ttracker.core

data class Task(
        val id: Long,
        val name: String) {

    override fun equals(other: Any?) : Boolean =
            if (other == null)
                false
            else
                IdWrapper(this) == IdWrapper(other as Task)

    override fun hashCode(): Int =
            IdWrapper(this).hashCode()
}

private data class IdWrapper(val id: Long) {
    constructor(task: Task) : this(id = task.id)
}
