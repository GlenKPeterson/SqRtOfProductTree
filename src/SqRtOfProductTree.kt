import kotlin.math.sqrt

/**
 * Problem:
 * The blocks in the diagram below are arranged so that the value in each
 * block is the square root of the product of the two values below it.
 * Find the number in the top block. Give an exact answer.
 *
 *                .----------.
 *                |          |
 *           .----------.----------.
 *           |    60    |          |
 *      .----------.----------.----------.
 *      |          |    36    |          |
 * .----------.----------.----------.----------.
 * |    625   |          |          |   1296   |
 * '----------'----------'----------'----------'
 *
 * This can be represented as a tree where each parent has 0 or 2 children.
 * It's built evenly, and it's 4 nodes deep.
 *
 * There are several problems here:
 *  - Represent the data.  Object-oriented programming suggests this has to go
 *    first.  Clojure programmers might not start here.
 *  - Solve a Triple: Solve a parent and 2 children when you have values for 2
 *    out of 3 of them.
 *  - Traverse the tree until all groups of 3 nodes are solved (and stop
 *    traversing)
 *  - Present the output
 *
 * Of these, Solve a Triple seems the most obvious.  Given Parent p and Child
 * 1 c1 and Child 2 c2:
 *
 * p = root(c1, c2)
 * c1 = p^2/c2
 * c2 = p^2/c1
 *
 * I can think of 3 ways to traverse the tree:
 *  - Doubly-link the Nodes, so that each each parent knows how to find its
 *    children and each child its parent.
 *  - Stop from the top and use a LIFO stack to store pairs of Parent and
 *    Child-number that you're currently visiting. I've had real trouble
 *    implementing the LIFO traversal, so I'd go with double-linking.
 *  - Throw the nodes in a list and don't worry about it.
 *
 * OK, Now I'm ready to think about representation.
 *
 *  At first I thought I'd have to store the left and right parents, but after
 *  throwing them all in a list, my compiler told me I didn't need parents
 *  anymore.
 */

class Node(
    var leftChild: Node? = null,
    var value: Double? = null,
    var rightChild: Node? = null,
)

/**
 * DAG looks like this:
 *
 *                      root
 *                      /  \
 * Level 1          l1n1    l1n2
 *                  /  \    /  \
 * Level 2      l2n1    l2n2    l2n3
 *              /  \   /   \   /   \
 * L3        l3n1    l3n2    l3n3    l3n4
 *
 * We'll build it from the bottom up
 */
// Level 3
val l3n1 = Node(null, 625.0, null)
val l3n2 = Node(null, null, null)
val l3n3 = Node(null, null, null)
val l3n4 = Node(null, 1296.0, null)

// Level 2
val l2n1 = Node(l3n1, null, l3n2)
val l2n2 = Node(l3n2, 36.0, l3n3)
val l2n3 = Node(l3n3, null, l3n4)

// Level 1 below root
val l1n1 = Node(l2n1, 60.0, l2n2)
val l1n2 = Node(l2n2, null, l2n3)

val root = Node(l1n1, null, l1n2)

val allNodes: List<Node> = listOf(
    root,
    l1n1, l1n2,
    l2n1, l2n2, l2n3,
    l3n1, l3n2, l3n3, l3n4,
)

fun square(x: Double) = x * x

fun printTree() {
    println("               .----------.")
    println("               |${pretty(root.value)}|")
    println("          .----------.----------.")
    println("          |${pretty(l1n1.value)}|${pretty(l1n2.value)}|")
    println("     .----------.----------.----------.")
    println("     |${pretty(l2n1.value)}|${pretty(l2n2.value)}|${pretty(l2n3.value)}|")
    println(".----------.----------.----------.----------.")
    println("|${pretty(l3n1.value)}|${pretty(l3n2.value)}|${pretty(l3n3.value)}|${pretty(l3n4.value)}|")
    println("'----------'----------'----------'----------'")
}

fun pretty(x: Double?): String = if (x == null) {
    "          "
} else {
    val s = "$x"
    when (s.length) {
        9 -> "$s "
        8 -> " $s "
        7 -> "  $s "
        6 -> "  $s  "
        5 -> "   $s  "
        4 -> "   $s   "
        3 -> "    $s   "
        2 -> "    $s    "
        1 -> "     $s    "
        else -> if (s.length > 10) {
            s.substring(0, 10)
        } else {
            s
        }
    }
}

/**
 * When we have 2 out of the 3 data points (parent and 2 children)
 * we can solve for the third.
 */
fun solveThirdNode(currNode: Node) {
    // Do we have 2/3 data points?
    var numDataPoints = 0
    if (currNode.value != null) {
        numDataPoints++
    }
    val leftChild = currNode.leftChild
    val rightChild = currNode.rightChild
    if (leftChild != null &&
        leftChild.value != null) {
        numDataPoints++
    }
    if (rightChild != null &&
        rightChild.value != null) {
        numDataPoints++
    }
    if (numDataPoints == 2) {
        if (currNode.value == null) {
            currNode.value = sqrt(leftChild!!.value!! * rightChild!!.value!!)
        } else if (leftChild!!.value == null) {
            leftChild.value = square(currNode.value!!) / rightChild!!.value!!
        } else {
            rightChild!!.value = square(currNode.value!!) / leftChild.value!!
        }
    }
}

fun main() {
    println("\nProblem:")
    printTree()

    // Visit each node once, top to bottom
    allNodes.forEach { solveThirdNode(it) }

    println("\nSolved Children:")
    printTree()

    // Visit each node once, bottom to top
    allNodes.reversed().forEach { solveThirdNode(it) }

    println("\nSolution:")
    printTree()
}