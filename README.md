# PicoDI

A very very simple Java Dependencies Injection (DI) engine.

## Json encoding

Use of Json library https://code.google.com/p/json-simple

Json value to Java type mapping:
* string => java.lang.string
* integer number => java.lang.Long
* decimal number => java.lang.Double
* true or false => java.lang.Boolean
* null => null
 
