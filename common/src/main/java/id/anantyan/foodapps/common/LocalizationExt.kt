package id.anantyan.foodapps.common

import android.content.Context
import java.util.Locale

fun updateResources(context: Context, language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val configuration = context.resources.configuration
    configuration.setLocale(locale)
    configuration.setLayoutDirection(locale)

    return context.createConfigurationContext(configuration)
}

@Suppress("DEPRECATION")
fun updateResourcesLegacy(context: Context, language: String): Context {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val resources = context.resources

    val configuration = resources.configuration
    configuration.locale = locale
    configuration.setLayoutDirection(locale)

    resources.updateConfiguration(configuration, resources.displayMetrics)

    return context
}