[Home](../README.md) > [Catalogue](../Antipatterns_catalogue.md) > Fire Drill

# Fire Drill

## Also Known As

n/a

## Summary

Requirements and Analysis phases prolonged and consuming disproportionate amount of resources (because management want to do them "right"), then frantic "everything needs to be done yesterday" period to finish on time (when management finds out they wasted most of project's schedule and resources on analysis).

## Context

## Unbalanced Forces

## Symptoms and Consequences
 - rock-edge burndown (especially when viewing implementation tasks only)
 - long period at project start where activities connected to requirements, analysis and planning prevale, and design and implementation activities are rare
 - only analytical or documentational artefacts for a long time
 - relatively short period towards project end with sudden increase in development efforts 
 - little testing/QA and project progress tracking activities during development period
 - final product with poor code quality, many open bug reports, poor or patchy documentation 
 - stark contrast between interlevel communication in project hierarchy (management - developers) during the first period (close to silence) and after realizating the problem (panic and loud noise)

## Symptoms in source code and Consequences
 - rock-edge burndown of esp. implementation tasks mean there are no or just very few adaptive maintenance activities before the burning down starts
 - the long period at project start translates to few modifications made to the source code, resulting in fewer commits (lower overall relative frequency)
 - likewise, documentational artifacts have a lower _source code density_, as less functionality is delivered; this density should increase as soon as adaptive activities are registered
 - the short period at project end is characterized by a higher frequency of higher-density implementation tasks, with little to no perfective or corrective work
 - at the end of the project, code quality is comparatively lower, while complexity is probably higher, due to pressure excerted on developers in the burst phase

## Causes

## (Refactored) Solution

## Variations (optional) 

## Example(s) (optional) 

## Related Anti-patterns

|Anti-pattern  | Relation |
|--|--|
| [Analysis Paralysis](Analysis_Paralysis.md) | potential cause |
| [Collective Procrastination](Collective_Procrastination.md) | more generic case |

## Notes (optional)

## Sources
[[SIL'15]](../References.md), [[SOU'18]](../References.md), [[BRO'98]](../References.md)
