**SpinJa** is a model checker for Promela, written in Java. Promela is the modelling language for the [SPIN](http://spinroot.com) model checker. SpinJa supports a large subset of the Promela language.

SpinJa can be used to check for the absence of deadlocks, assertions, liveness properties and LTL properties (via never claims). SpinJa verification mode can use (nested) depth first search or breadth first search. Bitstate hashing and hash compaction modes are also supported. Furthermore, SPIN's partial order reduction and statement merging are implemented. SpinJa can also be used for simulation: random, interactive or guided.

SpinJa is designed to behave similarly to SPIN, but to be more easily extendible and reusable. From the start we have committed ourselves to Java and a clean object-oriented approach. Despite the fact that SpinJa uses a layered object-oriented design and is written in Java, it is not slow: benchmark experiments have shown that on average it is about 3 times slower than the highly optimized SPIN which uses C as implementation language.

The initial version of SpinJa (v0.8) has been developed by Marc de Jonge for his MSc Project within the [Formal Methods and Tools](http://fmt.cs.utwente.nl/) group of the [University of Twente](http://www.utwente.nl/en).
Details on the implementation of SpinJa can be found in his [MSc Thesis](http://fmt.cs.utwente.nl/msc-completed/jonge-msc-thesis.pdf).

The current version of SpinJa is version 0.9.<br>
Under the <a href='http://code.google.com/p/spinja/downloads/list'>Downloads</a> tab you can find the latest binary and source distributions of SpinJa.<br>
Alternatively, you can browse or checkout the source code via the <a href='http://code.google.com/p/spinja/source/checkout'>Source</a> tab.<br>
<br>
For further information on SpinJa see the README.html file in the distributions.<br>
<br>
<i>Update (16 July 2012): The <a href='http://fmt.cs.utwente.nl/tools/ltsmin/'>LTSmin</a> toolset contains a version of SpinJa which emits C code, instead of Java. The LTSmin team has also improved and extended the Promela parser of SpinJa. Eventually, the LTSmin code of SpinJa will be merged with the code base available here at Google Project.</i>

<i><sub>SpinJa stands for SPin IN JAva. SpinJa rhymes with Ninja.</sub></i>