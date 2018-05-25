package ru.alexunder.ttracker.core

import java.nio.file.Files
import java.nio.file.Paths

object FileNames {
    private val userHome = FilePath(System.getProperty("user.home"))
    private val root = FilePath("${userHome.name}/.ttracker")

    val workLog  = FilePath("$root/work-log.txt")
    val tasks = FilePath("$root/tasks.txt")

    init {
        Files.createDirectory(root.path)
    }
}

class FilePath(val name : String) {
    val path = Paths.get(name)!!
}