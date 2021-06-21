package limitmutability

import java.awt.Color

class User(val name: String)

fun main() {

    val users = listOf(User("Name1"), User("Name2"), User("Name3"), User("Name4"), User("Name5"), User("Name6"))

    // Scoping
    // Bad Practice
    var user: User
    for (i in users.indices) {
        user = users[i]
        println("Nome: ${user.name}")
    }

    // Better
    for (i in users.indices) {
        val user = users[i]
        println("Nome: ${user.name}")
    }

    // Nicer syntax
    for((i, user) in users.withIndex()) {
        println("Nome ${user.name} naa posição $i")
    }
}


// Bad
fun updateWeather1(degrees: Int) {
    val description: String
    val color: Int

    if(degrees < 5) {
        description = "cold"
        color = 123
    } else if(degrees < 23) {
        description = "mild"
        color = 234
    } else {
        description = "hot"
        color = 345
    }
}

fun updateWeather2(degrees: Int) {
    val (description, color) = when {
        degrees < 5 -> "cold" to 123
        degrees < 23 -> "mild" to 234
        else -> "hot" to 345
    }
}