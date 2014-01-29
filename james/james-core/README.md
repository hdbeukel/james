James Core Module
=================

The James core module is part of the James framework for solving combinatorial optimization problems using metaheuristics in java. The framework is mainly focused on subset selection, which has many applications in various areas, though other types of problems can also easily be plugged in.

The core module includes general concepts modeling problems, objectives, constraints, solutions, etc. It also contains generic search algorithms to solve the problems, as well as implementations of specific components for subset selection.

A wide range of generic neighbourhood searches are provided out-of-the-box, including

1. basic local search (random descent)
2. steepest descent
3. tabu search
4. variable neighbourhood search
5. Metropolis search
6. parallel tempering

In addition, some specific subset sampling algorithms are available, as well as exhaustive search, which is of course only feasible for problems with a reasonably small search space.

Status
======

*James core module is currently under development*
  
Documentation
=============  

For more information and documentation, visit www.jamesframework.org.

License and copyright
=====================

The James core module is licensed under the Apache License, Version 2.0, see LICENSE file or http://www.apache.org/licenses/LICENSE-2.0. Copyright information is stated in the NOTICE file.

Contact
=======

The James core module is developed and maintained by

 - Herman De Beukelaer (Herman.DeBeukelaer@UGent.be)
 
Changes
=======

A list of changes is provided in the CHANGES file.