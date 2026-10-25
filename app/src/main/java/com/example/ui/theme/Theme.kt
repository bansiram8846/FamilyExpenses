package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FamilyWiseLightColorScheme = lightColorScheme(
  primary = Primary,
  onPrimary = OnPrimary,
  primaryContainer = PrimaryContainer,
  onPrimaryContainer = OnPrimaryContainer,
  inversePrimary = InversePrimary,
  secondary = Secondary,
  onSecondary = OnSecondary,
  secondaryContainer = SecondaryContainer,
  onSecondaryContainer = OnSecondaryContainer,
  tertiary = Tertiary,
  onTertiary = OnTertiary,
  tertiaryContainer = TertiaryContainer,
  onTertiaryContainer = OnTertiaryContainer,
  background = Background,
  onBackground = OnBackground,
  surface = Surface,
  onSurface = OnSurface,
  surfaceVariant = SurfaceVariant,
  onSurfaceVariant = OnSurfaceVariant,
  surfaceTint = SurfaceTint,
  inverseSurface = InverseSurface,
  inverseOnSurface = InverseOnSurface,
  error = Error,
  onError = OnError,
  errorContainer = ErrorContainer,
  onErrorContainer = OnErrorContainer,
  outline = Outline,
  outlineVariant = OutlineVariant
)

private val FamilyWiseDarkColorScheme = darkColorScheme(
  primary = InversePrimary,
  onPrimary = PrimaryContainer,
  primaryContainer = PrimaryContainer,
  onPrimaryContainer = OnPrimaryContainer,
  secondary = SecondaryFixedDim,
  onSecondary = OnSecondaryFixed,
  secondaryContainer = OnSecondaryFixedVariant,
  onSecondaryContainer = SecondaryContainer,
  tertiary = TertiaryFixedDim,
  onTertiary = OnTertiaryFixed,
  background = InverseSurface,
  onBackground = InverseOnSurface,
  surface = InverseSurface,
  onSurface = InverseOnSurface,
  surfaceVariant = SurfaceTint,
  onSurfaceVariant = InverseOnSurface,
  error = ErrorContainer,
  onError = OnErrorContainer,
  outline = OutlineVariant,
  outlineVariant = Outline
)

@Composable
fun FamilyWiseTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) FamilyWiseDarkColorScheme else FamilyWiseLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
