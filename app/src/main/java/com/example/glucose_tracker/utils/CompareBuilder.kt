package com.example.glucose_tracker.utils

class CompareBuilder {
    private var mResultFalse = false

    fun append(a: Boolean): CompareBuilder {
        add(a)
        return this
    }

    fun append(a: Int, b: Int): CompareBuilder {
        add(a == b)
        return this
    }

    fun append(a: Long, b: Long): CompareBuilder {
        add(a == b)
        return this
    }

    fun append(a: Float, b: Float): CompareBuilder {
        add(a == b)
        return this
    }

    fun append(a: Boolean, b: Boolean): CompareBuilder {
        add(a == b)
        return this
    }

    fun append(a: String?, b: String?): CompareBuilder {
        add(a == null && b == null || isEmpty(a) && isEmpty(b) || a != null && b != null && a.contentEquals(b))
        return this
    }

    fun append(a: Any?, b: Any?): CompareBuilder {
        add(a == null && b == null || a != null && b != null && a == b)
        return this
    }

    private fun add(compare: Boolean) {
        if (!compare) {
            mResultFalse = true
        }
    }

    private fun isEmpty(str: String?): Boolean = str.isNullOrEmpty()

    fun areEqual(): Boolean {
        val equal = !mResultFalse
        mResultFalse = false
        return equal
    }
}