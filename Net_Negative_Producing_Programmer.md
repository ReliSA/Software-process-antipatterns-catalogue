[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Net Negative Producing Programmer

# Net Negative Producing Programmer

## Also Known As

n/a


## Summary

A Net Negative Producing Programmer (NNPP) is a person whose overall contribution to a project is negative, despite appearing productive. Although the individual may produce an average or above-average amount of code, the defects, rework, disruption, and coordination costs introduced outweigh the benefits of their contributions. As a result, the team would progress faster and with higher quality if the NNPP were removed or reassigned.


## Context

This anti-pattern typically emerges in development teams that measure productivity using superficial metrics (e.g., lines of code, tasks completed) and lack effective quality controls. It is often tolerated due to staffing constraints or the perception that “any contribution is better than none”.


## Unbalanced Forces

-	Pressure to maintain or increase apparent productivity.
-	Difficulty in objectively assessing negative impact.
-	Reluctance to confront or reassign underperforming individuals.
-	Metrics that reward output rather than outcome.
-	Fear of reducing headcount in already understaffed projects.


## Symptoms and Consequences

-	The NNPP introduces a higher number of defects than other team members.
-	Defects introduced by the NNPP take disproportionately long to resolve.
-	Other team members spend significant time fixing or working around NNPP’s work.
-	The nominal productivity of the NNPP (e.g., LOC per time period) is average or higher.
-	Overall team velocity decreases despite apparent individual productivity.
-	Morale and trust within the team deteriorate.
-	Technical debt increases due to poor-quality contributions.


## Causes

-	Inadequate skill level for the assigned tasks.
-	Lack of proper mentoring, feedback, or code review.
-	Overreliance on misleading productivity metrics.
-	Poor hiring or onboarding processes.
-	Organizational reluctance to address performance issues.


## (Refactored) Solution

Measure productivity at the team and outcome level rather than focusing on individual output metrics. Introduce strong quality controls such as code reviews, testing, and pairing. Provide targeted mentoring or reassignment to better-suited tasks. If negative impact persists, make decisive management actions to protect overall team effectiveness.


## Variations (optional) 

-	Hidden NNPP: Negative impact only becomes visible over time.
-	Senior NNPP: Authority or reputation masks harmful behavior.
-	Temporary NNPP: A capable developer operating outside their competence domain.


## Example(s) (optional) 

A developer consistently delivers features quickly but introduces subtle defects that require extensive debugging and rework by other team members, resulting in net project slowdown.


## Related Anti-patterns

|Anti-pattern  | Relation |
|--|--|
| [Brook's Law](Brooks_Law.md) | TBD |
| [Lone-Wolf](Lone-Wolf.md) | NNPP person might be a Lone Wolf |
| [Warm Bodies](Warm_Bodies.md) | NNPP can be a source of Warm Bodies |

## Notes (optional) 

The NNPP anti-pattern highlights that productivity must be evaluated in terms of net value, not raw output.


## Sources

* [CUN'10](../References.md) -- see the [description on C2 wiki](https://wiki.c2.com/?NetNegativeProducingProgrammer)
* http://www.pyxisinc.com/NNPP_Article.pdf -- the original article discussing the anti-pattern in detail, referenced from \[CUN'10\]
