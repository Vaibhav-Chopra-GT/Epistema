package com.example.epistema.localization

object StringResources {
    private var currentLanguage = "English"

    fun setLanguage(lang: String) {
        currentLanguage = lang
    }

    fun getString(key: String): String {
        return when (currentLanguage) {
            "Hindi" -> hindiStrings[key] ?: key
            "Spanish" -> spanishStrings[key] ?: key
            "French" -> frenchStrings[key] ?: key
            else -> englishStrings[key] ?: key
        }
    }

    private val englishStrings = mapOf(
        "settings_title" to "Settings",
        "language" to "Language",
        "theme" to "Theme",
        "font_size" to "Font Size",
        "light" to "Light",
        "dark" to "Dark",
        "small" to "Small",
        "medium" to "Medium",
        "large" to "Large",
        "article_of_the_day" to "Article of the Day",
        "category_art" to "Art",
        "category_history" to "History",
        "category_geography" to "Geography",
        "category_science" to "Science",
        "category_politics" to "Politics",
        "category_mathematics" to "Mathematics",
        "category_literature" to "Literature",
        "category_philosophy" to "Philosophy"
    )

    private val hindiStrings = mapOf(
        "settings_title" to "सेटिंग्स",
        "language" to "भाषा",
        "theme" to "थीम",
        "font_size" to "फ़ॉन्ट आकार",
        "light" to "हल्का",
        "dark" to "गहरा",
        "small" to "छोटा",
        "medium" to "मध्यम",
        "large" to "बड़ा",
        "article_of_the_day" to "दिन का लेख",
        "category_art" to "कला",
        "category_history" to "इतिहास",
        "category_geography" to "भूगोल",
        "category_science" to "विज्ञान",
        "category_politics" to "राजनीति",
        "category_mathematics" to "गणित",
        "category_literature" to "साहित्य",
        "category_philosophy" to "दर्शन"
    )

    private val spanishStrings = mapOf(
        "settings_title" to "Configuración",
        "language" to "Idioma",
        "theme" to "Tema",
        "font_size" to "Tamaño de fuente",
        "light" to "Claro",
        "dark" to "Oscuro",
        "small" to "Pequeño",
        "medium" to "Mediano",
        "large" to "Grande",
        "article_of_the_day" to "Artículo del Día",
        "category_art" to "Arte",
        "category_history" to "Historia",
        "category_geography" to "Geografía",
        "category_science" to "Ciencia",
        "category_politics" to "Política",
        "category_mathematics" to "Matemáticas",
        "category_literature" to "Literatura",
        "category_philosophy" to "Filosofía"
    )

    private val frenchStrings = mapOf(
        "settings_title" to "Paramètres",
        "language" to "Langue",
        "theme" to "Thème",
        "font_size" to "Taille de police",
        "light" to "Clair",
        "dark" to "Sombre",
        "small" to "Petit",
        "medium" to "Moyen",
        "large" to "Grand",
        "article_of_the_day" to "Article du Jour",
        "category_art" to "Art",
        "category_history" to "Histoire",
        "category_geography" to "Géographie",
        "category_science" to "Science",
        "category_politics" to "Politique",
        "category_mathematics" to "Mathématiques",
        "category_literature" to "Littérature",
        "category_philosophy" to "Philosophie"
    )

    fun getCategories(): List<String> {
        val keys = listOf(
            "category_art",
            "category_history",
            "category_geography",
            "category_science",
            "category_politics",
            "category_mathematics",
            "category_literature",
            "category_philosophy"
        )

        return keys.map { getString(it) }
    }
}