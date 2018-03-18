# Aerodlyn Test Scripting Language

Aerodlyn Test Scripting Language (ATSL) is an attempt to "create" a scripting language. I was inspired to mess with ANTLR 
as one of my classes has assignments that work with ANTLR.

So, this is mostly an experiment to implement an object-oriented scripting language. I was originally going to do the 
implementation in C++ but ANTLR has a somewhat annoying issue where functions must return a type of ANTLR's design, instead
of one of my chosing, and working with that and arithmetic operations was rather annoying. So I instead decided to use Java 
instead.

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

The above script showcases if-statements, recursion, functions, and printing to the console.

## Compiling

The 'Generate_Sources' and 'Clean' scripts are for development purposes (intelli-sense). Simply remove the 'Remove unneeded files' execution from 'pom.xml'. Of course, you will need Maven. To compile, run `mvn package` and `mvn exec:java -Dexec.args="..."` where `...` is the name of the file to parse.
