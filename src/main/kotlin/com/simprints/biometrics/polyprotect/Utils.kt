import java.nio.ByteBuffer
import java.nio.ByteOrder

object Utils {
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

    /**
     * Converts an IntArray to a ByteArray.
     *
     * @param intArray The IntArray to convert.
     * @return A ByteArray representing the IntArray.
     */
    fun intArrayToByteArray(intArray: IntArray): ByteArray {
        val byteBuffer = ByteBuffer.allocate(intArray.size * 4).order(ByteOrder.nativeOrder())
        byteBuffer.asIntBuffer().put(intArray)
        return byteBuffer.array()
    }

    /**
     * Converts a ByteArray back to an IntArray.
     *
     * @param byteArray The ByteArray to convert.
     * @return An IntArray reconstructed from the ByteArray.
     */
    fun byteArrayToIntArray(byteArray: ByteArray): IntArray {
        val byteBuffer = ByteBuffer.wrap(byteArray).order(ByteOrder.nativeOrder())
        val intBuffer = byteBuffer.asIntBuffer()
        return IntArray(intBuffer.remaining()).apply { intBuffer.get(this) }
    }
}
