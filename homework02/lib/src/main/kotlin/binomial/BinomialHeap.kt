package binomial

/*
 * BinomialHeap - реализация биномиальной кучи
 *
 * https://en.wikipedia.org/wiki/Binomial_heap
 *
 * Запрещено использовать
 *
 *  - var
 *  - циклы
 *  - стандартные коллекции
 *
 * Детали внутренней реазации должны быть спрятаны
 * Создание - только через single() и plus()
 *
 * Куча совсем без элементов не предусмотрена
 *
 * Операции
 *
 * plus с кучей
 * plus с элементом
 * top - взятие минимального элемента
 * drop - удаление минимального элемента
 */
class BinomialHeap<T : Comparable<T>> private constructor(private val trees: FList<BinomialTree<T>?>) :
    SelfMergeable<BinomialHeap<T>> {
    companion object {
        fun <T : Comparable<T>> single(value: T): BinomialHeap<T> = BinomialHeap(flistOf(BinomialTree.single(value)))
    }

    fun findByOrder(order: Int): BinomialTree<T>? =
        trees.fold(null) { acc: BinomialTree<T>?, currentTree: BinomialTree<T>? ->
            if (currentTree?.order == order) currentTree else acc
        }

    /*
     * слияние куч
     *
     * Требуемая сложность - O(log(n))
     */
    override fun plus(other: BinomialHeap<T>): BinomialHeap<T> =
        BinomialHeap(
            trees.reverse()
                .fold(flistOf()) { acc: FList<BinomialTree<T>?>, currentTree: BinomialTree<T>? ->
                    val accTail: FList<BinomialTree<T>?> = (acc as? FList.Cons)?.tail ?: FList.nil()
                    val firstBinomialTree: BinomialTree<T>? =
                        if ((acc as? FList.Cons)?.head?.order == currentTree?.order && currentTree != null && (acc as FList.Cons).head != null) acc.head?.plus(
                            currentTree
                        ) else null
                    val necessaryTree: BinomialTree<T>? = other.findByOrder(currentTree?.order ?: -1)
                    if (firstBinomialTree != null && necessaryTree != null) {
                        FList.Cons(firstBinomialTree, FList.Cons(necessaryTree, accTail))
                    } else if (firstBinomialTree != null) {
                        FList.Cons(firstBinomialTree, accTail)
                    } else {
                        val mergedTree: BinomialTree<T>? = necessaryTree?.plus(currentTree ?: throw IllegalArgumentException("Merging uncorrect values")) ?: currentTree
                        FList.Cons(mergedTree, accTail)
                    }
                }.reverse())


    /*
     * добавление элемента
     * 
     * Требуемая сложность - O(log(n))
     */
    operator fun plus(elem: T): BinomialHeap<T> = plus(BinomialHeap(flistOf(BinomialTree.single(elem))))

    /*
     * минимальный элемент
     *
     * Требуемая сложность - O(log(n))
     */
    fun top(): T = trees.fold(
        (trees as? FList.Cons)?.head?.value ?: throw IllegalArgumentException("Heap must have at least 1 vertex")
    )
    { acc: T, currentTree: BinomialTree<T>? ->
        val currentTreeValue: T =
            currentTree?.value ?: throw IllegalArgumentException("Heap must have at least 1 vertex")
        if (acc < currentTreeValue) acc else currentTreeValue
    }

    /*
     * удаление элемента
     *
     * Требуемая сложность - O(log(n))
     */
    fun drop(): BinomialHeap<T> {
        TODO()
    }
}

