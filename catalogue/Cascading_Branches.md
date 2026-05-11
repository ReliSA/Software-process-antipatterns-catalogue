[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Cascading Branches

# Cascading Branches

## Also Known As

n/a

## Summary

Cascading Branches is a branching anti-pattern in which developers create side branches but never merge them back into the main development line. The branch completes its initial purpose (isolating a feature or experiment) but its lifecycle is never properly closed. It is worth noting that not every unmerged branch is necessarily problematic: an abandoned experimental branch or a branch tied to a discontinued feature may be intentionally left unmerged for valid reasons.

## Context

This anti-pattern can occur in any development methodology that uses version control branching, but is especially common in student or junior team environments where developers are unfamiliar with the full branch lifecycle. It also appears in teams under time pressure, where merge conflicts are encountered and avoided rather than resolved.

## Unbalanced Forces

- Branching is encouraged as a safe way to isolate work in progress without affecting the main line.
- Merging requires effort, conflict resolution, and coordination.

## Symptoms and Consequences

- Side branches exist in the repository that contain committed work but have no merge back into main.
- In closed or finished projects, functionality may be permanently lost.

## Causes

- Developers might encounter merge conflicts.
- In student projects, the experimental or exploratory purpose of a branch is forgotten and the branch is simply abandoned.

## (Refactored) Solution

- Establish a clear branch lifecycle policy: every branch opened for a feature or task must be either merged or explicitly closed with a documented reason.
- Use short-lived branches, merge back to main as soon as the work unit is complete.

## Related Anti-patterns

n/a

## Notes (optional)

n/a

## Sources
[BIR'06]
