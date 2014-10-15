James Core Module Changes
=========================


Version 0.2 (under development)
-------------------------------

 - Added support for efficient delta evaluations and validations.
 - Provided additional subset neighbourhoods.
 - Renamed SubsetData to IntegerIdentifiedData.
 - Renamed ProblemWithData to AbstractProblem.
 - Refactored abstract Solution class.
 - Reorganized package structure.
 - Various code simplifications and optimizations (e.g. using functional operations).
 - Removed SubsetProblem interface and renamed SubsetProblemWithData to SubsetProblem.
 - Removed MinMaxObjective.
 - Removed EmptySearchListener and EmptyLocalSearchListener. Default empty implementations of all callbacks are now directly provided in the corresponding interfaces.
 - Moved to SLF4J API 1.7.7.
 - Moved to Java 8.


Version 0.1 (25/06/2014)
------------------------

 - First stable release of the James Core Module.