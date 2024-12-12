package com.simprints.biometrics.polyprotect

import kotlin.math.pow

/**
 * The main entry point and configuration holder for the PolyProtect algorithm.
 *
 * @param polynomialDegree degree of the polynomial used in the PolyProtect transformation. Must be in `[5;14]` range.
 *
 * @param coefficientAbsMax range limit for the coefficients of the polynomial used in the PolyProtect transformation.
 *          The coefficients will be drawn from the `[-coefficientAbsMax, coefficientAbsMax]` range excluding 0.
 *
 * @param overlap of the intervals of the unprotected template to transform using the PolyProtect polynomial transformation.
 *          It can go from 0 to `polynomialDegree - 1`. The safest options would be: {0, 1, 2, 3}.
 */
class PolyProtect(
    private val polynomialDegree: Int = POLYNOMIAL_DEGREE_DEFAULT,
    private val coefficientAbsMax: Int = COEFFICIENT_ABS_MAX_DEFAULT,
    private val overlap: Int = OVERLAP_DEFAULT,
) {

    init {
        require(polynomialDegree in 5..14) {
            "Assertion failed: the degree of the polynomial should be >=5 and <=14."
        }
        require(coefficientAbsMax > 0) {
            "Assertion failed: the maximum coefficient value should be a positive integer."
        }
        require(overlap in 0..(polynomialDegree - 1)) {
            "Assertion failed: the overlap value should be >=0 and <= polynomial degree - 1."
        }
    }

    /**
     * Applies PolyProtect transformation to the provided unprotected template
     * using the accompanying `AuxData` values
     *
     * **NOTE**: The size of the protected templates depends on `polynomialDegree` and `overlap`.
     *
     * @param unprotectedTemplateByteArray
     * @param auxData a set of coefficients and exponents associated with specific biometric record
     *
     * @return protected template
     */
    fun transformTemplate(
        unprotectedTemplate: ByteArray,
        auxData: AuxData
    ): ByteArray {
        val (coefficients, exponents) = auxData // For convenience
        require(exponents.size == coefficients.size) { "Auxiliary data sizes must be equal." }
        require(Utils.byteArrayToIntArray(exponents).size == polynomialDegree) {
            "Auxiliary data sizes must be equal to polynomial degree."
        }

        // Converting from ByteArray
        val unprotectedTemplateDoubleArray = Utils.byteArrayToDoubleArray(unprotectedTemplate)
        val coefficientsIntArray = Utils.byteArrayToIntArray(coefficients)
        val exponentsIntArray = Utils.byteArrayToIntArray(exponents)

        val stepSize = exponentsIntArray.size - overlap

        val protectedTemplate = mutableListOf<Double>()
        for (templateIndex in 0..(unprotectedTemplateDoubleArray.lastIndex - overlap) step stepSize) {
            val s = exponentsIntArray.indices.map { i ->
                // If the target element is out of bounds, consider it 0 since 0^n==0
                // This would be the same as padding the provided array up to certain size
                if (templateIndex + i > unprotectedTemplateDoubleArray.lastIndex) {
                    0.0
                } else {
                    unprotectedTemplateDoubleArray[templateIndex + i]
                        .pow(exponentsIntArray[i])
                        .times(coefficientsIntArray[i])
                }
            }.sum()
            protectedTemplate.add(s)
        }
        return Utils.doubleArrayToByteArray(protectedTemplate.toDoubleArray())
    }

    /**
     * @return Randomly generated coefficients and exponents (auxiliary data) according
     *      to the configured `polynomialDegree` and `coefficientAbsMax` values.
     */
    fun generateAuxData(): AuxData {
        // Create a list that excludes 0, combining the ranges (-coefficientAbsMax to -1) and (1 to coefficientAbsMax)
        val coefficientRange =
            (-coefficientAbsMax until 0).toList() + (1..coefficientAbsMax).toList()
        // Shuffle the list randomly
        val shuffledList = coefficientRange.shuffled()
        // Return a sublist of the first 'polynomialDegree' elements
        val coefficients = shuffledList.take(polynomialDegree).toIntArray()

        // Create a list of exponents (1 to polynomialDegree)
        val exponentRange = (1..polynomialDegree)
        // Shuffle the list randomly
        val exponents = exponentRange.shuffled().toIntArray()

        return AuxData(Utils.intArrayToByteArray(coefficients), Utils.intArrayToByteArray(exponents))
    }

    companion object {

        private const val COEFFICIENT_ABS_MAX_DEFAULT: Int = 100
        private const val POLYNOMIAL_DEGREE_DEFAULT: Int = 7
        private const val OVERLAP_DEFAULT: Int = 2
    }
}
