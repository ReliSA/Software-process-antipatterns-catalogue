[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Architects Play Golf

# Architects Play Golf

## Also Known As

n/a

## Summary

Architects Play Golf is an anti-pattern in which system architects do not participate in the project after delivering the initial architectural design. They consider their contribution complete and expect the development team to comply strictly with the documented architecture. No further guidance, clarification, or decision-making is provided. This pattern is particularly common when design and development are handled by different organizations under a contract-based model.

## Context

This anti-pattern typically appears in projects with a strong phase-based or lifecycle, such as waterfall development or outsourced delivery models, where architecture is treated as a deliverable rather than an ongoing responsibility.

## Unbalanced Forces

- Desire to reduce architect workload vs. need for ongoing technical guidance.
- Desire for clear role separation vs. the need for shared ownership and close cooperation between design and development.
- Reliance on formal documentation to transfer architectural knowledge vs. the tacit knowledge that can only be transferred through continuous collaboration.

## Symptoms and Consequences

- Architecture and implementation are the responsibility of different teams or companies.
- People with the architect role do not interact with coding tasks (tickets), or implementation artifacts after the design phase.
- Architects are unavailable for questions, clarifications, or decision-making.
- Architectural decisions are not updated as the system evolves and new constraints emerge.
- Developers are forced to make significant architectural decisions without guidance, leading to divergence from the original design.
- Large redesigns or refactorings occur late in the project.

## Causes

- Misunderstanding of architecture as documentation rather than continuous decision-making.
- Architects are measured only on delivery of design artifacts, not system outcomes.
- Organization strictly separates architecture and development.

## (Refactored) Solution

- Architects should remain engaged throughout the full project lifecycle, not only during the design phase.
- Architects should share responsibility for both design quality and implementation outcomes.
- Architects should work closely with development teams.
- Architectural decisions should be documented and actively updated as the system evolves.

## Example(s) (optional)

An external architecture team delivers a complete system design and exits the project. During implementation, developers encounter constraints and performance issues not addressed in the architecture. Since architects are no longer involved, the team either implements inefficient workarounds or strictly follows the design at high cost, leading to delays and reduced system quality.

## Related Anti-patterns

| Anti-pattern  | Relation |
|--|--|
| [Architects Don't Code](Architects_Dont_Code.md) | milder version (Architects remain nominally present but do not engage with the implementation through coding.) |

## Notes (optional)

Architecture should not be treated as a project phase but as an ongoing responsibility that evolves with the system. A design that is not continuously validated against implementation reality will inevitably diverge from it.

## Sources

[[CUN'13]](../References.md)
