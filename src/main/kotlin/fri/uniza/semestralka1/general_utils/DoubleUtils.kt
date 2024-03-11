package fri.uniza.semestralka1.general_utils

import org.apache.commons.math3.util.Precision

private const val EPSILON = 0.0000001
val DOUBLE_REGEX = Regex("(0|([1-9][0-9]*))(\\\\.[0-9]+)?\$")
val INTEGER_REGEX = Regex("^[1-9]\\d*|0$")

fun isAGreaterOrEqualsToB(a: Double, b: Double): Boolean {
    return Precision.compareTo(a, b, EPSILON) != -1
}

fun isALessOrEqualsToB(a: Double, b: Double): Boolean {
    return Precision.compareTo(a, b, EPSILON) != 1
}

fun isAGreaterThanB(a: Double, b: Double): Boolean {
    return Precision.compareTo(a, b, EPSILON) == 1
}

fun isALessThanB(a: Double, b: Double): Boolean {
    return Precision.compareTo(a, b, EPSILON) == -1
}

fun isALessThanBComparator(a: Double, b: Double): Int {
    return Precision.compareTo(a, b, EPSILON)
}

fun isAEqualsToB(a: Double, b: Double): Boolean {
    return Precision.equals(a, b, EPSILON)
}
