# avltree

A WIP clojure implementation of an AVL tree, along with basic speclj tests.

Automatically balances on inserts and performs binary search, both on values that the < function can handle.

TODO: Deletes, in-order processing, and supporting things like seq and map.

## Usage

To create new trees:
(create-tree value)
or
(insert value) 

To insert multiple values, add to an existing tree:
(-> (create-tree 3)
    (insert 2)
    (insert 1))
or, equivilently, pass multiple arguments to insert:
(insert 3 2 1)

Trees are edn maps with :value, :left and :right tags, and this is currently the only way to access them.  Nodes also maintain a :height value, but this is an implementation detail.

## License

Copyright 2012 FIXME


