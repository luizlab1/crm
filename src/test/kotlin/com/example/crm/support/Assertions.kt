package com.example.crm.support

infix fun <T> T.shouldBe(expected: T) {
    if (this != expected) {
        throw AssertionError("Expected <$expected>, but was <$this>")
    }
}

fun Any?.shouldBeNull() {
    if (this != null) {
        throw AssertionError("Expected <null>, but was <$this>")
    }
}

fun <T> T?.shouldNotBeNull(): T {
    return this ?: throw AssertionError("Expected value to be not null")
}

inline fun <reified T : Throwable> shouldThrow(block: () -> Unit): T {
    try {
        block()
    } catch (error: Throwable) {
        if (error is T) return error
        throw AssertionError("Expected exception ${T::class.java.simpleName}, but was ${error::class.java.simpleName}")
    }
    throw AssertionError("Expected exception ${T::class.java.simpleName}, but no exception was thrown")
}
