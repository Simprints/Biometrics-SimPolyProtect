package com.simprints.biometrics.polyprotect

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random


class ComputeSimilarityScoreTest {

    @Test
    fun `similarity between two protected templates should be between 0 and 1`() {
        // Creation of two unprotected templates (randomly generated)
        val unprotectedTemplateDoubleArray1 = DoubleArray(512) { Random.nextDouble() - 0.5 }
        val unprotectedTemplateDoubleArray2 = DoubleArray(512) { Random.nextDouble() - 0.5 }

        val unprotectedTemplate1 = ArrayConverter.doubleArrayToByteArray(unprotectedTemplateDoubleArray1)
        val unprotectedTemplate2 = ArrayConverter.doubleArrayToByteArray(unprotectedTemplateDoubleArray2)


        // Custom parameters
        val polyProtect = PolyProtect(
            polynomialDegree = 7,
            coefficientAbsMax = 100,
            overlap = 2,
        )

        // Generation of subject-specific secret parameters (auxiliary data)
        val secrets1 = polyProtect.generateAuxData()
        val secrets2 = polyProtect.generateAuxData()

        // PolyProtect transformation: the unprotected templates are transformed using the
        // subject-specific secret auxiliary data
        val protectedRecord1 = polyProtect.transformTemplate(
            unprotectedTemplate = unprotectedTemplate1, auxData = secrets1
        )
        val protectedRecord2 = polyProtect.transformTemplate(
            unprotectedTemplate = unprotectedTemplate2, auxData = secrets2
        )

        // Score in the [0, 1] range based on cosine similarity: 1 = perfect match
        val score = computeSimilarityScore(protectedRecord1, protectedRecord2)

        assertTrue("Score should be between 0 and 1.", score in 0.0..1.0)
    }
}
