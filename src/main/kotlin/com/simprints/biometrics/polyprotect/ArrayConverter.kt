package com.simprints.biometrics.polyprotect

import java.nio.ByteBuffer
import java.nio.ByteOrder

object ArrayConverter {
    /**
     * Converts a DoubleArray to a ByteArray.
     *
     * @param doubleArray The DoubleArray to convert.
     * @return A ByteArray representing the DoubleArray.
     */
    fun doubleArrayToByteArray(doubleArray: DoubleArray): ByteArray {
        val byteBuffer = ByteBuffer.allocate(doubleArray.size * 8).order(ByteOrder.nativeOrder())
        byteBuffer.asDoubleBuffer().put(doubleArray)
        return byteBuffer.array()
    }

    /**
     * Converts a ByteArray back to a DoubleArray.
     *
     * @param byteArray The ByteArray to convert.
     * @return A DoubleArray reconstructed from the ByteArray.
     */
    fun byteArrayToDoubleArray(byteArray: ByteArray): DoubleArray {
        val byteBuffer = ByteBuffer.wrap(byteArray).order(ByteOrder.nativeOrder())
        val doubleBuffer = byteBuffer.asDoubleBuffer()
        return DoubleArray(doubleBuffer.remaining()).apply { doubleBuffer.get(this) }
    }

}
