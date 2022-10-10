interface NDArray : SizeAware, DimentionAware {
    /*
     * Получаем значение по индексу point
     *
     * Если размерность point не равна размерности NDArray
     * бросаем IllegalPointDimensionException
     *
     * Если позиция по любой из размерностей некорректна с точки зрения
     * размерности NDArray, бросаем IllegalPointCoordinateException
     */
    fun at(point: Point): Int

    /*
     * Устанавливаем значение по индексу point
     *
     * Если размерность point не равна размерности NDArray
     * бросаем IllegalPointDimensionException
     *
     * Если позиция по любой из размерностей некорректна с точки зрения
     * размерности NDArray, бросаем IllegalPointCoordinateException
     */
    fun set(point: Point, value: Int)

    /*
     * Копируем текущий NDArray
     *
     */
    fun copy(): NDArray

    /*
     * Создаем view для текущего NDArray
     *
     * Ожидается, что будет создан новая реализация  интерфейса.
     * Но она не должна быть видна в коде, использующем эту библиотеку как внешний артефакт
     *
     * Должна быть возможность делать view над view.
     *
     * In-place-изменения над view любого порядка видна в оригнале и во всех view
     *
     * Проблемы thread-safety игнорируем
     */
    fun view(): NDArray

    /*
     * In-place сложение
     *
     * Размерность other либо идентична текущей, либо на 1 меньше
     * Если она на 1 меньше, то по всем позициям, кроме "лишней", она должна совпадать
     *
     * Если размерности совпадают, то делаем поэлементное сложение
     *
     * Если размерность other на 1 меньше, то для каждой позиции последней размерности мы
     * делаем поэлементное сложение
     *
     * Например, если размерность this - (10, 3), а размерность other - (10), то мы для три раза прибавим
     * other к каждому срезу последней размерности
     *
     * Аналогично, если размерность this - (10, 3, 5), а размерность other - (10, 5), то мы для пять раз прибавим
     * other к каждому срезу последней размерности
     */
    fun add(other: NDArray)

    /*
     * Умножение матриц. Immutable-операция. Возвращаем NDArray
     *
     * Требования к размерности - как для умножения матриц.
     *
     * this - обязательно двумерна
     *
     * other - может быть двумерной, с подходящей размерностью, равной 1 или просто вектором
     *
     * Возвращаем новую матрицу (NDArray размерности 2)
     *
     */
    fun dot(other: NDArray): NDArray
}

/*
 * Базовая реализация NDArray
 *
 * Конструкторы должны быть недоступны клиенту
 *
 * Инициализация - через factory-методы ones(shape: Shape), zeros(shape: Shape) и метод copy
 */
class DefaultNDArray private constructor(val shape: Shape, val data: IntArray) : NDArray {
    override val size: Int
        get() = shape.size
    override val ndim: Int
        get() = shape.ndim

    private fun findIndexInData(point: Point): Int {
        if (point.ndim != ndim) throw NDArrayException.IllegalPointDimensionException(point.ndim, ndim)
        (0 until point.ndim).forEach {
            if (point.dim(it) !in 1..shape.dim(it)) throw NDArrayException.IllegalPointCoordinateException(
                it,
                point.dim(it),
                shape.dim(it)
            )
        }

        var indexInData: Int = 0
        var sizeWithoutPreviousDimensions: Int = size
        (0 until ndim).forEach { index ->
            sizeWithoutPreviousDimensions /= shape.dim(index)
            indexInData += (point.dim(index) - 1) * sizeWithoutPreviousDimensions
        }
        return indexInData
    }

    override fun at(point: Point): Int = data[findIndexInData(point)]

    override fun set(point: Point, value: Int) {
        data[findIndexInData(point)] = value
    }

    override fun copy(): NDArray {
        TODO("Not yet implemented")
    }

    override fun view(): NDArray {
        TODO("Not yet implemented")
    }

    override fun add(other: NDArray) {
        if (ndim - other.ndim > 1) throw NDArrayException.IllegalOperationShapeSizeException(
            Operation.ADD,
            ndim,
            other.ndim
        )

    }

    override fun dot(other: NDArray): NDArray {
        TODO("Not yet implemented")
    }

    override fun dim(i: Int): Int = shape.dim(i)

    companion object {
        fun ones(shape: Shape): DefaultNDArray = DefaultNDArray(shape, IntArray(shape.size) { 1 })

        fun zeros(shape: Shape): DefaultNDArray = DefaultNDArray(shape, IntArray(shape.size) { 0 })
    }

}

sealed class NDArrayException(reason: String = "") : Exception(reason) {
    class IllegalPointCoordinateException(indexDimension: Int, pointSize: Int, shapeSize: Int) : NDArrayException(
        "On Dimension with index = $indexDimension, point has size = $pointSize, but shape's size is $shapeSize"
    )

    class IllegalOperationShapeSizeException(operation: Operation, currentNDim: Int, otherNDim: Int) : NDArrayException(
        "In operation ${operation.text} currentShape ndim is $currentNDim and other's ndim is $otherNDim"
    )

    class IllegalPointDimensionException(pointDimension: Int, shapeDimension: Int) :
        NDArrayException("pointDimension = $pointDimension isn't equal shapeDimension = $shapeDimension")
}

enum class Operation(val text: String) {
    ADD("Add"),
    DOT("Dot")
}