package com.rainc.compose.datatable.model

sealed class CompilationKey : Comparable<CompilationKey> {
    data class DoubleKey(val value: Double) : CompilationKey()
    data class StringKey(val value: String) : CompilationKey()
    data class LongKey(val value: Long) : CompilationKey()
    data class BooleanKey(val value: Boolean) : CompilationKey()

    override fun compareTo(other: CompilationKey): Int {
        when (this) {
            is DoubleKey -> when (other) {
                is DoubleKey -> return this.value.compareTo(other.value)
                is StringKey -> {
                    runCatching { other.value.toDouble() }
                        .onSuccess {
                            return this.value.compareTo(it)
                        }

                    return this.value.toString().compareTo(other.value)

                }

                is LongKey -> return this.value.compareTo(other.value.toDouble())
                is BooleanKey -> return this.value.toString().compareTo(other.value.toString())
            }


            is StringKey -> when (other) {
                is DoubleKey -> {
                    runCatching { this.value.toDouble() }
                        .onSuccess {
                            return it.compareTo(other.value)
                        }

                    return this.value.compareTo(other.value.toString())
                }

                is StringKey -> return this.value.compareTo(other.value)
                is LongKey -> {
                    runCatching { this.value.toLong() }
                        .onSuccess {
                            return it.compareTo(other.value)
                        }

                    return this.value.compareTo(other.value.toString())
                }

                is BooleanKey -> return this.value.compareTo(other.value.toString())
            }

            is LongKey ->  when (other) {
                is DoubleKey -> return this.value.toDouble().compareTo(other.value)
                is StringKey -> {
                    runCatching { other.value.toLong() }
                        .onSuccess {
                            return this.value.compareTo(it)
                        }

                    return this.value.toString().compareTo(other.value)
                }

                is LongKey -> return this.value.compareTo(other.value)
                is BooleanKey -> return this.value.toString().compareTo(other.value.toString())
            }

            is BooleanKey -> return when (other) {
                is DoubleKey -> this.value.toString().compareTo(other.value.toString())
                is StringKey -> this.value.toString().compareTo(other.value)
                is LongKey -> this.value.toString().compareTo(other.value.toString())
                is BooleanKey -> this.value.compareTo(other.value)
            }
        }
    }
}