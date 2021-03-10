[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Fire Drill

# Fire Drill

## Also Known As

n/a

## Summary

Requirements and Analysis phases prolonged and consuming disproportionate amount of resources (because management want to do them "right"), then frantic "everything needs to be done yesterday" period to finish on time (when management finds out they wasted most of project's schedule and resources on analysis).

## Context

Waterfall(ish) projects, especially when project oversight is loose and/or management is not driven by outcome.

## Unbalanced Forces

 - need (desire) to have specifications perfect
 - management consumed by internal (political) issues
 - actual development of a high-quality product takes time
 - quality objectives formally stated and high
 - strict deadlines for delivery

## Symptoms and Consequences

 - long period at project start where activities connected to requirements, analysis and planning prevale, and design and implementation activities are rare
 - only analytical or documentational artefacts for a long time
 - relatively short period towards project end with sudden increase in development efforts (i.e. rock-edge burndown, especially when viewing implementation tasks only)
 - little testing/QA and project progress tracking activities during development period
 - final product with poor code quality, many open bug reports, poor or patchy documentation
 - if quality is not compromised, project schedule or scope compromised (i.e., either delayed delivery or descoping occurs)
 - stark contrast between interlevel communication in project hierarchy (management - developers) during the first period (close to silence) and after realizating the problem (panic and loud noise)

## Causes

 - management does not take seriously development effort (time) estimates
 - management absorbed in "various technopolitical issues (...) prevent[ing] the development staff from making progress"
 - team is happy to produce artefacts early in the project
 - requirements are complex and their prioritization is not forced early on
 - team overseeing the need to prioritize "working code over comprehensive documentation" 
 - management wants to appear the project to be on track
 - management believes it is more important to deliver complete functionality than good quality
 - project tracking and oversight is loose, easily lulled inco complacency by easy-to-reach outcomes

## (Refactored) Solution

 - force the team to start delivering (parts of) the "consumable solution" early, possibly alongside the analysis and planning artefacts, by instituting strong project tracking and oversight related to actual outcomes
 - it helps to follow an iterative process, architecture-driven development, and have a well-performing product owner 

## Variations (optional) 

## Example(s) (optional) 

## Related Anti-patterns

|Anti-pattern  | Relation |
|--|--|
| [Analysis Paralysis](Analysis_Paralysis.md) | potential cause |
| [Collective Procrastination](Collective_Procrastination.md) | more generic case |

## Notes (optional)

## Sources
[[SIL'15]](../References.md), [[SOU'18]](../References.md) [Fire Drill](https://sourcemaking.com/antipatterns/fire-drill), [[BRO'98]](../References.md)
