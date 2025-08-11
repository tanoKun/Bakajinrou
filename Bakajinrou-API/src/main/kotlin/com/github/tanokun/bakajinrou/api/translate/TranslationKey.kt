package com.github.tanokun.bakajinrou.api.translate

private val VALID_KEY_REGEX = Regex("^[a-z]+(?:\\.[a-z]+)*$")

/**
 * 翻訳キーを表します。
 * フォーマットは「小文字のa-z」と「.」のみです。
 *
 * @property key キーの文字列表現
 * @throws IllegalArgumentException 不正なフォーマットのキーで生成しようとした場合
 */
open class TranslationKey(val key: String) {

    init {
        require(VALID_KEY_REGEX.matches(key)) {
            "トランスレートキーは、(a-z .)で構成される必要があります。Miss -> '$key'"
        }
    }

    operator fun plus(translationKey: TranslationKey): TranslationKey = TranslationKey(this.key + "." + translationKey.key)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TranslationKey) return false

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int = key.hashCode()
}