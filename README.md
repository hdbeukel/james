James
=====

James is an extensive java framework for applying metaheuristics to combinatorial optimization problems. The framework is mainly focused on subset selection, which has many applications in various areas, though other types of problems can also easily be plugged in.

Modules
-------

The James framework consists of several modules:
 
 - [James Core Module][1]: as its name suggests, this module contains the core components of the framework. It includes general concepts modeling problems, objectives, constraints and algorithms.
 
   A number of generic optimization algorithms are provided out-of-the-box, including many metaheuristics such as random descent, steepest descent, tabu search, variable neighbourhood search, parallel tempering, etc. Exhaustive search is also included, which is of course only feasible for problems with a reasonable small search space.
   
   Moreover, the core module contains implementations of components for subset selection, as well as some specific subset sampling algorithms.
   
 - [James Extensions Module][2]: this module extends the core with components for advanced subset selection, as well as other types of problems (e.g. permutation problems). 

Status
======

*James is currently under development*

Documentation
=============

For more information and documentation, visit www.jamesframework.org.

Contact
=======

The James framework is developed and maintained by

 - Herman De Beukelaer (Herman.DeBeukelaer@UGent.be)
 - Guy Davenport (guy.davenport@bayer.com)
 
 
 
[1]: https://github.com/hdbeukel/james/tree/master/james/james-core
[2]: https://github.com/hdbeukel/james/tree/master/james/james-extensions