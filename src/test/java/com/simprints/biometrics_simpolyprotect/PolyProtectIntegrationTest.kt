package com.simprints.biometrics_simpolyprotect
import PolyProtect
import org.junit.Test
import kotlin.random.Random


class PolyProtectIntegrationTest {
    @Test

    fun `PolyProtect operations in sequence`() {

        val subjectId1 = "myUniqueId1"
        val subjectId2 = "myUniqueId2"

        // Creation of two unprotected templates (randomly generated)
        val unprotectedTemplate1 = DoubleArray(512) { Random.nextDouble() - 0.5 }
        val unprotectedTemplate2 = DoubleArray(512) { Random.nextDouble() - 0.5 }

        val polynomialDegree = 7
        val coefficientAbsMax = 100
        val overlap = 2

        assert((polynomialDegree <= 14) and (polynomialDegree >= 5))
        {"Assertion failed: the degree of the polynomial should be >=5 and <=14."}

        assert((coefficientAbsMax > 0))
        {"Assertion failed: the maximum coefficient value should be a positive integer."}

        assert((overlap >= 0) and (overlap <= polynomialDegree-1))
        {"Assertion failed: the overlap value should be >=0 and <= polynomial degree - 1."}

        // Custom parameters
        PolyProtect.POLYNOMIALDEGREE = polynomialDegree
        PolyProtect.COEFFICIENTABSMAX = coefficientAbsMax
        PolyProtect.OVERLAP = overlap

        // Generation of subject-specific secret parameters (auxiliary data)
        val secrets1 = PolyProtect.generateSecretAuxDataRecord(subjectId = subjectId1)
        val secrets2 = PolyProtect.generateSecretAuxDataRecord(subjectId = subjectId2)

        /*
        PolyProtect tranformation: the unprotected templates are transformed using the
        subject-specific secret auxiliary data
         */
        val protectedRecord1 = PolyProtect.transformTemplate(unprotectedTemplate = unprotectedTemplate1,
            subjectSecretAuxData = secrets1)
        val protectedRecord2 = PolyProtect.transformTemplate(unprotectedTemplate = unprotectedTemplate2,
            subjectSecretAuxData = secrets2)

        // Score in the [0, 1] range based on cosine similarity: 1 = perfect match
        val score = PolyProtect.computeScore(protectedRecord1.protectedTemplate,
            protectedRecord2.protectedTemplate)

        assert(score in 0.0..1.0) { "Value $score should be between 0 and 1." }

    }
}