
interface Point: DimentionAware

/**
 * Реализация Point по умолчаению
 *
 * Должны работать вызовы DefaultPoint(10), DefaultPoint(12, 3), DefaultPoint(12, 3, 12, 4, 56)
 * с любым количество параметров
 *
 * При попытке создать пустой Point бросается EmptyPointException
 *
 * Сама коллекция параметров недоступна, доступ - через методы интерфейса
 */
class DefaultPoint(private vararg val dimentions: Int) : Point {
    override val ndim: Int
        get() = dimentions.size

    init {
        if (dimentions.isEmpty()) throw PointArgumentException.EmptyPointException()
    }

    override fun dim(i: Int): Int = dimentions[i]
}

sealed class PointArgumentException(reason: String = "") : IllegalArgumentException(reason) {
    class EmptyPointException : PointArgumentException("Point must have at least 1 dimention")
}
