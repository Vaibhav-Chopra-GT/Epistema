package com.example.epistema



class WikipediaRepository {
    fun getArticlesBySection(sectionName: String): List<Article> {
        return listOf(
            Article(
                pageId = 1,
                title = "$sectionName - The Physics of Time Travel: Fact or Fiction?",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSOQq1HMgChjQbOygUJpxnedIpfieiWQSbCpg&s"
            )
            ,
            Article(
                pageId = 2,
                title = "$sectionName - Exploring the Multiverse Theory",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS66FccrsEeh3C2P64oJx26UvEjJulxF4VpJQ&s"
            ),
            Article(
                pageId = 3,
                title = "$sectionName - Black Holes: A Journey Beyond the Event Horizon",
                imageUrl = "https://www.thebrighterside.news/uploads/2024/04/95ac29_9f80ad27dd8d45efad504ce809d2ea6fmv2.jpeg?auto=webp&auto=webp&optimize=high&quality=70&width=1440"
            ),

            Article(
                pageId = 4,
                title = "$sectionName - How AI is Transforming Scientific Research",
                imageUrl = "https://erepublic.brightspotcdn.com/dims4/default/2400690/2147483647/strip/true/crop/1000x486+0+66/resize/1440x700!/quality/90/?url=http%3A%2F%2Ferepublic-brightspot.s3.us-west-2.amazonaws.com%2Fae%2F45%2Fbaf0cf324b789aac10caf77fa5e1%2Fai-research.jpg"
            ),
            Article(
                pageId = 5,
                title = "$sectionName - The Mystery of Quantum Entanglement",
                imageUrl = "https://static.scientificamerican.com/sciam/cache/file/A4B2CAD0-B9CD-4C06-82FF402AC2D4E41D_source.jpg?w=1200"
            )
        )
    }
}
