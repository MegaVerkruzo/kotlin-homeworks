interface Shape : DimentionAware, SizeAware

/**
 * Реализация Point по умолчаению
 *
 * Должны работать вызовы DefaultShape(10), DefaultShape(12, 3), DefaultShape(12, 3, 12, 4, 56)
 * с любым количество параметров
 *
 * При попытке создать пустой Shape бросается EmptyShapeException
 *
 * При попытке указать неположительное число по любой размерности бросается NonPositiveDimensionException
 * Свойство index - минимальный индекс с некорректным значением, value - само значение
 *
 * Сама коллекция параметров недоступна, доступ - через методы интерфейса
 */
class DefaultShape(private vararg val dimentions: Int) : Shape {
    override val ndim: Int
        get() = dimentions.size

    override val size: Int = dimentions.reduce { accumulator, element ->
        accumulator * element
    }

    constructor() : this(*IntArray(0)) {
        throw ShapeArgumentException.EmptyShapeException()
    }
    init {
        println("smth ${dimentions.joinToString(" ")}")
        if (dimentions.isEmpty()) throw ShapeArgumentException.EmptyShapeException()

        val firstIncorrectIndex = dimentions.indexOfFirst { it <= 0 }
        if (firstIncorrectIndex != -1) throw ShapeArgumentException.NonPositiveDimensionException(
            firstIncorrectIndex,
            dimentions[firstIncorrectIndex]
        )
    }

    override fun dim(i: Int): Int = dimentions.getOrNull(i) ?: throw ShapeArgumentException.NonPositiveDimensionException(i, null)
}

sealed class ShapeArgumentException(reason: String = "") : IllegalArgumentException(reason) {
    class EmptyShapeException() : ShapeArgumentException("Shape must have at least 1 dimention")

    class NonPositiveDimensionException(val index: Int, val value: Int?) :
        ShapeArgumentException(if (value == null) "Incorrect index = $index of dimention" else "Dimention with index = $index has incorrect value = $value")
}
