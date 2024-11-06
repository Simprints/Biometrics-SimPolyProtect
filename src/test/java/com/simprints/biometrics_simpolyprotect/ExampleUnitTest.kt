package com.simprints.biometrics_simpolyprotect
import PolyProtect
import org.junit.Test
import kotlin.random.Random


class ExampleUnitTest {
    @Test

    fun exampleUsage() {

        val subjectId1 = "myUniqueId1"
        val subjectId2 = "myUniqueId2"

        // Creation of two unprotected templates (randomly generated)
        val unprotectedTemplate1 = DoubleArray(512) { Random.nextDouble() - 0.5 }
        val unprotectedTemplate2 = DoubleArray(512) { Random.nextDouble() - 0.5 }

        // Custom parameters
        PolyProtect.POLYNOMIALDEGREE = 7
        PolyProtect.COEFFICIENTABSMAX = 100
        PolyProtect.OVERLAP = 2

        // Generation of subject-specific secret parameters (auxiliary data)
        val secrets1 = PolyProtect.generateSecretAuxDataRecord(subjectId = subjectId1)
        val secrets2 = PolyProtect.generateSecretAuxDataRecord(subjectId = subjectId2)

        assert(secrets1::class.simpleName == "secretAuxDataRecord")
            {"Secrets should be objects of the secretAuxDataRecord class."}
        assert(secrets1.subjectId::class.simpleName == "String")
            {"The subjectId should be a String."}
        assert(secrets1.coefficients::class.simpleName == "IntArray")
            {"The coefficients should be an IntArray."}
        assert(secrets1.exponents::class.simpleName == "IntArray")
            {"The exponents should be an IntArray."}

        assert(unprotectedTemplate1::class.simpleName == "DoubleArray")
            {"The unprotected template should be a DoubleArray."}

        /*
        PolyProtect tranformation: the unprotected templates are transformed using the
        subject-specific secret auxiliary data
         */
        val protectedTemplate1 = PolyProtect.transformTemplate(unprotectedTemplate = unprotectedTemplate1,
            subjectSecretAuxData = secrets1)
        val protectedTemplate2 = PolyProtect.transformTemplate(unprotectedTemplate = unprotectedTemplate2,
            subjectSecretAuxData = secrets2)

        // Score in the [0, 1] range based on cosine similarity: 1 = perfect match
        val score = PolyProtect.computeScore(protectedTemplate1.protectedTemplate,
            protectedTemplate2.protectedTemplate)

    }
}