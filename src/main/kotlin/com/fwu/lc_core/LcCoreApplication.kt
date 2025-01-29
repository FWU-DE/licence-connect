package com.fwu.lc_core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class LcCoreApplication

fun main(args: Array<String>) {
    runApplication<LcCoreApplication>(*args)
}

@RestController
class HtmlController {
    @GetMapping("/hello")
    fun hello(name: String?): String {
        val iName: String = name ?: "World"
		return "Hello, $iName!"
    }
}