[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Warm Bodies

# Warm Bodies

## Also Known As

Deadwood, Mythical Man-Month

## Summary

The Warm Bodies anti-pattern occurs when too many developers are assigned to a software project, or when additional developers are added to an ongoing project to increase speed. As a result, team productivity does not scale with team size and some team members contribute little or no real value to the project. Exceptionally productive programmers become frustrated, partly because they spend time fixing the damage of the warm bodies.

## Context

It is common for large-scale projects with hundreds of staff or for projects under time pressure or experiencing delays.

## Unbalanced Forces

- Pressure to deliver faster.
- Belief that adding more developers increases productivity.
- Limited availability of highly skilled programmers.

## Symptoms and Consequences

- Team size grows beyond what is manageable as a single cohesive unit (generally more than 5–7 developers per team).
- Productivity does not increase proportionally to team size and may actually decrease.
- Decision-making becomes slower and less efficient.
- Some team members make minimal contributions to the codebase.
- Decline in quality due to coordination problems.

## Causes

- Misunderstanding of how software productivity scales with team size (Brooks's Law: adding people to a late project makes it later).
- Attempt to compensate for poor architecture.
- Management panic under time pressure.
- Failure to recognize that some project durations cannot be shortened regardless of the number of people assigned.
- Overestimation of average developer productivity and underestimation of skill variance.

## (Refactored) Solution

- Keep team small, ideally around 4-7 developers.
- Define clear ownership and individual accountability.
- Fix design and architectural problems directly instead of compensating with adding more people.
- Prefer multiple small, focused teams over one large, loosely coordinated group.
- When adding new team members is necessary, plan for an explicit onboarding period during which productivity will temporarily decrease.


## Related Anti-patterns

|Anti-pattern  | Relation |
|--|--|
| [Brooks' Law](Brooks_Law.md) | one source of Warm Bodies |
| [Net Negative Producing Programmer](Net_Negative_Producing_Programmer.md) | extreme case of a Warm Body |

## Notes

Small, cohesive teams with strong accountability are significantly more likely to deliver successful software than large, inflated project groups.

The description in [CUN'13] and the full mini-AntiPattern description in [BRO'98] (taken from there) is rather unclear, focuses on staff size and differences in programmer productivity, does not explain what "warm body" actually is.  The [Never Fire Anyone](http://wiki.c2.com/?NeverFireAnyone) anti-pattern actually explains this, to some extent.

## Sources

[[CUN'13]](../References.md), [[BRO'98]](../References.md), [[NIS'22]](../References.md), [[BRO'22]](../References.md), [[SWC'nd]](../References.md)
