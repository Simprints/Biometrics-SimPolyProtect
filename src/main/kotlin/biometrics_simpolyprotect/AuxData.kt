package biometrics_simpolyprotect

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuxData(
    val coefficients: IntArray,
    val exponents: IntArray,
) : Parcelable
