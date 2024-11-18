package com.simprints.biometrics_simpolyprotect
import PolyProtect
import org.junit.Test
import kotlin.random.Random


class PolyProtectUnitTest {

    @Test
    fun `test generate secret auxiliary data record`() {

        val subjectId1 = "myUniqueId1"

        // Custom parameters (relevant to the generation of secret auxiliary data record)
        PolyProtect.POLYNOMIALDEGREE = 7
        PolyProtect.COEFFICIENTABSMAX = 100

        // Generation of subject-specific secret parameters (auxiliary data)
        val secrets1 = PolyProtect.generateSecretAuxDataRecord(subjectId = subjectId1)

        assert(secrets1::class.simpleName == "secretAuxDataRecord")
        {"Secrets should be objects of the secretAuxDataRecord class."}
        assert(secrets1.subjectId::class.simpleName == "String")
        {"The subjectId should be a String."}
        assert(secrets1.coefficients::class.simpleName == "IntArray")
        {"The coefficients should be an IntArray."}
        assert(secrets1.exponents::class.simpleName == "IntArray")
        {"The exponents should be an IntArray."}
    }

    @Test
    fun `test transform a template`() {

        val subjectId = "myUniqueId"

        // Dummy coefficients
        val coefficients = intArrayOf(18, -26, 30, 4, 59, 10, -91)

        // Check that all elements are within the appropriate range.
        assert(coefficients.all { it in -PolyProtect.COEFFICIENTABSMAX..PolyProtect.COEFFICIENTABSMAX })
        { "The coefficient values should not be outside the allowed range." }

        // Check that the array does not contain 0
        assert(coefficients.none { it == 0 })
        { "The coefficient values must not be 0." }

        // Dummy exponents
        val exponents = intArrayOf(1, 5, 3, 4, 6, 2, 7)

        assert(exponents.sortedArray().contentEquals(IntArray(exponents.size) { it + 1 }))
        { "The exponents should include values from 1 to PolyProtect.POLYNOMIALDEGREE." }

        val subjectSecretAuxData = PolyProtect.secretAuxDataRecord(subjectId = subjectId,
            coefficients = coefficients,
            exponents = exponents)

        val overlapValue = 2

        assert((0 < overlapValue) and (overlapValue < PolyProtect.POLYNOMIALDEGREE))
        { "The overlap value should be >=0 and <= polynomial degree - 1." }

        // Custom parameters (relevant to the generation of secret auxiliary data record)
        PolyProtect.OVERLAP = overlapValue

        // Creation of two unprotected templates (randomly generated)
        val unprotectedTemplate = DoubleArray(512) { Random.nextDouble() - 0.5 }

        /*
        PolyProtect tranformation: the unprotected templates are transformed using the
        subject-specific secret auxiliary data
         */
        val protectedRecord = PolyProtect.transformTemplate(unprotectedTemplate = unprotectedTemplate,
            subjectSecretAuxData = subjectSecretAuxData)

        assert(protectedRecord.subjectId::class.simpleName == "String")
        {"The coefficients should be an IntArray."}

        assert(protectedRecord.protectedTemplate::class.simpleName == "DoubleArray")
        {"The coefficients should be an IntArray."}

    }

    @Test
    fun `test compute scores`() {

        val randomArray1 = DoubleArray(512) { Random.nextDouble() - 0.5 }
        val randomArray2 = DoubleArray(512) { Random.nextDouble() - 0.5 }

        assert(randomArray1.size == randomArray2.size)
        { "The arrays should have the same size." }

        val score = PolyProtect.computeScore(randomArray1, randomArray2)

        assert(score in 0.0..1.0) { "Value $score should be between 0 and 1." }

    }

}