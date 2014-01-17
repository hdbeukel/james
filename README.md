James
=====

James is an extensive java framework for applying metaheuristics to combinatorial optimization problems. The framework is mainly focused on subset selection, which has many applications in various areas, though other types of problems can also easily be plugged in.

Modules
-------

The James framework consists of several modules:
 
 - [James Core Module][1]: as its name implies, this module contains the core components of the framework, including general classes modeling problems, objectives, constraints, solutions, search engines, etc. A number of generic optimization algorithms are provided, including many metaheuristics such as random descent, steepest descent, tabu search, variable neighbourhood search, parallel tempering, etc. Exhaustive search is also included, which is of course only feasible for problems with a reasonable small search space. Moreover, the core module contains implementations of components for subset selection, as well as some specific subset sampling algorithms.

Status
======

*James is currently under development.*

Documentation
=============

For more information and documentation, see http://www.jamesframework.org.

Contact
=======

The James framework is developed and maintained by

 - Herman De Beukelaer (Herman.DeBeukelaer@UGent.be)
 
 
 
[1]: https://github.ugent.be/hdbeukel/james/tree/master/james/james-core