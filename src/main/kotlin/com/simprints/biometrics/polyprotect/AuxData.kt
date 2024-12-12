package com.simprints.biometrics.polyprotect

/**
 * Simple data class to handle the template-specific secret parameters used in the PolyProtect
 * transformation, namely the coefficients and exponents of a polynomial function.
 *
 * @param coefficients of a polynomial function
 * @param exponents of a polynomial function
 */
data class AuxData(
    val coefficients: ByteArray,
    val exponents: ByteArray,
)
