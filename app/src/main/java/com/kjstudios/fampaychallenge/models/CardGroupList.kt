package com.kjstudios.fampaychallenge.models

data class CardGroupList(
    val card_groups: List<CardGroup>
)

data class CardGroup(
    val id: Int,
    val name: String,
    val card_type: Int,
    val cards: MutableList<Card>?,
    val design_type: String,
    val height: Int?,
    val is_scrollable: Boolean = true,
    val level: Int
)

data class Card(
    val name: String,
    val title: String?,
    val formatted_text: FormattedText?,
    val description: String?,
    val formatted_description: FormattedText?,
    val icon: CardImage?,
    val is_disabled: Boolean = false,
    val url: String?,
    val bg_color: String?,
    val bg_image: CardImage?,
    val bg_gradient: Gradient?,
    val cta: List<Cta>?
)

data class CardImage(
    val aspect_ratio: Double,
    val image_type: String,
    val image_url: String,
    val asset_type: String,
)

data class Gradient(
    val colours: List<Colors>?,
    val angle: Int = 0
)

data class Colors(
    val color: String
)

data class Cta(
    val bg_color: String?,
    val text: String,
    val text_color: String?,
    val url: String?
)

data class FormattedText(
    val align: String?,
    var Entities: List<Entity>?,
    var text: String
)

data class Entity(
    val text: String,
    val color: String?,
    val url: String?,
    val font_style: String?
)
