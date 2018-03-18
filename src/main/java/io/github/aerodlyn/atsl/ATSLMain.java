package io.github.aerodlyn.atsl;

import java.io.IOException;

import io.github.aerodlyn.atsl.ATSLLexer;
import io.github.aerodlyn.atsl.ATSLParser;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class ATSLMain {
    public static void main (String [] args) {
        switch (args.length) {
            case 0:
                System.out.println("ERROR: Usage is java ATSL [file names].");
                break;

            default:
                try {
                    ATSLLexer lexer = new ATSLLexer(new ANTLRFileStream(args[0]));
                    CommonTokenStream tokens = new CommonTokenStream(lexer);

                    ATSLParser parser = new ATSLParser(tokens);
                    ATSLMainVisitor visitor = new ATSLMainVisitor();

                    visitor.visit(parser.program());
                }

                catch (IOException ex) {
                    System.err.println("ERROR: Unable to read input file: " + args[0] + ".");
                }

                break;
        }
    }
}
