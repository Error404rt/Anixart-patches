package app.error404rt.patches.anixart

import app.error404rt.patches.shared.Constants.COMPATIBILITY_ANIXART
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.booleanOption
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Suppress("unused")
val anixartAdsPatch = bytecodePatch(
    name = "Remove ads",
    description = "Смотрите аниме без рекламы и отвлекающих факторов.",
    default = true
) {
    compatibleWith(COMPATIBILITY_ANIXART)

    val removeBannerAds = booleanOption(
        key = "removeBannerAds",
        title = "Баннерная реклама",
        description = "Убирает нижний рекламный баннер и надпись «Реклама».",
        default = true
    )

    val removeInterstitialAds = booleanOption(
        key = "removeInterstitialAds",
        title = "Межстраничная реклама",
        description = "Блокирует рекламные interstitial-показы.",
        default = true
    )

    val removeKodikPreRoll = booleanOption(
        key = "removeKodikPreRoll",
        title = "Реклама перед Kodik",
        description = "Пропускает рекламный pre-roll перед началом трансляции Kodik.",
        default = true
    )

    execute {
        if (removeBannerAds.value == true) {
            AdsSuppressedFingerprint.method.addInstructions(
                0,
                """
                    const/4 v0, 0x1
                    return v0
                """
            )
        }

        if (removeKodikPreRoll.value == true) {
            val method = KodikAdOnCreateFingerprint.method
            val instructions = method.implementation?.instructions
                ?: throw PatchException("KodikAdActivity: onCreate() has no implementation")

            val instruction = instructions.firstOrNull {
                it is ReferenceInstruction &&
                    it.reference is MethodReference &&
                    (it.reference as MethodReference).name == "getLayoutInflater"
            } ?: throw PatchException("KodikAdActivity: getLayoutInflater() not found")

            val index = instructions.indexOf(instruction)

            method.addInstructions(
                index,
                """
                    invoke-virtual { p0 }, ${KODIK_AD_ACTIVITY}->advertEnded()V
                    return-void
                """
            )

            try {
                val showMethod = KodikAdShowFingerprint.methodOrNull
                val showInstructions = showMethod?.implementation?.instructions

                val getBoolean = showInstructions?.firstOrNull {
                    it is ReferenceInstruction &&
                        it.reference is MethodReference &&
                        (it.reference as MethodReference).name == "getBoolean" &&
                        (it.reference as MethodReference).definingClass == "Landroid/content/SharedPreferences;"
                }

                if (getBoolean != null && showMethod != null && showInstructions != null) {
                    val index = showInstructions.indexOf(getBoolean)
                    val next = showInstructions.getOrNull(index + 1)

                    if (next is OneRegisterInstruction) {
                        showMethod.addInstructions(
                            index + 1,
                            """
                                const/16 v${next.registerA}, 0x1
                            """
                        )
                    }
                }
            } catch (e: Exception) {
                println("Warning: Kodik disclaimer bypass could not be applied: ${e.message}")
            }
        }

        if (removeInterstitialAds.value == true) {
            fun disable(name: String, fingerprint: Fingerprint) {
                fingerprint.methodOrNull?.addInstructions(
                    0,
                    """
                        return-void
                    """
                ) ?: println("Warning: $name fingerprint method not found")
            }

            disable("Interstitial load", InterstitialLoadFingerprint)
            disable("Interstitial show", InterstitialShowFingerprint)
        }
    }
}
