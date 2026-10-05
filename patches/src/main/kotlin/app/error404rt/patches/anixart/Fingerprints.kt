package app.error404rt.patches.anixart

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall

const val EPISODES_FRAGMENT = "Lcom/swiftsoft/anixartd/ui/fragment/main/episodes/EpisodesFragment;"
const val INTERSTITIAL_AD = "Lcom/yandex/mobile/ads/interstitial/InterstitialAd;"
const val INTERSTITIAL_LOADER = "Lcom/yandex/mobile/ads/interstitial/InterstitialAdLoader;"
const val DISPLAY_COMPAT = "Lcom/swiftsoft/anixartd/utils/ui/DisplayCompat;"
const val KODIK_AD_ACTIVITY = "Lcom/swiftsoft/anixartd/ui/activity/kodik/KodikAdActivity;"

object AdsSuppressedFingerprint : Fingerprint(
    definingClass = DISPLAY_COMPAT,
    returnType = "Z"
)

object KodikAdOnCreateFingerprint : Fingerprint(
    definingClass = KODIK_AD_ACTIVITY,
    name = "onCreate",
    returnType = "V",
    strings = listOf("KodikInterface")
)

object KodikAdShowFingerprint : Fingerprint(
    definingClass = EPISODES_FRAGMENT,
    name = "onShowKodikAd",
    returnType = "V"
)

object InterstitialLoadFingerprint : Fingerprint(
    definingClass = EPISODES_FRAGMENT,
    returnType = "V",
    filters = listOf(
        methodCall(
            definingClass = INTERSTITIAL_LOADER,
            name = "loadAd"
        )
    )
)

object InterstitialShowFingerprint : Fingerprint(
    definingClass = EPISODES_FRAGMENT,
    returnType = "V",
    filters = listOf(
        methodCall(
            definingClass = INTERSTITIAL_AD,
            name = "show"
        )
    )
)
