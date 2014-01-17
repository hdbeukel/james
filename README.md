James
=====

James is an extensive java framework for applying metaheuristics to combinatorial optimization problems. The framework is mainly focused on subset selection, which has many applications in various areas, though other types of problems can also easily be plugged in.


A wide range of generic neighbourhood searches are provided out-of-the-box, including

1. basic local search (random descent)
2. steepest descent
3. tabu search
4. variable neighbourhood search
5. Metropolis search
6. replica exchange Monte Carlo search (parallel tempering)

In addition, some specific subset sampling algorithms are available, as well as exhaustive search, which is of course only feasible for problems with a reasonably small search space.

Status
======

*James is currently under development.*

Documentation
=============

For more information and documentation, see http://www.jamesframework.org.

License
=======

The James framework is licensed under the Apache License, Version 2.0, see LICENSE.txt or http://www.apache.org/licenses/LICENSE-2.0. Feel free te reuse the software in source or binary form, with or without modifications, as long as this reuse is in compliance with the license. This basically means that you must:

 - retain the original copyright (see below), with proper acknowledgment
 - include a copy of the full Apache v2.0 license text in your derivative work distribution
 - state any significant changes made to the software

Copyright
=========

James Framework (c) 2014 - Ghent University, Belgium

Third-party components
======================

Copyright notices and full license texts (if applicable) of third-party components used by any of the James modules are included in ${MODULE-ROOT}/NOTICE.txt and ${MODULE-ROOT}/LICENSE.txt, respectively, where $MODULE-ROOT is the root folder of the respective module (e.g. james/james-core for the core module).

Contact
=======

The James framework is developed and maintained by

 - Herman De Beukelaer (Herman.DeBeukelaer@UGent.be)