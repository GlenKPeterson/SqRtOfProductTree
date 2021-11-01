import kotlin.math.sqrt

/**
It's a DAG where each parent has 0 or 2 children and children have 0, 1, or 2 parents, it's built evenly, and it's 4 nodes deep.

There are several problems here:
Represent the data.  Object-oriented programming suggests this has to go first.  Clojure programmers might not start here.
Solve a Triple: Solve a parent and 2 children when you have values for 2 out of 3 of them.
Traverse the tree until all groups of 3 nodes are solved (and stop traversing)
Present the output

Of these, Solve a Triple seems the most obvious.  Given Parent p and Child 1 c1 and Child 2 c2:

p = root(c1, c2)
c1 = p^2/c2
c2 = p^2/c1

I can think of 2 ways to traverse a triple:
doubly-link the triples, so that each each parent knows how to find its children and each child its parent.  OR
Stop from the top and use a LIFO stack to store pairs of Parent and Child-number that you're currently visiting.
I've had real trouble implementing the LIFO traversal, so I'd go with double-linking.

OK, Now I'm ready to think about representation.
*/

data class Node(
    var leftParent: Node?,
    var rightParent: Node?,
    var value: Double? = null,
    var leftChild: Node? = null,
    var rightChild: Node? = null,
)

/**
 * Making the nodes is hostile to a functional style, so here's the imperative way.
 * DAG looks like:
 *
 *                      root
 *                      /  \
 * Level 1          l1n1    l1n2
 *                  /  \    /  \
 * Level 2      l2n1    l2n2    l2n3
 *              /  \   /   \   /   \
 * L3        l3n1    l3n2    l3n3    l3n4
 */
val root = Node(null, null)

// Level 1 below root
val l1n1 = Node(null, root, 60.0)
val l1n2 = Node(root, null)

// Level 2
val l2n1 = Node(null, l1n1)
val l2n2 = Node(l1n1, l1n2, 36.0)
val l2n3 = Node(l1n2, null)

// Level 3
val l3n1 = Node(null, l2n1, 625.0)
val l3n2 = Node(l2n1, l2n2)
val l3n3 = Node(l2n2, l2n3)
val l3n4 = Node(l2n3, null, 1296.0)

val allNodes: List<Node> = listOf(
    root,
    l1n1, l1n2,
    l2n1, l2n2, l2n3,
    l3n1, l3n2, l3n3, l3n4,
)

fun square(x: Double) = x * x

fun solveThirdNode(currNode: Node): Unit {
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
    println(root)

    // Visit each node once, top to bottom, assigning parents
    allNodes.forEach {
        val lp = it.leftParent
        if (lp != null) {
            println("left parent: $lp")
            lp.rightChild = it
        }
        val rp = it.rightParent
        if (rp != null) {
            println("right parent: $rp")
            rp.leftChild = it
        }
    }

//    println(root)

//    // Visit each node once, top to bottom
//    allNodes.forEach { solveThirdNode(it) }
//    // Visit each node once, bottom to top
//    allNodes.reversed().forEach { solveThirdNode(it) }

//    println(root)
}
