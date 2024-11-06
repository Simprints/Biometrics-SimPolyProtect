import kotlin.math.pow
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


class PolyProtect {

    @Parcelize
    data class secretAuxDataRecord(
        val subjectId: String,
        val coefficients: IntArray,
        val exponents: IntArray,
    ) : Parcelable

    @Parcelize
    data class protectedTemplateRecord(
        val subjectId: String,
        val protectedTemplate: DoubleArray
    ) : Parcelable


    companion object {

        private var _COEFFICIENTABSMAX: Int = 100
        private var _POLYNOMIALDEGREE: Int  = 7
        private var _OVERLAP: Int = 2

        var POLYNOMIALDEGREE: Int
            get() = _POLYNOMIALDEGREE
            set(value) {
                assert((value <= 14) and (value >= 5))
                {"Assertion failed: the degree of the polynomial should be >=5 and <=14."}
                _POLYNOMIALDEGREE = value
            }

        var COEFFICIENTABSMAX: Int
            get() = _COEFFICIENTABSMAX
            set(value) {
                assert((value > 0))
                {"Assertion failed: the maximum coefficient value should be a positive integer."}
                _COEFFICIENTABSMAX = value
            }

        var OVERLAP: Int
            get() = _OVERLAP
            set(value) {
                assert((value >= 0) and (value <= _POLYNOMIALDEGREE-1))
                {"Assertion failed: the overlap value should be >=0 and <= polynomial degree - 1."}
                _OVERLAP = value
            }

        fun transformTemplate(unprotectedTemplate: DoubleArray,
            subjectSecretAuxData: secretAuxDataRecord
        ): protectedTemplateRecord {

            val (_, c, e) = subjectSecretAuxData // For convenience
            assert(e.size == c.size) { "Auxiliary data sizes must be equal." }

            val stepSize = e.size - OVERLAP
            val eIndices = e.indices

            val protectedTemplate = mutableListOf<Double>()
            for (templateIndex in 0..(unprotectedTemplate.lastIndex - OVERLAP) step stepSize) {
                val s = eIndices
                    .map { i ->
                        // If the target element is out of bounds, consider it 0 since 0^n==0
                        // This would be the same as padding the provided array up to certain size
                        if (templateIndex + i > unprotectedTemplate.lastIndex) {
                            0.0
                        } else {
                            unprotectedTemplate[templateIndex + i].pow(e[i]).times(c[i])
                        }
                    }
                    .sum()
                protectedTemplate.add(s)
            }
            return protectedTemplateRecord(subjectSecretAuxData.subjectId, protectedTemplate.toDoubleArray())
        }

        fun generateSecretAuxDataRecord(subjectId: String): secretAuxDataRecord {
            // Create a list that excludes 0, combining the ranges (-coefficientAbsMax to -1) and (1 to coefficientAbsMax)
            val coefficientRange = (-COEFFICIENTABSMAX until 0).toList() + (1..COEFFICIENTABSMAX).toList()
            // Shuffle the list randomly
            val shuffledList = coefficientRange.shuffled()
            // Return a sublist of the first 'polynomialDegree' elements
            val coefficients = shuffledList.take(POLYNOMIALDEGREE).toIntArray()

            // Create a list of exponents (1 to polynomialDegree)
            val exponentRange = (1..POLYNOMIALDEGREE)
            // Shuffle the list randomly
            val exponents = exponentRange.shuffled().toIntArray()

            return secretAuxDataRecord(subjectId, coefficients, exponents)
        }

        fun computeScore(array1: DoubleArray, array2: DoubleArray): Double {
            require(array1.size == array2.size) { "Arrays must be of the same size." }

            // Calculate the dot product of array1 and array2
            val dotProduct = array1.zip(array2) { x, y -> x * y }.sum()
            // Calculate the magnitudes of array1 and array2
            val magnitudeA = Math.sqrt(array1.map { it * it }.sum())
            val magnitudeB = Math.sqrt(array2.map { it * it }.sum())
            // Check to avoid division by zero
            if (magnitudeA == 0.0 || magnitudeB == 0.0) {
                throw IllegalArgumentException("Arrays must not be zero vectors.")
            }
            // Calculate cosine similarity
            val cosineSimilarity = dotProduct / (magnitudeA * magnitudeB)
            // Calculate cosine distance
            return (2.0-1.0 * cosineSimilarity)/2
        }
    }
}
