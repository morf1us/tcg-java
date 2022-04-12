# TCGenerator

TCGenerator is a tool to compute test cases covering different coverage properties. It takes output from Modelinho (https://modelinho.ist.tugraz.at/v1/) in form of constraints stored in a `*.smt2` file as input and prints the computed test cases. If desired, it also writes them to a `*.csv` file. Currently the tool provides implementations for branch coverage and path coverage.

# Usage

The tool expects 3 command line parameters:
1. **PATH**: path of `.smt2` input file
2. **COVERAGE PROPERTY**: desired coverage property, e.g. `bc` for branch coverage, `pc` for path coverage
3. **EXPORT**: specifies whether the test cases should also be exported to a `*.csv` file in addition to printing them as per default. `1` for export, `0` to avoid export

# Requirements

The tool was implemented in Java with OpenJDK 17 and requires Z3 (https://github.com/Z3Prover/z3) as the only external library.
