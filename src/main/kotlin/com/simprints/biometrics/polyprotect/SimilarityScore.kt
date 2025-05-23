package com.simprints.biometrics.polyprotect

import kotlin.math.sqrt

/**
 * Calculates a similarity score based on cosine distance.
 *
 * @param template1  byte stream representation of the first template's array of FLOAT32 values
 * @param template2  byte stream representation of the second template's array of FLOAT32 values
 *
 * @return a value in in the `[0,1]` range
 */
fun computeSimilarityScore(
    template1: ByteArray,
    template2: ByteArray,
): Double {
    require(template1.size == template2.size) { "Arrays must be of the same size." }

    val array1 = ArrayConverter.byteArrayToFloatArray(template1)
    val array2 = ArrayConverter.byteArrayToFloatArray(template2)

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
