# Anixart APK Analysis Checkpoint

Updated: 2026-10-10

## Project
- Repository: https://github.com/Error404rt/Anixart-patches
- Target app: Anixart 10.0
- Package observed in previous project context: `com.swiftsoft.anixartd`
- User's goal: identify and remove advertising across Anixart while preserving Kodik and other video playback.

## APK located in ChatGPT Library
- File: `Anixart_10.0.apk`
- Library path: `/Anixart_10.0.apk`
- Size: 17,453,477 bytes
- SHA-256: `f1805375552fc1be4018685a00acd08eb5633f8cd624f200cf1f7da7244c99bc`
- APK contains four DEX files:
  - `classes.dex`: 9,470,740 bytes; SHA-256 `a598a7893b54a255ba94340f5b6cb5d8d9315cf7e40ac4aeefcd3cd582a0e062`
  - `classes2.dex`: 567,348 bytes; SHA-256 `4005832b5228bb83df8624c914aa3779a5aa9ec96dd702642185a39a864660b2`
  - `classes3.dex`: 8,460,488 bytes; SHA-256 `38dd84b7203c88971ba93541cf048cfbd738de657b1772dbb29f29d590a9b7cc`
  - `classes4.dex`: 3,453,648 bytes; SHA-256 `3e04bd9ab73d80851232d715496f90ffaeed2d589f7673cbc0237d43d6c7c63e`
- DEX header version observed: 035.

## Initial APK inspection completed
The APK was successfully materialized from Library and unzipped locally. The four DEX files were extracted and scanned for strings/class references. This is a preliminary static inventory, NOT a full Java/Smali decompilation.

Confirmed strings/classes worth investigating:
- `com/swiftsoft/anixartd/ui/activity/kodik/KodikAdActivity`
- `com/swiftsoft/anixartd/ui/activity/kodik/Hilt_KodikAdActivity`
- `com/swiftsoft/anixartd/databinding/ActivityKodikAdBinding`
- `com/swiftsoft/anixartd/databinding/DialogKodikAdDisclaimerBinding`
- `com/swiftsoft/anixartd/parser/kodik/KodikParser`
- `getKodikAdIframeUrl`
- `getKodikIframeAd`
- `getKodikVideoLinksUrl`
- `KODIK_URL`
- `SELECTED_KODIK_VIDEO_QUALITY_VALUE`
- `YandexAds`
- Yandex Mobile Ads references including `com/yandex/mobile/ads/banner/BannerAdView`, `com/yandex/mobile/ads/interstitial/InterstitialAd`, and `com/yandex/mobile/ads/common/AdActivity`
- String `ads_count` and `ad_banner`
- Asset `assets/adblock.txt`, which contains a substantial host list for advertising/tracking domains. This is a domain blocklist asset, not proof that every listed domain is actively requested by the app.

Observed URL strings include `https://startup.mobile.yandex.net/`, `https://yandex.com/ads`, and `https://pagead2.googlesyndication.com/pagead/gen_204?id=gmob-apps`. Some strings may belong to bundled SDKs rather than app-specific ad logic; confirm call sites before patching.

Relevant assets include `assets/player_default.html`, `assets/player_youtube.html`, and `assets/adblock.txt`.

## Important prior debugging knowledge (from project history)
- Removing the entire Kodik ad Activity initialization previously broke episode playback. Preserve WebView setup and player-related initialization; prefer narrowly disabling only ad URL/request/display logic after examining call sites.
- Prior work identified both Yandex ad surfaces and Kodik's ad Activity/WebView as areas to inspect.
- The user has previously confirmed that a patch version worked after fixing an instruction insertion that had landed between an invoke and its move-result. Keep Smali instruction ordering valid; never insert instructions between an invoke-result pair.

## Decompilation status and blocker
- This runtime has Java and unzip but no installed `jadx`, `apktool`, or `aapt`; outbound DNS/network access from the runtime is unavailable. Therefore the APK could be extracted and string-scanned, but a full decompilation and call-site analysis could not be completed here.
- The APK exists in ChatGPT Library but is NOT currently present in this GitHub repository at `analysis/Anixart_10.0.apk`.
- A manual GitHub Actions workflow has been added at `.github/workflows/analyze-anixart-apk.yml`. It expects the APK at that path, downloads JADX 1.5.6 on the GitHub-hosted runner, decompiles the APK, creates a focused report for ad/Kodik/player references, and uploads the analysis as an artifact retained for 14 days.
- To finish full analysis, upload the Library APK into this repository as `analysis/Anixart_10.0.apk`, then run the **Analyze Anixart APK** workflow from GitHub Actions. The workflow has not been run yet because the APK is not committed to this repository.

## Safety / scope constraints
- Do not change production patch logic based on string matches alone.
- Do not remove Kodik Activity/WebView wholesale.
- Trace ad-related references to app-owned call sites, distinguish SDK strings from active app logic, and inspect the playback path before proposing a patch.
- After full decompilation, record exact source classes, method names, relevant branches/URLs, proposed minimal changes, build/test result, and any playback regression checks here.
