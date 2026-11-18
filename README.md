# BTree
## A Simple B-Tree Implementation

This code was developed based on the source code and explanations available in the following references (accessed on November 18, 2026):

- **B-Tree Basics and Search:** https://www.geeksforgeeks.org/dsa/introduction-of-b-tree-2/
- **B-Tree Insertion:** https://www.geeksforgeeks.org/dsa/insert-operation-in-b-tree/
- **B-Tree Deletion:** https://www.geeksforgeeks.org/dsa/delete-operation-in-b-tree/

**Modified by:** Carlos Michel Betemps - November 2026  
**Institution:** UNIPAMPA, Campus Bagé, Brazil.

## Implementation Notes

This B-Tree implementation does not use secondary storage. Therefore, the entire B-Tree resides in main memory.

Records are represented by integer (`int`) keys only.

A possible extension would be to store the nodes in a file, implementing disk-based operations such as `Disk-Read`, `Disk-Write`, and `Allocate-Node`.

## Reference Book

Cormen, Thomas H., Charles E. Leiserson, Ronald L. Rivest, and Clifford Stein. *Introduction to Algorithms*. 4th ed. Cambridge, MA: The MIT Press, 2022.

### BibTeX

```bibtex
@book{cormen2022introduction,
  author    = {Cormen, Thomas H. and Leiserson, Charles E. and
               Rivest, Ronald L. and Stein, Clifford},
  title     = {Introduction to Algorithms},
  edition   = {4th},
  publisher = {The MIT Press},
  year      = {2022},
  isbn      = {978-0-262-04630-5}
}
```

## Purpose

This implementation is intended for educational purposes, focusing on B-Tree operations and data structure management.

---

**Carlos Michel Betemps**  
November 18, 2026.
