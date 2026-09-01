package org.printscript.analyzer;

import java.util.regex.Pattern;

@FunctionalInterface
public interface NamingStyleRules {
  boolean matches(NamingStyle style, String name);

  static NamingStyleRules v1() {
    Pattern snakeCase = Pattern.compile("[a-z][a-z0-9]*(?:_[a-z0-9]+)*");
    Pattern camelCase = Pattern.compile("[a-z][a-zA-Z0-9]*");
    return (style, name) ->
        switch (style) {
          case SNAKE_CASE -> snakeCase.matcher(name).matches();
          case CAMEL_CASE -> camelCase.matcher(name).matches();
        };
  }
}
