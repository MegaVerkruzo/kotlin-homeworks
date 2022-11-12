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
                .fold(Pair(flistOf(), 0)) { acc: Pair<FList<BinomialTree<T>?>, Int>, currentTree: BinomialTree<T>? ->
                    val currentOrder: Int = currentTree?.order ?: throw IllegalArgumentException("Heap can't be empty")
                    val firstBinomialTree: BinomialTree<T>? =
                        if ((acc.first as? FList.Cons)?.head?.order == currentOrder && (acc.first as FList.Cons).head != null)
                                (acc.first as FList.Cons<BinomialTree<T>?>).head?.plus(currentTree)
                        else null
                    val otherTail: FList<BinomialTree<T>?> = other.trees.filter { currentOtherTree: BinomialTree<T>? ->
                        currentOtherTree?.order in (acc.second until currentOrder)
                    }.reverse()
                    val accTail: FList<BinomialTree<T>?> = otherTail.fold(
                        if (firstBinomialTree == null) ((acc.first as? FList.Cons) ?: FList.nil()) else ((acc.first as? FList.Cons)?.tail ?: FList.nil())
                    ) { accOfAcc: FList<BinomialTree<T>?>, currentTailTree: BinomialTree<T>? ->
                        FList.Cons(currentTailTree, accOfAcc)
                    }
                    val necessaryTree: BinomialTree<T>? = other.findByOrder(currentOrder)
                    if (firstBinomialTree != null && necessaryTree != null) {
                        Pair<FList<BinomialTree<T>?>, Int>(
                            FList.Cons(
                                firstBinomialTree,
                                FList.Cons(necessaryTree, accTail)
                            ), currentOrder + 1
                        )
                    } else if (firstBinomialTree != null) {
                        Pair<FList<BinomialTree<T>?>, Int>(FList.Cons(firstBinomialTree, accTail), currentOrder + 1)
                    } else if (necessaryTree != null){
                        val mergedTree: BinomialTree<T>? = necessaryTree?.plus(currentTree) ?: currentTree
                        Pair<FList<BinomialTree<T>?>, Int>(FList.Cons(mergedTree, accTail), currentOrder + 1)
                    } else {
                        Pair<FList<BinomialTree<T>?>, Int>(FList.Cons(currentTree, accTail), currentOrder + 1)
                    }
                }.first
        )


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
     * удаление элементIllegal argumentsа
     *
     * Требуемая сложность - O(log(n))
     */
    fun drop(): BinomialHeap<T> {

    }
}

