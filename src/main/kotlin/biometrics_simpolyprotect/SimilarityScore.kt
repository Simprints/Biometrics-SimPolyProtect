package biometrics_simpolyprotect

import kotlin.math.sqrt

fun computeSimilarityScore(
    array1: DoubleArray,
    array2: DoubleArray,
): Double {
    require(array1.size == array2.size) { "Arrays must be of the same size." }

    // Calculate the dot product of array1 and array2
    val dotProduct = array1.zip(array2) { x, y -> x * y }.sum()
    // Calculate the magnitudes of array1 and array2
    val magnitudeA = sqrt(array1.map { it * it }.sum())
    val magnitudeB = sqrt(array2.map { it * it }.sum())
    // Check to avoid division by zero
    check(magnitudeA > 0.0 && magnitudeB > 0.0) { "Arrays must not be zero vectors." }
    // Calculate cosine similarity
    val cosineSimilarity = dotProduct / (magnitudeA * magnitudeB)
    // Calculate cosine distance
    val cosineDistance = 1 - cosineSimilarity

    return (2.0 - 1.0 * cosineDistance) / 2
}
