# Branching and Looping

domonad expands functions into continuation-passing style, and wraps each continuation in a function defined by with-monad which can tinker with what's done at that point rather than just passing the input into the continuation.
