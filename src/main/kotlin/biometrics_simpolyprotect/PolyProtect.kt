package biometrics_simpolyprotect

import kotlin.math.pow

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

    fun transformTemplate(
        unprotectedTemplate: DoubleArray,
        auxData: AuxData
    ): DoubleArray {
        val (coefficients, exponents) = auxData // For convenience
        require(exponents.size == coefficients.size) { "Auxiliary data sizes must be equal." }
        require(exponents.size == polynomialDegree) {
            "Auxiliary data sizes must be equal to polynomial degree."
        }

        val stepSize = exponents.size - overlap

        val protectedTemplate = mutableListOf<Double>()
        for (templateIndex in 0..(unprotectedTemplate.lastIndex - overlap) step stepSize) {
            val s = exponents.indices.map { i ->
                // If the target element is out of bounds, consider it 0 since 0^n==0
                // This would be the same as padding the provided array up to certain size
                if (templateIndex + i > unprotectedTemplate.lastIndex) {
                    0.0
                } else {
                    unprotectedTemplate[templateIndex + i]
                        .pow(exponents[i])
                        .times(coefficients[i])
                }
            }.sum()
            protectedTemplate.add(s)
        }
        return protectedTemplate.toDoubleArray()
    }

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

        return AuxData(coefficients, exponents)
    }

    companion object {

        private const val COEFFICIENT_ABS_MAX_DEFAULT: Int = 100
        private const val POLYNOMIAL_DEGREE_DEFAULT: Int = 7
        private const val OVERLAP_DEFAULT: Int = 2
    }
}
