//  ========================================================================
//  Copyright (c) 2017 Direct Solution Software Builders (DSSB).
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
package dssb.utils.pipeable.tools;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.IntStream.range;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import lombok.val;


// IF YOU COME HERE TO SEE THE CODE, TURN AWAY, IGNORE THE MAN BEHIND THE CURTAIN!!!


@SuppressWarnings("javadoc")
public class PipeableCodeGenerator {
    
    static enum Flags {
        NoCatcher, CheckedCatcher, UncheckedCatcher;
        
        public boolean hasCheckedChecker()  { return this == CheckedCatcher; }
        public boolean hasNoCheckedCacher() { return this == UncheckedCatcher; }
        public boolean withCatcher()        { return this != NoCatcher; }
    }
    
    public static void main(String[] args) {
        
        val indexTexts = asList(
                "1st",
                "2nd",
                "3rd",
                "4th",
                "5th",
                "6th",
                "7th",
                "8th",
                "9th",
                "10th",
                "11th",
                "12th",
                "13th",
                "14th",
                "15th",
                "16th",
                "17th",
                "18th",
                "19th",
                "20th",
                "21st",
                "22nd",
                "23rd",
                "24th",
                "25th",
                "26th",
                "27th",
                "28th",
                "29th",
                "30th",
                "31st",
                "32nd",
                "33rd"
        );
        val indentation = "    ";
        
        range(1, 17).forEach(operatorCount->{
        
            val flags = asList(Flags.values());
            flags.forEach(flag->{
                val hasCheckedCacher   = flag.hasCheckedChecker();
                val hasNoCheckedCacher = flag.hasNoCheckedCacher();
                val withCatcher = hasCheckedCacher || hasNoCheckedCacher;
                val descriptionSuffix
                        = hasCheckedCacher   ? " and a catcher that might throw a checked exception"
                        : hasNoCheckedCacher ? " and a catcher that does not throw a checked exception"
                        :                      "";
                
                val javaDocStart     = "/**";
                val javaDocDesc      = format(" * Pipe with %s operators%s.", operatorCount, descriptionSuffix);
                val javaDocSpace     = " * ";
                val javaDocOperators = range(0, operatorCount).mapToObj(javaDocOperator(indexTexts));
                val javaDocCatch     = withCatcher ? " * @param catcher     the catcher." : null;
                val javaDocReturn    = " * @return            the result.";
                val javaDocThrow     = hasCheckedCacher ? Stream.of(" * @throws FINAL_THROWABLE if the catcher throw an exception.") : Stream.empty();
                val javaDocEnd       = " **/";
                val javaDocParts     = asList(javaDocStart, javaDocDesc, javaDocSpace, javaDocOperators, javaDocCatch, javaDocReturn, javaDocThrow, javaDocEnd);
                
                val javaDoc = javaDocParts.stream().flatMap(toStreams());
                
                javaDoc.forEach(System.out::println);
                
                val signPrefix    = "public default <";
                val firstLine     = signPrefix + genericDeclaration("").apply(0);
                val genrOperators = range(1, operatorCount - 1).mapToObj(genericDeclaration("            "));
                val genrSuffix    = ">";
                val genrResult    = "            RESULT, THROWABLE   extends Throwable" + (hasCheckedCacher ? "," : genrSuffix);
                val genrThrow     =  hasCheckedCacher  ? "            FINAL_THROWABLE extends Throwable" + genrSuffix: null;
                val signSuffix    = ") {";
                val signName      = "    RESULT pipe(" + signOperator("", operatorCount, withCatcher, signSuffix, 0);
                val signOperators = range(1, operatorCount).mapToObj(operIndex -> signOperator(indentation + indentation, operatorCount, withCatcher, signSuffix, operIndex));
                val signCatch = hasCheckedCacher ? indentation + indentation + indentation + indentation + "Catch<RESULT, FINAL_THROWABLE> catcher) throws FINAL_THROWABLE {"
                            : hasNoCheckedCacher ? indentation + indentation + indentation + indentation + "CatchNoCheckException<RESULT> catcher" + signSuffix
                            :                      "";
                
                System.out.println(firstLine);
                val signParts = asList(genrOperators, genrResult, genrThrow, signName, signOperators, signCatch);
                val sign = signParts.stream().flatMap(toStreams()).map(line->indentation + line);
                sign.forEach(System.out::println);
                
                val bodyIndentation = withCatcher ? "    " : "";
                val bodyPrefix = withCatcher ? Stream.of(indentation + "try {") : Stream.empty();
                val bodyPipe = range(0, operatorCount)
                        .mapToObj(operIndex -> {
                            val operNum = (operIndex + 1);
                            val input    = (operIndex ==                   0) ? "this" : "pipe" + (operNum - 1);
                            val operator = "operator" + operNum;
                            val var      = (operIndex != (operatorCount - 1)) ? "pipe" + operNum : "result";
                            val method   = (operIndex != (operatorCount - 1)) ? "operateToPipe" : "operateToResult";
                            val space    = ((operNum < 10) && (operNum != operatorCount)) ? " " : "";
                            return format(indentation + "val %s %s= %s.%s(%s);", var, space, operator, method, input);
                        })
                        .map(line->bodyIndentation + line);
                val bodyReturn = bodyIndentation + indentation + "return result;";
                val bodySuffix = withCatcher
                        ? Stream.of(
                                indentation + "} catch (FailableException failableException) {",
                                indentation + "    return catcher.handle(failableException);",
                                indentation + "}")
                        : Stream.empty();
                
                val bodyParts = asList(bodyPrefix, bodyPipe, bodyReturn, bodySuffix);
                val body = bodyParts.stream().flatMap(toStreams()).map(line->indentation + line);
                body.forEach(System.out::println);
                
                System.out.println(indentation + "}");
                System.out.println();
                
            });
        });
    }

    private static String signOperator(final java.lang.String indentation, int operatorCount, final boolean withCatcher,
            final java.lang.String signSuffix, int operIndex) {
        val operNum = (operIndex + 1);
        val inType  = (operIndex ==                   0) ? "TYPE" : "TYPE" + (operNum - 1);
        val space1  = (operIndex ==                   0) ? " "    : "";
        val space2  = (operIndex < 10) ? " " : "";
        val space3  = (operIndex < 9) ? " " : "";
        val space4  = (operIndex < 9) ? " " : (operIndex != (operatorCount - 1)) ? "" : "  ";
        val outType = (operIndex != (operatorCount - 1)) ? "TYPE"      + operNum : "RESULT";
        val thrType = (operIndex != (operatorCount - 1)) ? "THROWABLE" + operNum : "THROWABLE";
        val name    = "operator"  + operNum;
        val suffix  = (withCatcher || (operIndex != (operatorCount - 1))) ? ",": signSuffix;
        return format(indentation + indentation + "Operator<%s, %s%s%s, %s%s> %s%s%s", inType, space1, space2, outType, space3, thrType, space4, name, suffix);
    }
    
    private static IntFunction<? extends String> genericDeclaration(String indent) {
        return operIndex->{ 
            val space = (operIndex < 9) ? " " : "";
            return format("%sTYPE%d, %sTHROWABLE%d %sextends Throwable,", indent, (operIndex + 1), space, (operIndex + 1), space);
        };
    }
    
    @SuppressWarnings("unchecked")
    private static Function<? super Object, ? extends Stream<? extends String>> toStreams() {
        return obj->(Stream<String>)((obj instanceof Stream) ? obj : (obj != null) ? Stream.of(obj) : Stream.empty());
    }
    
    private static IntFunction<? extends String> javaDocOperator(final java.util.List<java.lang.String> indexTexts) {
        return operIdx->format(
                " * @param operator%s%s  the %s operator.",
                    (operIdx + 1),
                    (operIdx < 9 ? " " : ""),
                    indexTexts.get(operIdx));
    }
    
    
}
