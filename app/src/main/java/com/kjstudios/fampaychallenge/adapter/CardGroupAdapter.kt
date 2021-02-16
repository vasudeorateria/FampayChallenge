package com.kjstudios.fampaychallenge.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.kjstudios.fampaychallenge.R
import com.kjstudios.fampaychallenge.SharedPrefs
import com.kjstudios.fampaychallenge.models.Card
import com.kjstudios.fampaychallenge.models.Entity
import com.kjstudios.fampaychallenge.models.Gradient


class CardGroupAdapter(
    private val context: Context,
    private val designType: String,
    private val height: Int?,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cards: List<Card> = ArrayList()

    private val sharedPrefs = SharedPrefs(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> SmallCard(
                LayoutInflater.from(context).inflate(R.layout.small_card, parent, false)
            )

            3 -> BigDisplayCard(
                LayoutInflater.from(context).inflate(R.layout.big_display_card, parent, false)
            )

            5 -> CardWithImage(
                LayoutInflater.from(context).inflate(R.layout.card_with_imageview, parent, false)
            )

            6 -> SmallCard(
                LayoutInflater.from(context).inflate(R.layout.small_card, parent, false)
            )

            else -> CardWithImage(
                LayoutInflater.from(context).inflate(R.layout.card_with_imageview, parent, false)
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (designType == "HC5" || designType == "HC9") {
            (holder as CardWithImage).bind(position)
        }
        if (designType == "HC3") {
            (holder as BigDisplayCard).bind(position)
        }
        if (designType == "HC1" || designType == "HC6") {
            (holder as SmallCard).bind(position)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (designType) {
            "HC1" -> 1
            "HC3" -> 3
            "HC5" -> 5
            "HC6" -> 6
            else -> 9
        }
    }

    override fun getItemCount(): Int = cards.size

    inner class SmallCard(itemview: View) : RecyclerView.ViewHolder(itemview) {

        private val small_card_cl: LinearLayout by lazy {
            itemview.findViewById(R.id.small_card_cl)
        }
        private val small_card_image: ImageView by lazy {
            itemview.findViewById(R.id.small_card_image)
        }
        private val small_card_text: TextView by lazy {
            itemview.findViewById(R.id.small_card_text)
        }
        private val small_card_arrow: ImageView by lazy {
            itemview.findViewById(R.id.small_card_arrow)
        }

        fun bind(position: Int) {

            val card = cards[position]

            if (designType == "HC6") {
                small_card_arrow.visibility = View.VISIBLE
            } else {
                small_card_arrow.visibility = View.GONE
            }

            Glide.with(context)
                .load(card.icon?.image_url)
                .into(small_card_image)

            small_card_cl.setBackgroundColor(Color.parseColor(card.bg_color ?: "#FFFFFF"))

            if (card.bg_gradient != null) {
                small_card_cl.background = gradientDrawable(card.bg_gradient)
            }

            if (!card.is_disabled) {
                small_card_cl.setOnClickListener {
                    launchUrl(card.url!!)
                }
            }

            var formattedTitle =
                htmlFormatter(card.formatted_text?.text, card.formatted_text?.Entities)
                    ?: card.title
            formattedTitle = "<b>$formattedTitle</b>"
            val formattedDescription = htmlFormatter(
                card.formatted_description?.text, card.formatted_description?.Entities
            ) ?: card.description

            if (formattedDescription != null) {
                formattedTitle = "$formattedTitle <br> $formattedDescription"
            }
            small_card_text.text =
                HtmlCompat.fromHtml(formattedTitle!!, HtmlCompat.FROM_HTML_MODE_LEGACY)

        }

    }

    inner class CardWithImage(itemview: View) : RecyclerView.ViewHolder(itemview) {

        private val image_card: CardView by lazy {
            itemView.findViewById(R.id.image_card)
        }
        private val image_card_frame: FrameLayout by lazy {
            itemView.findViewById(R.id.image_card_frame)
        }

        fun bind(position: Int) {
            val card = cards[position]

            if (designType == "HC9") {
                val scale = context.resources.displayMetrics.density
                val height_dp = (height!! * scale + 0.5f).toInt()
                val layoutParams: ViewGroup.LayoutParams = itemView.layoutParams
                layoutParams.width = (height_dp * card.bg_image!!.aspect_ratio).toInt()
                layoutParams.height = height_dp
                itemView.layoutParams = layoutParams
            } else {
                image_card_frame.setBackgroundColor(Color.parseColor(card.bg_color ?: "#FFFFFF"))
            }

            if (card.bg_gradient != null) {
                image_card_frame.background = gradientDrawable(card.bg_gradient)
            }

            Glide.with(context)
                .load(card.bg_image?.image_url)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                    ) {
                        image_card_frame.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        TODO("Not yet implemented")
                    }
                })

            if (!card.is_disabled) {
                image_card.setOnClickListener {
                    launchUrl(card.url!!)
                }
            }
        }
    }

    inner class BigDisplayCard(itemview: View) : RecyclerView.ViewHolder(itemview) {

        val action_buttons_layout: ConstraintLayout by lazy {
            itemview.findViewById(R.id.action_buttons_layout)
        }

        val big_card_ll: LinearLayout by lazy {
            itemview.findViewById(R.id.big_card_ll)
        }

        val big_card: CardView by lazy {
            itemview.findViewById(R.id.big_card)
        }
        val remind_later: CardView by lazy {
            itemview.findViewById(R.id.remind_later)
        }

        val dismiss_now: CardView by lazy {
            itemview.findViewById(R.id.dismiss_now)
        }
        val big_card_title: TextView by lazy {
            itemview.findViewById(R.id.big_card_title)
        }
        val big_card_description: TextView by lazy {
            itemview.findViewById(R.id.big_card_description)
        }
        val big_card_cta: Button by lazy {
            itemview.findViewById(R.id.big_card_cta)
        }

        fun bind(position: Int) {

            val card = cards[position]

            big_card.setCardBackgroundColor(Color.parseColor(card.bg_color))

            if (card.bg_gradient != null) {
                big_card_ll.background = gradientDrawable(card.bg_gradient)
            }

            Glide.with(context)
                .load(card.bg_image?.image_url)
                .override(500, 500)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                    ) {
                        big_card_ll.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        TODO("Not yet implemented")
                    }
                })

            val formattedTitle =
                htmlFormatter(card.formatted_text?.text, card.formatted_text?.Entities)
                    ?: card.title
            big_card_title.text = HtmlCompat.fromHtml(
                formattedTitle!!,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            if (card.formatted_text?.Entities?.get(0)?.url != null) {
                big_card_title.movementMethod = LinkMovementMethod.getInstance()
            }

            val formattedDescription = htmlFormatter(
                card.formatted_description?.text, card.formatted_description?.Entities
            ) ?: card.description
            big_card_description.text = HtmlCompat.fromHtml(
                formattedDescription!!,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            if (card.formatted_description?.Entities?.get(0)?.url != null) {
                big_card_description.movementMethod = LinkMovementMethod.getInstance()
            }

            if (card.cta != null) {
                big_card_cta.apply {
                    text = card.cta[0].text
                    setBackgroundColor(Color.parseColor(card.cta[0].bg_color))
                    setTextColor(Color.parseColor(card.cta[0].text_color))
                    setOnClickListener {
                        launchUrl(card.cta[0].url!!)
                    }
                }
            }

            big_card.setOnClickListener {
                launchUrl(card.url!!)
            }

            big_card.setOnLongClickListener {
                if (action_buttons_layout.isVisible) {
                    ObjectAnimator.ofFloat(
                        big_card,
                        "translationX",
                        0f
                    ).apply {
                        duration = 200
                        start()
                    }.doOnEnd {
                        action_buttons_layout.visibility = View.INVISIBLE
                    }
                } else {
                    action_buttons_layout.visibility = View.VISIBLE
                    ObjectAnimator.ofFloat(
                        big_card,
                        "translationX",
                        action_buttons_layout.measuredWidth.toFloat()
                    ).apply {
                        duration = 200
                        start()
                    }
                }
                true
            }

            dismiss_now.setOnClickListener {
                sharedPrefs.addDismissNowCard(card)
                setCards(cards)
            }
            remind_later.setOnClickListener {
                sharedPrefs.addRemindLaterCard(card)
                setCards(cards)
            }
        }

    }

    private fun htmlFormatter(text: String?, entityList: List<Entity>?): String? {
        if (entityList == null || entityList.isEmpty() || text == null || !text.contains("{}")) {
            return null
        }
        var finalText = ""
        var position = 0
        return try {
            for (word in text.split(' ')) {
                if (word == "{}") {
                    val entity = entityList[position]
                    var formatted_wordt = entity.text
                    if (entity.font_style != null) {
                        if (entity.font_style == "underline") {
                            formatted_wordt = "<u>$formatted_wordt</u>"
                        } else {
                            formatted_wordt = "<i>$formatted_wordt</i>"
                        }
                    }
                    if (entity.color != null) {
                        formatted_wordt =
                            "<font color='${entity.color}'> $formatted_wordt </font>"
                    }
                    if (entity.url != null) {
                        formatted_wordt = "<a href='${entity.url}'>$formatted_wordt</a>"
                    }
                    finalText += "$formatted_wordt "
                    position++
                } else {
                    finalText += "$word "
                }
            }
            finalText
        } catch (e: Exception) {
            null
        }
    }

    fun launchUrl(url: String) {
        CustomTabsIntent.Builder().build()
            .launchUrl(context, Uri.parse(url))
    }

    fun gradientDrawable(bgGradient: Gradient): GradientDrawable? {

        if(bgGradient.colours == null){
            return null
        }

        val colors = bgGradient.colours.map { Color.parseColor(it.color) }.toIntArray()

        val orientation = when(bgGradient.angle%360){
            in 0..45-> GradientDrawable.Orientation.TOP_BOTTOM
            in 45..90-> GradientDrawable.Orientation.TR_BL
            in 90..135-> GradientDrawable.Orientation.RIGHT_LEFT
            in 135..180-> GradientDrawable.Orientation.BR_TL
            in 180..225-> GradientDrawable.Orientation.BOTTOM_TOP
            in 225..270-> GradientDrawable.Orientation.BL_TR
            in 270..315-> GradientDrawable.Orientation.LEFT_RIGHT
            else -> GradientDrawable.Orientation.TL_BR
        }

        return GradientDrawable( orientation, colors )

    }

    fun setCards(new_cards: List<Card>) {
        val remindLaterCards = sharedPrefs.getRemindLaterCards()
        val dismissNowCards = sharedPrefs.getDismissNowCards()
        cards = new_cards - dismissNowCards - remindLaterCards
        notifyDataSetChanged()
    }
}
