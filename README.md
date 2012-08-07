# VARIANCE

The aim of *Variance* is to provide a general-purpose, custom-wirable junction box for conversion between Java types.

You put a value - any value - into a Variant, like so:

```java
Variant myVariant = Variant.of(myValue);
```

You get it out again like so:

```java
MyType myInstance = myVariant.as(MyType.class);
```

The value you put in doesn't have to belong to the type you pull out, but it must be *convertible* to that type.

By default, Variants can convert:

  * Anything to String
  * Any type of Number to any other type of Number (and implement the Number interface themselves)
  * String to any kind of Number, and
  * Arrays/Iterables of things it knows how to convert into arrays/Iterables of converted things

```java
assertThat(Variant.of("12").doubleValue(), is(12.0));
assertThat(Variant.of(12.0).toString(), is("12"));
assertThat(Variant.of(1, 2, 3, 4).asIterableOf(String.class), Contains.inOrder("1", "2", "3", "4"));
assertThat(Variant.of("1,2,3,4").asIterableOf(Integer.class), Contains.inOrder(1,2,3,4));
```

You can wire in other conversions by adding them to the *context* the Variant uses to resolve requests for different types. A vanilla Variant will pull its context out of a ThreadLocal, such that you can write:

```java
\\ Enter a context in which numbers are formatted to 4d.p. on conversion to string
ThreadLocalTypeConversionContext.enterExtended(myCustomContext);

assertThat(Variant.of(12).as(String.class), is("12.0000"));

\\ Leave the custom context
ThreadLocalTypeConversionContext.exit();

assertThat(Variant.of(12).as(String.class), is("12"));
```

Alternatively, you can bind a Variant explicitly to a context:

```java
assertThat(Variant.of(12).in(myCustomContext).as(String.class), is("12.0000"));
```

In most cases, you will want to extend an existing context rather than create a new context from scratch:

```java
TypeConversionContext extended = ThreadLocalTypeConversionContext.getCurrent().extendedWith(extensions);
```