package com.simprints.biometrics.polyprotect

import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import kotlin.math.abs

@RunWith(Parameterized::class)
class PolyProtectTemplateEncoderParameterTest(
    private val given: Pair<List<Float>, AuxData>,
    private val expected: List<Float>
) {

    private val subject = PolyProtect(
        polynomialDegree = 5,
        coefficientAbsMax = 100,
        overlap = 2,
    )

    @Test
    fun `encode template works correctly`() {
        val (template, aux) = given
        val encoded = subject.transformTemplate(
            ArrayConverter.floatArrayToByteArray(template.toFloatArray()),
            aux
        )
            .let { ArrayConverter.byteArrayToFloatArray(it) } // Converting to compare actual float values

        println("expected: $expected")
        println("actual:   ${encoded.joinToString(", ", prefix = "[", postfix = "]") { it.toString() }} ")

        assertEquals(expected.size, encoded.size)
        assertTrue(
            expected.toFloatArray()
                .zip(encoded) { e, a -> abs(e - a) < PRECISION }
                .all { it }
        )
    }

    companion object {
        private const val PRECISION = 1e-5f

        @JvmStatic
        @Parameters
        fun data(): Collection<Array<Any>> = listOf(
            // Full template with overlap 2 and no padding
            arrayOf(
                // Given
                listOf(
                    0.1331335545f,
                    0.4449820169f,
                    -0.2204751117f,
                    0.02803098769f,
                    0.0488200593f,
                    0.1831083583f,
                    -0.007320145106f,
                    0.3660261928f,
                    -0.3799865143f,
                    0.2509288685f,
                    -0.1363977355f,
                    0.4834820974f,
                    0.4518768993f,
                    0.3874402622f,
                    0.002547916374f,
                    0.1281368887f,
                    -0.2143639432f,
                    -0.4538202661f,
                    0.0495447296f,
                    -0.2834924903f,
                    0.2892785979f,
                    0.3398633496f,
                    -0.09878652073f,
                ) to AuxData(
                    exponents = intArrayOf(1, 2, 3, 4, 5),
                    coefficients = intArrayOf(10, 20, 30, 40, 50),
                ),
                // Expected
                listOf(
                    4.970039955f,
                    0.8406559131f,
                    1.116541916f,
                    8.376161797f,
                    7.509119748f,
                    -0.6948732895f,
                    3.362235598f,
                )
            ),
            // This template needs to be padded
            arrayOf(
                // Given
                listOf(
                    0.1331335545f,
                    0.4449820169f,
                    -0.2204751117f,
                    0.02803098769f,
                    0.0488200593f,
                    0.1831083583f,
                    -0.007320145106f,
                    0.3660261928f,
                    -0.3799865143f,
                    0.2509288685f,
                    -0.1363977355f,
                    0.4834820974f,
                    0.4518768993f,
                    0.3874402622f,
                    0.002547916374f,
                    0.1281368887f,
                    -0.2143639432f,
                    -0.4538202661f,
                    0.0495447296f,
                    -0.2834924903f,
                    0.2892785979f,
                ) to AuxData(
                    exponents = intArrayOf(1, 2, 3, 4, 5),
                    coefficients = intArrayOf(10, 20, 30, 40, 50),
                ),
                // Expected
                listOf(
                    4.970039955f,
                    0.8406559131f,
                    1.116541916f,
                    8.376161797f,
                    7.509119748f,
                    -0.6948732895f,
                    2.829030416f,
                )
            ),
            // Template with some padding and randomised C and E values
            arrayOf(
                // Given
                listOf(
                    0.4952767838f,
                    0.04582746275f,
                    0.4912871269f,
                    -0.05188964656f,
                    0.1710497578f,
                    0.4828839484f,
                    0.4789628756f,
                    -0.1757431758f,
                    -0.1946878787f,
                    -0.1893160354f,
                    -0.2281576101f,
                    -0.3595243313f,
                    0.144228172f,
                    -0.4046463718f,
                    0.08317913623f,
                    -0.4290612621f,
                    -0.03695776633f,
                    -0.1710147956f,
                    -0.2621668255f,
                    -0.00713160069f,
                    0.2892785979f,
                    0.4425149409f,
                ) to AuxData(
                    exponents = intArrayOf(3, 5, 1, 2, 4),
                    coefficients = intArrayOf(35, -83, -27, 79, 12),
                ),
                // Expected
                listOf(
                    -8.789603428f,
                    5.079566739f,
                    11.98009351f,
                    11.48605497f,
                    13.30302774f,
                    7.28262971f,
                    7.02854756f,
                )
            ),
        )
    }
}
