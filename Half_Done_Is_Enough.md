[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Half Done Is Enough

# Half Done Is Enough

## Also Known As

Untested but Finished


## Summary

Half Done Is Enough is an anti-pattern in which project activities are overly focused on visible progress, typically driven by management decisions. Effort is concentrated on adding new functionality, while essential activities such as testing, quality assurance, refactoring, maintenance, and proper analysis are reduced or ignored. Although the project appears to advance, technical debt accumulates rapidly, eventually requiring major corrective changes to deployed systems.


## Context

This anti-pattern commonly arises in projects under strong schedule or delivery pressure, where progress is measured primarily by the number of implemented features rather than by system quality, stability, or maintainability.


## Symptoms

-	A small proportion of project effort is allocated to activities other than implementation.
-	Testing, QA, refactoring, and architectural work are postponed or minimized.
-	Technical debt increases steadily.
-	Lack of modularity, reuse, and architectural consistency.
-	Rising defect rates as new features are added.
-	Deadlines are repeatedly postponed despite apparent progress.
-	Long periods dominated by bug fixing and maintenance activities.


## Consequences

-	System quality degrades over time.
-	Development velocity decreases instead of increasing.
-	Maintenance costs grow significantly.
-	Major rework or system redesign becomes necessary.
-	Stakeholder confidence erodes as delivery becomes unpredictable.


## Causes

-	Management emphasis on short-term, visible results.
-	Metrics that reward feature count rather than quality.
-	Underestimation of testing and maintenance effort.
-	Lack of understanding of technical debt and its long-term impact.
-	Pressure to demonstrate progress to stakeholders.


## (Refactored) Solution

Adopt a balanced approach to development that treats testing, quality assurance, refactoring, and maintenance as first-class activities. Define “done” to include adequate testing and documentation. Track technical debt explicitly and allocate time to reduce it continuously. Use progress metrics that reflect quality and sustainability, not just feature delivery.


## Variations (optional) 

-	Feature-Driven Illusion: Progress measured only by added functionality.
-	Deferred Quality: Quality activities are systematically postponed to “later”.
-	Permanent Stabilization Phase: Project becomes trapped in endless bug fixing.


## Example(s) (optional) 

A team rapidly delivers new features to meet milestone deadlines, skipping automated tests and refactoring. After deployment, defect reports surge, and development halts for months while the team stabilizes the system.


## Related Anti-patterns

|Anti-pattern  | Relation |
|--|--|
| [False Economy](False_Economy.md) | Opposite case: cost-saving measures ignore long-term consequences |


## Notes (optional)

This anti-pattern often emerges gradually and may initially appear successful before its long-term effects become visible.


## Sources

[[CUN'10]](../References.md)
