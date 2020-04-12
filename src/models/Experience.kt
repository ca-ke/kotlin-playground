package models

sealed class Experience {
    object Junior: Experience()
    object Pleno: Experience()
    object Senior: Experience()
}