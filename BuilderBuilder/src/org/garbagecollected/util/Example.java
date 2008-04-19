/**
 * Copyright (C) 2007 Robbie Vanbrabant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.garbagecollected.util;

public class Example {
  private final String mandatory;
  private final int optional1;
  private final char optional2;

  private Example(ExampleBuilder builder, String mandatory) {
    this.mandatory = mandatory;
    this.optional1 = builder.getOptional1();
    this.optional2 = builder.getOptional2();
  }

  public interface ExampleBuilder extends Builder<Example> {
    ExampleBuilder optional1(int optional1);
    ExampleBuilder optional2(char optional2);
    int getOptional1();
    char getOptional2();
  }

  public static ExampleBuilder builder(final String mandatory) {
    return new BuilderFactory(BuilderType.SIMPLE_SETTER).make(ExampleBuilder.class,
      new BuilderCallback<ExampleBuilder, Example>() {
        public Example call(ExampleBuilder builder) throws Exception {
          return new Example(builder, mandatory);
        }
    });
  }

  public String toString() {
    return new StringBuilder()
      .append(getClass().getName())
      .append(String.format("[optional1=%s, optional2=%s, mandatory=%s]",
                              optional1, optional2, mandatory))
      .toString();
  }
  
  public static void main(String[] args) {
    System.out.println(Example.builder("Mandatory!")
        .optional1(4).build()
    );
  }
}
