/*
 * This code was developed based on the source code and explanations
 * available in the following references (accessed on November 18, 2026):
 *
 * B-Tree Basics and Search:
 * https://www.geeksforgeeks.org/dsa/introduction-of-b-tree-2/
 *
 * B-Tree Insertion:
 * https://www.geeksforgeeks.org/dsa/insert-operation-in-b-tree/
 *
 * B-Tree Deletion:
 * https://www.geeksforgeeks.org/dsa/delete-operation-in-b-tree/
 *
 * Modified by: Carlos Michel Betemps - Nov/2026
 * UNIPAMPA, Campus Bagé, Brazil.
 *
 * Implementation notes:
 *
 * This B-Tree implementation does not use secondary storage.
 * Therefore, the entire B-Tree resides in main memory.
 * Records are represented by integer (int) keys only.
 * A possible extension would be to store the nodes in a file,
 * implementing disk-based operations such as Disk-Read,
 * Disk-Write, and Allocate-Node.
 *
 * Reference book:
 * Cormen, Thomas H., Charles E. Leiserson, Ronald L. Rivest,
 * and Clifford Stein. Introduction to Algorithms.
 * 4th ed. Cambridge, MA: The MIT Press, 2022.
 *
 * BibTeX:
 * @book{cormen2022introduction,
 *   author    = {Cormen, Thomas H. and Leiserson, Charles E. and Rivest, Ronald L. and Stein, Clifford},
 *   title     = {Introduction to Algorithms},
 *   edition   = {4th},
 *   publisher = {The MIT Press},
 *   year      = {2022},
 *   isbn      = {978-0-262-04630-5}
 * }
 *
 * This implementation is intended for educational purposes,
 * focusing on B-Tree operations and data structure management.
 */

class BTreeNode {
	int[] keys;
	int t;	// min degree t (max is 2t)
	BTreeNode[] C;	// Children
	int n;	// number of the node's keys
	boolean leaf;

	public BTreeNode(int t, boolean leaf) {
		this.keys = new int[2 * t - 1];
		this.t = t;
		this.C = new BTreeNode[2 * t];
		this.n = 0;
		this.leaf = leaf;
	}

	void insertNonFull(int k) { // Insert in a non full node
		int i = n - 1;	// i receives the last key index
		if (leaf) {
			while (i >= 0 && keys[i] > k){ // Shift keys and find location 
				keys[i + 1] = keys[i];
				i--;
			}
			keys[i + 1] = k; // INSERT the new key 'k'
			n++;			 // increment the number of the node's key
		}else {
			while (i >= 0 && keys[i] > k) { // Find the right child's key
				i--;
			}
			if (C[i + 1].n == 2 * t - 1) { // the child is full
				splitChild(i + 1, C[i + 1]); // preemptive split
				if (keys[i + 1] < k) {	// Find the right child's key
					i++;
				}
			}
			C[i + 1].insertNonFull(k); // Recursively INSERT 'k' in a non-full child.
		}
	}

	// Splits the child y of this node. y must be full (2t-1 keys)
	void splitChild(int i, BTreeNode y) {
		BTreeNode z = new BTreeNode(y.t, y.leaf); // allocates a new node to z
		z.n = t - 1;	// new node with the minimum number of keys
		// Copy the last (t-1) keys and children of 
		// y (full child) to z (new child)
		for (int j = 0; j < t - 1; j++) { 
			z.keys[j] = y.keys[j + t]; 
		}
		if (!y.leaf) {	// 'y' is not a leaf, then copy the last children of 'y' to 'z'.
			for (int j = 0; j < t; j++) {
				z.C[j] = y.C[j + t]; 
			}
		}
		y.n = t - 1; // Reduce key count in y
		for (int j = n; j > i; j--) { // making room for the new child 'z'.
			C[j + 1] = C[j]; 
		}
		C[i + 1] = z;	
		for (int j = n - 1; j >= i; j--) { // making room for a new key in this node.
			keys[j + 1] = keys[j]; 
		}
		keys[i] = y.keys[t - 1]; 
		n++; // increment the key count of this node
	}

	void traverse() { // Shows all keys in the B-tree in ascending order.
		for (int i = 0; i < n; i++) {
			if (!leaf) {
				C[i].traverse();
			}
			System.out.print(" " + keys[i]);
		}
		if (!leaf) {
			C[n].traverse();
		}
	}

	void print(int l) { // Shows all nodes (and their keys) in the B-tree.
		for (int i=0;i<l;i++) System.out.print("\t");
		System.out.print("[");
		for (int i = 0; i < n; i++) {
			System.out.print(" " + keys[i]);
		}
		System.out.println(" ]");
		if (!leaf) {
			for (int i = 0; i <= n; i++) {
				C[i].print(l+1);
			}
		}
	}

	BTreeNode search(int k) {
		int i = 0;
		while (i < n && k > keys[i]) {
			i++;
		}
		if (i < n && k == keys[i]) {
			return this;
		}
		if (leaf) {
			return null;
		}
		return C[i].search(k);
	}

	// Helper to find the first key greater than or equal to k in the 'remove' method
	int findKey(int k) {
		int idx = 0;
		while (idx < n && keys[idx] < k) ++idx;
		return idx;
	}

	// Rebalances child C[idx] if it has fewer than t-1 keys
    // Pad child C[idx] via borrowing or merging if it has fewer than t - 1 keys.
	void fill(int idx) {
		// Case 2a: Borrow from left sibling
		if (idx!= 0 && C[idx-1].n >= t) { // left sibling has extra keys (> t - 1).
			BTreeNode child = C[idx], sib = C[idx-1];
			System.arraycopy(child.keys, 0, child.keys, 1, child.n); // Making room in the child's keys
			if (!child.leaf) // Internal nodes must also shift their child pointers to keep them aligned with the shifted keys.
				System.arraycopy(child.C, 0, child.C, 1, child.n+1); //shift the child's children one position to right
			child.keys[0] = keys[idx-1]; //Insert the parent's separating key as the child's first key.
			if (!child.leaf) 
				child.C[0] = sib.C[sib.n]; // Move the left sibling's last child pointer to become this child's first pointer
			keys[idx-1] = sib.keys[sib.n-1]; //Replace the parent key with the left sibling's rightmost key.
			child.n++;  // Increment the child's key count. 
			sib.n--;    // Decrement the sibling's key count.
		} else {
            // Case 2b: Borrow from right sibling.
			if (idx!= n && C[idx+1].n >= t) { // right sibling has extra keys (> t - 1).
				BTreeNode child = C[idx], sib = C[idx+1];
				child.keys[child.n] = keys[idx]; // Insert the parent's separating key as the child's last key.
				if (!child.leaf) 
					child.C[child.n+1] = sib.C[0]; // Move the right sibling's first child pointer to become this child's last pointer
				keys[idx] = sib.keys[0]; // Replace the parent key with the right sibling's leftmost key.
				System.arraycopy(sib.keys, 1, sib.keys, 0, sib.n-1); // shift the sibling's keys one position to the left.
				if (!sib.leaf) 
					System.arraycopy(sib.C, 1, sib.C, 0, sib.n); // shift the sibling's children one position to the left.
				child.n++; // Increment the child's key count. 
				sib.n--;   // Decrement the sibling's key count.
			} else {
                // Case 2c: Merge C[i] (child), keys[i], and C[i + 1] (sib) into one node.
				int i = (idx!= n) ? idx : idx-1;
				BTreeNode child = C[i], sib = C[i+1];
				child.keys[t-1] = keys[i];
				System.arraycopy(sib.keys, 0, child.keys, t, sib.n); // copy the sibling keys to the child.
				if (!child.leaf) 
					System.arraycopy(sib.C, 0, child.C, t, sib.n+1); // copy the sibling children to the child.
				System.arraycopy(keys, i+1, keys, i, n-i-1); // remove the keys[i] from this node.
				System.arraycopy(C, i+2, C, i+1, n-i-1);     // remove the corresponding children from this node.
				child.n += sib.n + 1;       // Update the child's key count. 
				n--;                        // Decrement the key count of this node.
			}
		}
	}

	void remove(int k) {
		int idx = findKey(k);
		if (idx < n && keys[idx] == k) { // 'k' is on this node
			if (leaf) { // Case 1: removing 'k' from a leaf.
				// Delete from leaf -- remove 'k' from this node
				System.arraycopy(keys, idx+1, keys, idx, n-idx-1);
				n--;
			} else {
                // Case 2: Remove key from an internal node and rebalance

                // Case 2a: Predecessor child C[idx] has at least t keys (more than the minimum).
                // Replace the target key with its predecessor, then recursively delete it.
				if (C[idx].n >= t) { 
                    // Traverse down the rightmost path of C[idx] to find the predecessor					
                    BTreeNode cur = C[idx]; 
					while (!cur.leaf) 
                        cur = cur.C[cur.n];
					int pred = cur.keys[cur.n-1]; 
					keys[idx] = pred; 
                    C[idx].remove(pred);
				} else {
                    // Case 2b: Successor child C[idx + 1] has at least t keys (more than the minimum).
                    // Replace the target key with its successor, then recursively delete it.
                    if (C[idx+1].n >= t) { 	
                        // Traverse down the leftmost path of C[idx + 1] to find the successor
						BTreeNode cur = C[idx+1];
						while (!cur.leaf) 
                            cur = cur.C[0];
						int succ = cur.keys[0]; 
						keys[idx] = succ; 
						C[idx+1].remove(succ); 
					} else {
                        // Case 2c: both surrounding children have t-1 keys. 
						// Merge C[idx], keys[idx], and C[idx + 1] into one node, then recursively remove 'k'.
						fill(idx); // Merge key[idx] and the two sibling children into one node.
                        remove(k); // Remove 'k' from the merged node
					}
				}
			}
		} else {
			// Key ‘k’ is not in this node
			if (leaf) return; // Leaf nodes contain no key 'k' to remove.
			boolean flag = (idx == n); // Flagged if 'idx' is at the upper bound (n)
			if (C[idx].n < t) // Check if the child node has only the minimum required keys to merge preemptively.
                fill(idx); // Combine key[idx] and both adjacent child nodes into a single node
			if (flag && idx > n) // If 'idx' was originally the last child and was merged into its left sibling ...
                C[idx-1].remove(k); // ...adjust to the new last child (remove 'k' from 'idx-1')
			else 
                C[idx].remove(k); // otherwise, remove 'k' from 'idx'
		}
	}

}	// End of class BTreeNode 

public class BTree {
	BTreeNode root;
	int t;

	public BTree(int t) {
		this.root = null;
		this.t = t;
	}

	void traverse() { 
		if (root!= null) root.traverse(); 
		System.out.println(); 
	}

	void print() { 
		if (root!= null) {
            System.out.println("B-Tree (min. degree: " + t + "):");
			root.print(0); 
			System.out.println(); 
		}
		else System.out.println("Empty B-Tree (min. degree: " + t + "):"); 
	}

	BTreeNode search(int k) {
		return root == null ? null : root.search(k);
	}

	void insert(int k) { 
		if (root == null) { // If the B-tree is empty, create a new root
			root = new BTreeNode(t, true);
			root.keys[0] = k;
			root.n = 1;
		} else {
            if (search(k) != null) return; // Exit early if 'k' is already in the tree.
			if (root.n == 2 * t - 1) { // If root is full.
				BTreeNode s = new BTreeNode(t, false); // Create a node (s) to serve as the new root
				s.C[0] = root; // Set the current root as the new node's first child.
				s.splitChild(0, root); // Call splitChild on the current root, which is full.
				int i = 0;
				if (s.keys[0] < k) { // Determine which child (i) of the root will receive the new key (k).
					i++;
				}
				s.C[i].insertNonFull(k); // Insert in the non full child 'i'
				root = s; // Set the new root, increasing the B-Tree's height by one
			} else {
				root.insertNonFull(k); // Insert key 'k' into the non-full root node
			}
		}
	}

	void remove(int k) {
		if (root == null) return; // If the B-tree (root) is empty, return immediately
		root.remove(k); // Let the remove(k) method of BTreeNode delete key 'k'
		if (root.n == 0) {  // if the root turns to empty.
			// Shrink tree height      {BTreeNode tmp = root;}removed 
            // If the root is a leaf, the B-Tree becomes empty (null); 
            // otherwise, the root is replaced by its first child (shrinking the tree height).
			root = root.leaf? null : root.C[0];  
		}
	}

	public static void main(String[] args) {
    /* Testing the BTree with some insertions and deletions, showing the BTree structure and keys. */
        int MAX=100, N=50, T=2, key;
		BTree tree = new BTree(T);
		java.util.Random r = new java.util.Random();
        System.out.println("B-Tree after instantiation...");
        tree.print();
        System.out.println("Inserting keys:");
		for(int i=0;i<N;i++){
            key=r.nextInt(MAX)+1;
            System.out.print(key + " ");
			tree.insert(key);
		}

        System.out.println("\nB-Tree after the insertions...");
        tree.print();

		key = 23;
		if (tree.search(key) != null) {
			System.out.println("Key "+ key + ": Present");
		}
		else{
			System.out.println("Key "+ key + ": Not Present");
		}
		key = 13;
		if (tree.search(key) != null) {
			System.out.println("Key "+ key + ": Present");
		}
		else{
			System.out.println("Key "+ key + ": Not Present");
		}
        

        System.out.println("\nRemoving keys:");
		for(int i=0;i<N;i++){
            key=r.nextInt(MAX)+1;
            System.out.print(key + " ");
			tree.remove(key);
		}

        System.out.println("\nB-Tree after the deletions...");
        tree.print();

        System.out.println("\nB-Tree's keys in ascending order...");
        tree.traverse();


	}
}
