[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Berlin Wall

# Berlin Wall

## Also Known As

n/a

## Summary

Berlin Wall is a branching anti-pattern in which branches are organized around the members of the development team rather than around the work they are performing. It is important to note that branching per team member is not inherently wrong. Microsoft's own branching guide explicitly lists "a project team member" as a valid branching strategy. The anti-pattern arises specifically when personal branches replace rather than complement a work-based branching strategy, and when they result in long-lived isolation, absence of integration, and division of the team rather than division of the work.

## Context

This anti-pattern appears in any project using version control branching, but is particularly common in student or junior team environments.

## Unbalanced Forces

- Organizational or team structure is more visible and intuitive to developers than work breakdown structure, making it a natural basis for branching decisions.
- Personal branches can be a legitimate short-term tool for individual isolation, which makes the anti-pattern harder to recognize until its consequences become visible.

## Symptoms and Consequences

- Branch names in the repository correspond to team member names or personal identifiers rather than features, tasks or components.

## Causes

- Misunderstanding of the purpose of branching. Branches should reflect units of work, not teammates, unless personal branches are used intentionally as a short-term isolation tool within a broader strategy.

## (Refactored) Solution

- Define and communicate a branching strategy before the project begins, branches should primarily correspond to features, tasks, or components rather than to people.

## Related Anti-patterns

n/a

## Notes (optional)

n/a

## Sources
[BIR'06]
