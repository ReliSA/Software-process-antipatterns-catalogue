[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Brownie's Works

# Brownie's Works

## Also Known As

n/a


## Summary

Brownie’s Works is an anti-pattern in which junior team members actively contribute code, but their work is later completely rewritten and committed by a senior developer without prior discussion or collaboration. Although the code technically improves, the junior developer’s motivation and sense of ownership deteriorate. This anti-pattern often emerges as a side effect of Collective Code Ownership combined with a failure by senior members to apply mentoring practices such as pair programming or constructive code reviews. The root cause is a lack of team-building awareness on the part of senior developers.


## Context

This anti-pattern typically occurs in teams that practice Collective Code Ownership, especially within XP-like environments, where any developer is allowed to change any part of the codebase. It is more likely to appear in teams with large skill gaps between junior and senior developers.


## Unbalanced Forces

-	Junior developers need feedback, learning opportunities, and recognition.
-	Senior developers aim to maintain high code quality and system consistency.
-	Collective Ownership allows unrestricted code changes.
-	Time pressure discourages mentoring and collaboration.
-	Lack of explicit mentoring responsibility leads to unilateral rewrites.


## Symptoms and Consequences

-	A junior developer commits code related to a specific ticket or task.
-	The same code is later completely rewritten by a senior developer.
-	The senior’s rewrite is committed without prior discussion or explanation.
-	No evidence of pair programming or collaborative refactoring.
-	The rewritten code is included in a release.
-	Junior developers feel undervalued and discouraged.
-	Knowledge transfer is reduced, reinforcing skill gaps.
-	Team cohesion and trust gradually erode.


## Causes

-	Senior developers prioritize code quality over team development.
-	Absence of structured mentoring practices.
-	Misinterpretation of Collective Ownership as permission to overwrite work.
-	High delivery pressure reducing time for collaboration.
-	Lack of awareness of the motivational impact on junior developers.


## (Refactored) Solution

Encourage senior developers to collaborate with junior members through pair programming, joint refactoring sessions, or detailed code reviews before making substantial changes. Establish explicit mentoring responsibilities and promote a culture where improving code also means improving people. Significant rewrites should be discussed and explained to ensure learning and shared ownership rather than silent replacement.


## Variations (optional) 

-	Silent Rewrite: The junior developer only notices the rewrite after the release.
-	Repeated Brownie’s Works: The same junior developer experiences this pattern repeatedly.
-	Distributed Brownie’s Works: Occurs in distributed teams where communication gaps amplify the effect.


## Example(s) (optional) 

A junior developer implements a feature and commits it to the repository. Before the next release, a senior developer rewrites the entire implementation without discussion and commits it under the same ticket. The junior developer sees their work disappear and receives no feedback.


## Related Anti-patterns

n/a


## Notes (optional) 

This anti-pattern most commonly occurs in projects using XP methodology when mentoring practices are weak or inconsistently applied.


## Sources
[[KUR'04]](../References.md)
