[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Architects Don't Code
# Architects Don't Code

## Also Known As

Ivory Tower Architects, Astronaut Architects

## Summary

Architects Do Not Code is an anti-pattern in which software architects define system designs, standards, and major technical decisions without actively participating in coding. As a result, architectural decisions are detached from practical implementation realities. Architects lose understanding of the codebase, tools, and constraints, which leads to designs that are difficult to implement, evolve, or maintain.

## Context

This anti-pattern commonly appears in large organizations with rigid role separation and strong hierarchies. It will most likely occur in waterfall or document-driven development models, where architecture is defined upfront and handed over to development teams. The organization may have many junior programmers and relatively few experts.

## Unbalanced Forces

- Expert time is expensive, rare, or both.
- Developers need practical, implementable guidance rather than theoretical models.
- Managers values documentation and planning as evidence of architectural progress.

## Symptoms and Consequences

- Architectural designs are impractical or overly abstract.
- Architecture documents quickly become outdated or ignored.
- The actual codebase diverges significantly from the official architecture.
- People with the architect role do not interact with coding tasks (tickets).
- Architects do not generate or modify any source code artifacts.
- Architects only interact with non-coding people, tasks (tickets) and artifacts.
- Increased technical debt due to mismatches between design and implementation.
- Developers silently simplify or bypass architectural decisions to make the system work.
- Large refactorings are required late in the project.
- Reduced trust between architects and developers.

## Causes

- Organizational culture enforces strict separation between roles and responsibilities.
- Architects are not expected or incentivized to contribute to the codebase.
- The belief that architectural authority should remain independent from day-to-day development.
- Architects lose coding skills over time and become reluctant to re-engage with implementation.

## (Refactored) Solution

- Architects should actively participate in code reviews, technical spikes, and challenging implementation tasks.
- The organization should establish short feedback loops between architectural decisions and their implementation.
- Architectural documents should be treated as living artifacts, updated as implementation reveals new constraints.

## Example(s) (optional)

An architect defines a complex service interaction model without implementing any part of it. Developers struggle with performance and deployment issues that were not anticipated. Eventually, the team simplifies the design locally, diverging from the official architecture while still being expected to comply with it in reviews.

## Related Anti-patterns

| Anti-pattern  | Relation |
|--|--|
| [Viewgraph Engineering](Viewgraph_Engineering.md) | similar in kind (Technical roles avoid technical work in favor of documentation and presentations.) |
| [Architects Play Golf](Architects_Play_Golf.md) | extreme version (Architects do not participate in the project after the architecture phase is done) |

## Notes

Architects do not need to code all the time, but they must code often enough to stay in touch with reality. Architecture that is not continuously validated through implementation tends to degrade into theory rather than guidance.

## Sources

[[BRA'19]](../References.md), [[CUN'13]](../References.md), [[LAN'12]](../References.md), [[DOR'24]](../References.md), [[CUN'10]](../References.md), [[BRO'95]](../References.md)