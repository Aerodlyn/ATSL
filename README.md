# Aerodlyn Test Scripting Language

Aerodlyn Test Scripting Language (ATSL) is an attempt to "create" a scripting language. I was inspired to mess with ANTLR as one of my classes has assignments that work with ANTLR.

So, this is mostly an experiment to implement an object-oriented scripting language. I was originally going to do the implementation in C++ but ANTLR has a somewhat annoying issue where functions must return a type of ANTLR's design, instead of one of my choosing, and working with that and arithmetic operations was rather annoying. So I instead decided to use Java instead.

## Example Script
```
begin script Fib 
  begin function f
    as int with int x
    
    begin if x <= 1 then
      return x
    end if
    
    return (call f <- x - 1) + (call f <- x - 2)
  end function f
  
  call write <- "The 25th term of the Fibonacci sequence is: ", (call f <- 25)
end script Fib
```

The above script showcases if-statements, recursion, functions, and printing to the console. The above script also gives an example of one of the limitations of ATSL, that is due to the way functions are called: if there are multiple function calls in an arithmetic expression, they must be surrounded by parentheses (as seen in `return (call f <- x - 1) + (call f <- x - 2)`). I recommend doing this anyway for readability.

## Compiling

The 'Generate_Sources' and 'Clean' scripts are for development purposes (intelli-sense). Simply remove / comment out the 'Delete unneeded files' execution from 'pom.xml'. Of course, you will need Maven. To compile, run `mvn package` and run `mvn exec:java -Dexec.args="..."` where `...` is the name of the file to parse, to execute.
