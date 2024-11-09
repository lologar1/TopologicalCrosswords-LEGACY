# TopologicalCrosswords
Small program for bruteforcing prefix-fillable crosswords.

The pattern.txt file must start with a declaration of all pattern characters.
Each consecutive line is a word formed of its pattern characters (to represent its position in the grid).
Be wary of the order of those declarations ! 
The pattern.txt file must be prefix-fillable, which means that under no circumstances should a test in the grid not be applied to either empty space, a full word, or the beginning of a word (a prefix).
