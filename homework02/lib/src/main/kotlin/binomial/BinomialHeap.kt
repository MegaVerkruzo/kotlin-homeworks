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
                    val otherTail: FList<BinomialTree<T>?> = other.trees.filter { currentOtherTree: BinomialTree<T>? ->
                        currentOtherTree?.order in (acc.second until currentOrder)
                    }
                    val mergedAcc: FList<BinomialTree<T>?> = when {
                        acc.first.isEmpty -> otherTail
                        otherTail.isEmpty -> acc.first
                        else -> (BinomialHeap(acc.first) + BinomialHeap(otherTail)).trees
                    }
                    val firstBinomialTree: BinomialTree<T>? =
                        if ((mergedAcc as? FList.Cons)?.head?.order == currentOrder && mergedAcc.head != null) mergedAcc.head.plus(
                            currentTree
                        )
                        else null
                    val accTail: FList<BinomialTree<T>?> = (
                            if (firstBinomialTree == null) (mergedAcc as? FList.Cons)
                            else (mergedAcc as? FList.Cons)?.tail
                            ) ?: FList.nil()
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
                    } else if (necessaryTree != null) {
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

    private fun getPairWithMainPartOfBinomialTreeAndWithTreeConsistsDetermineOrder(order: T): Pair<FList<BinomialTree<T>?>, BinomialTree<T>?> =
        trees.fold(
            Pair(
                FList.nil(),
                null
            )
        ) { acc: Pair<FList<BinomialTree<T>?>, BinomialTree<T>?>, currentTree: BinomialTree<T>? ->
            val currentMinValue: T = currentTree?.value ?: throw IllegalArgumentException("BinaryTree can't be empty")
            return@fold if (order == currentMinValue) Pair(acc.first, currentTree) else Pair(
                FList.Cons(
                    currentTree,
                    acc.first
                ), acc.second
            )
        }

    fun drop(): BinomialHeap<T> {
        val splitHeap = getPairWithMainPartOfBinomialTreeAndWithTreeConsistsDetermineOrder(top())
        val childrenOfDroppedTree: FList<BinomialTree<T>> =
            splitHeap.second?.children ?: throw IllegalArgumentException("You can't delete last element or Empty Heap")
        return BinomialHeap(splitHeap.first.reverse()) + BinomialHeap(childrenOfDroppedTree.map { tree: BinomialTree<T> -> tree })
    }
}

