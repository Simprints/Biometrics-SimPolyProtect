package com.simprints.biometrics_simpolyprotect

import biometrics_simpolyprotect.AuxData
import biometrics_simpolyprotect.PolyProtect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random


class PolyProtectUnitTest {

    @Test
    fun `test generate secret auxiliary data record`() {
        // Custom parameters (relevant to the generation of secret auxiliary data record)
        val polyProtect = PolyProtect(
            polynomialDegree = 7,
            coefficientAbsMax = 100,
        )

        // Generation of subject-specific secret parameters (auxiliary data)
        val secrets1 = polyProtect.generateAuxData()

        assertEquals(
            "Exponents count is same as provided polynomial degree", 7, secrets1.exponents.size
        )
        assertTrue(
            "The exponents should include values from 1 to PolyProtect.POLYNOMIALDEGREE.",
            secrets1.exponents.sortedArray().contentEquals(IntArray(7) { it + 1 })
        )

        assertEquals(
            "Coefficient count is same as provided polynomial degree", 7, secrets1.coefficients.size
        )
        assertTrue("The coefficient values should not be outside the allowed range",
            secrets1.coefficients.all { it in -100..100 })
        assertTrue("The coefficient values must not be 0", secrets1.coefficients.none { it == 0 })

    }

    @Test
    fun `test transform a template`() {
        // Dummy coefficients
        val coefficients = intArrayOf(18, -26, 30, 4, 59, 10, -91)
        // Dummy exponents
        val exponents = intArrayOf(1, 5, 3, 4, 6, 2, 7)

        val subjectSecretAuxData = AuxData(
            coefficients = coefficients, exponents = exponents
        )

        // Custom parameters (relevant to the generation of secret auxiliary data record)
        val polyProtect = PolyProtect(
            polynomialDegree = 7,
            coefficientAbsMax = 100,
            overlap = 2,
        )

        // Creation of two unprotected templates (randomly generated)
        val unprotectedTemplate = DoubleArray(512) { Random.nextDouble() - 0.5 }

        // PolyProtect trandformation: the unprotected templates are transformed using the
        // subject-specific secret auxiliary data
        val protectedTemplate = polyProtect.transformTemplate(
            unprotectedTemplate = unprotectedTemplate, auxData = subjectSecretAuxData
        )

        assertFalse(unprotectedTemplate.contentEquals(protectedTemplate))
    }
}
