# TopologicalCrosswords
Small program for bruteforcing prefix-fillable crosswords.

The pattern.txt file must start with a declaration of all pattern characters.
Each consecutive line is a word formed of its pattern characters (to represent its position in the grid).
Be wary of the order of those declarations ! 
The pattern.txt file must be prefix-fillable, which means that under no circumstances should a word being tested in the grid not be either empty, full, or its beginning (a prefix).
