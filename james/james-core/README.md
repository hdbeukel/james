James Core Module
=================

The James core module is part of the [James framework][james-github].

The core module includes many general components for both problem specification and search application.

A wide range of local search algorithms are provided out-of-the-box, including

1. Random descent (basic local search)
2. Steepest descent
3. Tabu search
4. Variable neighbourhood search
5. Metropolis search
6. Parallel tempering
7. ...

Exhaustive search is also available, which is of course only feasible for problems with a reasonably small search space.

In addition, the core contains specific components for subset selection such as a predefined solution type, a generic problem specification and various subset neighbourhoods, as well as a greedy subset sampling heuristic (LR subset search).

  
Documentation
=============

More information, user documentation and examples of how to use the framework are provided at the [website][james-website].
Additional developer documentation is posted on the [wiki][james-wiki].

License and copyright
=====================

The James core module is licensed under the Apache License, Version 2.0, see LICENSE file or http://www.apache.org/licenses/LICENSE-2.0.
Copyright information is stated in the NOTICE file.

Contact
=======

The James core module is developed and maintained by

 - Herman De Beukelaer (Herman.DeBeukelaer@UGent.be)
 
Changes
=======

A list of changes is provided in the CHANGES file.


[james-github]:   https://github.com/hdbeukel/james
[james-website]:  http://www.jamesframework.org
[james-wiki]:     http://github.com/hdbeukel/james/wiki
